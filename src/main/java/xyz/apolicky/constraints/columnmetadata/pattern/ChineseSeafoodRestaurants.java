package xyz.apolicky.constraints.columnmetadata.pattern;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.util.regex.Pattern;

@Component
public class ChineseSeafoodRestaurants implements BasePattern {
    private static final Pattern chineseSeafoodRestaurantsPattern = Pattern.compile("Seafood, Restaurants, Chinese");
    private static final String patternDescription = """
            Matches such value, that is exactly `"Seafood, Restaurants, Chinese"`
            """;

    @Override
    public String getPatternName() {
        return "ChineseSeafoodRestaurants";
    }

    @Override
    public String getPatternDescription() {
        return patternDescription;
    }

    @Override
    public boolean matchesPattern(Object candidate) {
        if (candidate instanceof String) {
            return chineseSeafoodRestaurantsPattern.matcher((String) candidate).matches();
        }
        return false;
    }

    @Override
    public boolean supports(InfoType infoType) {
        return infoType == InfoType.STRING;
    }

    @Override
    public String getPatternString() {
        return chineseSeafoodRestaurantsPattern.pattern();
    }
}