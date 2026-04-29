// R405 are there any rules that have been over the threshold
// x-checked-rule
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2:Column)
WHERE size(r.comparisonIdsOverThreshold) > 0
RETURN c1.name, c2.name, r.comparisonIdsOverThreshold