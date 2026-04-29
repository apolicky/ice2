package xyz.apolicky.constraints.utils;

public class StringUtils {
    public static String stringValue(Object obj) {
        if (obj instanceof Double d) {
            // Check if the double value is equal to its floor (integer part)
            // And also check for infinite or NaN values
            if (d == Math.floor(d) && !Double.isInfinite(d) && !Double.isNaN(d)) {
                // It's effectively an integer, so cast to long and convert to string
                return String.valueOf(d.longValue());
            } else {
                // It has a fractional part, convert as is
                return String.valueOf(d);
            }
        } else if (obj == null) {
            return null;
        } else {
            return obj.toString();
        }
    }
}
