package xyz.apolicky.constraints.model;

import xyz.apolicky.constraints.inclusiondependencies.model.ColumnAndDatasetPair;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static xyz.apolicky.constraints.model.ColumnFromTable.tablesAndColumnsMap;

public interface WithColumnAndDatasetPair {
    static Map<String, Set<String>> getColumnNamesAndTables(List<? extends WithColumnAndDatasetPair> values) {
        return tablesAndColumnsMap(values.stream()
                .map(WithColumnAndDatasetPair::columnAndDatasetPair)
                .flatMap(colDatasetPair -> Stream.of(
                        new ColumnFromTable(colDatasetPair.referencedColumnName(), colDatasetPair.referencedTableName()),
                        new ColumnFromTable(colDatasetPair.dependantColumnName(), colDatasetPair.dependantTableName())
                ))
                .distinct());
    }

    ColumnAndDatasetPair columnAndDatasetPair();
}
