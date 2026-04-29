package xyz.apolicky.constraints.columncomparisons.comparison;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columncomparisons.comparison.stringlength.StringLengthComparisonGroup;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

@Component
public class StringEqualityComparison implements TwoColumnComparisonOperation, NewTwoColComparisonIfce {
    public static final String id = "string-equality";

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
        return StringLengthComparisonGroup.isSupportedForStringLengthComparison(left, right);
    }

    @Override
    public RowWiseCellComparison compare(Object leftObj, Object rightObj) {
        if (leftObj == null || rightObj == null) {
            return null;
        }

        var left = leftObj.toString();
        var right = rightObj.toString();

        if (left.contentEquals(right)) {
            return (RowWiseCellComparison.EQUAL);
        }
        return null;
    }

    @Override
    public boolean holdsTrue(Object l, Object r) {
        if (l == null || r == null) {
            return false;
        }

        var left = l.toString();
        var right = r.toString();

        return left.contentEquals(right);
    }
}
