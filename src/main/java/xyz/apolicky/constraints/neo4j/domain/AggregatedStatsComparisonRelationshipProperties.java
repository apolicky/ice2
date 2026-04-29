package xyz.apolicky.constraints.neo4j.domain;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import xyz.apolicky.constraints.inclusiondependencies.aggregation.AggregatedReferencedToDependantAggregationResult;
import xyz.apolicky.constraints.inclusiondependencies.aggregation.RefToAggrValueResult;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

@RelationshipProperties
public class AggregatedStatsComparisonRelationshipProperties implements Serializable {
    @TargetNode
    private ColumnEntity column;
    @RelationshipId
    private String id;

    private int numberOfPairings;

    private String comparedAgainstMinimums;
    private String comparedAgainstMaximums;
    private String comparedAgainstSums;
    private String comparedAgainstAverages;

    public static AggregatedStatsComparisonRelationshipProperties fromValue(ColumnEntity column, AggregatedReferencedToDependantAggregationResult result) {
        var properties = new AggregatedStatsComparisonRelationshipProperties();
        properties.column = column;
        properties.numberOfPairings = result.numberOfPairings();
        properties.comparedAgainstMinimums = transformMapToString(result.valueComparedToMinOfAllTheDependantRows());
        properties.comparedAgainstMaximums = transformMapToString(result.valueComparedToMaxOfAllTheDependantRows());
        properties.comparedAgainstAverages = transformMapToString(result.valueComparedToAvgOfAllTheDependantRows());
        properties.comparedAgainstSums = transformMapToString(result.valueComparedToSumOfAllTheDependantRows());
        return properties;
    }

    private static String transformMapToString(Map<RefToAggrValueResult, Integer> map) {
        return map.entrySet().stream()
                .map(entry -> "[" + entry.getKey().toString() + ":" + entry.getValue().toString() + "]")
                .collect(Collectors.joining("~"));
    }

    public ColumnEntity getColumn() {
        return column;
    }

    public void setColumn(ColumnEntity column) {
        this.column = column;
    }

    public int getNumberOfPairings() {
        return numberOfPairings;
    }

    public void setNumberOfPairings(int numberOfPairings) {
        this.numberOfPairings = numberOfPairings;
    }

    public String getComparedAgainstMinimums() {
        return comparedAgainstMinimums;
    }

    public void setComparedAgainstMinimums(String comparedAgainstMinimums) {
        this.comparedAgainstMinimums = comparedAgainstMinimums;
    }

    public String getComparedAgainstMaximums() {
        return comparedAgainstMaximums;
    }

    public void setComparedAgainstMaximums(String comparedAgainstMaximums) {
        this.comparedAgainstMaximums = comparedAgainstMaximums;
    }

    public String getComparedAgainstSums() {
        return comparedAgainstSums;
    }

    public void setComparedAgainstSums(String comparedAgainstSums) {
        this.comparedAgainstSums = comparedAgainstSums;
    }

    public String getComparedAgainstAverages() {
        return comparedAgainstAverages;
    }

    public void setComparedAgainstAverages(String comparedAgainstAverages) {
        this.comparedAgainstAverages = comparedAgainstAverages;
    }
}
