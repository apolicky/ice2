package xyz.apolicky.constraints.columnmetadata.model.results;

import de.metanome.algorithm_integration.results.FunctionalDependency;

import java.util.List;

public record FdSearchResult(
        String message,
        List<String> columnNames,
        List<FunctionalDependency> fds
) {
}
