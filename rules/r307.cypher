// R307 columns with 2 distinct arithmetic comparison results, i.e. <: 98\%, >=: 2\%
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WITH
  c1,
  c2,
  r,
  [a IN r.comparisonResults WHERE a STARTS WITH "arithm" | a] AS arithmResults
WHERE size(arithmResults) > 1
RETURN c1, r, c2, arithmResults