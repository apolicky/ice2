package xyz.apolicky.constraints.columncomparisons.comparison.arithmetic;

import org.springframework.stereotype.Component;
import xyz.apolicky.constraints.columncomparisons.RowWiseCellComparison;
import xyz.apolicky.constraints.columncomparisons.comparison.NewTwoColComparisonIfce;
import xyz.apolicky.constraints.columncomparisons.comparison.TwoColumnComparisonOperation;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;

@Component
public class ArithmeticGreaterThanOrEqual implements TwoColumnComparisonOperation, NewTwoColComparisonIfce {

    public static final String id = "arithm-greater-than-or-equal";

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
        // skip this comparison, use gt, lt instead.
        // use this comparison as an aggregation result
        return false;
    }

    @Override
    public RowWiseCellComparison compare(Object leftObj, Object rightObj) {
        return null;
    }

    @Override
    public boolean holdsTrue(Object l, Object r) {
        return false;
    }
}
