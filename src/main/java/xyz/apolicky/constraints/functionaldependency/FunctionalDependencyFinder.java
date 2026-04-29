package xyz.apolicky.constraints.functionaldependency;

import de.metanome.algorithm_integration.result_receiver.FunctionalDependencyResultReceiver;
import de.metanome.algorithm_integration.results.FunctionalDependency;
import de.metanome.algorithms.hyfd.HyFD;
import de.metanome.backend.input.file.DefaultFileInputGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionalDependencyFinder {

    private static final Logger log = LoggerFactory.getLogger(FunctionalDependencyFinder.class);

    public static Tuple2<List<String>, List<FunctionalDependency>> findFunctionalDependencies(String csvPath) {
        HyFD hyfd = new HyFD();
        File inputFile = new File(csvPath);
        List<String> columnNames;
        List<FunctionalDependency> fds = new ArrayList<>();
        var resultReceiver = buildResultReceiver(fds);

        try (DefaultFileInputGenerator inputGenerator = new DefaultFileInputGenerator(inputFile)) {
            hyfd.setRelationalInputConfigurationValue(HyFD.Identifier.INPUT_GENERATOR.name(), inputGenerator);
            hyfd.setResultReceiver(resultReceiver);
            hyfd.execute();
            columnNames = inputGenerator.generateNewCopy().columnNames();
        } catch (Exception e) {
            log.error("Error reading file {}", inputFile.getAbsolutePath(), e);
            return new Tuple2<>(null, null);
        }
        return new Tuple2<>(columnNames, fds);
    }

    public static List<FunctionalDependency> findFunctionalDependencies(List<String> csvPaths) {
        HyFD hyfd = new HyFD();

        // Convert filePaths to DefaultFileInputGenerator instances
        var generators = csvPaths.stream()
                .map(filePath -> {
                    try {
                        // Create a new DefaultFileInputGenerator for each file path
                        return new DefaultFileInputGenerator(new File(filePath));
                    } catch (FileNotFoundException e) {
                        log.error("Error creating a new fileInputGenerator for file {}", filePath, e);
                        return null; // TODO: Handle exception appropriately
                    }
                }).toArray(DefaultFileInputGenerator[]::new);

        log.info("Got {} Generators: [{}]", generators.length, Arrays.stream(generators).map(Object::toString).collect(Collectors.joining(", ")));

        List<FunctionalDependency> fds = new ArrayList<>();
        var resultReceiver = buildResultReceiver(fds);

        try {
            hyfd.setRelationalInputConfigurationValue(HyFD.Identifier.INPUT_GENERATOR.name(), generators);
            log.info("just set relational input generators: {}", hyfd);
            hyfd.setResultReceiver(resultReceiver);
            hyfd.execute();
        } catch (Exception e) {
            log.error("Failed to execute HyFD: {}", hyfd, e);
            return List.of();
        }
        return fds;
    }

    private static FunctionalDependencyResultReceiver buildResultReceiver(List<FunctionalDependency> fds) {
        return new FunctionalDependencyResultReceiver() {
            @Override
            public void receiveResult(FunctionalDependency functionalDependency) {
                fds.add(functionalDependency);
            }

            @Override
            public Boolean acceptedResult(FunctionalDependency functionalDependency) {
                return true;
            }
        };
    }
}
