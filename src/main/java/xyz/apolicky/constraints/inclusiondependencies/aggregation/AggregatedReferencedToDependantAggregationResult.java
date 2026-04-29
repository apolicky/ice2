package xyz.apolicky.constraints.inclusiondependencies.aggregation;

import scala.Tuple2;
import xyz.apolicky.constraints.AppConfiguration;
import xyz.apolicky.constraints.inclusiondependencies.model.ColumnAndDatasetPair;
import xyz.apolicky.constraints.model.WithColumnAndDatasetPair;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public record AggregatedReferencedToDependantAggregationResult(
        ColumnAndDatasetPair columnAndDatasetPair,
        int numberOfPairings,
        Map<RefToAggrValueResult, Integer> valueComparedToMinOfAllTheDependantRows,
        Map<RefToAggrValueResult, Integer> valueComparedToMaxOfAllTheDependantRows,
        Map<RefToAggrValueResult, Integer> valueComparedToSumOfAllTheDependantRows,
        Map<RefToAggrValueResult, Integer> valueComparedToAvgOfAllTheDependantRows
) implements Serializable, WithColumnAndDatasetPair {

    public static AggregatedReferencedToDependantAggregationResult mergeForResult(Tuple2<ColumnAndDatasetPair, Iterable<ReferencedValueComparedToAggregatedValues>> columnAndDatasetPairIterableTuple2) {
        Map<RefToAggrValueResult, Integer> min = new HashMap<>();
        Map<RefToAggrValueResult, Integer> max = new HashMap<>();
        Map<RefToAggrValueResult, Integer> sum = new HashMap<>();
        Map<RefToAggrValueResult, Integer> avg = new HashMap<>();
        AtomicInteger numberOfPairings = new AtomicInteger();

        StreamSupport.stream(columnAndDatasetPairIterableTuple2._2().spliterator(), false).forEach(v -> {
            addToMap(min, v.valueComparedToMin());
            addToMap(max, v.valueComparedToMax());
            addToMap(sum, v.valueComparedToSum());
            addToMap(avg, v.valueComparedToAvg());
            numberOfPairings.getAndIncrement();
        });

        return new AggregatedReferencedToDependantAggregationResult(
                columnAndDatasetPairIterableTuple2._1(),
                numberOfPairings.get(),
                min,
                max,
                sum,
                avg
        );
    }

    private static void addToMap(Map<RefToAggrValueResult, Integer> map, RefToAggrValueResult value) {
        if (map.computeIfPresent(value, (key, oldValue) -> oldValue + 1) == null) {
            map.put(value, 1);
        }
    }

    public boolean shouldBeFilteredOut() {
        return !(AppConfiguration.AGGREGATED_COMPARISON__SHOULD_FILTER_ALL_UNKNOWN_RESULTS &&
                valueComparedToMinOfAllTheDependantRows.size() == 1 && valueComparedToMinOfAllTheDependantRows.containsKey(RefToAggrValueResult.UNKNOWN) &&
                valueComparedToMaxOfAllTheDependantRows.size() == 1 && valueComparedToMaxOfAllTheDependantRows.containsKey(RefToAggrValueResult.UNKNOWN) &&
                valueComparedToSumOfAllTheDependantRows.size() == 1 && valueComparedToSumOfAllTheDependantRows.containsKey(RefToAggrValueResult.UNKNOWN) &&
                valueComparedToAvgOfAllTheDependantRows.size() == 1 && valueComparedToAvgOfAllTheDependantRows.containsKey(RefToAggrValueResult.UNKNOWN));
    }
}
