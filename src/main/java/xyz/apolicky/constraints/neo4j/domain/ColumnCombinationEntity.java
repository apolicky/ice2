package xyz.apolicky.constraints.neo4j.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Node("ColumnCombination")
public class ColumnCombinationEntity {
    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;

    @Relationship(type = "PART_OF_FUNC_DEP_DETERMINANT", direction = Relationship.Direction.INCOMING)
    private List<ColumnEntity> ofEntities;
    private int lhsCount;

    private String name;

    public ColumnCombinationEntity() {
    }

    public List<ColumnEntity> getOfEntities() {
        return ofEntities;
    }

    public void setOfEntities(List<ColumnEntity> ofEntities) {
        this.ofEntities = ofEntities;
        setLhsCount(ofEntities.size());
        updateName();
    }

    public String getName() {
        return this.name;
    }

    private void updateName() {
        this.name = this.ofEntities.stream().map(ColumnEntity::getName).collect(Collectors.joining(","));
    }

    public int getLhsCount() {
        return lhsCount;
    }

    public void setLhsCount(int lhsCount) {
        this.lhsCount = lhsCount;
    }
}
