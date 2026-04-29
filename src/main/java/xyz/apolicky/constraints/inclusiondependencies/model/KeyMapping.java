package xyz.apolicky.constraints.inclusiondependencies.model;

import java.io.Serializable;

public record KeyMapping(
        String referencedTable,
        String referencedColumn,
        String dependantTable,
        String dependantColumn,
        KeyMappingType keyMappingType
) implements Serializable {
}
