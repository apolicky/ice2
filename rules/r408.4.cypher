// R408.4 reference comparisons; string-length disregarded, with distribution
// x-checked-rule
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t1)
MATCH (c2)-[:FROM_TABLE]->(t2)
WITH
  c1,
  t1,
  c2,
  t2,
  [x IN r.comparisonResults WHERE substring(x, 0, 14) <> 'string-length-' | x] AS withoutStringLength
WHERE size(withoutStringLength) > 0
RETURN c1.name, t1.name, c2.name, t2.name, withoutStringLength