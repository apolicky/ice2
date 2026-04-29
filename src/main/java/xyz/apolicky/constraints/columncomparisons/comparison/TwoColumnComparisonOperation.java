package xyz.apolicky.constraints.columncomparisons.comparison;

import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import javax.annotation.Nullable;
import java.io.Serializable;

public interface TwoColumnComparisonOperation extends Serializable {

    /**
     * A unique identifier for this comparison.
     */
    String getId();

    /**
     * A descriptive name for this comparison.
     */
    String getName();

    /**
     * Determines if this comparison is applicable to the given InfoType pair.
     * This is crucial for filtering which comparisons to apply.
     */
    boolean supports(InfoType left, InfoType right);

    @Nullable
    RowWiseCellComparison compare(Object left, Object right);
}
