package xyz.apolicky.constraints.columncomparisons.comparison;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

@Component
public class NumberComparedToStringLength implements TwoColumnComparisonOperation, NewTwoColComparisonIfce {

    @Override
    public String getId() {
        return "number-compared-to-string-length";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean supports(InfoType left, InfoType right) {
        return (left.equals(InfoType.NUMBER) || left.equals(InfoType.NUMBER_0_1)) && right.equals(InfoType.STRING);
    }

    @Override
    public RowWiseCellComparison compare(Object leftObj, Object rightObj) {
        if (leftObj == null && rightObj == null) {
            return RowWiseCellComparison.EQUAL;
        } else if (leftObj == null || rightObj == null) {
            return null;
        }

        var left = (Number) leftObj;
        var right = (String) rightObj;

        var rightLength = right.length();

        if (left.doubleValue() == rightLength) {
            return RowWiseCellComparison.LENGTH_OF_STRING_IS_THE_NUMBER;
        } else if (left.doubleValue() < rightLength) {
            return RowWiseCellComparison.LENGTH_OF_STRING_IS_LESS_THAN_THE_NUMBER;
        } else if (left.doubleValue() > rightLength) {
            return RowWiseCellComparison.LENGTH_OF_STRING_IS_GREATER_THAN_THE_NUMBER;
        }

        return RowWiseCellComparison.UNDEFINED;
    }

    @Override
    public boolean holdsTrue(Object l, Object r) {
        return false;
    }
}
