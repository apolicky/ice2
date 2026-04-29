// R404 how many row pairs have there been diffenent tables
// x-checked-rule
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2:Column)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t2:Table)
WHERE t1 <> t2
RETURN max(r.numberOfComparisons), t1.name, t2.name