package xyz.apolicky.constraints.neo4j.domain;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Table")
public class TableEntity {
    @Id
    private String name;

    public TableEntity() {
    }

    public TableEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
