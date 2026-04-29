package xyz.apolicky.constraints.inclusiondependencies.aggregation;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.inclusiondependencies.comparisons.GroupComparisonRddKey;
import xyz.apolicky.constraints.inclusiondependencies.model.ColumnAndDatasetPair;
import xyz.apolicky.constraints.inclusiondependencies.model.ColumnPairWithTheirInfoTypes;
import xyz.apolicky.constraints.inclusiondependencies.model.CustomRow;
import xyz.apolicky.constraints.inclusiondependencies.model.MyInclusionDependency;
import xyz.apolicky.constraints.inclusiondependencies.pairings.InclusionDepBasedRowPairer;
import xyz.apolicky.constraints.neo4j.model.DatasetNamePair;
import xyz.apolicky.constraints.neo4j.service.InclusionDependencyService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
public class AggregatedStatisticsComparator {

    private final InclusionDependencyService inclusionDependencyService;

    @Autowired
    public AggregatedStatisticsComparator(InclusionDependencyService inclusionDependencyService) {
        this.inclusionDependencyService = inclusionDependencyService;
    }

    /**
     * Performs comparison of aggregated statistics of dependant rows against the referenced row.
     * For example:
     * - tables: Orders[id, totalAmount, dateCreated], OrderItems[orderId,itemId,itemPrice]
     * - order.totalAmount vs sum(orderItem.price)
     * - order.totalAmount vs avg(orderItem.price)
     * - ...
     */
    public JavaRDD<AggregatedReferencedToDependantAggregationResult> compareReferencedRowCellsAgainstAggregatedResultsOfReferencingRows(SparkSession spark, Boolean idCandidateMustBeString) {
        var inclusionDependencies = inclusionDependencyService.getInclusionDependenciesWithIdCandidates(idCandidateMustBeString);
        var rddPairsOfSourceRowToGroupedTargetRows = InclusionDepBasedRowPairer.pairInclusionDependencyRows(spark, inclusionDependencies).groupByKey();
        var dependantDatasets = inclusionDependencies.stream().map(MyInclusionDependency::dependantDatasetName).distinct().toList();
        var datasetColumnNames = inclusionDependencyService.getDatasetColumnNamesAndTypes(dependantDatasets);

        var datasetColumnPairsThatCanBeCompared = inclusionDependencyService.getInclusionDependentDatasetColumnPairsWithComparableType();
        var referencedRowsPairedWithAggregatedStats = createAggregatedStatisticsForPairedRows(rddPairsOfSourceRowToGroupedTargetRows, datasetColumnNames);

        return compareComparableReferencedColumnsWithAggregatedColumnResults(referencedRowsPairedWithAggregatedStats, datasetColumnPairsThatCanBeCompared);
    }

    public JavaRDD<AggregatedReferencedToDependantAggregationResult> compareReferencedRowCellsAgainstAggregatedResultsOfReferencingRows(SparkSession spark) {
        return compareReferencedRowCellsAgainstAggregatedResultsOfReferencingRows(spark, false);
    }

    private JavaPairRDD<GroupComparisonRddKey, AggregatedGroupResult> createAggregatedStatisticsForPairedRows(JavaPairRDD<CustomRow, Iterable<CustomRow>> rddPairsOfSourceRowToGroupedTargetRows,
                                                                                                              HashMap<String, List<Tuple2<String, InfoType>>> datasetColumnNames) {
        JavaPairRDD<GroupComparisonRddKey, AggregatedGroupResult> res = rddPairsOfSourceRowToGroupedTargetRows.flatMapToPair(pairOfLinkedRows -> {
            var referencedRow = pairOfLinkedRows._1();
            var groupOfDependantRows = pairOfLinkedRows._2();

            return StreamSupport.stream(groupOfDependantRows.spliterator(), false).flatMap(dependantRow -> {
                var dependantRowOriginal = dependantRow.originalRow();

                return datasetColumnNames.get(dependantRow.datasetName()).stream()
                        .map(columnNameAndInfoType -> {
                            var columnName = columnNameAndInfoType._1;
                            var infoType = columnNameAndInfoType._2;
                            var value = dependantRowOriginal.getAs(columnName);
                            var key = new GroupComparisonRddKey(referencedRow, dependantRow.datasetName(), columnName);
                            var aggregatedValue = AggregatedGroupResult.fromValue(value, columnName, infoType);
                            return new Tuple2<>(key, aggregatedValue);
                        });
            }).iterator();
        });

        return res.reduceByKey(AggregatedGroupResult::combineByKeyAndValue);
    }

    /*  here I'd like to have: for each referencedRow:
     *   - columns that make sense to be compared
     *   - value of referenced VS the value of min/max/../avg
     *
     * [refColName, refColDataset, depColName, depColDataset]:
     *   - valueComparedToMin: <, >, ==
     *   - valueComparedToMax: <, >, ==
     *   - valueComparedToSum: <, >, ==
     *   - valueComparedToAvg: <, >, ==
     *   - countOfValuesThatTheGroupHad: int
     * this gets then reduced/paired by the key [refColName, refColDataset, depColName, depColDataset]
     */
    private JavaRDD<AggregatedReferencedToDependantAggregationResult> compareComparableReferencedColumnsWithAggregatedColumnResults(JavaPairRDD<GroupComparisonRddKey, AggregatedGroupResult> referencedRowsPairedWithAggregatedStats,
                                                                                                                                    Map<DatasetNamePair, @NotNull List<ColumnPairWithTheirInfoTypes>> datasetColumnPairsThatCanBeCompared) {
        JavaPairRDD<ColumnAndDatasetPair, ReferencedValueComparedToAggregatedValues> standaloneRefValuesComparedToAggregationResults = referencedRowsPairedWithAggregatedStats.flatMapToPair(pair -> {
            var referencedRow = pair._1().referencedRow();
            var dependantDatasetName = pair._1().dependantDatasetName();
            var dependantColumnName = pair._1().dependantColumnName();

            var aggregatedResultOfDependantDatasetColumn = pair._2();

            var pairsToBeCompared = datasetColumnPairsThatCanBeCompared.get(new DatasetNamePair(referencedRow.datasetName(), dependantDatasetName)).stream()
                    .filter(columnPair -> columnPair.dependantDatasetColumn().equals(dependantColumnName))
                    .toList();

            return pairsToBeCompared.stream().map(p -> {
                var key = new ColumnAndDatasetPair(p.referencedDatasetColumn(), referencedRow.datasetName(), dependantColumnName, dependantDatasetName);
                return new Tuple2<>(key, ReferencedValueComparedToAggregatedValues.from(referencedRow.originalRow().getAs(p.referencedDatasetColumn()), aggregatedResultOfDependantDatasetColumn));
            }).iterator();
        });

        return standaloneRefValuesComparedToAggregationResults.groupByKey()
                .map(AggregatedReferencedToDependantAggregationResult::mergeForResult)
                .filter(AggregatedReferencedToDependantAggregationResult::shouldBeFilteredOut);
    }
}
