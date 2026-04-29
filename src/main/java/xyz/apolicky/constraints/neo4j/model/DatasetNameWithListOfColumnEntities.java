package xyz.apolicky.constraints.neo4j.model;

import xyz.apolicky.constraints.neo4j.domain.ColumnEntity;

import java.io.Serializable;
import java.util.List;

public record DatasetNameWithListOfColumnEntities(
        String datasetName,
        List<ColumnEntity> columnEntities
) implements Serializable {
}
