package xyz.apolicky.constraints;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    public static boolean NULL_MATCHES_ALL_PATTERNS;
    public static boolean COMPUTE_VALUE_DISTRIBUTION;
    public static int MIN_OCCURRENCES_FOR_DISTRIBUTION;
    public static double ROW_COMPARISON_THRESHOLD;
    public static boolean AGGREGATED_COMPARISON__SHOULD_FILTER_ALL_UNKNOWN_RESULTS;
    public static String DATE_TIME_FORMAT;

    @Value("${ice.minCountOfOccurrencesForDistribution}")
    public void setMinOccurrencesInDistribution(final int minOccurrencesInDistribution) {
        MIN_OCCURRENCES_FOR_DISTRIBUTION = minOccurrencesInDistribution;
    }

    @Value("${ice.nullMatchesAllPatterns}")
    public void setNullMatchesAllPatterns(final boolean nullMatchesAllPatterns) {
        NULL_MATCHES_ALL_PATTERNS = nullMatchesAllPatterns;
    }

    @Value("${ice.computeValueDistribution}")
    public void setComputeValueDistribution(final boolean computeValueDistribution) {
        COMPUTE_VALUE_DISTRIBUTION = computeValueDistribution;
    }

    @Value("${ice.rowComparisonThreshold}")
    public void setRowComparisonThreshold(final double rowComparisonThreshold) {
        ROW_COMPARISON_THRESHOLD = rowComparisonThreshold;
    }

    @Value("${ice.aggregatedComparison_shouldFilterAllUnknownResults}")
    public void setAggregatedComparison_shouldFilterAllUnknownResults(final boolean aggregatedComparison_shouldFilterAllUnknownResults) {
        AGGREGATED_COMPARISON__SHOULD_FILTER_ALL_UNKNOWN_RESULTS = aggregatedComparison_shouldFilterAllUnknownResults;
    }

    @Value("${ice.dateTimeFormat}")
    public void setDateTimeFormat(final String dateTimeFormat) {
        DATE_TIME_FORMAT = dateTimeFormat;
    }
}