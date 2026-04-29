package xyz.apolicky.constraints.utils;

public class ModelNameHelpers {

    public static String getValueString(Object value) {
        return value == null ? "null" : value.toString();
    }

    public static String getValueStringWithDatasetAndKey(Object value, String datasourceName, String entityName) {
        return datasourceName + "/" + entityName + "::" + getValueString(value);
    }
}
