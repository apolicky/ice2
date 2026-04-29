package xyz.apolicky.constraints.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import xyz.apolicky.constraints.inclusiondependencies.model.InclusionDependentDatasetColumnPairsWithComparableType;
import xyz.apolicky.constraints.inclusiondependencies.model.MyInclusionDependency;
import xyz.apolicky.constraints.neo4j.domain.ColumnEntity;
import xyz.apolicky.constraints.neo4j.model.ColumnPairWithInfoTypes;
import xyz.apolicky.constraints.neo4j.model.DatasetNameWithListOfColumnEntities;
import xyz.apolicky.constraints.neo4j.model.MyFD;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ColumnRepository extends Neo4jRepository<ColumnEntity, UUID> {

    List<ColumnEntity> findByNullableFalseAndUniqueTrue();

    List<ColumnEntity> findAllByNameInAndFromTable_Name(Collection<String> name, String fromTable_name);

    ColumnEntity findByNameAndFromTable_Name(String name, String fromTableName);

    List<ColumnEntity> findAllByIsInclusionDependentOn_IdCandidate(boolean isInclusionDependentIdCandidate);

    @Query("""
            MATCH (referencedTable:Table)<-[:FROM_TABLE]-(idc:Column {idCandidate: TRUE})<-[:IS_INCLUSION_DEPENDENT_ON]-(dep:Column)-[:FROM_TABLE]->(dependantTable:Table)
            RETURN
                idc.name as idCandidateColumn,
                dep.name as dependantColumn,
                referencedTable.name as referencedDatasetName,
                dependantTable.name as dependantDatasetName
            """)
    List<MyInclusionDependency> getInclusionDependenciesWithIdCandidates();

    @Query("""
            MATCH (referencedTable:Table)<-[:FROM_TABLE]-(idc:Column {idCandidate: TRUE, infoType: "STRING"})<-[:IS_INCLUSION_DEPENDENT_ON]-(dep:Column)-[:FROM_TABLE]->(dependantTable:Table)
            RETURN
                idc.name as idCandidateColumn,
                dep.name as dependantColumn,
                referencedTable.name as referencedDatasetName,
                dependantTable.name as dependantDatasetName
            """)
    List<MyInclusionDependency> getInclusionDependenciesWithStringIdCandidates();

    @Query("""
            UNWIND $comparableInfoTypes AS comparableInfoTypePair
            MATCH (aColumnFromReferencedTable: Column)-[:FROM_TABLE]->(referencedTable:Table)<-[:FROM_TABLE]-
                  (ref:Column)<-[:IS_INCLUSION_DEPENDENT_ON]-(dep:Column)
                  -[:FROM_TABLE]->(dependantTable:Table)<-[:FROM_TABLE]-(aColumnFromDependantTable: Column)
            WHERE (aColumnFromReferencedTable.infoType = comparableInfoTypePair.first AND aColumnFromDependantTable.infoType = comparableInfoTypePair.second)
            RETURN
                 referencedTable.name AS referencedDatasetName,
                 dependantTable.name AS dependantDatasetName,
                 collect({
                    referencedDatasetColumn: aColumnFromReferencedTable.name,
                    dependantDatasetColumn: aColumnFromDependantTable.name,
                    referencedInfoType: aColumnFromReferencedTable.infoType,
                    dependantInfoType: aColumnFromDependantTable.infoType
                 }) AS columnPairs
            """)
        // TODO: how do i, ffs, get a pair of the same column. Might also be good to compare the same column
    List<InclusionDependentDatasetColumnPairsWithComparableType> getInclusionDependentDatasetColumnPairsWithComparableType(@Param("comparableInfoTypes") List<Map<String, String>> comparableInfoTypes);

    @Query("""
            MATCH (ac:Column)-[:FROM_TABLE]->(t1:Table)<-[:FROM_TABLE]-(ref:Column)<-[:IS_INCLUSION_DEPENDENT_ON]-(dep:Column)-[:FROM_TABLE]->(t1)
            WHERE (ac.infoType IN $sameTypePairs)
            RETURN
                 t1.name AS referencedDatasetName,
                 t1.name AS dependantDatasetName,
                 collect({
                    referencedDatasetColumn: ac.name,
                    dependantDatasetColumn: ac.name,
                    referencedInfoType: ac.infoType,
                    dependantInfoType: ac.infoType
                 }) AS columnPairs
            """)
    List<InclusionDependentDatasetColumnPairsWithComparableType> getInclusionDependentDatasetColumnPairsWithSameType(@Param("sameTypePairs") List<String> sameTypePairs);

    List<ColumnEntity> findAllByUniqueAndNullable(boolean unique, boolean nullable);

    List<ColumnEntity> findAllByFromTable_Name(String fromTableName);

    @Query("""
            UNWIND $comparableInfoTypes AS comparableInfoTypePair
            MATCH (c1:Column)-[:FROM_TABLE]->(t:Table {name: $dataset })<-[:FROM_TABLE]-(c2: Column)
            WHERE c1 <> c2
               AND (c1.infoType = comparableInfoTypePair.first AND c2.infoType = comparableInfoTypePair.second)
            RETURN
                c1.name as leftColumn,
                c2.name as rightColumn,
                c1.infoType as leftColumnInfoType,
                c2.infoType as rightColumnInfoType
            """)
    List<ColumnPairWithInfoTypes> getColumnPairsWithComparableTypeFromDataset(@Param("comparableInfoTypes") List<Map<String, String>> comparableInfoTypes, @Param("dataset") String datasetName);

    @Query("""
            MATCH (c:Column)-[:FROM_TABLE]->(t:Table)
            WHERE t.name IN $datasetNames
            RETURN
                t.name as datasetName,
                collect(c) as columnEntities
            """)
    List<DatasetNameWithListOfColumnEntities> getColumnsForTheseDatasets(@Param("datasetNames") List<String> dependantDatasets);

    @Query("""
            MATCH (c:Column)-[:PART_OF_FUNC_DEP_DETERMINANT]->(cc:ColumnCombination)<-[:IS_FUNCTIONALLY_DEPENDENT_ON]-(d:Column)
            RETURN
                collect(c) as columns,
                cc as columnCombination,
                d as dependant
            """)
    List<MyFD> todoGetAllFDs();
}

