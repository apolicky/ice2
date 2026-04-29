package xyz.apolicky.constraints.inclusiondependencies.comparisons;

import xyz.apolicky.constraints.inclusiondependencies.model.CustomRow;

import java.io.Serializable;

public record GroupComparisonRddKey(
        CustomRow referencedRow,
        String dependantDatasetName,
        String dependantColumnName
) implements Serializable {
}
