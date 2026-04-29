package xyz.apolicky.constraints.inclusiondependencies.model;

import java.util.*;

/**
 * maps DatasetName-->Set[ImportantColumns]
 * ImportantColumn: either IND referenced or dependant
 */
public class ImportantColumnsForInclusionsDependencies {

    private final Map<String, Set<String>> datasetNameToInclusionDependencyRelatedColumnNames;

    private ImportantColumnsForInclusionsDependencies(Map<String, Set<String>> importantColumns) {
        this.datasetNameToInclusionDependencyRelatedColumnNames = importantColumns;
    }

    public static ImportantColumnsForInclusionsDependencies fromInclusionDependencies(List<MyInclusionDependency> inclusionDependencies) {
        Map<String, Set<String>> datasetNameToInclusionDependencyRelatedColumnNames = new HashMap<>();
        for (var inclusionDependency : inclusionDependencies) {
            var referencedTable = inclusionDependency.referencedDatasetName();
            var referencedColumn = inclusionDependency.idCandidateColumn();
            var dependantTable = inclusionDependency.dependantDatasetName();
            var dependantColumn = inclusionDependency.dependantColumn();

            addColumnsToImportantColumnMap(datasetNameToInclusionDependencyRelatedColumnNames, referencedTable, referencedColumn, dependantTable, dependantColumn);
        }
        return new ImportantColumnsForInclusionsDependencies(datasetNameToInclusionDependencyRelatedColumnNames);
    }

    private static void addColumnsToImportantColumnMap(Map<String, Set<String>> datasetNameToInclusionDependencyRelatedColumnNames, String referencedTable, String referencedColumn, String dependantTable, String dependantColumnName) {
        if (datasetNameToInclusionDependencyRelatedColumnNames.containsKey(referencedTable)) {
            datasetNameToInclusionDependencyRelatedColumnNames.get(referencedTable).add(referencedColumn);
        } else {
            datasetNameToInclusionDependencyRelatedColumnNames.put(referencedTable, new HashSet<>(Set.of(referencedColumn)));
        }

        if (datasetNameToInclusionDependencyRelatedColumnNames.containsKey(dependantTable)) {
            datasetNameToInclusionDependencyRelatedColumnNames.get(dependantTable).add(dependantColumnName);
        } else {
            datasetNameToInclusionDependencyRelatedColumnNames.put(dependantTable, new HashSet<>(Set.of(dependantColumnName)));
        }
    }

    public Set<String> forDataset(String datasetName) {
        return datasetNameToInclusionDependencyRelatedColumnNames.get(datasetName);
    }
}
