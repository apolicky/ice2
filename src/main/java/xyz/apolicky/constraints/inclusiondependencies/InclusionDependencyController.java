package xyz.apolicky.constraints.inclusiondependencies;

import de.metanome.algorithm_integration.results.InclusionDependency;
import io.swagger.v3.oas.annotations.Operation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scala.Tuple2;
import xyz.apolicky.constraints.columnmetadata.model.Info;
import xyz.apolicky.constraints.inclusiondependencies.aggregation.AggregatedReferencedToDependantAggregationResult;
import xyz.apolicky.constraints.inclusiondependencies.aggregation.AggregatedStatisticsComparator;
import xyz.apolicky.constraints.inclusiondependencies.comparisons.ReferencedPairsComparator;
import xyz.apolicky.constraints.inclusiondependencies.comparisons.RowComparisonResult;
import xyz.apolicky.constraints.inclusiondependencies.model.ColumnPairWithTheirInfoTypes;
import xyz.apolicky.constraints.neo4j.model.DatasetNamePair;
import xyz.apolicky.constraints.neo4j.service.InclusionDependencyService;
import xyz.apolicky.constraints.utils.FileUtils;
import xyz.apolicky.constraints.utils.MeasureUtils;
import xyz.apolicky.constraints.utils.SparkHelpers;

import java.util.List;
import java.util.Map;

import static xyz.apolicky.constraints.utils.FileUtils.defaultInputFilesForRequestParam;

@RestController
@Controller("/inclusion-dependency-controller")
public class InclusionDependencyController {
    private static List<InclusionDependency> inclusionDependencies;
    private static List<RowComparisonResult> perRowComparisonResults;
    private static List<AggregatedReferencedToDependantAggregationResult> aggregatedReferencedToDependantAggregationResults;

    private final InclusionDependencyService inclusionDependencyService;
    private final ReferencedPairsComparator referencedPairsComparator;
    private final AggregatedStatisticsComparator aggregatedStatisticsComparator;

    @Autowired
    public InclusionDependencyController(InclusionDependencyService inclusionDependencyService, ReferencedPairsComparator referencedPairsComparator, AggregatedStatisticsComparator aggregatedStatisticsComparator) {
        this.inclusionDependencyService = inclusionDependencyService;
        this.referencedPairsComparator = referencedPairsComparator;
        this.aggregatedStatisticsComparator = aggregatedStatisticsComparator;
    }

    @GetMapping("/inclusion-dependencies")
    @Operation(summary = "Step7, find Inclusion Dependencies")
    public Tuple2<Integer, List<InclusionDependency>> findInclusionDependencies(@RequestParam(name = "csvFilePaths", defaultValue = defaultInputFilesForRequestParam) List<String> csvFilePaths) {
        var sanitizedPaths = csvFilePaths.stream().map(FileUtils::getFilename).toList();
        MeasureUtils.measure("/inclusion-dependencies", () -> {
            inclusionDependencies = InclusionDependencyFinder.findInclusionDependencies(sanitizedPaths);
            return null;
        });
        return new Tuple2<>(inclusionDependencies.size(), inclusionDependencies);
    }

    @GetMapping("/IND-based-column-pairs-with-comparable-types")
    public Map<DatasetNamePair, @NotNull List<ColumnPairWithTheirInfoTypes>> getInclusionDependentDatasetColumnPairsWithComparableType() {
        return inclusionDependencyService.getInclusionDependentDatasetColumnPairsWithComparableType();
    }

    @GetMapping("/neo4j-inclusion-dependencies")
    public List<Info> findInclusionDependenciesFromNeo4j() {
        return inclusionDependencyService.getColumnsInclusionDependentOnIdCandidates();
    }

