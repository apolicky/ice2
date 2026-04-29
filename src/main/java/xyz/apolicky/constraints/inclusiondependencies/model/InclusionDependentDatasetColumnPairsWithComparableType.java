package xyz.apolicky.constraints.inclusiondependencies.model;

import java.io.Serializable;
import java.util.List;

/**
 * For a given pair of datasets provides list of Column Pairs (each with their own, comparable type)
 *
 * @param referencedDatasetName
 * @param dependantDatasetName
 * @param columnPairs
 */
public record InclusionDependentDatasetColumnPairsWithComparableType(
        String referencedDatasetName,
        String dependantDatasetName,
        List<ColumnPairWithTheirInfoTypes> columnPairs
) implements Serializable {
    @Override
    public String toString() {
        return "InclusionDependentDatasetColumnPairsWithComparableType[refDataset:" + referencedDatasetName +
                ",depDataset:" + dependantDatasetName +
                ",columnPairs:" + String.join(",", columnPairs.toString()) + "]";
    }
}