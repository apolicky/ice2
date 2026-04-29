// R305.2 columns compared with at least 50\% success rate for a comparison; disregard string-length comparisons
// x-checked-rule
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WITH
  c1,
  c2,
  r,
  [
    comparisonResult IN r.comparisonResults
    WHERE
      toFloat(split(comparisonResult, "~")[1]) > (toFloat(c1.count) * 0.5) AND
      substring(split(comparisonResult, "~")[0], 0, 14) <> 'string-length-'
    | comparisonResult
  ] AS comparisonResults
WHERE size(comparisonResults) > 0
RETURN c1.name, c2.name, c1.count AS numberOfComparisons, comparisonResults