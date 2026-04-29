package xyz.apolicky.constraints.functionaldependency;

import de.metanome.algorithm_integration.results.FunctionalDependency;
import xyz.apolicky.constraints.functionaldependency.model.FunctionalDependencyColumn;

import java.util.*;

public class FunctionalDependencyHelpers {
    public static HashMap<String, Set<String>> getSingleDeterminantFDs(List<FunctionalDependency> fds) {
        var result = new HashMap<String, Set<String>>();
        fds.forEach(functionalDependency -> {
            var determinant = functionalDependency.getDeterminant().getColumnIdentifiers();
            if (determinant.size() == 1) {
                var columnName = determinant.iterator().next().getColumnIdentifier();
                result.putIfAbsent(columnName, new HashSet<>());
                result.get(columnName).add(functionalDependency.getDependant().getColumnIdentifier());
            }
        });
        return result;
    }

    public static List<FunctionalDependencyColumn> getColumnsRelatedToFDs(List<FunctionalDependency> fds) {
        return fds.stream()
                .flatMap(functionalDependency -> {
                    List<FunctionalDependencyColumn> fdColumns = new ArrayList<>();
                    fdColumns.add(new FunctionalDependencyColumn(functionalDependency.getDependant().getColumnIdentifier(), functionalDependency.getDependant().getTableIdentifier()));
                    functionalDependency.getDeterminant().getColumnIdentifiers().forEach(columnIdentifier ->
                            fdColumns.add(new FunctionalDependencyColumn(columnIdentifier.getColumnIdentifier(), columnIdentifier.getTableIdentifier())));
                    return fdColumns.stream();
                })
                .distinct()
                .toList();
    }
}
