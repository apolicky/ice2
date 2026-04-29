// R407 what is the highest number of row pair that have been linked together
MATCH (dep:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(ref:Column)
RETURN ref.name, dep.name, r.numberOfComparisons
ORDER BY r.numberOfComparisons DESC
LIMIT 1