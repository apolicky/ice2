package xyz.apolicky.constraints.neo4j.domain;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RelationshipProperties
public class ColumnComparisonRelationshipProperties implements Serializable {
    @TargetNode
    private ColumnEntity column;
    @RelationshipId
    private String id;

    private String kvMappingKey;
    private long numberOfComparisons;

    // TODO: could be list of scanned rules/properties and the result of check
    // works like:
    // [property/rule1]: true --> present
    // [property/rule2]: false --> missing
    // [property/rule3]: false --> missing
    // [property/rule4]: true --> present
    private Set<String> comparisonIdsOverThreshold;

    // works like:
    // [property/rule-id-1: countTrue]
    // [property/rule-id-2: countTrue]
    // [property/rule-id-3: countTrue]
    // [property/rule-id-4: countTrue]
    private Set<String> comparisonResults;

    public static ColumnComparisonRelationshipProperties fromValue(ColumnEntity column, String kvMappingKey, long numberOfComparisons, Set<String> comparisonIds, Map<String, Integer> comparisonResults) {
        var properties = new ColumnComparisonRelationshipProperties();
        properties.column = column;
        properties.kvMappingKey = kvMappingKey;
        properties.numberOfComparisons = numberOfComparisons;
        properties.comparisonIdsOverThreshold = comparisonIds;
        properties.comparisonResults = createSetFromComparisonResultsMap(comparisonResults);
        return properties;
    }

    private static Set<String> createSetFromComparisonResultsMap(Map<String, Integer> map) {
        return map.entrySet().stream().map(e -> e.getKey() + "~" + e.getValue()).collect(Collectors.toSet());
    }

    public ColumnEntity getColumn() {
        return column;
    }

    public void setColumn(ColumnEntity column) {
        this.column = column;
    }

    public Set<String> getComparisonIdsOverThreshold() {
        return comparisonIdsOverThreshold;
    }

    public void setComparisonIdsOverThreshold(Set<String> comparisonIdsOverThreshold) {
        this.comparisonIdsOverThreshold = comparisonIdsOverThreshold;
    }

    public Set<String> getComparisonResults() {
        return comparisonResults;
    }

    public void setComparisonResults(Set<String> comparisonResults) {
        this.comparisonResults = comparisonResults;
    }

    public long getNumberOfComparisons() {
        return numberOfComparisons;
    }

    public void setNumberOfComparisons(long numberOfComparisons) {
        this.numberOfComparisons = numberOfComparisons;
    }

    public String getKvMappingKey() {
        return kvMappingKey;
    }

    public void setKvMappingKey(String kvMappingKey) {
        this.kvMappingKey = kvMappingKey;
    }
}
