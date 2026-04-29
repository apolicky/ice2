package xyz.apolicky.constraints.neo4j.model;

import java.io.Serializable;

public record DatasetNamePair(
        String referencedDatasetName,
        String dependantDatasetName) implements Serializable {
}
