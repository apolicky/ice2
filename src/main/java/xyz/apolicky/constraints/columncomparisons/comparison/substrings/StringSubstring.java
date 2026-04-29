package xyz.apolicky.constraints.columncomparisons.comparison.substrings;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columncomparisons.comparison.NewTwoColComparisonIfce;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonOperation;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.utils.StringUtils;

@Component
public class StringSubstring implements TwoColumnComparisonOperation, NewTwoColComparisonIfce {
    public static final String id = "string-substring-strict";

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean supports(InfoType left, InfoType right) {
        return SubstringUtils.supported(left, right);
    }

    @Override
    public RowWiseCellComparison compare(Object leftObj, Object rightObj) {
        if (leftObj == null || rightObj == null) {
            return null;
        }

        var left = leftObj.toString();
        var right = rightObj.toString();

        if (!right.startsWith(left) &&
                !right.endsWith(left) &&
                right.contains(left)) {
            return RowWiseCellComparison.SUBSTRING;
        }
        return null;
    }

    @Override
    public boolean holdsTrue(Object l, Object r) {
        if (l == null || r == null) {
            return false;
        }

        var left = StringUtils.stringValue(l);
        var right = StringUtils.stringValue(r);

        return (!right.startsWith(left) &&
                !right.endsWith(left) &&
                right.contains(left));
    }
}
