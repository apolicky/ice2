package xyz.apolicky.constraints.inclusiondependencies.model;

import java.io.Serializable;
import java.util.List;

/**
 * For a given pair of datasets provides list of Column Pairs having the same type
 *
 * @param referencedDatasetName
 * @param dependantDatasetName
 * @param columnPairs
 */
public record InclusionDependentDatasetColumnPairsWithSameType(
        String referencedDatasetName,
        String dependantDatasetName,
        List<ColumnPairWithSharedInfoType> columnPairs
) implements Serializable {
    @Override
    public String toString() {
        return "InclusionDependentDatasetColumnPairsWithSameType[refDataset:" + referencedDatasetName +
                ",depDataset:" + dependantDatasetName +
                ",columnPairs:" + String.join(",", columnPairs.toString()) + "]";
    }
}