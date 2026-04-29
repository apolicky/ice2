package xyz.apolicky.constraints.neo4j.model;

import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.io.Serializable;

public record ColumnPairWithInfoTypes(
        String leftColumn,
        String rightColumn,
        InfoType leftColumnInfoType,
        InfoType rightColumnInfoType) implements Serializable {
}
