package xyz.apolicky.constraints.inclusiondependencies.comparisons;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;
import xyz.apolicky.constraints.columncomparisons.ColumnComparisonRegistry;
import xyz.apolicky.constraints.columncomparisons.InfoTypePair;
import xyz.apolicky.constraints.columnmetadata.model.ColumnPairWithTypeAndApplicableComparisons;
import xyz.apolicky.constraints.inclusiondependencies.model.ColumnPairWithTheirInfoTypes;
import xyz.apolicky.constraints.inclusiondependencies.model.CustomRow;
import xyz.apolicky.constraints.inclusiondependencies.pairings.InclusionDepBasedRowPairer;
import xyz.apolicky.constraints.neo4j.model.ColumnPairWithInfoTypes;
import xyz.apolicky.constraints.neo4j.model.DatasetNamePair;
import xyz.apolicky.constraints.neo4j.service.InclusionDependencyService;
import xyz.apolicky.constraints.utils.SparkHelpers;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
public class ReferencedPairsComparator {
    private final InclusionDependencyService inclusionDependencyService;
    private final ColumnComparisonRegistry columnComparisonRegistry;

    @Autowired
    public ReferencedPairsComparator(InclusionDependencyService inclusionDependencyService, ColumnComparisonRegistry columnComparisonRegistry) {
        this.inclusionDependencyService = inclusionDependencyService;
        this.columnComparisonRegistry = columnComparisonRegistry;

    }

    /* odfiltruju inclusionDependencies, ktere nejsou mezi IDcky
     * zkusim najit pary datasetu z inclusion dependencies --> to je zatim k nicemu 🤷
     *   vezmu tableIdentifiery, udelam nakonec Set<Pair<SourceTableName,TargetTableName>>
     * chci skoncit ve stavu, kdy mam
     *        klic: radek z SourceTable
     *        value: radek z TargetTable
     *        nakonec to zgroupuju podle klice
     * proto ale potrebuju sparovat radky ze SourceTable s radky s TargetTableName
     *        u kazde inclusionDependency vim, co je source a table dulezitej sloupec
     *        z toho bych si mohl udelat klice, stejne jako to mam u findCardinalitiesOfInclusionDependencies
     *        podle toho sparovat radek ze SourceTable a radek z TargetTable
     *        jedine, co by bylo jinak by byla hodnota, kterou posilam dal, zu by to nebyla ValueDistribution ale neco jineho/lepsiho
     */
    public JavaRDD<RowComparisonResult> compareCellsOfRowsPairedByInclusionDependencies(SparkSession spark, Boolean idCandidateMustBeString) {
        var inclusionDependencies = inclusionDependencyService.getInclusionDependenciesWithIdCandidates(idCandidateMustBeString);
        var rddPairsOfSourceRowToTargetRow = InclusionDepBasedRowPairer.pairInclusionDependencyRows(spark, inclusionDependencies);

        // perform neo4j lookup to find pairs of columns to compare for them datasets 🐶
        var datasetColumnPairsThatCanBeCompared = inclusionDependencyService.getInclusionDependentDatasetColumnPairsWithComparableType();

        // now we can take a look at per-column statistics
        var rddPairsOfSourceRowToGroupedTargetRows = rddPairsOfSourceRowToTargetRow.groupByKey();

        var rowComparisonResults = compareCellsBasedOnProvidedPairs(rddPairsOfSourceRowToGroupedTargetRows, datasetColumnPairsThatCanBeCompared);

        // prepare for map-reduce
        var rowComparisonResultsWithKeys = rowComparisonResults.mapToPair(rowComparisonResult -> new Tuple2<>(rowComparisonResult.getKvMappingKey(), rowComparisonResult));

        var perRowComparisonResults = rowComparisonResultsWithKeys.reduceByKey(RowComparisonResult::aggregateRowComparisons).values();

        SparkHelpers.debugPrintBasicRdd(perRowComparisonResults);
        return perRowComparisonResults.filter(res ->
                !res.comparisonResultEnumForm().isEmpty()); // TODO: tohle odstraň za chvíli, jestli nebude potřeba
    }

    public long getNumberOfIndBasedPairingsInGivenDataset(SparkSession spark, Boolean idCandidateMustBeString) {
        var inclusionDependencies = inclusionDependencyService.getInclusionDependenciesWithIdCandidates(idCandidateMustBeString);
        var rddPairsOfSourceRowToTargetRow = InclusionDepBasedRowPairer.pairInclusionDependencyRows(spark, inclusionDependencies);
        return rddPairsOfSourceRowToTargetRow.count();
    }

    public JavaRDD<RowComparisonResult> compareCellsOfRowsPairedByInclusionDependencies(SparkSession spark) {
        return compareCellsOfRowsPairedByInclusionDependencies(spark, false);
    }

    // tady dostanu rdd
    //      referenced row
    //      [depenant rows]
    private JavaRDD<RowComparisonResult> compareCellsBasedOnProvidedPairs(JavaPairRDD<CustomRow, Iterable<CustomRow>> rddPairsOfSourceRowToAllTargetRows,
                                                                          Map<DatasetNamePair, @NotNull List<ColumnPairWithTheirInfoTypes>> columnPairsThatCanBeComparedForPairOfDatasets) {
        var allAvailableComparisonsForInfoTypeEnumForm = columnComparisonRegistry.getAllAvailableComparisonsEnumFormatForInfoType();
        var allAvailableComparisonsForInfoTypeStringForm = columnComparisonRegistry.getAllAvailableComparisonsStringFormatForInfoType();

        JavaRDD<RowComparisonResult> rowComparisonResults = rddPairsOfSourceRowToAllTargetRows.flatMap(pairOfLinkedRows -> {
                    var streamOfSourceAndTargetRowComparisons = StreamSupport.stream(pairOfLinkedRows._2.spliterator(), false).flatMap(dependantRow -> {
                                var referencedRow = pairOfLinkedRows._1();
                                var columnPairsForComparisonOfTheseTwoDatasets = columnPairsThatCanBeComparedForPairOfDatasets.get(new DatasetNamePair(referencedRow.datasetName(), dependantRow.datasetName()));
                                var columnPairsWithTypesAndApplicableComparisons = columnPairsForComparisonOfTheseTwoDatasets.stream().map(cpwtit -> {
                                            var infoTypePair = new InfoTypePair(cpwtit.referencedInfoType(), cpwtit.dependantInfoType());
                                            return new ColumnPairWithTypeAndApplicableComparisons(
                                                    new ColumnPairWithInfoTypes(cpwtit.referencedDatasetColumn(), cpwtit.dependantDatasetColumn(), cpwtit.referencedInfoType(), cpwtit.dependantInfoType()),
                                                    allAvailableComparisonsForInfoTypeEnumForm.get(infoTypePair),
                                                    allAvailableComparisonsForInfoTypeStringForm.get(infoTypePair)
                                            );
                                        }
                                ).toList();
                                return RowComparisonResult.ofSourceAndTargetRowsAsStream(referencedRow, dependantRow, columnPairsWithTypesAndApplicableComparisons);
                            }
                    );
                    return streamOfSourceAndTargetRowComparisons.iterator();
                }
        );
        SparkHelpers.debugPrintBasicRdd(rowComparisonResults);
        return rowComparisonResults;
    }

}
