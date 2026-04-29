package xyz.apolicky.constraints.columnmetadata.pattern;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.util.regex.Pattern;

@Component
public class PhoneNumber implements BasePattern {
    private static final Pattern phoneNumberPattern = Pattern.compile("^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$");
    private static final String patternDescription = """
            Matches phone number representation, forgot where i took the patter, probably stackoverflow.
            """;

    @Override
    public String getPatternName() {
        return "PhoneNumber";
    }

    @Override
    public String getPatternDescription() {
        return patternDescription;
    }

    @Override
    public boolean matchesPattern(Object candidate) {
        if (candidate instanceof String) {
            return phoneNumberPattern.matcher((String) candidate).matches();
        }
        return false;
    }

    @Override
    public boolean supports(InfoType infoType) {
        return infoType == InfoType.STRING;
    }

    @Override
    public String getPatternString() {
        return phoneNumberPattern.pattern();
    }
}