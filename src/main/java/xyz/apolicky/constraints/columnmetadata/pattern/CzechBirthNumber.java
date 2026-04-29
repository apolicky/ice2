package xyz.apolicky.constraints.columnmetadata.pattern;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.util.regex.Pattern;

@Component
public class CzechBirthNumber implements BasePattern {
    private static final Pattern birthNumberPattern = Pattern.compile("^[0-9]{2}[0156][0-9][0123][0-9]/?[0-9]{4}$");
    private static final String patternDescription = """
            Matches Czech Birth Numbers, YYMMDD(/)?1234
            """;

    @Override
    public String getPatternName() {
        return "CzechBirthNumber";
    }

    @Override
    public String getPatternDescription() {
        return patternDescription;
    }

    @Override
    public boolean matchesPattern(Object candidate) {
        if (candidate instanceof String candidateString) {
            return birthNumberPattern.matcher(candidateString).matches();
        } else if (candidate instanceof Number candidateNumber) {
            var candidateAsString = (candidateNumber).toString();
            return birthNumberPattern.matcher(candidateAsString).matches();
        }
        return false;
    }

    @Override
    public boolean supports(InfoType infoType) {
        return infoType == InfoType.STRING || infoType == InfoType.NUMBER;
    }

    @Override
    public String getPatternString() {
        return birthNumberPattern.pattern();
    }
}