package xyz.apolicky.constraints.functionaldependency;

import de.metanome.algorithm_integration.ColumnIdentifier;
import de.metanome.algorithm_integration.results.FunctionalDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FdIdDiscovery {

    private static final Logger log = LoggerFactory.getLogger(FdIdDiscovery.class);

    public static List<String> findIdentifiers(String csvPath) {
        var result = FunctionalDependencyFinder.findFunctionalDependencies(csvPath);
        var idCandidates = getIdentifierCandidates(result._1, result._2);
        log.debug("Got id candidates: {}", idCandidates);
        return idCandidates;
    }

    static List<String> getIdentifierCandidates(List<String> columnNames, List<FunctionalDependency> fds) {
        var numberOfColumns = columnNames.size();
        AtomicInteger countOfSingleValueColumns = new AtomicInteger();

        Map<String, List<String>> candidates = new HashMap<>();
        for (var columnName : columnNames) {
            candidates.put(columnName, new ArrayList<>());
        }

        fds.stream()
                .filter(fd -> {
                    if (fd.getDeterminant().getColumnIdentifiers().isEmpty()) {
                        countOfSingleValueColumns.getAndIncrement();
                    }

                    return fd.getDeterminant().getColumnIdentifiers().size() == 1;
                })
                .forEach(fd -> {
                    var determinant = fd.getDeterminant().getColumnIdentifiers().toArray(ColumnIdentifier[]::new)[0].getColumnIdentifier();
                    candidates.get(determinant).add(fd.getDependant().toString());
                });

        return candidates.entrySet().stream().filter(candidate ->
                        candidate.getValue().size() == numberOfColumns - countOfSingleValueColumns.get() - 1)
                .map(Map.Entry::getKey).toList();
    }
}
