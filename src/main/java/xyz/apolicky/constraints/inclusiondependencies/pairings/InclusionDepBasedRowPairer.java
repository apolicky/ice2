package xyz.apolicky.constraints.inclusiondependencies.pairings;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;
import scala.Tuple2;
import xyz.apolicky.constraints.inclusiondependencies.InclusionDependencyUtils;
import xyz.apolicky.constraints.inclusiondependencies.model.*;
import xyz.apolicky.constraints.utils.FileUtils;
import xyz.apolicky.constraints.utils.SparkHelpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static xyz.apolicky.constraints.utils.SparkHelpers.debugPrintBasicJavaPairRdd;

@Service
public class InclusionDepBasedRowPairer {

    public static JavaPairRDD<CustomRow, CustomRow> pairInclusionDependencyRows(SparkSession spark, List<MyInclusionDependency> inclusionDependencies) {
        var rddPairsOfInclusionDependencyMappingKeyToRow = prepareRowsForDependencyPairing(spark, inclusionDependencies);
        var rowsFromBothDatasetsGroupedByInclusionDependencyValue = rddPairsOfInclusionDependencyMappingKeyToRow.groupByKey().values();

        JavaPairRDD<CustomRow, CustomRow> rddPairsOfSourceRowToTargetRow = rowsFromBothDatasetsGroupedByInclusionDependencyValue.flatMapToPair(groupOfRowsWithSameCellValue -> {
            List<CustomRow> referenced = new ArrayList<>(); // TODO: this can be for sure made differently, so that i don't have to have a list of referenced rows
            List<CustomRow> dependants = new ArrayList<>();

            for (var aRow : groupOfRowsWithSameCellValue) {
                if (aRow.keyMappingType() == KeyMappingType.REFERENCED) {
                    assert referenced.isEmpty();
                    referenced.add(aRow);
                } else if (aRow.keyMappingType() == KeyMappingType.DEPENDANT) {
                    dependants.add(aRow);
                } else {
                    throw new IllegalArgumentException("Unsupported key mapping type: " + aRow.keyMappingType());
                }
            }

            if (referenced.isEmpty()) {
                return Collections.emptyIterator();
            }
            if (referenced.size() > 1) {
                throw new IllegalArgumentException("Too many referenced rows: " + referenced);
            }

            return dependants.stream().map(dep ->
                    new Tuple2<>(referenced.get(0), dep)).iterator();
        });
        debugPrintBasicJavaPairRdd(rddPairsOfSourceRowToTargetRow);
        return rddPairsOfSourceRowToTargetRow;
    }

    /**
     * Here I want to transform rows from any of the datasets I get as an input to:
     * key: dependency key (refTable_depTable/ refColumn_depColumn::valueAsString)
     * value: customRow (contains Row from the dataset, info about ID column, what are numeric and string columns, etc.)
     */
    private static JavaPairRDD<String, CustomRow> prepareRowsForDependencyPairing(SparkSession spark, List<MyInclusionDependency> inclusionDependencies) {
        JavaRDD<CustomRow> temporaryCustomRowsRDD = JavaSparkContext.fromSparkContext(spark.sparkContext()).emptyRDD();
        JavaPairRDD<String, CustomRow> customRowsWithInclusionDependencyKeys = temporaryCustomRowsRDD.mapToPair(tcr -> new Tuple2<>("key", tcr));

        var datasetNameToFileNameMap = FileUtils.getMapOfFilenamesOfInclusionDependencies(inclusionDependencies);
        var importantColumnsForInclusionsDependencies = ImportantColumnsForInclusionsDependencies.fromInclusionDependencies(inclusionDependencies);

        for (var datasetNameToFileNameEntry : datasetNameToFileNameMap.entrySet()) {
            var datasetName = datasetNameToFileNameEntry.getKey();
            var datasetFileName = datasetNameToFileNameEntry.getValue();

            var dataset = SparkHelpers.datasetFromCsv(spark, datasetFileName, SparkHelpers.FilenameMapper.LAST_FILENAME_PART);

            JavaRDD<Row> rowRDD = dataset.javaRDD();
            String[] columnNames = dataset.columns();

            var valueColumnIndicesForInclusionDependencies = getValueColumnIndices(importantColumnsForInclusionsDependencies.forDataset(datasetName), columnNames);

            var columnIndexToKeyMappings = valueColumnIndicesForInclusionDependencies.stream()
                    .map(valueColumnIndex ->
                            new Tuple2<>(valueColumnIndex, getInclusionDependencyMappingsForGivenColumnAndDataset(datasetName, columnNames[valueColumnIndex], inclusionDependencies))
                    ).collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));

