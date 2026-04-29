package xyz.apolicky.constraints.neo4j.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.UUID;

@Node("Distribution")
public class DistributionEntity {

    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;

    @Relationship(type = "HAS_DISTRIBUTION_POINT", direction = Relationship.Direction.OUTGOING)
    private List<DistributionPointEntity> distributionPoints;

    public List<DistributionPointEntity> getDistributionPoints() {
        return distributionPoints;
    }

    public void setDistributionPoints(List<DistributionPointEntity> distributionPoints) {
        this.distributionPoints = distributionPoints;
    }
}
