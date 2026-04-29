package xyz.apolicky.constraints.neo4j.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@Node("Column")
public class ColumnEntity {

    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;
    private String name;
    private boolean idCandidate;
    private boolean unique;
    private boolean nullable;
    private int distinctValues;
    private int nullCount;
    private int count;
    private int zeroCount;
    private int positiveCount;
    private int minLength;
    private int maxLength;
    private Object min;
    private Object max;
    private Object average;
    private InfoType infoType;
    private String infoTypeDistribution;
    @Relationship(type = "FROM_TABLE", direction = Relationship.Direction.OUTGOING)
    private TableEntity fromTable;
    @Relationship(type = "IS_FUNCTIONALLY_DEPENDENT_ON", direction = Relationship.Direction.OUTGOING)
    private List<ColumnCombinationEntity> isFunctionallyDependentOn;
    @Relationship(type = "IS_INCLUSION_DEPENDENT_ON", direction = Relationship.Direction.OUTGOING)
    private List<ColumnEntity> isInclusionDependentOn;
    @Relationship(type = "HAS_DISTRIBUTION", direction = Relationship.Direction.OUTGOING)
    private DistributionEntity distribution;
    @Relationship(type = "TWO_COLUMN_COMPARISON_HOLDS", direction = Relationship.Direction.OUTGOING)
    private List<ColumnComparisonRelationshipProperties> hasColumnComparisonHoldingTrueWith;
    @Relationship(type = "PATTERN_HOLDS", direction = Relationship.Direction.OUTGOING)
    private List<PatternMatchRelationshipProperties> hasPatternHoldingTrueWith;
    @Relationship(type = "REFERENCE_COMPARISON_HOLDS", direction = Relationship.Direction.OUTGOING)
    private List<ColumnComparisonRelationshipProperties> hasReferenceComparisonHoldingTrueWith;
    @Relationship(type = "AGGREGATED_REFERENCE_COMPARISON_HOLDS", direction = Relationship.Direction.OUTGOING)
    private List<AggregatedStatsComparisonRelationshipProperties> hasAggregatedReferenceComparisonHoldingTrueWith;

    public ColumnEntity() {
        this.isFunctionallyDependentOn = new ArrayList<>();
        this.isInclusionDependentOn = new ArrayList<>();
        this.hasReferenceComparisonHoldingTrueWith = new ArrayList<>();
        this.hasAggregatedReferenceComparisonHoldingTrueWith = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getDistinctValues() {
        return distinctValues;
    }

    public void setDistinctValues(int distinctValues) {
        this.distinctValues = distinctValues;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public Object getAverage() {
        return average;
    }

    public void setAverage(Object average) {
        this.average = average;
    }

    public TableEntity getFromTable() {
        return fromTable;
    }

    public void setFromTable(TableEntity fromTable) {
        this.fromTable = fromTable;
    }

    public List<ColumnCombinationEntity> getIsFunctionallyDependentOn() {
        return isFunctionallyDependentOn;
    }

    public void setIsFunctionallyDependentOn(ColumnCombinationEntity newFunctionalDependency) {
        this.isFunctionallyDependentOn.add(newFunctionalDependency);
    }

    public List<ColumnEntity> getIsInclusionDependentOn() {
        return isInclusionDependentOn;
    }

    public void addIsInclusionDependentOnEntity(ColumnEntity referencedEntity) {
        this.isInclusionDependentOn.add(referencedEntity);
    }

    public DistributionEntity getDistribution() {
        return distribution;
    }

    public void setDistribution(DistributionEntity distribution) {
        this.distribution = distribution;
    }

    public boolean isIdCandidate() {
        return idCandidate;
    }

    public void setIdCandidate(boolean idCandidate) {
        this.idCandidate = idCandidate;
    }

    public InfoType getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoType infoType) {
        this.infoType = infoType;
    }

    public String toString() {
        return "Column [id=" + id + ", name=" + name + ", idCandidate=" + idCandidate + ", unique=" + unique
                + "," + nullable + ", distinctValues=" + distinctValues + ", count=" + count + ", min=" + min + ", max=" + max + ", infoType=" + infoType + "]";
    }

    public int getNullCount() {
        return nullCount;
    }

    public void setNullCount(int nullCount) {
        this.nullCount = nullCount;
    }

    public List<ColumnComparisonRelationshipProperties> getHasColumnComparisonHoldingTrueWith() {
        return hasColumnComparisonHoldingTrueWith;
    }

    public void setHasColumnComparisonHoldingTrueWith(List<ColumnComparisonRelationshipProperties> hasColumnComparisonHoldingTrueWith) {
        this.hasColumnComparisonHoldingTrueWith = hasColumnComparisonHoldingTrueWith;
    }

    public int getZeroCount() {
        return zeroCount;
    }

    public void setZeroCount(int zeroCount) {
        this.zeroCount = zeroCount;
    }

    public int getPositiveCount() {
        return positiveCount;
    }

    public void setPositiveCount(int positiveCount) {
        this.positiveCount = positiveCount;
    }

    public List<PatternMatchRelationshipProperties> getHasPatternHoldingTrueWith() {
        return hasPatternHoldingTrueWith;
    }

    public void setHasPatternHoldingTrueWith(List<PatternMatchRelationshipProperties> hasPatternHoldingTrueWith) {
        this.hasPatternHoldingTrueWith = hasPatternHoldingTrueWith;
    }

    public List<ColumnComparisonRelationshipProperties> getHasReferenceComparisonHoldingTrueWith() {
        return hasReferenceComparisonHoldingTrueWith;
    }

    public void setHasReferenceComparisonHoldingTrueWith(List<ColumnComparisonRelationshipProperties> hasReferenceComparisonHoldingTrueWith) {
        this.hasReferenceComparisonHoldingTrueWith = hasReferenceComparisonHoldingTrueWith;
    }

    public void addHasReferenceComparisonHoldingTrueWithEntity(ColumnComparisonRelationshipProperties referencedEntity) {
        this.hasReferenceComparisonHoldingTrueWith.add(referencedEntity);
    }

    public List<AggregatedStatsComparisonRelationshipProperties> getHasAggregatedReferenceComparisonHoldingTrueWith() {
        return hasAggregatedReferenceComparisonHoldingTrueWith;
    }

    public void setHasAggregatedReferenceComparisonHoldingTrueWith(List<AggregatedStatsComparisonRelationshipProperties> hasAggregatedReferenceComparisonHoldingTrueWith) {
        this.hasAggregatedReferenceComparisonHoldingTrueWith = hasAggregatedReferenceComparisonHoldingTrueWith;
    }

    // TODO: use with aggregated ones
    public void addHasAggregatedReferenceComparisonHoldingTrueWith(AggregatedStatsComparisonRelationshipProperties properties) {
        this.hasAggregatedReferenceComparisonHoldingTrueWith.add(properties);
    }

    public String getInfoTypeDistribution() {
        return infoTypeDistribution;
    }

    public void setInfoTypeDistribution(String infoTypeDistribution) {
        this.infoTypeDistribution = infoTypeDistribution;
    }
}
