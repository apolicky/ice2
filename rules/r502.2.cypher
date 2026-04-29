// R502.2 such results where the value of REFerenced is greater than or equal the maximum of all DEPs in the reference group; within single table, hierarchical
// x-checked-rule
MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t1)
WHERE
  c1.infoType = "NUMBER"
  // impl detail of how the table is written to neo4j
  AND
  size(split(r.comparedAgainstMaximums, "~")) >= 1
  // impl detail of how to get the maximum
  AND
  (NOT r.comparedAgainstMaximums CONTAINS "LESS" AND
    NOT r.comparedAgainstMaximums CONTAINS "UNKNOWN")
RETURN t1.name, c1, r