package xyz.apolicky.constraints.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ColumnFromTable(
        String columnName,
        String datasetName
) implements Serializable {
    public static Map<String, Set<String>> tablesAndColumnsMap(Stream<ColumnFromTable> stream) {
        return stream.collect(Collectors.toMap(
                ColumnFromTable::datasetName,
                (v) -> {
                    var s = new HashSet<String>();
                    s.add(v.columnName());
                    return s;
                },
                (a, b) -> {
                    a.addAll(b);
                    return a;
                }));
    }
}
