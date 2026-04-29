package xyz.apolicky.constraints.columnmetadata;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scala.Tuple2;
import xyz.apolicky.constraints.columncomparisons.InfoTypePair;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonOperation;
import xyz.apolicky.constraints.columnmetadata.model.Info;
import xyz.apolicky.constraints.columnmetadata.model.MultiColumnRelationResult;
import xyz.apolicky.constraints.neo4j.service.MetadataService;
import xyz.apolicky.constraints.utils.FileUtils;
import xyz.apolicky.constraints.utils.MeasureUtils;
import xyz.apolicky.constraints.utils.SparkHelpers;

import java.util.List;
import java.util.Map;

@RestController
@Controller("/column-metadata")
public class ColumnMetadataController {
    private static final String defaultDatasetName = "inputs/yelp_business10k.csv";
    private static Map<String, Map<String, Info>> singleColumnMetadata;
    private static Tuple2<String, List<MultiColumnRelationResult>> multiColumnRelationshipMetadata;
    private static Map<String, List<MultiColumnRelationResult>> multiColumnRelationshipMetadata2;

    private final MetadataService metadataService;
    private final MultiColumnRelationDiscovery multiColumnRelationDiscovery;
    private final ColumnMetadataDiscovery columnMetadataDiscovery;

    public ColumnMetadataController(MetadataService metadataService, ColumnMetadataDiscovery columnMetadataDiscovery, MultiColumnRelationDiscovery discovery) {
        this.metadataService = metadataService;
        this.columnMetadataDiscovery = columnMetadataDiscovery;
        this.multiColumnRelationDiscovery = discovery;
    }

    @GetMapping("/single-column")
    @Operation(summary = "Step1, Single Column Metadata Scan")
    public Map<String, Map<String, Info>> singleColumnMetadata(@RequestParam(name = "csvFilePaths", defaultValue = FileUtils.defaultInputFilesForRequestParam) List<String> csvFilePaths) {
        var spark = SparkHelpers.getSparkSession("Single Column Metadata");
        MeasureUtils.measure("/single-column", () -> {
            singleColumnMetadata = columnMetadataDiscovery.getColumnMetadata(spark, csvFilePaths);
            return null;
        });
        return singleColumnMetadata;
    }

    // requires metadata to be present in neo4j
    @GetMapping("/two-columns")
    @Operation(summary = "Step3, Column Comparisons")
    public Map<String, List<MultiColumnRelationResult>> findTwoColumnRelationsWithinDataset(@RequestParam(name = "csvFilePaths", defaultValue = FileUtils.defaultInputFilesForRequestParam) List<String> csvFilePaths) {
        var spark = SparkHelpers.getSparkSession("Multi Column Relations");
        MeasureUtils.measure("/two-columns", () -> {
            multiColumnRelationshipMetadata2 = multiColumnRelationDiscovery.getTwoColumnComparisons(spark, csvFilePaths, metadataService);
            return null;
        });
        return multiColumnRelationshipMetadata2;
    }

    @GetMapping("/available-two-column-comparisons")
    public Map<InfoTypePair, List<TwoColumnComparisonOperation>> findTwoColumnRelationsWithinDataset() {
        return multiColumnRelationDiscovery.getComparisonsThatCanBeApplied();
    }

    @GetMapping("/possible-ids-from-metadata-scan")
    public List<String> getPossibleIds() {
        if (singleColumnMetadata == null) {
            return List.of("No data, run metadata scan first");
        }
        return MetadataHelpers.idCandidates(singleColumnMetadata);
    }

    @GetMapping("/neo4j-id-candidates")
    public List<Info> getIdCandidatesFromNeo() throws UnsupportedOperationException {
        return metadataService.getIdCandidates();
    }

    @PostMapping("/neo4j-column-metadata")
    @Operation(summary = "Step2, save Single Column Metadata Scan to neo4j instance")
    public void saveColumnMetadata() throws UnsupportedOperationException {
        if (singleColumnMetadata == null) {
            throw new UnsupportedOperationException("No metadata found, pls run metadata scan first");
        }
        metadataService.saveSingleColumnMetadata(singleColumnMetadata);
    }

    @PostMapping("/neo4j-two-column-relationship-metadata")
    @Operation(summary = "Step4, save Column Comparisons to neo4j instance")
    public void saveMultiColumnRelationshipMetadata() throws UnsupportedOperationException {
        if (multiColumnRelationshipMetadata2 == null) {
            throw new UnsupportedOperationException("No multi-column metadata found, pls run metadata scan first");
        }
        metadataService.saveMultiColumnRelationMetadata(multiColumnRelationshipMetadata2);
    }
}
