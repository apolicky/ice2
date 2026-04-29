package xyz.apolicky.constraints.columnmetadata;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;
import xyz.apolicky.constraints.columnmetadata.helpers.InfoHelpers;
import xyz.apolicky.constraints.columnmetadata.helpers.PatternHelpers;
import xyz.apolicky.constraints.columnmetadata.model.Info;
import xyz.apolicky.constraints.columnmetadata.pattern.PatternRegistry;
import xyz.apolicky.constraints.utils.SparkHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ColumnMetadataDiscovery {

    private final PatternRegistry patternRegistry;

    @Autowired
    public ColumnMetadataDiscovery(PatternRegistry patternRegistry) {
        this.patternRegistry = patternRegistry;
    }

    public Map<String, Map<String, Info>> getColumnMetadata(SparkSession spark, List<String> datasetNames) {
        var results = new HashMap<String, Map<String, Info>>();

        for (String datasetName : datasetNames) {
            var dataset = SparkHelpers.loadDataset(spark, datasetName, SparkHelpers.FilenameMapper.LAST_FILENAME_PART);
            var result = getColumnMetadata(dataset);

            results.put(datasetName, result.collectAsMap());
        }

        return results;
    }

    public JavaPairRDD<String, Info> getColumnMetadata(Dataset<Row> dataset) {
        JavaRDD<Row> rowRDD = dataset.javaRDD();
        String[] columnNames = dataset.columns();

        var matchingPatternsForTypes = patternRegistry.getMatchingPattersForGivenTypes();

        JavaPairRDD<String, Info> kvPairs = rowRDD.flatMapToPair((PairFlatMapFunction<Row, String, Info>) row -> {
            List<Tuple2<String, Info>> pairs = new ArrayList<>();

            var fileNameIndex = row.fieldIndex("filename");
            var datasetName = row.getString(fileNameIndex);

            for (int i = 0; i < row.size(); i++) {
                if (i == fileNameIndex) {
                    continue;
                }

                Object value = row.get(i);
                var columnName = columnNames[i];
                var matchingType = InfoHelpers.matchingType(value);
                var matchingPatterns = PatternHelpers.findMatchingPatterns(value, matchingPatternsForTypes.get(matchingType));
                var info = Info.fromValue(value, columnName, datasetName, matchingType, matchingPatterns);
                String key = info.getValueString();

                pairs.add(new Tuple2<>(key, info));
            }
            return pairs.iterator();
        });

        JavaPairRDD<String, Object> kvPairs2 =
                rowRDD.flatMapToPair((PairFlatMapFunction<Row, String, Object>) row -> {
                    List<Tuple2<String, Object>> cells = new ArrayList<>();
                    for (int i = 0; i < row.size(); i++) {
                        var value = row.get(i);
                        var columnName = columnNames[i];
                        cells.add(new Tuple2<>(columnName, value));
                    }
                    return cells.iterator();
                });


        var v = kvPairs2.reduceByKey((left, right) -> Math.max((Integer) left, (Integer) right));

        var cellInfo = kvPairs
                .reduceByKey(Info::combineByKeyValue)
                .values();
        return cellInfo
                .mapToPair(info -> new Tuple2<>(info.name(), info))
                .reduceByKey(Info::combineByKey);
    }
}
