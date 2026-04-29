package xyz.apolicky.constraints.inclusiondependencies.model;

public record MyInclusionDependency(
        String idCandidateColumn,
        String referencedDatasetName,
        String dependantColumn,
        String dependantDatasetName
) {
}