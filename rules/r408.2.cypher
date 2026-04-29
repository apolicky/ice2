// R408.2 hierarchical comparisons in the same column; string-length disregarded, with distribution
// x-checked-rule
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c1)
WITH
  c1,
  r,
  [x IN r.comparisonResults WHERE substring(x, 0, 14) <> 'string-length-' | x] AS withoutStringLength
WHERE size(withoutStringLength) > 0
RETURN c1.name, r.numberOfComparisons, withoutStringLength