    @GetMapping("/IND-per-row-comparisons")
    @Operation(summary = "Step10, find Reference-based Column Comparison results")
    public Tuple2<Integer, List<RowComparisonResult>> getPerRowComparisonsFromInclusionDependencies(@RequestParam(name = "idCandidateMustBeString", defaultValue = "false") Boolean idCandidateMustBeString) {
        var spark = SparkHelpers.getSparkSession("Inclusion Dependency: Per-Row Comparisons");
        MeasureUtils.measure("/IND-per-row-comparisons", () -> {
            perRowComparisonResults = referencedPairsComparator.compareCellsOfRowsPairedByInclusionDependencies(spark, idCandidateMustBeString).collect();
            return null;
        });
        return new Tuple2<>(perRowComparisonResults.size(), perRowComparisonResults);
    }

    @GetMapping("/IND-per-row-comparisons---number-of-pairings")
    @Operation(summary = "Step9, figure out how many row-pairs are there for given dataset. Table names, etc. are taken from neo4j")
    public Long getNumberOfPairingsInIndBasedComparisons(@RequestParam(name = "idCandidateMustBeString", defaultValue = "false") Boolean idCandidateMustBeString) {
        var spark = SparkHelpers.getSparkSession("Inclusion Dependency: Number of Per-Row Comparisons");
        return MeasureUtils.measure("/IND-per-row-comparisons---number-of-pairings", () ->
                referencedPairsComparator.getNumberOfIndBasedPairingsInGivenDataset(spark, idCandidateMustBeString)
        );
    }

    @GetMapping("/IND-aggregated-ref-to-all-dependant-comparisons")
    @Operation(summary = "Step12, find Reference-based Aggregated Column Comparison results")
    public Tuple2<Integer, List<AggregatedReferencedToDependantAggregationResult>> getAggregatedComparisonsReferencedToAllDependantFromInclusionDependencies(@RequestParam(name = "idCandidateMustBeString", defaultValue = "false") Boolean idCandidateMustBeString) {
        var spark = SparkHelpers.getSparkSession("Inclusion Dependency: Aggregated Comparisons of Referenced to all Dependant");
        MeasureUtils.measure("/IND-aggregated-ref-to-all-dependant-comparisons", () -> {
            aggregatedReferencedToDependantAggregationResults = aggregatedStatisticsComparator.compareReferencedRowCellsAgainstAggregatedResultsOfReferencingRows(spark, idCandidateMustBeString).collect();
            return null;
        });
        return new Tuple2<>(aggregatedReferencedToDependantAggregationResults.size(), aggregatedReferencedToDependantAggregationResults);
    }

    @PostMapping("/neo4j-inclusion-dependencies")
    @Operation(summary = "Step8, save Inclusion Dependencies to neo4j instance")
    public void saveInclusionDependencies() throws UnsupportedOperationException {
        if (inclusionDependencies == null) {
            throw new UnsupportedOperationException("No INDs found, pls run InclusionDependency scan first");
        }
        inclusionDependencyService.saveInclusionDependencies(inclusionDependencies);
    }

    @PostMapping("/IND-per-row-comparisons")
    @Operation(summary = "Step11, save Reference-based Column Comparison results to neo4j instance")
    public void savePerRowComparisonsFromInclusionDependencies() {
        if (perRowComparisonResults == null) {
            throw new UnsupportedOperationException("No per-row results found, pls run scan first");
        }
        inclusionDependencyService.savePerRowComparisonResults(perRowComparisonResults);
    }

    @PostMapping("/IND-aggregated-ref-to-all-dependant-comparisons")
    @Operation(summary = "Step13, save Reference-based Aggregated Column Comparison results to neo4j instance")
    public void saveAggregatedComparisonsReferencedToAllDependantFromInclusionDependencies() {
        if (aggregatedReferencedToDependantAggregationResults == null) {
            throw new UnsupportedOperationException("No aggregated-ref-to-all-dependant-comparison results found, pls run scan first");
        }
        inclusionDependencyService.saveAggregatedComparisonsReferencedToAllDependantFromInclusionDependencies(aggregatedReferencedToDependantAggregationResults);
    }
}
