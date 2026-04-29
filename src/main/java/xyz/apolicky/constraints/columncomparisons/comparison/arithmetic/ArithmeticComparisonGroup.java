package xyz.apolicky.constraints.columncomparisons.comparison.arithmetic;

import org.jetbrains.annotations.NotNull;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonGroup;
import xyz.apolicky.constraints.utils.SetUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ArithmeticComparisonGroup implements TwoColumnComparisonGroup {
    private static final Set<@NotNull RowWiseCellComparison> exclusionSetOfValuesEnumForm = Set.of(
            RowWiseCellComparison.EQUAL,
            RowWiseCellComparison.LESS_THAN,
            RowWiseCellComparison.LESS_THAN_OR_EQUAL,
            RowWiseCellComparison.GREATER_THAN,
            RowWiseCellComparison.GREATER_THAN_OR_EQUAL
    );

    private static final Set<@NotNull String> exclusionSetOfValuesStringForm = Set.of(
            ArithmeticEqual.id,
            ArithmeticGreaterThan.id,
            ArithmeticLessThan.id,
            ArithmeticLessThanOrEqual.id,
            ArithmeticGreaterThanOrEqual.id
    );

    @Override
    public void validateColumnResultsFulfillAggregationRulesEnumForm(Set<RowWiseCellComparison> rowWiseCellComparisons) throws IllegalArgumentException {
        var setIntersection = SetUtils.intersection(rowWiseCellComparisons, exclusionSetOfValuesEnumForm);
        if (setIntersection.size() > 1) {
            throw new IllegalArgumentException("Set `resultsOfOneColumn` should contain max one element from given combination");
        }
    }

    @Override
    public void validateColumnResultsFulfillAggregationRulesStringForm(Set<String> rowWiseCellComparisons) throws IllegalArgumentException {
        var setIntersection = SetUtils.intersection(rowWiseCellComparisons, exclusionSetOfValuesStringForm);
        if (setIntersection.size() > 1) {
            throw new IllegalArgumentException("Set `resultsOfOneColumn` should contain max one element from given combination");
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
        if ((leftRowWiseCellComparisons.contains(RowWiseCellComparison.EQUAL) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LESS_THAN)) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.EQUAL) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LESS_THAN_OR_EQUAL) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LESS_THAN) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LESS_THAN_OR_EQUAL) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LESS_THAN) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.EQUAL) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LESS_THAN_OR_EQUAL) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.LESS_THAN) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.LESS_THAN_OR_EQUAL) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.EQUAL)
        ) {
            combinedResults.add(RowWiseCellComparison.LESS_THAN_OR_EQUAL);
        }
        // [==, >]
        // [==, >=]
        // [>, >=]
        // [>, ==]
        // [>=,>]
        // [>=, ==]
        // --> >=
        else if ((leftRowWiseCellComparisons.contains(RowWiseCellComparison.EQUAL) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.GREATER_THAN)) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.EQUAL) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.GREATER_THAN_OR_EQUAL) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.GREATER_THAN) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.GREATER_THAN_OR_EQUAL) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.GREATER_THAN) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.EQUAL) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.GREATER_THAN_OR_EQUAL) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.GREATER_THAN) ||
                leftRowWiseCellComparisons.contains(RowWiseCellComparison.GREATER_THAN_OR_EQUAL) && rightRowWiseCellComparisons.contains(RowWiseCellComparison.EQUAL)) {
            combinedResults.add(RowWiseCellComparison.GREATER_THAN_OR_EQUAL);
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
        if ((leftRowWiseCellComparisons.contains(ArithmeticEqual.id) && rightRowWiseCellComparisons.contains(ArithmeticLessThan.id)) ||
                leftRowWiseCellComparisons.contains(ArithmeticEqual.id) && rightRowWiseCellComparisons.contains(ArithmeticLessThanOrEqual.id) ||
                leftRowWiseCellComparisons.contains(ArithmeticLessThan.id) && rightRowWiseCellComparisons.contains(ArithmeticLessThanOrEqual.id) ||
                leftRowWiseCellComparisons.contains(ArithmeticLessThan.id) && rightRowWiseCellComparisons.contains(ArithmeticEqual.id) ||
                leftRowWiseCellComparisons.contains(ArithmeticLessThanOrEqual.id) && rightRowWiseCellComparisons.contains(ArithmeticLessThan.id) ||
                leftRowWiseCellComparisons.contains(ArithmeticLessThanOrEqual.id) && rightRowWiseCellComparisons.contains(ArithmeticEqual.id)
        ) {
            combinedResults.add(ArithmeticLessThanOrEqual.id);
        }
        // [==, >]
        // [==, >=]
        // [>, >=]
        // [>, ==]
        // [>=,>]
        // [>=, ==]
        // --> >=
        else if ((leftRowWiseCellComparisons.contains(ArithmeticEqual.id) && rightRowWiseCellComparisons.contains(ArithmeticGreaterThan.id)) ||
                leftRowWiseCellComparisons.contains(ArithmeticEqual.id) && rightRowWiseCellComparisons.contains(ArithmeticGreaterThanOrEqual.id) ||
                leftRowWiseCellComparisons.contains(ArithmeticGreaterThan.id) && rightRowWiseCellComparisons.contains(ArithmeticGreaterThanOrEqual.id) ||
                leftRowWiseCellComparisons.contains(ArithmeticGreaterThan.id) && rightRowWiseCellComparisons.contains(ArithmeticEqual.id) ||
                leftRowWiseCellComparisons.contains(ArithmeticGreaterThanOrEqual.id) && rightRowWiseCellComparisons.contains(ArithmeticGreaterThan.id) ||
                leftRowWiseCellComparisons.contains(ArithmeticGreaterThanOrEqual.id) && rightRowWiseCellComparisons.contains(ArithmeticEqual.id)) {
            combinedResults.add(ArithmeticGreaterThanOrEqual.id);
        }
        return combinedResults;
    }
}
