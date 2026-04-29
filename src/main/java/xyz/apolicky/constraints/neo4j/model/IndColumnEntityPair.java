package xyz.apolicky.constraints.neo4j.model;

import xyz.apolicky.constraints.neo4j.domain.ColumnEntity;

import java.io.Serializable;

public record IndColumnEntityPair(
        ColumnEntity referencedEntity,
        ColumnEntity dependantEntity
) implements Serializable {
}
