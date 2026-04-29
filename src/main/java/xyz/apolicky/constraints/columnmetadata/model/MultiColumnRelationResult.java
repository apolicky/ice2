package xyz.apolicky.constraints.columnmetadata.model;

import xyz.apolicky.constraints.columncomparisons.ColumnPair;
import xyz.apolicky.constraints.columncomparisons.ComparisonUtils;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public record MultiColumnRelationResult(
        ColumnPair columnPair,
        Set<RowWiseCellComparison> rowWiseCellComparisonSet,
        Map<String, Integer> comparisonResultsHoldingTrue) implements Serializable {

    public static MultiColumnRelationResult aggregateRowComparisons(MultiColumnRelationResult a, MultiColumnRelationResult b) {
        assert a.columnPair().equals(b.columnPair());
        var comparisonResult = ComparisonUtils.getAggregatedComparisonEnumForm(a.rowWiseCellComparisonSet(), b.rowWiseCellComparisonSet());
        var comparisonResult2 = ComparisonUtils.getAggregatedComparisonStringForm(a.comparisonResultsHoldingTrue(), b.comparisonResultsHoldingTrue());
        return new MultiColumnRelationResult(a.columnPair(), comparisonResult, comparisonResult2);
    }

    public String getKvMappingKey() {
        return columnPair.leftColumn() + "_" + columnPair.rightColumn();
    }
}
