package xyz.apolicky.constraints.neo4j.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.apolicky.constraints.columnmetadata.model.Info;
import xyz.apolicky.constraints.neo4j.mappers.ColumnMapper;
import xyz.apolicky.constraints.neo4j.repository.ColumnRepository;

import java.util.List;

@Service
public class BusinessRulesService {

    private final ColumnRepository columnRepository;

    @Autowired
    public BusinessRulesService(ColumnRepository repo) {
        this.columnRepository = repo;
    }

    public List<Info> getIdentifierCandidates() {
        return columnRepository.findAllByUniqueAndNullable(true, false).stream().map(ColumnMapper::fromColumnEntity).toList();
    }
}

