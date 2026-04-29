package xyz.apolicky.constraints.columncomparisons;

import xyz.apolicky.constraints.AppConfiguration;
import xyz.apolicky.constraints.columncomparisons.comparison.NewTwoColComparisonIfce;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonGroup;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonOperation;
import xyz.apolicky.constraints.columncomparisons.comparison.arithmetic.ArithmeticComparisonGroup;
import xyz.apolicky.constraints.columncomparisons.comparison.stringlength.StringLengthComparisonGroup;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.utils.SetUtils;

import java.util.*;

public class ComparisonUtils {

    public static final List<ComparableInfoTypes> comparableInfoTypes = List.of(
            new ComparableInfoTypes(InfoType.BOOLEAN, InfoType.BOOLEAN),
            new ComparableInfoTypes(InfoType.BOOLEAN, InfoType.NUMBER_0_1),
            new ComparableInfoTypes(InfoType.BOOLEAN, InfoType.NUMBER),
            new ComparableInfoTypes(InfoType.NUMBER_0_1, InfoType.NUMBER_0_1),
            new ComparableInfoTypes(InfoType.NUMBER_0_1, InfoType.NUMBER),
            new ComparableInfoTypes(InfoType.NUMBER, InfoType.NUMBER),
            new ComparableInfoTypes(InfoType.NUMBER, InfoType.STRING),
            new ComparableInfoTypes(InfoType.STRING, InfoType.STRING)
    );
    // TODO: this is a place for improvement
    // comparison groups could be registered on load or by configuration
    private static final Collection<TwoColumnComparisonGroup> comparisonGroups = List.of(
            new StringLengthComparisonGroup(),
            new ArithmeticComparisonGroup()
    );

    /**
     * Comparisons holding true for both datasets are intersected
     * Some comparisons can be combined, we'll combine those
     */
    public static Set<RowWiseCellComparison> getAggregatedComparisonEnumForm(Set<RowWiseCellComparison> leftRowWiseCellComparisons, Set<RowWiseCellComparison> rightRowWiseCellComparisons) {

        for (var comparisonGroup : comparisonGroups) {
            comparisonGroup.validateColumnResultsFulfillAggregationRulesEnumForm(leftRowWiseCellComparisons);
            comparisonGroup.validateColumnResultsFulfillAggregationRulesEnumForm(rightRowWiseCellComparisons);
        }

        var resultingSet = new HashSet<RowWiseCellComparison>();

        for (var comparisonGroup : comparisonGroups) {
            resultingSet.addAll(comparisonGroup.combineComparisonResultsEnumForm(leftRowWiseCellComparisons, rightRowWiseCellComparisons));
        }

        resultingSet.addAll(SetUtils.intersection(leftRowWiseCellComparisons, rightRowWiseCellComparisons));
        return resultingSet;
    }

    public static Set<String> getAggregatedComparisonStringForm(Set<String> leftRowWiseCellComparisons, Set<String> rightRowWiseCellComparisons) {
        for (var comparisonGroup : comparisonGroups) {
            comparisonGroup.validateColumnResultsFulfillAggregationRulesStringForm(leftRowWiseCellComparisons);
            comparisonGroup.validateColumnResultsFulfillAggregationRulesStringForm(rightRowWiseCellComparisons);
        }

        var resultingSet = new HashSet<String>();

        for (var comparisonGroup : comparisonGroups) {
            resultingSet.addAll(comparisonGroup.combineComparisonResultsStringForm(leftRowWiseCellComparisons, rightRowWiseCellComparisons));
        }

        resultingSet.addAll(SetUtils.intersection(leftRowWiseCellComparisons, rightRowWiseCellComparisons));
        return resultingSet;
    }

    public static Map<String, Integer> getAggregatedComparisonStringForm(Map<String, Integer> leftRowWiseCellComparisons, Map<String, Integer> rightRowWiseCellComparisons) {
        var resultingMap = new HashMap<>(leftRowWiseCellComparisons);
        addComparisonResult(resultingMap, rightRowWiseCellComparisons);
        return resultingMap;
    }

    public static Set<RowWiseCellComparison> applyAllComparisonsEnum(Object l, Object r, List<TwoColumnComparisonOperation> comparisonsToPerform) {
        var comparisonResults = new HashSet<RowWiseCellComparison>();
        for (var comparison : comparisonsToPerform) {
            var comparisonResult = comparison.compare(l, r);
            if (comparisonResult != null) {
                comparisonResults.add(comparisonResult);
            }
        }
        return comparisonResults;
    }

    public static Map<String, Integer> applyAllComparisonsString(Object l, Object r, List<NewTwoColComparisonIfce> comparisonsToPerform) {
        var comparisonResults = new HashMap<String, Integer>();
        for (var comparison : comparisonsToPerform) {
            if (comparison.holdsTrue(l, r)) {
                addComparisonResult(comparisonResults, comparison.getId());
            }
        }
        return comparisonResults;
    }

    public static boolean filterRowComparisonResultsAboveThreshold(long totalComparisonCount, int comparisonsHoldingTrue) {
        var b = totalComparisonCount * AppConfiguration.ROW_COMPARISON_THRESHOLD;
        return comparisonsHoldingTrue > b;
    }

    private static void addComparisonResult(Map<String, Integer> comparisonResults, String comparisonId) {
        var tryIncrementResult = comparisonResults.computeIfPresent(comparisonId, (k, v) -> ++v);
        if (tryIncrementResult == null) {
            comparisonResults.put(comparisonId, 1);
        }
    }

    private static void addComparisonResult(Map<String, Integer> comparisonResults, Map<String, Integer> otherComparisonResults) {
        for (var comparison : otherComparisonResults.entrySet()) {
            var trySumResult = comparisonResults.computeIfPresent(comparison.getKey(), (k, v) -> v + comparison.getValue());
            if (trySumResult == null) {
                comparisonResults.put(comparison.getKey(), comparison.getValue());
            }
        }
    }
}
