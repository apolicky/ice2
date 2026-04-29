package xyz.apolicky.constraints.columnmetadata.model;

import xyz.apolicky.constraints.columncomparisons.comparison.NewTwoColComparisonIfce;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonOperation;
import xyz.apolicky.constraints.neo4j.model.ColumnPairWithInfoTypes;

import java.io.Serializable;
import java.util.List;

public record ColumnPairWithTypeAndApplicableComparisons(
        ColumnPairWithInfoTypes columnPairWithInfoTypes,
        List<TwoColumnComparisonOperation> applicableComparisonsEnumForm,
        List<NewTwoColComparisonIfce> applicableComparisonsStringForm
) implements Serializable {
}
