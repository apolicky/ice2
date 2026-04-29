package xyz.apolicky.constraints.neo4j.service;

import de.metanome.algorithm_integration.ColumnIdentifier;
import de.metanome.algorithm_integration.results.FunctionalDependency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.apolicky.constraints.columncomparisons.ColumnPair;
import xyz.apolicky.constraints.columncomparisons.ComparisonUtils;
import xyz.apolicky.constraints.columnmetadata.model.Info;
import xyz.apolicky.constraints.columnmetadata.model.MultiColumnRelationResult;
import xyz.apolicky.constraints.columnmetadata.pattern.PatternRegistry;
import xyz.apolicky.constraints.functionaldependency.FunctionalDependencyHelpers;
import xyz.apolicky.constraints.functionaldependency.model.FunctionalDependencyColumn;
import xyz.apolicky.constraints.model.ColumnFromTable;
import xyz.apolicky.constraints.neo4j.domain.*;
import xyz.apolicky.constraints.neo4j.mappers.ColumnMapper;
import xyz.apolicky.constraints.neo4j.mappers.TodoRecordMappers;
import xyz.apolicky.constraints.neo4j.model.ColumnPairWithInfoTypes;
import xyz.apolicky.constraints.neo4j.model.MyFD;
import xyz.apolicky.constraints.neo4j.repository.ColumnCombinationRepository;
import xyz.apolicky.constraints.neo4j.repository.ColumnRepository;
import xyz.apolicky.constraints.neo4j.repository.PatternRepository;
import xyz.apolicky.constraints.neo4j.repository.TableRepository;
import xyz.apolicky.constraints.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static xyz.apolicky.constraints.model.ColumnFromTable.tablesAndColumnsMap;

@Service
public class MetadataService {

    private final ColumnRepository columnRepository;
    private final ColumnCombinationRepository columnCombinationRepository;
    private final TableRepository tableRepository;
    private final PatternRepository patternRepository;
    private final PatternRegistry patternRegistry;

    @Autowired
    public MetadataService(ColumnRepository columnRepository, ColumnCombinationRepository columnCombinationRepository, TableRepository tableRepository, PatternRepository patternRepository, PatternRegistry patternRegistry) {
        this.columnRepository = columnRepository;
        this.columnCombinationRepository = columnCombinationRepository;
        this.tableRepository = tableRepository;
        this.patternRepository = patternRepository;
        this.patternRegistry = patternRegistry;
    }

    public void saveAll(Iterable<ColumnEntity> columnEntities) {
        columnRepository.saveAll(columnEntities);
    }

    public void saveAllTableEntities(Iterable<TableEntity> tableEntities) {
        tableRepository.saveAll(tableEntities);
    }

    public List<MyFD> getFunctionalDependencies() {
        var bb = columnRepository.todoGetAllFDs();
        return bb;
    }

    public void saveFunctionalDependencies(List<FunctionalDependency> functionalDependenciesInitial, int maxFdSourceSize) {
        var functionalDependencies = functionalDependenciesInitial.stream()
                .filter(fd -> fd.getDeterminant().getColumnIdentifiers().size() <= maxFdSourceSize)
                .toList();

        var allColumns = FunctionalDependencyHelpers.getColumnsRelatedToFDs(functionalDependencies);

        var columnEntities = getAllColumnEntities(allColumns);

        List<ColumnCombinationEntity> columnCombinationEntities = new ArrayList<>();

        for (FunctionalDependency functionalDependency : functionalDependencies) {
            var columnIdentifiersForThisFD = functionalDependency.getDeterminant().getColumnIdentifiers().stream().map(ColumnIdentifier::getColumnIdentifier).toList();
            var columnEntitiesForThisFD = columnEntities.stream().filter(entity -> columnIdentifiersForThisFD.contains(entity.getName())).toList();
            var columnCombination = new ColumnCombinationEntity();
            columnCombination.setOfEntities(columnEntitiesForThisFD);

            var dependants = columnEntities.stream().filter(entity ->
                    functionalDependency.getDependant().getColumnIdentifier().contentEquals(entity.getName())
            ).toList();

            assert dependants.size() == 1;

            var dependant = dependants.get(0);
            dependant.setIsFunctionallyDependentOn(columnCombination);
            columnCombinationEntities.add(columnCombination);
        }

        columnCombinationRepository.saveAll(columnCombinationEntities);
        columnRepository.saveAll(columnEntities);
    }

    public List<Info> getIdCandidates() {
        return columnRepository.findByNullableFalseAndUniqueTrue().stream().map(ColumnMapper::fromColumnEntity).toList();
    }

    public void clearEverything() {
        columnRepository.deleteAll();
        tableRepository.deleteAll();
        columnCombinationRepository.deleteAll();
        patternRepository.deleteAll();
    }

    public List<ColumnPairWithInfoTypes> getColumnPairsWithComparableTypeFromDataset(String datasetName) {
        var comparableInfoTypes = ComparisonUtils.comparableInfoTypes.stream().map(TodoRecordMappers::toNeo4jSupportedMap).toList();
        var trimmedDatasetName = FileUtils.getDatasetName(datasetName);
        return columnRepository.getColumnPairsWithComparableTypeFromDataset(comparableInfoTypes, trimmedDatasetName);
    }

