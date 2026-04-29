package xyz.apolicky.constraints.neo4j.model;

import xyz.apolicky.constraints.neo4j.domain.ColumnCombinationEntity;
import xyz.apolicky.constraints.neo4j.domain.ColumnEntity;

import java.util.List;

public record MyFD(
        List<ColumnEntity> columns,
        ColumnCombinationEntity columnCombination,
        ColumnEntity dependant
) {
}