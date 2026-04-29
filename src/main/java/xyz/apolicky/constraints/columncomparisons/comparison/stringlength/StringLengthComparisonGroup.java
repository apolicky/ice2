package xyz.apolicky.constraints.columncomparisons.comparison.stringlength;

import org.jetbrains.annotations.NotNull;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonGroup;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.utils.SetUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StringLengthComparisonGroup implements TwoColumnComparisonGroup {

    private static final Set<@NotNull RowWiseCellComparison> exclusionSetOfValues = Set.of(
            RowWiseCellComparison.LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER,
            RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_THE_OTHER,
            RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_OR_EQUAL_THE_OTHER,
            RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_THE_OTHER,
            RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_OR_EQUAL_THE_OTHER
    );

    private static final Set<@NotNull String> exclusionSetOfValuesStringForm = Set.of(
            StringLengthEqual.id,
            StringLengthLessThan.id,
            StringLengthLessThanOrEqual.id,
            StringLengthGreaterThan.id,
            StringLengthGreaterThanOrEqual.id
    );

    public static boolean isSupportedForStringLengthComparison(InfoType left, InfoType right) {
        return (left == InfoType.STRING && right == InfoType.STRING) ||
                (left.equals(InfoType.STRING) && right.equals(InfoType.NUMBER_0_1)) ||
                (left.equals(InfoType.STRING) && right.equals(InfoType.NUMBER)) ||
                (left.equals(InfoType.NUMBER) && right.equals(InfoType.NUMBER_0_1)) ||
                (left.equals(InfoType.NUMBER) && right.equals(InfoType.NUMBER)) ||
                (left.equals(InfoType.NUMBER) && right.equals(InfoType.STRING)) ||
                (left.equals(InfoType.NUMBER_0_1) && right.equals(InfoType.NUMBER_0_1)) ||
                (left.equals(InfoType.NUMBER_0_1) && right.equals(InfoType.NUMBER)) ||
                (left.equals(InfoType.NUMBER_0_1) && right.equals(InfoType.STRING));
    }

    @Override
    public void validateColumnResultsFulfillAggregationRulesEnumForm(Set<RowWiseCellComparison> rowWiseCellComparisons) throws IllegalArgumentException {
        var setIntersection = SetUtils.intersection(rowWiseCellComparisons, exclusionSetOfValues);
        if (setIntersection.size() > 1) {
            throw new IllegalArgumentException("Set `rowWiseCellComparisons` should contain max one element from given combination");
        }
    }

    public void validateColumnResultsFulfillAggregationRulesStringForm(Set<String> rowWiseCellComparisons) throws IllegalArgumentException {
        var setIntersection = SetUtils.intersection(rowWiseCellComparisons, exclusionSetOfValuesStringForm);
        if (setIntersection.size() > 1) {
            throw new IllegalArgumentException("Set `rowWiseCellComparisons` should contain max one element from given combination");
        }
    }

    @Override
    public Collection<RowWiseCellComparison> combineComparisonResultsEnumForm(Set<RowWiseCellComparison> leftRowWiseCellComparisons, Set<RowWiseCellComparison> rightRowWiseCellComparisons) {
        var combinedResults = new HashSet<RowWiseCellComparison>();
        // [==, <]
        // [==, <=]
        // [<, <=]
        // [<, ==]
        // [<=, <]
        // [<=, ==]
        // --> <=
        if ((leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_THE_OTHER)) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_OR_EQUAL_THE_OTHER) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_OR_EQUAL_THE_OTHER) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_OR_EQUAL_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_THE_OTHER) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_OR_EQUAL_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER)
        ) {
            combinedResults.add(RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_OR_EQUAL_THE_OTHER);
        }
        // [==, >]
        // [==, >=]
        // [>, >=]
        // [>, ==]
        // [>=,>]
        // [>=, ==]
        // --> >=
        else if ((leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_THE_OTHER)) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_OR_EQUAL_THE_OTHER) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_OR_EQUAL_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_THE_OTHER) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_OR_EQUAL_THE_OTHER) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER)) {
            combinedResults.add(RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_OR_EQUAL_THE_OTHER);
        }
        return combinedResults;
    }

    @Override
    public Collection<String> combineComparisonResultsStringForm(Set<String> leftRowWiseCellComparisons, Set<String> rightRowWiseCellComparisons) {
        var combinedResults = new HashSet<String>();
        // [==, <]
        // [==, <=]
        // [<, <=]
        // [<, ==]
        // [<=, <]
        // [<=, ==]
        // --> <=
        if ((leftRowWiseCellComparisons.contains(StringLengthEqual.id) && rightRowWiseCellComparisons.contains(StringLengthLessThan.id)) ||
                leftRowWiseCellComparisons.contains(StringLengthEqual.id) && rightRowWiseCellComparisons.contains(StringLengthLessThanOrEqual.id) ||
                leftRowWiseCellComparisons.contains(StringLengthLessThan.id) && rightRowWiseCellComparisons.contains(StringLengthLessThanOrEqual.id) ||
                leftRowWiseCellComparisons.contains(StringLengthLessThan.id) && rightRowWiseCellComparisons.contains(StringLengthEqual.id) ||
                leftRowWiseCellComparisons.contains(StringLengthLessThanOrEqual.id) && rightRowWiseCellComparisons.contains(StringLengthLessThan.id) ||
                leftRowWiseCellComparisons.contains(StringLengthLessThanOrEqual.id) && rightRowWiseCellComparisons.contains(StringLengthEqual.id)
        ) {
            combinedResults.add(StringLengthLessThanOrEqual.id);
        }
        // [==, >]
        // [==, >=]
        // [>, >=]
        // [>, ==]
        // [>=,>]
        // [>=, ==]
        // --> >=
        else if ((leftRowWiseCellComparisons.contains(StringLengthEqual.id) && rightRowWiseCellComparisons.contains(StringLengthGreaterThan.id)) ||
                leftRowWiseCellComparisons.contains(StringLengthEqual.id) && rightRowWiseCellComparisons.contains(StringLengthGreaterThanOrEqual.id) ||
                leftRowWiseCellComparisons.contains(StringLengthGreaterThan.id) && rightRowWiseCellComparisons.contains(StringLengthEqual.id) ||
                leftRowWiseCellComparisons.contains(StringLengthGreaterThanOrEqual.id) && rightRowWiseCellComparisons.contains(StringLengthGreaterThan.id) ||
                leftRowWiseCellComparisons.contains(StringLengthGreaterThanOrEqual.id) && rightRowWiseCellComparisons.contains(StringLengthEqual.id)) {
            combinedResults.add(StringLengthGreaterThanOrEqual.id);
        }
        return combinedResults;
    }
}
