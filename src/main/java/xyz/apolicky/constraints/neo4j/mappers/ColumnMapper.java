package xyz.apolicky.constraints.neo4j.mappers;

import org.neo4j.driver.internal.value.BooleanValue;
import org.neo4j.driver.internal.value.FloatValue;
import org.neo4j.driver.internal.value.IntegerValue;
import org.neo4j.driver.internal.value.StringValue;
import xyz.apolicky.constraints.columnmetadata.model.Info;
import xyz.apolicky.constraints.columnmetadata.model.InfoType;
import xyz.apolicky.constraints.neo4j.domain.ColumnEntity;
import xyz.apolicky.constraints.neo4j.domain.PatternEntity;
import xyz.apolicky.constraints.neo4j.domain.PatternMatchRelationshipProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColumnMapper {

    public static ColumnEntity fromInfo(final Info info) {
        var ce = new ColumnEntity();
        ce.setName(info.name());
        ce.setMin(info.min());
        ce.setMax(info.max());
        ce.setAverage(info.average());
        ce.setMinLength(info.minLength());
        ce.setMaxLength(info.maxLength());
        ce.setUnique(info.unique());
        ce.setNullable(info.nullable());
        ce.setCount(info.count());
        ce.setDistinctValues(info.distinctValues());
        ce.setIdCandidate(info.unique() && !info.nullable());
        ce.setInfoType(info.type());
        ce.setNullCount(info.nullCount());
        ce.setZeroCount(info.zeroCount());
        ce.setPositiveCount(info.positiveCount());
        ce.setInfoTypeDistribution(convertInfoTypeDistributionMapToString(info.typeDistribution()));
        // matching patterns need to be set afterward
        return ce;
    }

    public static Info fromColumnEntity(final ColumnEntity ce) {
        return new Info(
                ce.getName(),
                ce.getFromTable().getName(),
                neo4jValueToJavaValue(ce.getMin()),
                neo4jValueToJavaValue(ce.getMax()),
                neo4jValueToJavaValue(ce.getAverage()),
                ce.getMinLength(),
                ce.getMaxLength(),
                ce.isUnique(),
                ce.isNullable(),
                ce.getNullCount(),
                ce.getDistinctValues(),
                Map.of(), // TODO: we are not storing value distributions
                ce.getCount(),
                ce.getZeroCount(),
                ce.getPositiveCount(),
                ce.getInfoType(),
                convertInfoTypeDistributionStringToMap(ce.getInfoTypeDistribution()),
                convertPatternsHoldingTrueToMapOfCounts(ce.getHasPatternHoldingTrueWith())
        );
    }

    private static Object neo4jValueToJavaValue(final Object value) {
        if (value instanceof StringValue stringValue) {
            return stringValue.asString();
        } else if (value instanceof FloatValue floatValue) {
            return floatValue.asDouble();
        } else if (value instanceof IntegerValue integerValue) {
            return integerValue.asInt();
        } else if (value instanceof BooleanValue booleanValue) {
            return booleanValue.asBoolean();
        }

        return value;
    }

    private static Map<String, Integer> convertPatternsHoldingTrueToMapOfCounts(List<PatternMatchRelationshipProperties> b) {
        return b.stream().map(a -> Map.entry(a.getPattern().getName(), a.getCount())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static String convertInfoTypeDistributionMapToString(Map<InfoType, Integer> infoDistributionMap) {
        if (infoDistributionMap == null) {
            return "[]";
        }

        var content = infoDistributionMap.entrySet().stream()
                .map(entry -> String.format("%s:%s", entry.getKey().toString(), entry.getValue().toString()))
                .collect(Collectors.joining(","));
        return "[" + content + "]";
    }

    private static Map<InfoType, Integer> convertInfoTypeDistributionStringToMap(String infoTypeDistributionMapAsString) {
        // looks like: '[NUMBER:5,STRING:10]'
        if ("[]".equals(infoTypeDistributionMapAsString)) {
            return Map.of();
        }

        return Arrays.stream(infoTypeDistributionMapAsString.substring(1, infoTypeDistributionMapAsString.length() - 1).split(","))
                .map(kv -> {
                            var parts = kv.split(":");
                            assert parts.length == 2;
                            var type = InfoType.valueOf(parts[0]);
                            var count = Integer.parseInt(parts[1]);
                            return Map.entry(type, count);
                        }

                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static void addMatchingPatternsForColumnFromInfo(ColumnEntity ce, Info info, List<PatternEntity> patternEntities) {
        var relativePatternEntities = patternEntities.stream().filter(pe -> info.matchingPatterns().containsKey(pe.getName())).toList();
        var patternMatchRelationshipProperties = relativePatternEntities.stream().map(r -> {
            var p = new PatternMatchRelationshipProperties();
            p.setPattern(r);
            p.setCount(info.matchingPatterns().get(r.getName()));
            return p;
        }).toList();
        ce.setHasPatternHoldingTrueWith(patternMatchRelationshipProperties);
    }
}
