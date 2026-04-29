package xyz.apolicky.constraints.inclusiondependencies.model;

import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.io.Serializable;

/**
 * Columns can be from distinct datasets
 *
 * @param referencedDatasetColumn
 * @param dependantDatasetColumn
 * @param infoType
 */
public record ColumnPairWithSharedInfoType(
        String referencedDatasetColumn,
        String dependantDatasetColumn,
        InfoType infoType) implements Serializable {
}
