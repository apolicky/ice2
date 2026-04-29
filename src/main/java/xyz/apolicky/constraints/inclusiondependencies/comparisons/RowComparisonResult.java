package xyz.apolicky.constraints.inclusiondependencies.comparisons;

import xyz.apolicky.constraints.columncomparisons.ComparisonUtils;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columnmetadata.model.ColumnPairWithTypeAndApplicableComparisons;
import xyz.apolicky.constraints.model.WithColumnAndDatasetPair;
import xyz.apolicky.constraints.inclusiondependencies.model.ColumnAndDatasetPair;
import xyz.apolicky.constraints.inclusiondependencies.model.CustomRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public record RowComparisonResult(
        ColumnAndDatasetPair columnAndDatasetPair,
        long numberOfComparisons,
        Set<RowWiseCellComparison> comparisonResultEnumForm,
        Map<String, Integer> comparisonResultStringForm) implements Serializable, WithColumnAndDatasetPair {

    public static Stream<RowComparisonResult> ofSourceAndTargetRowsAsStream(CustomRow sourceRow, CustomRow targetRow, List<ColumnPairWithTypeAndApplicableComparisons> columnsToCompare) {
        List<RowComparisonResult> results = new ArrayList<>();
        var source = sourceRow.originalRow();
        var target = targetRow.originalRow();

        for (var c : columnsToCompare) {
            var columnPair = c.columnPairWithInfoTypes();
            var applicableComparisons = c.applicableComparisonsEnumForm();
            var applicableComparisons2 = c.applicableComparisonsStringForm();
            var referencedColumnName = columnPair.leftColumn();
            var dependantColumnName = columnPair.rightColumn();
            var referencedColumnIndex = source.fieldIndex(referencedColumnName);
            var dependantColumnIndex = target.fieldIndex(dependantColumnName);
            var comparisonKey = new ColumnAndDatasetPair(referencedColumnName, sourceRow.datasetName(), dependantColumnName, targetRow.datasetName());

            var sourceValue = source.get(referencedColumnIndex);
            var targetValue = target.get(dependantColumnIndex);

            var comparisonResults = ComparisonUtils.applyAllComparisonsEnum(sourceValue, targetValue, applicableComparisons);
            var comparisonResults2 = ComparisonUtils.applyAllComparisonsString(sourceValue, targetValue, applicableComparisons2);
            results.add(new RowComparisonResult(comparisonKey, 1L, comparisonResults, comparisonResults2));
        }

        return results.stream();
    }

    public static RowComparisonResult aggregateRowComparisons(RowComparisonResult a, RowComparisonResult b) {
        assert a.columnAndDatasetPair().equals(b.columnAndDatasetPair());
        var comparisonResultEnumForm = ComparisonUtils.getAggregatedComparisonEnumForm(a.comparisonResultEnumForm(), b.comparisonResultEnumForm());
        var comparisonResultStringForm = ComparisonUtils.getAggregatedComparisonStringForm(a.comparisonResultStringForm(), b.comparisonResultStringForm());
        return new RowComparisonResult(a.columnAndDatasetPair(), a.numberOfComparisons + b.numberOfComparisons, comparisonResultEnumForm, comparisonResultStringForm);
    }

    public String getKvMappingKey() {
        return columnAndDatasetPair().referencedTableName() +
                "_" + columnAndDatasetPair.dependantTableName() +
                "/" + columnAndDatasetPair.referencedColumnName() +
                "_" + columnAndDatasetPair.dependantColumnName();
    }
}
