// R303.2 columns with comparison X over threshold
WITH 'arithm-greater-than' AS operation
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
// any of 'string-length-greater-than', 'arithm-less-than', 'arithm-greater-than', ...
WHERE operation IN r.comparisonIdsOverThreshold
RETURN c1, r, c2