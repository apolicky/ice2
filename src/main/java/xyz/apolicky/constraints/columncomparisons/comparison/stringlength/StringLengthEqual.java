package xyz.apolicky.constraints.columncomparisons.comparison.stringlength;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columncomparisons.comparison.NewTwoColComparisonIfce;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonOperation;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.utils.StringUtils;

@Component
public class StringLengthEqual implements TwoColumnComparisonOperation, NewTwoColComparisonIfce {
    public static final String id = "string-length-equal";

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
        return null;
    }

    @Override
    public boolean holdsTrue(Object l, Object r) {
        if (l == null || r == null) {
            return false;
        }

        var left = StringUtils.stringValue(l);
        var right = StringUtils.stringValue(r);

        return left.length() == right.length();
    }
}
