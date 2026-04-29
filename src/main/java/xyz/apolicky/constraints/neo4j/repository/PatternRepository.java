package xyz.apolicky.constraints.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import xyz.apolicky.constraints.neo4j.domain.PatternEntity;

public interface PatternRepository extends Neo4jRepository<PatternEntity, String> {
}