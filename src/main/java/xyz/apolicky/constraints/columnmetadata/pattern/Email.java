package xyz.apolicky.constraints.columnmetadata.pattern;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

import java.util.regex.Pattern;

@Component
public class Email implements BasePattern {
    private static final Pattern owaspEmailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final String patternDescription = """
            Matches email representation based on owasp rules, see https://owasp.org/www-community/OWASP_Validation_Regex_Repository
            """;

    @Override
    public String getPatternName() {
        return "Email";
    }

    @Override
    public String getPatternDescription() {
        return patternDescription;
    }

    @Override
    public boolean matchesPattern(Object candidate) {
        if (candidate instanceof String) {
            return owaspEmailPattern.matcher((String) candidate).matches();
        }
        return false;
    }

    @Override
    public boolean supports(InfoType infoType) {
        return infoType == InfoType.STRING;
    }

    @Override
    public String getPatternString() {
        return owaspEmailPattern.pattern();
    }
}