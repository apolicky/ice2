package xyz.apolicky.constraints.columnmetadata.pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columnmetadata.helpers.PatternHelpers;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PatternRegistry {
    private static final Logger log = LoggerFactory.getLogger(PatternRegistry.class);
    private final Map<InfoType, List<BasePattern>> availablePatterns;
    private final Set<BasePattern> basePatterns;

    @Autowired
    public PatternRegistry(Set<BasePattern> discoveredPatterns) {
        log.info("ColumnComparisonRegistry initialized with comparisons: ");
        discoveredPatterns.forEach(pattern ->
                log.info("  - {}", pattern.getPatternName()));
        this.availablePatterns = populateAvailablePatternsPerInfoType(discoveredPatterns);
        this.basePatterns = discoveredPatterns;
    }

    private static Map<InfoType, List<BasePattern>> populateAvailablePatternsPerInfoType(Set<BasePattern> discoveredPatterns) {
        Map<InfoType, List<BasePattern>> results = new ConcurrentHashMap<>();
        for (var infoType : InfoType.values()) {
            var listOfSupportedComparisons = new ArrayList<BasePattern>();
            for (var discoveredPattern : discoveredPatterns) {
                if (discoveredPattern.supports(infoType)) {
                    listOfSupportedComparisons.add(discoveredPattern);
                }
            }
            results.put(infoType, listOfSupportedComparisons);
            log.debug("  - {} is supported for {} comparisons", infoType, listOfSupportedComparisons.size());
        }
        // override Info.NULL patterns, if null match enabled
        if (PatternHelpers.NullMatchesAllPatters) {
            results.put(InfoType.NULL, discoveredPatterns.stream().toList());
        }
        return results;
    }

    public Map<InfoType, List<BasePattern>> getMatchingPattersForGivenTypes() {
        return availablePatterns;
    }

    public Set<BasePattern> getBasePatterns() {
        return basePatterns;
    }
}
