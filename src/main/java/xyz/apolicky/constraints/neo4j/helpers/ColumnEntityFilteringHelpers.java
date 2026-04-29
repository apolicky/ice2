package xyz.apolicky.constraints.neo4j.helpers;

import xyz.apolicky.constraints.inclusiondependencies.model.ColumnAndDatasetPair;
import xyz.apolicky.constraints.neo4j.domain.ColumnEntity;
import xyz.apolicky.constraints.neo4j.model.IndColumnEntityPair;

import java.util.List;

public class ColumnEntityFilteringHelpers {

    public static IndColumnEntityPair findReferencedAndDependantColumnEntity(ColumnAndDatasetPair columnAndDatasetPair, List<ColumnEntity> columnEntities) {
        var referencedColumn = columnEntities.stream().filter(c -> c.getName().contentEquals(columnAndDatasetPair.referencedColumnName()) &&
                c.getFromTable().getName().contentEquals(columnAndDatasetPair.referencedTableName())).findFirst().orElse(null);
        var dependantColumn = columnEntities.stream().filter(c -> c.getName().contentEquals(columnAndDatasetPair.dependantColumnName()) &&
                c.getFromTable().getName().contentEquals(columnAndDatasetPair.dependantTableName())).findFirst().orElse(null);
        assert referencedColumn != null;
        assert dependantColumn != null;
        return new IndColumnEntityPair(referencedColumn, dependantColumn);
    }
}
