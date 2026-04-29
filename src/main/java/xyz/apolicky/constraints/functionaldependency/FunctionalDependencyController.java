package xyz.apolicky.constraints.functionaldependency;

import de.metanome.algorithm_integration.results.FunctionalDependency;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scala.Tuple2;
import xyz.apolicky.constraints.neo4j.service.MetadataService;
import xyz.apolicky.constraints.utils.FileUtils;
import xyz.apolicky.constraints.utils.MeasureUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Controller("/functional-dependency")
public class FunctionalDependencyController {
    private static List<FunctionalDependency> functionalDependencies;

    private final MetadataService metadataService;

    @Autowired
    public FunctionalDependencyController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @GetMapping("/single-determinant-fds")
    public Map<String, Set<String>> getSingleDeterminantFDs() {
        if (functionalDependencies == null) {
            throw new UnsupportedOperationException("No metadata found, pls run metadata scan first");
        }
        return FunctionalDependencyHelpers.getSingleDeterminantFDs(functionalDependencies);
    }

    @GetMapping("/all")
    @Operation(summary = "Step5, find Functional Dependencies")
    public Tuple2<Integer, List<FunctionalDependency>> getFunctionalDependencies(@RequestParam(name = "csvFilePaths", defaultValue = FileUtils.defaultInputFilesForRequestParam) List<String> csvFilePaths) {
        var sanitizedPaths = csvFilePaths.stream().map(FileUtils::getFilename).toList();
        MeasureUtils.measure("/fds/all", () -> {
            functionalDependencies = FunctionalDependencyFinder.findFunctionalDependencies(sanitizedPaths);
            return null;
        });
        return new Tuple2<>(functionalDependencies.size(), functionalDependencies);
    }

    @GetMapping("/identifiers-from-function-dependencies")
    public List<String> findIdentifiersWithFunctionalDependencies(@RequestParam(name = "csvFile", defaultValue = "inputs/yelp_business10k.csv") String csvFile) {
        var sanitizedPath = FileUtils.getFilename(csvFile);
        return FdIdDiscovery.findIdentifiers(sanitizedPath);
    }

    @PostMapping("/neo4j-functional-dependencies")
    @Operation(summary = "Step6, save Functional Dependencies to neo4j instance")
    public void saveFDs(@RequestParam(name = "maxFdSourceCombinationSize", defaultValue = "5") int maxFdSourceCombinationSize) throws UnsupportedOperationException {
        if (functionalDependencies == null) {
            throw new UnsupportedOperationException("No FDs found, pls run FDs scan first");
        }
        metadataService.saveFunctionalDependencies(functionalDependencies, maxFdSourceCombinationSize);
    }

    @GetMapping("/fds-with-pattern")
    public void findFdsAdheringToPattern() {
        var fds = metadataService.getFunctionalDependencies();
        FdFakeValidator.findFdsAdheringToPattern(fds);
    }
}
