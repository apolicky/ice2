package xyz.apolicky.constraints.inclusiondependencies.model;

import org.apache.spark.sql.Row;

import java.io.Serializable;

public record CustomRow(
        Row originalRow,
        String datasetName,
        KeyMappingType keyMappingType) implements Serializable {
}
