package xyz.apolicky.constraints.columnmetadata;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;
import xyz.apolicky.constraints.columncomparisons.ColumnComparisonRegistry;
import xyz.apolicky.constraints.columncomparisons.ColumnPair;
import xyz.apolicky.constraints.columncomparisons.ComparisonUtils;
import xyz.apolicky.constraints.columncomparisons.InfoTypePair;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonOperation;
import xyz.apolicky.constraints.columnmetadata.model.ColumnPairWithTypeAndApplicableComparisons;
import xyz.apolicky.constraints.columnmetadata.model.MultiColumnRelationResult;
import xyz.apolicky.constraints.neo4j.service.MetadataService;
import xyz.apolicky.constraints.utils.SparkHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MultiColumnRelationDiscovery {

    private static final Logger log = LoggerFactory.getLogger(MultiColumnRelationDiscovery.class);
    private final ColumnComparisonRegistry registry;

    @Autowired
    public MultiColumnRelationDiscovery(ColumnComparisonRegistry registry) {
        this.registry = registry;
    }

    private List<MultiColumnRelationResult> getTwoColumnComparisonsInner(SparkSession spark, String datasetName, MetadataService metadataService) {
        var comparableColumnPairsAndTheirTypes = metadataService.getColumnPairsWithComparableTypeFromDataset(datasetName);
        log.info("ComparableColumnPairsAndTheirTypes: {}", comparableColumnPairsAndTheirTypes.stream().map(Record::toString).collect(Collectors.joining(",")));
        Dataset<Row> dataset = SparkHelpers.loadDataset(spark, datasetName);
        JavaRDD<Row> rowRDD = dataset.javaRDD();

        var columnPairsWithTypesAndApplicableComparisons = comparableColumnPairsAndTheirTypes.stream()
                .map(colPairWithInfo -> new ColumnPairWithTypeAndApplicableComparisons(colPairWithInfo,
                        registry.getAvailableComparisonsEnumFormatForInfoType(colPairWithInfo.leftColumnInfoType(), colPairWithInfo.rightColumnInfoType()),
                        registry.getAvailableComparisonsStringFormatForInfoType(colPairWithInfo.leftColumnInfoType(), colPairWithInfo.rightColumnInfoType())
                ))
                .toList();

        JavaRDD<MultiColumnRelationResult> columnComparisonResults = rowRDD.flatMap((FlatMapFunction<Row, MultiColumnRelationResult>) row -> {
            List<MultiColumnRelationResult> results = new ArrayList<>();

            for (var columnPairWithApplicableComparisons : columnPairsWithTypesAndApplicableComparisons) {
                var columnPairWithType = columnPairWithApplicableComparisons.columnPairWithInfoTypes();
                var comparisonsToPerformEnumForm = columnPairWithApplicableComparisons.applicableComparisonsEnumForm();
                var comparisonsToPerformStringForm = columnPairWithApplicableComparisons.applicableComparisonsStringForm();
                var l = row.getAs(columnPairWithType.leftColumn());
                var r = row.getAs(columnPairWithType.rightColumn());

                var columnPair = new ColumnPair(columnPairWithType.leftColumn(), columnPairWithType.rightColumn());

                var comparisonResultsEnumForm = ComparisonUtils.applyAllComparisonsEnum(l, r, comparisonsToPerformEnumForm);
                var comparisonResultsStringForm = ComparisonUtils.applyAllComparisonsString(l, r, comparisonsToPerformStringForm);

                var res = new MultiColumnRelationResult(columnPair, comparisonResultsEnumForm, comparisonResultsStringForm);
                results.add(res);
            }
            return results.iterator();
        });

        var kvPairs = columnComparisonResults.mapToPair(c -> new Tuple2<>(c.getKvMappingKey(), c));
        var multiColumnRelationResult = kvPairs.reduceByKey(MultiColumnRelationResult::aggregateRowComparisons).values();

        SparkHelpers.debugPrintBasicRdd(multiColumnRelationResult);

        return multiColumnRelationResult.collect();
    }

    public Map<String, List<MultiColumnRelationResult>> getTwoColumnComparisons(SparkSession spark, List<String> csvFilePaths, MetadataService metadataService) {
        var results = new HashMap<String, List<MultiColumnRelationResult>>();
        for (String datasetName : csvFilePaths) {
            var result = getTwoColumnComparisonsInner(spark, datasetName, metadataService);
            results.put(datasetName, result);
        }
        return results;
    }

    public Map<InfoTypePair, List<TwoColumnComparisonOperation>> getComparisonsThatCanBeApplied() {
        return registry.getAllAvailableComparisonsEnumFormatForInfoType();
    }
}

