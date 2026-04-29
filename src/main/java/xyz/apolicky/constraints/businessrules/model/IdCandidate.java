package xyz.apolicky.constraints.businessrules.model;

import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.neo4j.domain.ColumnEntity;

public record IdCandidate(
        String columnName,
        String datasetName,
        InfoType dataType
) {
    public static IdCandidate from(ColumnEntity column) {
        return new IdCandidate(
                column.getName(),
                column.getFromTable().getName(),
                column.getInfoType()
        );
    }
}
