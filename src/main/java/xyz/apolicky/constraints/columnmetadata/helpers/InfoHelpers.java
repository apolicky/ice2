package xyz.apolicky.constraints.columnmetadata.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.apolicky.constraints.AppConfiguration;
import xyz.apolicky.constraints.columnmetadata.model.Info;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.utils.TypeComparisonUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InfoHelpers {
    private static final Logger log = LoggerFactory.getLogger(InfoHelpers.class);

    public static Object min(Object aMin, Object bMin) {
        if (aMin instanceof Number && bMin instanceof Number) {
            return Math.min(((Number) aMin).doubleValue(), ((Number) bMin).doubleValue());
        } else if (aMin instanceof String && bMin instanceof String) {
            var res = ((String) aMin).compareTo((String) bMin);
            return res < 0 ? aMin : bMin;
        } else if (aMin instanceof Date aDate && bMin instanceof Date bDate) {
            var res = (aDate).compareTo(bDate);
            return res < 0 ? aDate : bDate;
        }
        // TODO: time, datetime
        return aMin;
    }

    public static Object max(Object aMax, Object bMax) {
        if (aMax instanceof Number && bMax instanceof Number) {
            return Math.max(((Number) aMax).doubleValue(), ((Number) bMax).doubleValue());
        } else if (aMax instanceof String && bMax instanceof String) {
            var res = ((String) aMax).compareTo((String) bMax);
            return res > 0 ? aMax : bMax;
        } else if (aMax instanceof Date aDate && bMax instanceof Date bDate) {
            var res = (aDate).compareTo(bDate);
            return res > 0 ? aDate : bDate;
        }
        return aMax;
    }

    public static InfoType matchingType(Object a) {
        if (a == null) {
            return InfoType.NULL;
        } else if (a instanceof Boolean) {
            return InfoType.BOOLEAN;
        } else if (a instanceof Number) {
            if (((Number) a).doubleValue() == 0.0 || ((Number) a).doubleValue() == 1.0) {
                return InfoType.NUMBER_0_1;
            }
            return InfoType.NUMBER;
        } else if (a instanceof String) {
            return fromString((String) a);
        } else if (a instanceof Date) {
            return InfoType.DATETIME;
        }
        log.debug("What type is `a`? {}", a.getClass().getSimpleName());
        return InfoType.ANY;
    }

    public static InfoType matchingType(InfoType a, InfoType b) {
        // types are matching
        // [null, null]
        // [any, any]
        // [string, string]
        // [number, number]
        // [bool, bool]
        if (a.equals(b)) {
            return a;
        } else if (a.equals(InfoType.ANY) || b.equals(InfoType.ANY)) {
            return InfoType.ANY;
        } else if (isOneNullType(a, b)) {
            return getTheNotNull(a, b);
        } else if (TypeComparisonUtils.anyRotationHolds(InfoType.NUMBER_0_1, InfoType.NUMBER, a, b)) {
            return InfoType.NUMBER;
        } else if (a.equals(InfoType.STRING) || b.equals(InfoType.STRING)) {
            return InfoType.STRING;
        } else {
            log.debug("What should be here, dog 🐶? a:{}, b{}", a, b);
            return InfoType.ANY;
        }
    }

    public static InfoType fromString(String value) {
        if (value == null) return InfoType.NULL;

        String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            return InfoType.STRING;
        }
        if (trimmed.equalsIgnoreCase("null")) {
            return InfoType.NULL;
        }

        // Boolean
        if (trimmed.equalsIgnoreCase("true") || trimmed.equalsIgnoreCase("false")) {
            return InfoType.BOOLEAN;
        }

        // Integer or Double
        if (trimmed.matches("-?\\d+") || trimmed.matches("-?\\d+\\.\\d+")) {
            return InfoType.NUMBER;
        }

        var fmt = DateTimeFormatter.ofPattern(AppConfiguration.DATE_TIME_FORMAT);
        try {
            // Try LocalDate
            LocalDate.parse(trimmed, fmt);
            return InfoType.DATETIME;
        } catch (Exception ignored) {
        }

        try {
            LocalDateTime.parse(trimmed, fmt);
            return InfoType.DATETIME;
        } catch (Exception ignored) {
        }

        return InfoType.STRING;
    }

    public static boolean isZeroNumber(Object a) {
        if (!(a instanceof Number aNumber)) {
            return false;
        }

        return aNumber.doubleValue() == 0.0;
    }

    public static boolean isPositiveNumber(Object a) {
        if (!(a instanceof Number aNumber)) {
            return false;
        }
        return aNumber.doubleValue() > 0.0;
    }

    public static Map<String, Integer> combineMatchingPatterns(Info a, Info b) {
        var result = new HashMap<>(a.matchingPatterns());
        for (var entry : b.matchingPatterns().entrySet()) {
            var k = entry.getKey();
            if (result.computeIfPresent(k, (_k, val) -> val + entry.getValue()) == null) {
                result.put(k, entry.getValue());
            }
        }
        return result;
    }

    public static Map<String, Integer> convertMatchingPatternsToMapOfOccurrences(Set<String> patterns) {
        return patterns.stream().map(patternName -> Map.entry(patternName, 1)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static boolean isOneNullType(InfoType a, InfoType b) {
        return a.equals(InfoType.NULL) || b.equals(InfoType.NULL);
    }

    private static InfoType getTheNotNull(InfoType a, InfoType b) {
        if (a.equals(InfoType.NULL)) {
            return b;
        }
        return a;
    }

    public static Object average(Info a, Info b) {
        if (!a.type().equals(InfoType.NUMBER) || !b.type().equals(InfoType.NUMBER)) {
            return null;
        }

        var aAvg = a.average();
        var aNotNullCount = a.count() - a.nullCount();
        var bAvg = b.average();
        var bNotNullCount = b.count() - b.nullCount();

        if (aAvg == null && bAvg == null) {
            log.debug("Both cells have average as null, colA: {}, colB: {}", a.name(), b.name());
            return null;
        }

        if (aAvg == null && bAvg instanceof Number) {
            return bAvg;
        }

        if (aAvg instanceof Number && bAvg == null) {
            return aAvg;
        }

        if (aAvg instanceof Number aAvgNumber && bAvg instanceof Number bAvgNumber) {
            return (aAvgNumber.doubleValue() * aNotNullCount + bAvgNumber.doubleValue() * bNotNullCount) / (aNotNullCount + bNotNullCount);
        }

        throw new IllegalArgumentException("Average is `" + aAvg + "` and `" + bAvg + "` which are not allowed candidates for average value computation");
    }

    public static Object average(InfoType matchingType, Object value) {
        return getNumberOrReturnNull(matchingType, value);
    }

    public static Object sum(InfoType matchingType, Object value) {
        return getNumberOrReturnNull(matchingType, value);
    }

    private static Object getNumberOrReturnNull(InfoType matchingType, Object value) {
        if (value == null) {
            return null;
        }

        if (!matchingType.equals(InfoType.NUMBER)) {
            return null;
        }
        if (value instanceof Number valueNumber) {
            return valueNumber.doubleValue();
        }
        log.warn("Got value `{}` which is not an instance of number. Instance type `{}`", value, value.getClass().getSimpleName());
        // throw new IllegalArgumentException("Unsupported value for NUMBER type: " + value);
        return null;
    }

    public static Object sum(InfoType type, Object sumA, Object sumB) {
        if (!type.equals(InfoType.NUMBER)) {
            return null;
        }

        if (sumA == null && sumB == null) {
            return null;
        }

        if (sumA == null && sumB instanceof Number) {
            return sumB;
        }

        if (sumA instanceof Number && sumB == null) {
            return sumA;
        }

        if (sumA instanceof Number aSumNumber && sumB instanceof Number bSumNumber) {
            return aSumNumber.doubleValue() + bSumNumber.doubleValue();
        }

        throw new IllegalArgumentException("Average is `" + sumA + "` and `" + sumB + "` which are not allowed candidates for sum value computation");
    }
}

