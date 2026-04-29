package xyz.apolicky.constraints.columncomparisons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columncomparisons.comparison.NewTwoColComparisonIfce;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonOperation;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class ColumnComparisonRegistry {
    private static final Logger logger = Logger.getLogger(ColumnComparisonRegistry.class.getName());
    private final Map<InfoTypePair, List<TwoColumnComparisonOperation>> availableComparisonsEnumFormatForInfoType;
    private final Map<InfoTypePair, List<NewTwoColComparisonIfce>> availableComparisonsStringFormatForInfoType;

    @Autowired
    public ColumnComparisonRegistry(List<TwoColumnComparisonOperation> discoveredComparisons, List<NewTwoColComparisonIfce> discoveredComparisons2) {
        logger.info("ColumnComparisonRegistry initialized with comparisons: ");
        discoveredComparisons.forEach(comparison ->
                logger.info("  - " + comparison.getId() + ": " + comparison.getName()));
        this.availableComparisonsEnumFormatForInfoType = populateAvailableComparisonsPerInfoType(discoveredComparisons);
        this.availableComparisonsStringFormatForInfoType = populateAvailableComparisonsPerInfoType(discoveredComparisons2);
    }

    private static <T extends TwoColumnComparisonOperation> Map<InfoTypePair, List<T>> populateAvailableComparisonsPerInfoType(List<T> discoveredComparisons) {
        Map<InfoTypePair, List<T>> results = new ConcurrentHashMap<>();
        for (var leftInfoType : InfoType.values()) {
            for (var rightInfoType : InfoType.values()) {
                var listOfSupportedComparisons = new ArrayList<T>();
                for (var discoveredComparison : discoveredComparisons) {
                    if (discoveredComparison.supports(leftInfoType, rightInfoType)) {
                        listOfSupportedComparisons.add(discoveredComparison);
                    }
                }
                results.put(new InfoTypePair(leftInfoType, rightInfoType), listOfSupportedComparisons);
                logger.fine("  - " + leftInfoType + " and " + rightInfoType + " are supported for " + listOfSupportedComparisons.size() + " comparisons.");
            }
        }
        return results;
    }

    public List<TwoColumnComparisonOperation> getAvailableComparisonsEnumFormatForInfoType(InfoType leftInfoType, InfoType rightInfoType) {
        return availableComparisonsEnumFormatForInfoType.get(new InfoTypePair(leftInfoType, rightInfoType));
    }

    public List<NewTwoColComparisonIfce> getAvailableComparisonsStringFormatForInfoType(InfoType leftInfoType, InfoType rightInfoType) {
        return availableComparisonsStringFormatForInfoType.get(new InfoTypePair(leftInfoType, rightInfoType));
    }

    public Map<InfoTypePair, List<TwoColumnComparisonOperation>> getAllAvailableComparisonsEnumFormatForInfoType() {
        return availableComparisonsEnumFormatForInfoType;
    }

    public Map<InfoTypePair, List<NewTwoColComparisonIfce>> getAllAvailableComparisonsStringFormatForInfoType() {
        return availableComparisonsStringFormatForInfoType;
    }
}
