package xyz.apolicky.constraints.columncomparisons.comparison.substrings;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columncomparisons.comparison.NewTwoColComparisonIfce;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonOperation;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.utils.StringUtils;

@Component
public class StringSubstringWhitespacesAndDashesRemoved implements TwoColumnComparisonOperation, NewTwoColComparisonIfce {
    public static final String id = "string-substring-whitespaces-and-dashes-removed";

    private static boolean holds(Object leftObj, Object rightObj) {
        if (leftObj == null || rightObj == null) {
            return false;
        }

        var left = StringUtils.stringValue(leftObj);
        var right = StringUtils.stringValue(rightObj);

        if (left.length() < 5 || right.length() < 5) {
            return false;
        }

        var leftRemovedUnwanted = removeWhitespaces(left);
        var rightRemovedUnwanted = removeWhitespaces(right);

        return rightRemovedUnwanted.contains(leftRemovedUnwanted);
    }

    private static String removeWhitespaces(String s) {
        return s.replaceAll("([\\t\\n\\r\\s_-]|%20)", "");
    }

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
        return left == InfoType.STRING && right == InfoType.STRING;
    }

    @Override
    public RowWiseCellComparison compare(Object leftObj, Object rightObj) {
        if (holds(leftObj, rightObj)) {
            return RowWiseCellComparison.SUBSTRING_WHITESPACES_DASHED_REMOVED;
        }
        return null;
    }

    @Override
    public boolean holdsTrue(Object l, Object r) {
        return holds(l, r);
    }
}
