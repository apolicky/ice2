package xyz.apolicky.constraints.columnmetadata.model;

import xyz.apolicky.constraints.AppConfiguration;
import xyz.apolicky.constraints.columnmetadata.helpers.InfoHelpers;
import xyz.apolicky.constraints.utils.ModelNameHelpers;
import xyz.apolicky.constraints.utils.ValueStringProvider;

import java.io.Serializable;
import java.util.*;

public record Info(String name,
                   String datasourceName,
                   Object min,
                   Object max,
                   Object average,
                   int minLength,
                   int maxLength,
                   boolean unique,
                   boolean nullable,
                   int nullCount,
                   int distinctValues,
                   Map<String, Integer> distribution,
                   int count,
                   int zeroCount,
                   int positiveCount,
                   InfoType type,
                   Map<InfoType, Integer> typeDistribution,
                   Map<String, Integer> matchingPatterns) implements Serializable, ValueStringProvider {

    private static final int minCountOfOccurrencesForDistribution = AppConfiguration.MIN_OCCURRENCES_FOR_DISTRIBUTION;

    /**
     * Both a and b are expected to have the same value, as well as being from the same dataset and column
     */
    public static Info combineByKeyValue(Info a, Info b) {
        return new Info(a.name,
                a.datasourceName,
                a.min,
                a.max,
                a.average,
                a.minLength,
                a.maxLength,
                false,
                a.nullable,
                a.nullCount + b.nullCount,
                1,
                null, // no need to have a map here just yet, only creates unnecessary object
                a.count + b.count,
                a.zeroCount + b.zeroCount,
                a.positiveCount + b.positiveCount,
                a.type,
                null,// no need to have a map here just yet, only creates unnecessary object
                InfoHelpers.combineMatchingPatterns(a, b));
    }

    /**
     * Both a and b are expected be from the same column and dataset
     */
    public static Info combineByKey(Info a, Info b) {
        var min = InfoHelpers.min(a.min, b.min);
        var max = InfoHelpers.max(a.max, b.max);
        var minL = Math.min(a.minLength, b.minLength);
        var maxL = Math.max(a.maxLength, b.maxLength);
        var average = InfoHelpers.average(a, b);
        var anyNullable = a.nullable || b.nullable;
        var patterns = InfoHelpers.combineMatchingPatterns(a, b);
        var newType = InfoHelpers.matchingType(a.type, b.type);

        var isUnique = a.unique && b.unique; // if any value wasn't unique, then the whole column doesn't have unique vals
        var distinctValues = a.distinctValues + b.distinctValues;
        var distribution = createDistributionMap(a, b);
        var typeDistribution = createTypeDistributionMap(a, b);

        return new Info(a.name,
                a.datasourceName,
                min,
                max,
                average,
                minL,
                maxL,
                isUnique,
                anyNullable,
                a.nullCount + b.nullCount,
                distinctValues,
                distribution,
                a.count + b.count,
                a.zeroCount + b.zeroCount,
                a.positiveCount + b.positiveCount,
                newType,
                typeDistribution,
                patterns);
    }


    public static Info fromValue(Object value, String columnName, String datasourceName, InfoType matchingType, Set<String> matchingPatterns) {
        var valueString = ModelNameHelpers.getValueString(value);
        var valueLength = value == null ? 0 : valueString.length();
        var zeroCount = InfoHelpers.isZeroNumber(valueLength) ? 1 : 0;
        var positiveCount = InfoHelpers.isPositiveNumber(value) ? 1 : 0;
        var patterns = InfoHelpers.convertMatchingPatternsToMapOfOccurrences(matchingPatterns);
        var average = InfoHelpers.average(matchingType, value);

        return new Info(columnName, datasourceName, value, value, average, valueLength, valueLength, true, value == null,
                value == null ? 1 : 0, 1, null, 1, zeroCount, positiveCount, matchingType, null, patterns);
    }

    private static Map<String, Integer> createDistributionMap(Info a, Info b) {
        if (!AppConfiguration.COMPUTE_VALUE_DISTRIBUTION) {
            return Map.of();
        }

        Map<String, Integer> distribution = new HashMap<>();
        for (var i : List.of(a, b)) {
            if (i.distribution == null) {
                // `i` has only a single value
                if (i.count > minCountOfOccurrencesForDistribution) {
                    distribution.put(ModelNameHelpers.getValueString(i.min), i.count);
                }
            } else {
                // `i` already has a distribution, merge it
                for (var kv : i.distribution.entrySet()) {
                    if (kv.getValue() > minCountOfOccurrencesForDistribution) {
                        distribution.put(kv.getKey(), kv.getValue());
                    }
                }
            }
        }

        return distribution;
    }

    private static Map<InfoType, Integer> createTypeDistributionMap(Info a, Info b) {
        Map<InfoType, Integer> mergedTypeDistribution = new HashMap<>();
        for (var i : List.of(a, b)) {
            Map<InfoType, Integer> elementsTypeDistribution;
            if (i.typeDistribution != null) {
                elementsTypeDistribution = i.typeDistribution;
            } else {
                // `i` has only a single value
                elementsTypeDistribution = new HashMap<>();
                elementsTypeDistribution.put(i.type, i.count);
            }

            // `i` already has a distribution, merge it
            for (var kv : elementsTypeDistribution.entrySet()) {
                if (mergedTypeDistribution.computeIfPresent(kv.getKey(), (_k, val) -> val + kv.getValue()) == null) {
                    mergedTypeDistribution.put(kv.getKey(), kv.getValue());
                }
            }
        }
        return mergedTypeDistribution;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Info that = (Info) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public String getValueString() {
        return ModelNameHelpers.getValueStringWithDatasetAndKey(min, datasourceName, name);
    }
}