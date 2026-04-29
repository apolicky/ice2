// R304 columns with more than one comparison over threshold
// x-checked-rule
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WHERE size(r.comparisonIdsOverThreshold) > 1
RETURN c1, r, c2