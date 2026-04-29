package xyz.apolicky.constraints.columnmetadata.pattern;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.util.regex.Pattern;

@Component
public class SyntheticBusinessIdMatcher implements BasePattern {
    private static final Pattern syntheticBusinessIdPattern = Pattern.compile("[a-zA-Z0-9_-]{22}");
    private static final String patternDescription = """
            Matches business/user id's from yelp business dataset, https://www.kaggle.com/datasets/yelp-dataset/yelp-dataset
            """;

    public String getPatternName() {
        return "SyntheticBusinessIdMatcher";
    }

    @Override
    public String getPatternDescription() {
        return patternDescription;
    }

    @Override
    public boolean matchesPattern(Object candidate) {
        if (candidate instanceof String) {
            return syntheticBusinessIdPattern.matcher((String) candidate).matches();
        }
        return false;
    }

    @Override
    public boolean supports(InfoType infoType) {
        return infoType == InfoType.STRING;
    }

    @Override
    public String getPatternString() {
        return syntheticBusinessIdPattern.pattern();
    }
}