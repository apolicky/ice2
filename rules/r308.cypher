// R308 outlier columns with 2 distinct arithmetic comparison results, i.e. <: 98\%, >=: 2\%. Here threshold max 10\%
// x-checked-rule
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WITH
  c1,
  c2,
  r,
  [a IN r.comparisonResults WHERE a STARTS WITH "arithm" | a] AS arithmResults
WHERE size(arithmResults) = 2
// only such pairs that have 2 distinct 'arithm' results
WITH
  c1,
  c2,
  r,
  arithmResults,
  toFloatList([split(arithmResults[0], '~')[1], split(arithmResults[1], '~')[1]]) AS countsFirstAndSecond
// parse the map from weird format
UNWIND countsFirstAndSecond AS c
WITH c1, c2, r, arithmResults, min(c) / sum(c) AS percentualCountOfLessFrequent
WHERE percentualCountOfLessFrequent < 0.1
RETURN c1, r, c2, percentualCountOfLessFrequent, arithmResults