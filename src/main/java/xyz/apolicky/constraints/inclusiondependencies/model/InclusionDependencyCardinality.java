package xyz.apolicky.constraints.inclusiondependencies.model;

import xyz.apolicky.constraints.utils.ModelNameHelpers;
import xyz.apolicky.constraints.utils.ValueStringProvider;

import java.io.Serializable;
import java.util.Objects;

public record InclusionDependencyCardinality(String referencedName,
                                             String referencedDatasource,
                                             String dependantName,
                                             String dependantDatasource,
                                             Object value,
                                             int referencedCount,
                                             int dependantCount) implements Serializable, ValueStringProvider {

    public static InclusionDependencyCardinality combineByKey(InclusionDependencyCardinality a, InclusionDependencyCardinality b) {
        assert (Objects.equals(a.referencedName, b.referencedName));
        assert (Objects.equals(a.referencedDatasource, b.referencedDatasource));
        assert (Objects.equals(a.dependantName, b.dependantName));
        assert (Objects.equals(a.dependantDatasource, b.dependantDatasource));

        return new InclusionDependencyCardinality(a.referencedName, a.referencedDatasource,
                a.dependantName, a.dependantDatasource,
                a.value,
                a.referencedCount + b.referencedCount, a.dependantCount + b.dependantCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.referencedName
                + this.dependantName + this.referencedDatasource + this.dependantDatasource + this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InclusionDependencyCardinality that = (InclusionDependencyCardinality) obj;
        return this.hashCode() == that.hashCode(); //Objects.equals(this.value, that.value) && (Objects.equals());
    }

    @Override
    public String getValueString() {
        return referencedDatasource + "_" + dependantDatasource + "/" + referencedName + "_" + dependantName + "::" + ModelNameHelpers.getValueString(value);
    }
}