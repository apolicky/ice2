package xyz.apolicky.constraints.columncomparisons.comparison;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columncomparisons.comparison.stringlength.StringLengthComparisonGroup;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.utils.StringUtils;

@Component
public class StringLengthComparison implements TwoColumnComparisonOperation {
    public static final String id = "string-length-comparison";

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
        if (leftObj == null && rightObj == null) {
            return RowWiseCellComparison.EQUAL;
        } else if (leftObj == null || rightObj == null) {
            return null;
        }

        var left = StringUtils.stringValue(leftObj);
        var right = StringUtils.stringValue(rightObj);

        if (left.length() > right.length()) {
            return RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_THE_OTHER;
        } else if (left.length() < right.length()) {
            return RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_THE_OTHER;
        } else {
            return RowWiseCellComparison.LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER;
        }
    }
}
