package xyz.apolicky.constraints.columnmetadata.pattern;

import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.io.Serializable;

public interface BasePattern extends Serializable {
    String getPatternName();

    String getPatternDescription();

    boolean matchesPattern(Object candidate);

    boolean supports(InfoType infoType);

    String getPatternString();
}
