// R306 columns with both arithmetic '<' and string length '<". Indicates the columns are of different orders of magnitude
// x-checked-rule
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WHERE
  ('arithm-less-than' IN r.comparisonIdsOverThreshold) AND
  ('string-length-less-than' IN r.comparisonIdsOverThreshold)
RETURN c1, r, c2