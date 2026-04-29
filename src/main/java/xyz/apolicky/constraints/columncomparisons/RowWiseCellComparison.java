package xyz.apolicky.constraints.columncomparisons;

// TODO: does this really need to exist?
// Couldn't we just use the IDs of comparisons holding true for each column pair though?
public enum RowWiseCellComparison {
    UNDEFINED,
    EQUAL,
    NOT_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    PREFIX,
    PREFIX_CASE_INSENSITIVE,
    SUFFIX,
    SUFFIX_CASE_INSENSITIVE,
    /**
     * strict substring, doesn't contain first and last letter of another string (otherwise it's pre-/suf-fix)
     */
    SUBSTRING,
    SUBSTRING_CASE_INSENSITIVE,
    PATTERN,
    SUBSTRING_WHITESPACES_DASHED_REMOVED,
    // number vs string length
    LENGTH_OF_STRING_IS_THE_NUMBER,
    LENGTH_OF_STRING_IS_LESS_THAN_THE_NUMBER,
    LENGTH_OF_STRING_IS_GREATER_THAN_THE_NUMBER,
    // string length
    LENGTH_OF_STRING_IS_SAME_AS_THE_OTHER,
    LENGTH_OF_STRING_IS_LESS_THAN_THE_OTHER,
    LENGTH_OF_STRING_IS_LESS_THAN_OR_EQUAL_THE_OTHER,
    LENGTH_OF_STRING_IS_GREATER_THAN_THE_OTHER,
    LENGTH_OF_STRING_IS_GREATER_THAN_OR_EQUAL_THE_OTHER,
}
