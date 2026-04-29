// R408.1 hierarchical comparisons in the same column; string-length disregarded, over threshold
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c1)
WHERE
  size(r.comparisonIdsOverThreshold) > 0 AND
  NOT isEmpty(
    [
      x IN r.comparisonIdsOverThreshold
      WHERE substring(x, 0, 14) <> 'string-length-'
      | x
    ])
RETURN c1.name, r.comparisonIdsOverThreshold