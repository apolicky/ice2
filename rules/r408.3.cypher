// R408.3 reference comparisons; string-length disregarded, over threshold
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t1)
MATCH (c2)-[:FROM_TABLE]->(t2)
WITH
  c1,
  c2,
  t1,
  t2,
  [
    x IN r.comparisonIdsOverThreshold
    WHERE substring(x, 0, 14) <> 'string-length-'
    | x
  ] AS comparisonsOverThreshold
WHERE c1 <> c2 AND NOT isEmpty(comparisonsOverThreshold)
RETURN c1.name, t1.name, c2.name, t2.name, comparisonsOverThreshold