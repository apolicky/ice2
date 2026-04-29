package xyz.apolicky.constraints.neo4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.apolicky.constraints.neo4j.service.MetadataService;

@RestController
@Controller("/neo4j-helper-controller")
public class Neo4jHelperController {

    private final MetadataService metadataService;

    @Autowired
    public Neo4jHelperController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @DeleteMapping("/all")
    public void deleteEverything() {
        metadataService.clearEverything();
    }


    @PostMapping("/patterns")
    public void initializePatternsInNeo4j() {
        metadataService.initializePatternsInGraphDb();
    }
}
