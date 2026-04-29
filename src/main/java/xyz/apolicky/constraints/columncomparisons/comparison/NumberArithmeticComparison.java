package xyz.apolicky.constraints.columncomparisons.comparison;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

@Component
public class NumberArithmeticComparison implements TwoColumnComparisonOperation {

    @Override
    public String getId() {
        return "number-to-number-arithmetic-comparison";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean supports(InfoType left, InfoType right) {
        return (left.equals(InfoType.NUMBER) && right.equals(InfoType.NUMBER)) ||
                (left.equals(InfoType.NUMBER_0_1) && right.equals(InfoType.NUMBER)) ||
                (left.equals(InfoType.NUMBER) && right.equals(InfoType.NUMBER_0_1));
    }

    @Override
    public RowWiseCellComparison compare(Object leftObj, Object rightObj) {
        if (leftObj == null && rightObj == null) {
            return RowWiseCellComparison.EQUAL;
        } else if (leftObj == null || rightObj == null) {
            return null;
        }

        var left = (Number) leftObj;
        var right = (Number) rightObj;

        if (left.doubleValue() == right.doubleValue()) {
            return RowWiseCellComparison.EQUAL;
        } else if (left.doubleValue() < right.doubleValue()) {
            return RowWiseCellComparison.LESS_THAN;
        } else if (left.doubleValue() > right.doubleValue()) {
            return RowWiseCellComparison.GREATER_THAN;
        }

        return null;
    }
}
