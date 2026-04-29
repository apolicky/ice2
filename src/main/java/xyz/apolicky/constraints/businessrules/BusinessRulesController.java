package xyz.apolicky.constraints.businessrules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.apolicky.constraints.columnmetadata.model.Info;
import xyz.apolicky.constraints.neo4j.service.BusinessRulesService;

import java.util.List;

@RestController
@Controller("/business-rules")
public class BusinessRulesController {

    private final BusinessRulesService businessRulesService;

    @Autowired
    public BusinessRulesController(BusinessRulesService businessRulesService) {
        this.businessRulesService = businessRulesService;
    }

    @GetMapping("/id-candidates")
    public List<Info> getIdCandidates() {
        return businessRulesService.getIdentifierCandidates();
    }
}
