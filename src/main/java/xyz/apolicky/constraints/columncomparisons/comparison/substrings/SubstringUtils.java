package xyz.apolicky.constraints.columncomparisons.comparison.substrings;

import xyz.apolicky.constraints.columnmetadata.model.InfoType;

public class SubstringUtils {
    public static boolean supported(InfoType left, InfoType right) {
        return (left == InfoType.STRING && right == InfoType.STRING) ||
                (left.equals(InfoType.NUMBER) && right.equals(InfoType.NUMBER)) ||
                (left.equals(InfoType.NUMBER) && right.equals(InfoType.STRING)) ||
                (left.equals(InfoType.STRING) && right.equals(InfoType.NUMBER));
    }
}
