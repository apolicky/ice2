package xyz.apolicky.constraints.utils;

public class TypeComparisonUtils {

    /**
     * Useful when you have a pair of values you want to see
     * whether any rotation of (targetValue1, targetValue2)[unordered] matches (first,second)[ordered]
     *
     * @param <T> type with equals method defined
     */
    public static <T> boolean anyRotationHolds(T targetValue1, T targetValue2, T first, T second) {
        return (first.equals(targetValue1) && second.equals(targetValue2)) || (first.equals(targetValue2) && second.equals(targetValue1));
    }
}