    public void saveSingleColumnMetadata(Map<String, Map<String, Info>> singleColumnMetadata) {
        var patterns = getAllOrCreatePatternEntities();
        var tableEntities = singleColumnMetadata.keySet().stream().distinct().collect(Collectors.toMap(tableName -> tableName, TableEntity::new));
        this.saveAllTableEntities(tableEntities.values());


        var columnEntities = singleColumnMetadata.entrySet().stream().flatMap(tableAndColumns -> {
            var tableName = tableAndColumns.getKey();
            var columns = tableAndColumns.getValue();
            return columns.entrySet().stream().map(column -> {
                var columnName = column.getKey();
                var columnInfo = column.getValue();

                var columnEntity = ColumnMapper.fromInfo(columnInfo);
                var table = tableEntities.get(tableName);
                columnEntity.setFromTable(table);

                ColumnMapper.addMatchingPatternsForColumnFromInfo(columnEntity, columnInfo, patterns);
                return columnEntity;
            });
        }).toList();
        this.saveAll(columnEntities);
    }

    public void saveMultiColumnRelationMetadata(Map<String, List<MultiColumnRelationResult>> multiColumnRelationshipMetadata) {
        multiColumnRelationshipMetadata.forEach(this::saveMultiColumnRelationMetadataForGivenDataset);
    }

    public void saveMultiColumnRelationMetadataForGivenDataset(String datasetName, List<MultiColumnRelationResult> multiColumnRelationshipMetadata) {
        var columnEntities = columnRepository.findAllByFromTable_Name(datasetName);
        for (var leftColumnEntity : columnEntities) {
            List<ColumnComparisonRelationshipProperties> comparisonsHoldingForLeftEntity = new ArrayList<>();
            for (var rightColumnEntity : columnEntities) {
                var colPair = new ColumnPair(leftColumnEntity.getName(), rightColumnEntity.getName());
                var columnRelationshipsForPair = multiColumnRelationshipMetadata.stream().filter(m -> m.columnPair().equals(colPair)).toList();
                if (columnRelationshipsForPair.isEmpty()) {
                    // no metadata found for given pair
                    continue;
                } else if (columnRelationshipsForPair.size() > 1) {
                    throw new IllegalArgumentException("Multiple column relationships found for " + leftColumnEntity.getName() + " " + rightColumnEntity.getName());
                }

                var columnRelationships = columnRelationshipsForPair.get(0);

                if (columnRelationships.rowWiseCellComparisonSet().isEmpty() && columnRelationships.comparisonResultsHoldingTrue().isEmpty()) {
                    continue;
                }

                var comparisonsOverThreshold = columnRelationships.comparisonResultsHoldingTrue().entrySet().stream()
                        .filter(e -> ComparisonUtils.filterRowComparisonResultsAboveThreshold(rightColumnEntity.getCount(), e.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());

                if (comparisonsOverThreshold.isEmpty() && columnRelationships.comparisonResultsHoldingTrue().isEmpty()) {
                    continue;
                }

                var properties = ColumnComparisonRelationshipProperties.fromValue(rightColumnEntity,
                        columnRelationships.getKvMappingKey(),
                        rightColumnEntity.getCount(),
                        comparisonsOverThreshold,
                        columnRelationships.comparisonResultsHoldingTrue());
                comparisonsHoldingForLeftEntity.add(properties);
            }
            leftColumnEntity.setHasColumnComparisonHoldingTrueWith(comparisonsHoldingForLeftEntity);
        }
        columnRepository.saveAll(columnEntities);
    }


    public void initializePatternsInGraphDb() {
        getAllOrCreatePatternEntities();
    }

    private List<ColumnEntity> getAllColumnEntities(List<FunctionalDependencyColumn> allColumns) {
        List<ColumnEntity> columnEntities = new ArrayList<>();
        var tablesAndColumnNames = tablesAndColumnsMap(allColumns.stream().map(c -> new ColumnFromTable(c.columnName(), c.datasetName())));
        for (var entry : tablesAndColumnNames.entrySet()) {
            columnEntities.addAll(columnRepository.findAllByNameInAndFromTable_Name(entry.getValue(), entry.getKey()));
        }
        return columnEntities;
    }

    private List<PatternEntity> getAllOrCreatePatternEntities() {
        var patterns = this.patternRepository.findAll();
        if (patterns.size() == this.patternRegistry.getBasePatterns().size()) {
            return patterns;
        }

        this.patternRepository.deleteAll();
        var newPatterns = this.patternRegistry.getBasePatterns().stream()
                .map(pattern -> {
                    var p = new PatternEntity(pattern.getPatternName());
                    p.setDescription(pattern.getPatternDescription());
                    p.setPatternString(pattern.getPatternString());
                    return p;
                })
                .toList();
        this.patternRepository.saveAll(newPatterns);
        return newPatterns;
    }
}