package xyz.apolicky.constraints.columncomparisons.comparison.arithmetic;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columncomparisons.comparison.NewTwoColComparisonIfce;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonOperation;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

@Component
public class ArithmeticLessThan implements TwoColumnComparisonOperation, NewTwoColComparisonIfce {

    public static final String id = "arithm-less-than";

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
        return (left.equals(InfoType.NUMBER) && right.equals(InfoType.NUMBER)) ||
                (left.equals(InfoType.NUMBER_0_1) && right.equals(InfoType.NUMBER)) ||
                (left.equals(InfoType.NUMBER) && right.equals(InfoType.NUMBER_0_1));
    }

    @Override
    public RowWiseCellComparison compare(Object leftObj, Object rightObj) {
        return null;
    }

    @Override
    public boolean holdsTrue(Object l, Object r) {
        var tupleTypeAndNumbers = ArithmHelpers.prepareNumbers(l, r);
        if (tupleTypeAndNumbers._1().equals(ArithmHelpers.TUPLE_TYPE.SOME_NULL)) {
            return false;
        }

        var left = tupleTypeAndNumbers._2();
        var right = tupleTypeAndNumbers._3();

        return left.doubleValue() < right.doubleValue();
    }
}
