package xyz.apolicky.constraints.neo4j.mappers;

import xyz.apolicky.constraints.columncomparisons.ComparableInfoTypes;
import xyz.apolicky.constraints.functionaldependency.model.FunctionalDependencyColumn;
import xyz.apolicky.constraints.model.ColumnFromTable;

import java.util.Map;

// TODO: i have to pass a map to neo4j, record isn't treated properly by neo4j
public class TodoRecordMappers {

    public static Map<String, String> toNeo4jSupportedMap(FunctionalDependencyColumn fdc) {
        return Map.of("columnName", fdc.columnName(), "tableName", fdc.datasetName());
    }

    public static Map<String, String> toNeo4jSupportedMap(ComparableInfoTypes cit) {
        return Map.of("first", cit.first().toString(), "second", cit.second().toString());
    }

    public static Map<String, String> toNeo4jSupportedMap(ColumnFromTable columnFromTable) {
        return Map.of("columnName", columnFromTable.columnName(), "tableName", columnFromTable.datasetName());
    }
}
