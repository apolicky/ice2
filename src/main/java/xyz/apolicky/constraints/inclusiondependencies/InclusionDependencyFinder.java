package xyz.apolicky.constraints.inclusiondependencies;

import de.metanome.algorithm_integration.result_receiver.InclusionDependencyResultReceiver;
import de.metanome.algorithm_integration.results.InclusionDependency;
import de.metanome.algorithms.spider.SPIDERFile;
import de.metanome.backend.input.file.DefaultFileInputGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class InclusionDependencyFinder {
    private static final Logger log = LoggerFactory.getLogger(InclusionDependencyFinder.class);

    public static List<InclusionDependency> findInclusionDependencies(List<String> csvFilePaths) {
        var generators = csvFilePaths.stream().map(File::new).map(inputFile -> {
            try {
                return new DefaultFileInputGenerator(inputFile);
            } catch (FileNotFoundException e) {
                log.error("Failed to create fileInputGenerator for file {}", inputFile, e);
                return null;
            }
        }).toArray(DefaultFileInputGenerator[]::new);

        var spiderFile = new SPIDERFile();
        List<InclusionDependency> inclusionDependencies = new ArrayList<>();
        var resultReceiver = buildResultReceiver(inclusionDependencies);

        try {
            spiderFile.setRelationalInputConfigurationValue(SPIDERFile.Identifier.INPUT_FILES.name(), generators);
            spiderFile.setResultReceiver(resultReceiver);
            spiderFile.execute();
        } catch (Exception e) {
            log.error("Failed execute SPIDER", e);
            return List.of();
        }

        return inclusionDependencies;
    }

    private static InclusionDependencyResultReceiver buildResultReceiver(List<InclusionDependency> inclusionDependencies) {
        return new InclusionDependencyResultReceiver() {
            @Override
            public void receiveResult(InclusionDependency inclusionDependency) {
                inclusionDependencies.add(inclusionDependency);
            }

            @Override
            public Boolean acceptedResult(InclusionDependency inclusionDependency) {
                return true;
            }
        };
    }
}
