package xyz.apolicky.constraints.neo4j.domain;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Pattern")
public class PatternEntity {
    @Id
    private String name;
    private String description;
    private String patternString;

    public PatternEntity() {
        // empty for spring data adapter
    }

    public PatternEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPatternString() {
        return patternString;
    }
    
    public void setPatternString(String patternString) {
        this.patternString = patternString;
    }
}
