// R302 columns that have been compared and at least one value was over the threshold
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WHERE size(r.comparisonIdsOverThreshold) > 0
RETURN c1, r, c2