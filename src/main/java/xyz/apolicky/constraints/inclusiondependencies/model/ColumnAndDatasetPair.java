package xyz.apolicky.constraints.inclusiondependencies.model;

import java.io.Serializable;
import java.util.Objects;

public record ColumnAndDatasetPair(
        String referencedColumnName,
        String referencedTableName,
        String dependantColumnName,
        String dependantTableName
) implements Serializable {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ColumnAndDatasetPair that = (ColumnAndDatasetPair) obj;
        return Objects.equals(this.referencedColumnName, that.referencedColumnName) &&
                Objects.equals(this.referencedTableName, that.referencedTableName) &&
                Objects.equals(this.dependantColumnName, that.dependantColumnName) &&
                Objects.equals(this.dependantTableName, that.dependantTableName);
    }
}
