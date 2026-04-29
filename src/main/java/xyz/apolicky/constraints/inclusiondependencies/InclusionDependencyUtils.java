package xyz.apolicky.constraints.inclusiondependencies;

import de.metanome.algorithm_integration.ColumnIdentifier;
import de.metanome.algorithm_integration.ColumnPermutation;
import de.metanome.algorithm_integration.results.InclusionDependency;
import xyz.apolicky.constraints.inclusiondependencies.model.KeyMapping;
import xyz.apolicky.constraints.model.ColumnFromTable;
import xyz.apolicky.constraints.utils.ModelNameHelpers;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static xyz.apolicky.constraints.model.ColumnFromTable.tablesAndColumnsMap;

public class InclusionDependencyUtils {

    /**
     * Format: refTable_depTable/refColumn_depColumn::valueAsString
     */
    public static String getInclusionDependencyMappingKey(KeyMapping keyMapping, Object value) {
        return keyMapping.referencedTable() +
                "_" + keyMapping.dependantTable() +
                "/" + keyMapping.referencedColumn() +
                "_" + keyMapping.dependantColumn() +
                "::" + ModelNameHelpers.getValueString(value);
    }

    public static ColumnIdentifier getSingleColumnIdentifier(ColumnPermutation columnPermutation) {
        if (columnPermutation.getColumnIdentifiers().size() > 1) {
            throw new IllegalArgumentException("Only one column permitted as of now");
        }
        return columnPermutation.getColumnIdentifiers().get(0);
    }

    public static Map<String, Set<String>> getColumnNamesAndTablesForInds(Iterable<InclusionDependency> inclusionDependencies) {
        return tablesAndColumnsMap(
                StreamSupport.stream(inclusionDependencies.spliterator(), false)
                        .filter(id -> {
                            var dependants = id.getDependant().getColumnIdentifiers();
                            var referencedElements = id.getReferenced().getColumnIdentifiers();

                            if (dependants.size() != referencedElements.size() && dependants.size() != 1) {
                                throw new RuntimeException("Dependants and referenced are expected to be of length 1");
                            }

                            return true;
                        })
                        .flatMap(id -> {

                            var dependants = id.getDependant().getColumnIdentifiers();
                            var referencedElements = id.getReferenced().getColumnIdentifiers();

                            var dependant = dependants.get(0);
                            var referenced = referencedElements.get(0);
                            return Stream.of(
                                    new ColumnFromTable(dependant.getColumnIdentifier(), dependant.getTableIdentifier()),
                                    new ColumnFromTable(referenced.getColumnIdentifier(), referenced.getTableIdentifier())

                            );
                        })
                        .distinct());
    }
}
