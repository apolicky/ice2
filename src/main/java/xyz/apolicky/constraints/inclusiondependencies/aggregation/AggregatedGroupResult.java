package xyz.apolicky.constraints.inclusiondependencies.aggregation;

import xyz.apolicky.constraints.columnmetadata.helpers.InfoHelpers;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.io.Serializable;

public record AggregatedGroupResult(
        String columnName,
        Object min,
        Object max,
        Object average,
        Object sum,
        int count,
        InfoType infoType
) implements Serializable {

    /**
     * Creates an AggregatedGroupResult, intermediate value that's used for further reduce calls
     */
    public static AggregatedGroupResult fromValue(Object value, String columnName, InfoType matchingType) {
        var average = InfoHelpers.average(matchingType, value);
        var sum = InfoHelpers.sum(matchingType, value);

        return new AggregatedGroupResult(
                columnName,
                value,
                value,
                average,
                sum,
                1,
                matchingType
        );
    }

    public static AggregatedGroupResult combineByKeyAndValue(AggregatedGroupResult a, AggregatedGroupResult b) {
        validateSameValue(a.columnName, b.columnName);
        validateSameValue(a.infoType, b.infoType);

        var min = InfoHelpers.min(a.min, b.min);
        var max = InfoHelpers.max(a.max, b.max);
        var average = getAverage(a, b);
        var sum = InfoHelpers.sum(a.infoType, a.sum, b.sum);

        return new AggregatedGroupResult(
                a.columnName,
                min,
                max,
                average,
                sum,
                a.count + b.count,
                a.infoType);
    }

    private static void validateSameValue(Object value1, Object value2) {
        if (value1.equals(value2)) {
            return;
        }
        throw new IllegalArgumentException(String.format("Value '%s' is not same as value '%s'", value1, value2));
    }

    private static Object getAverage(AggregatedGroupResult a, AggregatedGroupResult b) {
        if (!a.infoType.equals(InfoType.NUMBER) || !b.infoType.equals(InfoType.NUMBER)) {
            return null;
        }

        var aAvg = a.average();
        var bAvg = b.average();

        if (aAvg == null && bAvg == null) {
            return null;
        }

        if (aAvg == null && bAvg instanceof Number) {
            return bAvg;
        }

        if (aAvg instanceof Number && bAvg == null) {
            return aAvg;
        }

        if (aAvg instanceof Number && bAvg instanceof Number) {
            return (((Number) aAvg).doubleValue() * a.count + ((Number) bAvg).doubleValue() * b.count) / (a.count + b.count);
        }

        throw new IllegalArgumentException("Average is `" + aAvg + "` and `" + bAvg + "` which are not allowed candidates for average value computation");
    }
}
