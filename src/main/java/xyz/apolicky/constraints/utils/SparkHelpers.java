package xyz.apolicky.constraints.utils;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.apolicky.constraints.AppConfiguration;

import static org.apache.spark.sql.functions.input_file_name;
import static org.apache.spark.sql.functions.regexp_extract;

public class SparkHelpers {

    private static final Logger log = LoggerFactory.getLogger(SparkHelpers.class);

    public static SparkSession getSparkSession(String name) {
        return SparkSession.builder().appName(name)
                .master("local[6]")
//                .config("spark.sql.shuffle.partitions", 12)
                .getOrCreate();
    }

    public static Dataset<Row> datasetFromCsv(SparkSession spark, String datasetName, FilenameMapper filenameMapper) {
        return spark.read()
                .option("escape", "\"")
                .option("multiLine", true)
                .option("delimiter", ",")
                .option("header", true)
                .option("inferSchema", true)
                .option("timestampFormat", AppConfiguration.DATE_TIME_FORMAT)
                .csv(datasetName)
                .withColumn("filename", getColumnMapping(filenameMapper));
    }

    public static Dataset<Row> loadDataset(SparkSession spark, String datasetName) {
        return loadDataset(spark, datasetName, SparkHelpers.FilenameMapper.LAST_FILENAME_PART);
    }

    public static Dataset<Row> loadDataset(SparkSession spark, String datasetName, FilenameMapper filenameMapper) {
        var datasetFilePath = FileUtils.getFilename(datasetName);
        return SparkHelpers.datasetFromCsv(spark, datasetFilePath, filenameMapper);
    }

    private static Column getColumnMapping(FilenameMapper mapper) {
        return switch (mapper) {
            case WHOLE_FILENAME -> input_file_name();
            case LAST_FILENAME_PART -> regexp_extract(input_file_name(), "^.*\\/(.*)$", 1);
        };
    }

    public static <K, V> void debugPrintBasicJavaPairRdd(JavaPairRDD<K, V> jpRdd) {
        log.debug("debugPrintBasicJavaPairRdd: ");
        for (var pair : jpRdd.take(30)) {
            log.info(pair.toString());
        }
    }

    public static <T> void debugPrintBasicRdd(JavaRDD<T> card) {
        log.debug("debugPrintBasicRdd: ");
        for (var c : card.take(30)) {
            log.info(c.toString());
        }
    }

    public enum FilenameMapper {
        WHOLE_FILENAME,
        /**
         * use only the filename, e.g. /home/bob/inputs/table.csv -> table.csv
         */
        LAST_FILENAME_PART
    }
}
