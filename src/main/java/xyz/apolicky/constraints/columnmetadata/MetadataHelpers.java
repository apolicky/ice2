package xyz.apolicky.constraints.columnmetadata;

import org.apache.spark.api.java.JavaPairRDD;
import xyz.apolicky.constraints.columnmetadata.model.Info;

import java.util.List;
import java.util.Map;

public class MetadataHelpers {
    public static List<String> idCandidates(JavaPairRDD<String, Info> metadata) {
        return metadata.filter(ci -> ci._2.unique() && !ci._2.nullable()).keys().collect();
    }

    public static List<String> idCandidates(Map<String, Map<String, Info>> metadata) {
        return metadata.entrySet().stream().flatMap(e -> {
                    var datasetName = e.getKey();
                    var columnMetadata = e.getValue();
                    return columnMetadata.entrySet().stream()
                            .filter(m -> m.getValue().unique() && !m.getValue().nullable())
                            .map(column -> column.getKey() + " (" + datasetName + ")");

                })
                .toList();
    }
}
