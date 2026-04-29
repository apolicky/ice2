package xyz.apolicky.constraints.utils;

import xyz.apolicky.constraints.inclusiondependencies.model.MyInclusionDependency;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public static final String defaultInputFilesForRequestParam =
//            "yelp_tip_tinkered2.csv," +
//            "imdb_title.csv," +
            "scammer-info.csv," +
                    "cz-birth-numbers.csv," +
//            "one-to-one-mappings.csv," +
                    "employees.csv," +
                    "pattern-matching.csv," +
//            "dp-example-01-depts.csv," +
//            "dp-example-01-employees.csv," +
//            "dp-example-02-order-items.csv," +
//            "dp-example-02-orders.csv," +
//            "dp-example-02-stock-items.csv," +
//            "type_inference.csv," +
//            "f2-circuits.csv," +
//            "f2-constructors.csv," +
//            "f2-constructor_results.csv," +
//            "f2-constructor_standings.csv," +
//            "f2-drivers.csv," +
//            "f2-driver_standings.csv," +
//            "f2-lap_times.csv," +
//            "f2-pit_stops.csv," +
//            "f2-qualifying.csv," +
//            "f2-races.csv," +
//            "f2-results.csv," +
//            "f2-seasons.csv," +
//            "f2-sprint_results.csv," +
//            "f2-status.csv," +
//            "f3-circuits.csv," +
//            "f3-constructors.csv," +
//            "f3-constructor_results.csv," +
//            "f3-constructor_standings.csv," +
//            "f3-drivers.csv," +
//            "f3-driver_standings.csv," +
//            "f3-lap_times.csv," +
//            "f3-pit_stops.csv," +
//            "f3-qualifying.csv," +
//            "f3-races.csv," +
//            "f3-results.csv," +
//            "f3-seasons.csv," +
//            "f3-sprint_results.csv," +
//            "f3-status.csv," +
//            "yelp1-business_100k.csv," +
//            "yelp1-business_500.csv," +
//            "yelp1-business_50k.csv," +
//            "yelp1-business_5k.csv," +
//            "yelp1-tip_100k-tinkered.csv," +
//            "yelp1-tip_500-tinkered.csv," +
//            "yelp1-tip_500.csv," +
//            "yelp1-tip_50k-tinkered.csv," +
//            "yelp1-tip_50k.csv," +
//            "yelp1-tip_5k-tinkered.csv," +
//            "yelp1-tip_5k.csv," +
//            "yelp1-user_100k.csv," +
//            "yelp1-user_500.csv," +
//            "yelp1-user_50k.csv," +
//            "yelp1-user_5k.csv," +
//            "imdb-title.akas_200k.csv," +
//            "imdb-title.akas_20k.csv," +
//            "imdb-title.basics_200k.csv," +
//            "imdb-title.basics_20k.csv," +
//            "imdb-title.ratings_200k.csv," +
//            "imdb-title.ratings_20k.csv," +
                    "xxx-non-existent.csv"; // for easier changes of datasets


    public static Map<String, String> getMapOfFilenamesOfInclusionDependencies(List<MyInclusionDependency> inclusionDependencies) {
        return inclusionDependencies.stream().flatMap(inclusionDependency ->
                        Stream.of(inclusionDependency.referencedDatasetName(), inclusionDependency.dependantDatasetName()))
                .distinct()
                .collect(Collectors.toMap(
                        tableIdentifier -> tableIdentifier,
                        FileUtils::getFilename));
    }

    public static String getFilename(String datasetName) {
        if (datasetName.startsWith("inputs/")) {
            return datasetName;
        } else {
            return "inputs/" + datasetName;
        }
    }

    public static String getDatasetName(String datasetName) {
        if (datasetName.startsWith("inputs/")) {
            var datasetNameParts = datasetName.split("/");
            return datasetNameParts[datasetNameParts.length - 1];
        }
        return datasetName;
    }
}

