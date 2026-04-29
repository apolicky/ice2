// R402 how many row pairs have there been in a singe table
// x-checked-rule
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t:Table)
MATCH (c2)-[:FROM_TABLE]->(t)
RETURN t.name, r.numberOfComparisons