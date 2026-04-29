package xyz.apolicky.constraints.columnmetadata.helpers;

import xyz.apolicky.constraints.AppConfiguration;
import xyz.apolicky.constraints.columnmetadata.pattern.BasePattern;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PatternHelpers {

    public static final boolean NullMatchesAllPatters = AppConfiguration.NULL_MATCHES_ALL_PATTERNS;

    public static Set<String> findMatchingPatterns(Object value, List<BasePattern> basePatterns) {
        if (NullMatchesAllPatters && value == null) {
            return basePatterns.stream().map(BasePattern::getPatternName).collect(Collectors.toSet());
        }
        return basePatterns.stream().filter(pattern -> pattern.matchesPattern(value)).map(BasePattern::getPatternName).collect(Collectors.toSet());
    }
}
