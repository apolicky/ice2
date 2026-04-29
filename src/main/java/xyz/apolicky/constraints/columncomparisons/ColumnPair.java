package xyz.apolicky.constraints.columncomparisons;

import java.io.Serializable;

public record ColumnPair(
        String leftColumn,
        String rightColumn
) implements Serializable {
}
