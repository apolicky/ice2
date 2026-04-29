package xyz.apolicky.constraints.columncomparisons.comparison.arithmetic;

import scala.Tuple3;

public class ArithmHelpers {
    private ArithmHelpers() {
        // hidden
    }

    public static Tuple3<TUPLE_TYPE, Number, Number> prepareNumbers(Object l, Object r) {
        var left = (Number) l;
        var right = (Number) r;

        if (left == null || right == null) {
            return new Tuple3<>(TUPLE_TYPE.SOME_NULL, left, right);
        }

        return new Tuple3<>(TUPLE_TYPE.BOTH_NUMBERS, left, right);
    }

    public enum TUPLE_TYPE {
        BOTH_NUMBERS,
        SOME_NULL
    }
}
