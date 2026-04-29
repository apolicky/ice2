package xyz.apolicky.constraints.inclusiondependencies.aggregation;

import xyz.apolicky.constraints.columnmetadata.helpers.InfoHelpers;

import java.io.Serializable;

public record ReferencedValueComparedToAggregatedValues(
        RefToAggrValueResult valueComparedToMin,
        RefToAggrValueResult valueComparedToMax,
        RefToAggrValueResult valueComparedToSum,
        RefToAggrValueResult valueComparedToAvg,
        int countOfValuesThatTheGroupHad
) implements Serializable {
    public static ReferencedValueComparedToAggregatedValues from(Object value, AggregatedGroupResult aggregatedResultOfDependantDatasetColumn) {
        var valueType = InfoHelpers.matchingType(value);

        if (!valueType.equals(aggregatedResultOfDependantDatasetColumn.infoType())) {
            return new ReferencedValueComparedToAggregatedValues(
                    RefToAggrValueResult.UNKNOWN,
                    RefToAggrValueResult.UNKNOWN,
                    RefToAggrValueResult.UNKNOWN,
                    RefToAggrValueResult.UNKNOWN,
                    aggregatedResultOfDependantDatasetColumn.count()
            );
        }

        return new ReferencedValueComparedToAggregatedValues(
                compare(value, aggregatedResultOfDependantDatasetColumn.min()),
                compare(value, aggregatedResultOfDependantDatasetColumn.max()),
                compare(value, aggregatedResultOfDependantDatasetColumn.sum()),
                compare(value, aggregatedResultOfDependantDatasetColumn.average()),
                aggregatedResultOfDependantDatasetColumn.count()
        );
    }

    private static RefToAggrValueResult compare(Object referencedValue, Object aggregatedValue) {
        if (referencedValue.equals(aggregatedValue)) {
            return RefToAggrValueResult.EQUAL;
        }
        if (!(referencedValue instanceof Number && aggregatedValue instanceof Number)) {
            return RefToAggrValueResult.UNKNOWN;
        }
        // only numbers here
        var refNum = ((Number) referencedValue).doubleValue();
        var aggregatedNum = ((Number) aggregatedValue).doubleValue();

        if (refNum < aggregatedNum) {
            return RefToAggrValueResult.LESS_THAN;
        } else if (refNum > aggregatedNum) {
            return RefToAggrValueResult.GREATER_THAN;
        } else {
            return RefToAggrValueResult.EQUAL;
        }
    }
}
