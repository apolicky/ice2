// R405.1 are there any other rules than string-length-< (most common) that have been over the threshold
// x-checked-rule
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2:Column)
WHERE
  size(r.comparisonIdsOverThreshold) > 0 AND
  NOT isEmpty(
    [
      x IN r.comparisonIdsOverThreshold
      WHERE substring(x, 0, 14) <> 'string-length-'
      | x
    ])
RETURN c1.name, c2.name, r.comparisonIdsOverThreshold