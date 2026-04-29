package xyz.apolicky.constraints.inclusiondependencies.model;

import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.io.Serializable;

/**
 * Columns can be from distinct datasets
 *
 * @param referencedDatasetColumn
 * @param dependantDatasetColumn
 * @param referencedInfoType
 * @param dependantInfoType
 */
public record ColumnPairWithTheirInfoTypes(
        String referencedDatasetColumn,
        String dependantDatasetColumn,
        InfoType referencedInfoType,
        InfoType dependantInfoType) implements Serializable {
}