            JavaPairRDD<String, CustomRow> kvPairsOfInclusionDependencyKeyToCustomRow = rowRDD.flatMapToPair((PairFlatMapFunction<Row, String, CustomRow>) row -> {
                List<Tuple2<String, CustomRow>> pairs = new ArrayList<>();

                for (var valueColumnIndex : valueColumnIndicesForInclusionDependencies) {
                    Object value = row.get(valueColumnIndex);
                    var inclusionDependencyMappingsForGivenColumnAndDataset = columnIndexToKeyMappings.get(valueColumnIndex);

                    for (var inclusionDependencyMappingForGivenColumnAndDataset : inclusionDependencyMappingsForGivenColumnAndDataset) {
                        var mappingKey = InclusionDependencyUtils.getInclusionDependencyMappingKey(inclusionDependencyMappingForGivenColumnAndDataset, value);
                        var customRow = new CustomRow(row, datasetName, inclusionDependencyMappingForGivenColumnAndDataset.keyMappingType());
                        pairs.add(new Tuple2<>(mappingKey, customRow));
                    }
                }
                return pairs.iterator();
            });
            customRowsWithInclusionDependencyKeys = customRowsWithInclusionDependencyKeys.union(kvPairsOfInclusionDependencyKeyToCustomRow);
        }
        debugPrintBasicJavaPairRdd(customRowsWithInclusionDependencyKeys);
        return customRowsWithInclusionDependencyKeys;
    }

    private static List<Integer> getValueColumnIndices(Set<String> importantColumnsForGivenDataset, String[] columnNames) {
        if (importantColumnsForGivenDataset == null) {
            throw new IllegalArgumentException("importantColumnsForGivenDataset can't be null. Possibly datasetName wasn't a key in provided datasetNameToInclusionDependencyRelatedColumnNames");
        }

        List<Integer> valueColumnIndices = new ArrayList<>();
        for (int i = 0; i < columnNames.length; i++) {
            if (importantColumnsForGivenDataset.contains(columnNames[i])) {
                valueColumnIndices.add(i);
            }
        }
        return valueColumnIndices;
    }

    /**
     * @return [referencedTable, dependantTable, referencedColumn, dependantColumn]
     */
    public static List<KeyMapping> getInclusionDependencyMappingsForGivenColumnAndDataset(String datasetName, String columnName, List<MyInclusionDependency> inclusionDependencies) {
        List<KeyMapping> inclusionDependencyPairs = new ArrayList<>();
        inclusionDependencies.forEach(inclusionDependency -> {
            var refColumn = inclusionDependency.idCandidateColumn();
            var refTable = inclusionDependency.referencedDatasetName();
            var depColumn = inclusionDependency.dependantColumn();
            var depTable = inclusionDependency.dependantDatasetName();

            if (columnName.equals(depColumn) && datasetName.equals(depTable)) {
                inclusionDependencyPairs.add(new KeyMapping(refTable, datasetName, refColumn, columnName, KeyMappingType.DEPENDANT));
            } else if (columnName.equals(refColumn) && datasetName.equals(refTable)) {
                inclusionDependencyPairs.add(new KeyMapping(datasetName, depTable, columnName, depColumn, KeyMappingType.REFERENCED));
            }
        });
        return inclusionDependencyPairs;
    }
}
