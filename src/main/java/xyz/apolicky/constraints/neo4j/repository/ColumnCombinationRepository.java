package xyz.apolicky.constraints.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import xyz.apolicky.constraints.neo4j.domain.ColumnCombinationEntity;

import java.util.UUID;

public interface ColumnCombinationRepository extends Neo4jRepository<ColumnCombinationEntity, UUID> {
}

