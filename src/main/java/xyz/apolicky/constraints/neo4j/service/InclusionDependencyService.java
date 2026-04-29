package xyz.apolicky.constraints.neo4j.service;

import de.metanome.algorithm_integration.results.InclusionDependency;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;
import xyz.apolicky.constraints.columncomparisons.ComparableInfoTypes;
import xyz.apolicky.constraints.columncomparisons.ComparisonUtils;
import xyz.apolicky.constraints.columnmetadata.model.Info;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.inclusiondependencies.aggregation.AggregatedReferencedToDependantAggregationResult;
import xyz.apolicky.constraints.inclusiondependencies.comparisons.RowComparisonResult;
import xyz.apolicky.constraints.inclusiondependencies.model.ColumnPairWithTheirInfoTypes;
import xyz.apolicky.constraints.inclusiondependencies.model.InclusionDependentDatasetColumnPairsWithComparableType;
import xyz.apolicky.constraints.inclusiondependencies.model.MyInclusionDependency;
import xyz.apolicky.constraints.neo4j.domain.AggregatedStatsComparisonRelationshipProperties;
import xyz.apolicky.constraints.neo4j.domain.ColumnComparisonRelationshipProperties;
import xyz.apolicky.constraints.neo4j.mappers.ColumnMapper;
import xyz.apolicky.constraints.neo4j.mappers.TodoRecordMappers;
import xyz.apolicky.constraints.neo4j.model.DatasetNamePair;
import xyz.apolicky.constraints.neo4j.repository.ColumnRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InclusionDependencyService {

    private final ColumnRepository columnRepository;

    @Autowired
    public InclusionDependencyService(ColumnRepository repo) {
        this.columnRepository = repo;
    }

    public List<Info> getColumnsInclusionDependentOnIdCandidates() {
        return columnRepository.findAllByIsInclusionDependentOn_IdCandidate(true).stream().map(ColumnMapper::fromColumnEntity).toList();
    }

    public List<MyInclusionDependency> getInclusionDependenciesWithIdCandidates(Boolean idCandidateMustBeString) {
        if (idCandidateMustBeString) {
            return columnRepository.getInclusionDependenciesWithStringIdCandidates();
        }
        return columnRepository.getInclusionDependenciesWithIdCandidates();
    }

    public Map<DatasetNamePair, @NotNull List<ColumnPairWithTheirInfoTypes>> getInclusionDependentDatasetColumnPairsWithComparableType() {
        var comparableInfoTypes = ComparisonUtils.comparableInfoTypes.stream().map(TodoRecordMappers::toNeo4jSupportedMap).toList();
        // going to take only those types that can be compared together, ie string:string, number:number, ..
        var sameComparableTypes = ComparisonUtils.comparableInfoTypes.stream().filter(cit -> cit.first().equals(cit.second())).map(ComparableInfoTypes::first).map(Objects::toString).toList();
        var columnPairsOfDifferentTypes = toInclDepMap(columnRepository.getInclusionDependentDatasetColumnPairsWithComparableType(comparableInfoTypes));
        // also get the columns that can be compared for dataset that has inclusion dependency within itself
        var columnPairsOfSameTypeFromTheSameDataset = columnRepository.getInclusionDependentDatasetColumnPairsWithSameType(sameComparableTypes);

        var resultingMapOfDsPairsToColumnsToCompare = new HashMap<>(columnPairsOfDifferentTypes);
        for (var pairOfSameColumnsFromSameDataset : columnPairsOfSameTypeFromTheSameDataset) {
            var datasetNamePair = new DatasetNamePair(pairOfSameColumnsFromSameDataset.referencedDatasetName(), pairOfSameColumnsFromSameDataset.dependantDatasetName());
            // concat the lists for given datasetNamePair
            if (resultingMapOfDsPairsToColumnsToCompare.computeIfPresent(datasetNamePair, (k, currentListOfPairs) -> {
                currentListOfPairs.addAll(pairOfSameColumnsFromSameDataset.columnPairs());
                return currentListOfPairs;
            }) == null) {
                // if not present, add it
                resultingMapOfDsPairsToColumnsToCompare.put(datasetNamePair, pairOfSameColumnsFromSameDataset.columnPairs());
            }
        }

        return resultingMapOfDsPairsToColumnsToCompare;
    }

    public void saveInclusionDependencies(Iterable<InclusionDependency> inclusionDependencies) {
        var columnEntities = columnRepository.findAll();
        var columnEntityMapByName = columnEntities.stream().map(ce -> Map.entry(
                new Tuple2<>(ce.getName(), ce.getFromTable().getName()), ce
        )).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (InclusionDependency inclusionDependency : inclusionDependencies) {
            var dependants = inclusionDependency.getDependant().getColumnIdentifiers();
            var referencedElements = inclusionDependency.getReferenced().getColumnIdentifiers();

            if (dependants.size() != referencedElements.size() && dependants.size() != 1) {
                throw new RuntimeException("Dependants and referenced are expected to be of length 1");
            }

            var dependant = dependants.get(0);
            var referenced = referencedElements.get(0);

            var dependantEntity = columnEntityMapByName.get(new Tuple2<>(dependant.getColumnIdentifier(), dependant.getTableIdentifier()));
            var referencedEntity = columnEntityMapByName.get(new Tuple2<>(referenced.getColumnIdentifier(), referenced.getTableIdentifier()));

            dependantEntity.addIsInclusionDependentOnEntity(referencedEntity);

        }
        columnRepository.saveAll(columnEntities);
    }

    public HashMap<String, List<Tuple2<String, InfoType>>> getDatasetColumnNamesAndTypes(List<String> dependantDatasets) {
        var res = columnRepository.getColumnsForTheseDatasets(dependantDatasets);
        var map = new HashMap<String, List<Tuple2<String, InfoType>>>();
        for (var cols : res) {
            var namesAndTypes = cols.columnEntities().stream().map(ce -> new Tuple2<>(ce.getName(), ce.getInfoType())).toList();
            map.put(cols.datasetName(), namesAndTypes);
        }
        return map;
    }


    /**
     * Maps the argument the list type to map
     * values of format { referencedDs, dependantDs, allColumnsToBeCompared} are mapped to [{referencedDs, dependantDs}: allColumnsToBeCompared]
     */
    private Map<DatasetNamePair, @NotNull List<ColumnPairWithTheirInfoTypes>> toInclDepMap(List<InclusionDependentDatasetColumnPairsWithComparableType> inclusionDependentDatasetColumnPairsWithComparableType) {
        return inclusionDependentDatasetColumnPairsWithComparableType.stream()
                .map(a1 -> Map.entry(new DatasetNamePair(a1.referencedDatasetName(), a1.dependantDatasetName()), a1.columnPairs()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void savePerRowComparisonResults(List<RowComparisonResult> perRowComparisonResults) {
        // get column entities all at once
        var columnEntities = columnRepository.findAll();
        var columnMapByName = columnEntities.stream().map(ce -> Map.entry(new Tuple2<>(ce.getName(), ce.getFromTable().getName()), ce)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for (var aggregatedPerRowComparisonResult : perRowComparisonResults) {
            if (aggregatedPerRowComparisonResult.comparisonResultStringForm().isEmpty() &&
                    aggregatedPerRowComparisonResult.comparisonResultEnumForm().isEmpty()) {
                continue;
            }

            var columnAndDatasetPair = aggregatedPerRowComparisonResult.columnAndDatasetPair();
            var comparisonsInStringForm = aggregatedPerRowComparisonResult.comparisonResultStringForm().entrySet().stream()
                    .filter(e -> ComparisonUtils.filterRowComparisonResultsAboveThreshold(aggregatedPerRowComparisonResult.numberOfComparisons(), e.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            var properties = ColumnComparisonRelationshipProperties.fromValue(
                    columnMapByName.get(new Tuple2<>(columnAndDatasetPair.dependantColumnName(), columnAndDatasetPair.dependantTableName())),
                    aggregatedPerRowComparisonResult.getKvMappingKey(),
                    aggregatedPerRowComparisonResult.numberOfComparisons(),
                    comparisonsInStringForm,
                    aggregatedPerRowComparisonResult.comparisonResultStringForm());

            columnMapByName.get(new Tuple2<>(columnAndDatasetPair.referencedColumnName(), columnAndDatasetPair.referencedTableName())).addHasReferenceComparisonHoldingTrueWithEntity(properties);
        }
        columnRepository.saveAll(columnEntities);
    }

    public void saveAggregatedComparisonsReferencedToAllDependantFromInclusionDependencies
            (List<AggregatedReferencedToDependantAggregationResult> aggregatedReferencedToDependantAggregationResults) {
        var columnEntities = columnRepository.findAll();
        var columnMapByName = columnEntities.stream().map(ce -> Map.entry(new Tuple2<>(ce.getName(), ce.getFromTable().getName()), ce)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (var aggregatedReferencedToDependantAggregationResult : aggregatedReferencedToDependantAggregationResults) {
            var columnAndDatasetPair = aggregatedReferencedToDependantAggregationResult.columnAndDatasetPair();

            var properties = AggregatedStatsComparisonRelationshipProperties.fromValue(
                    columnMapByName.get(new Tuple2<>(columnAndDatasetPair.dependantColumnName(), columnAndDatasetPair.dependantTableName())),
                    aggregatedReferencedToDependantAggregationResult);

            columnMapByName.get(new Tuple2<>(columnAndDatasetPair.referencedColumnName(), columnAndDatasetPair.referencedTableName())).addHasAggregatedReferenceComparisonHoldingTrueWith(properties);
        }
        columnRepository.saveAll(columnEntities);
    }
}

