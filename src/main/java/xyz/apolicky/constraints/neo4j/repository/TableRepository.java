package xyz.apolicky.constraints.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import xyz.apolicky.constraints.neo4j.domain.TableEntity;

public interface TableRepository extends Neo4jRepository<TableEntity, String> {
}

