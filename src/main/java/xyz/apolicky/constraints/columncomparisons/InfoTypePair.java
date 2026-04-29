package xyz.apolicky.constraints.columncomparisons;

import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.io.Serializable;

public record InfoTypePair(
        InfoType left,
        InfoType right
) implements Serializable {
}
