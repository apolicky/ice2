package xyz.apolicky.constraints.columncomparisons.comparison;

import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;

import java.util.Collection;
import java.util.Set;

public interface TwoColumnComparisonGroup {
    void validateColumnResultsFulfillAggregationRulesEnumForm(Set<RowWiseCellComparison> rowWiseCellComparisons) throws IllegalArgumentException;

    void validateColumnResultsFulfillAggregationRulesStringForm(Set<String> rowWiseCellComparisons) throws IllegalArgumentException;

    /**
     * Performs combination/aggregation of results.
     * From fields that have distinct values creates a new value: (<), (<=) --> (<=)
     */
    Collection<RowWiseCellComparison> combineComparisonResultsEnumForm(Set<RowWiseCellComparison> leftRowWiseCellComparisons, Set<RowWiseCellComparison> rightRowWiseCellComparisons);

    Collection<String> combineComparisonResultsStringForm(Set<String> leftRowWiseCellComparisons, Set<String> rightRowWiseCellComparisons);
}
