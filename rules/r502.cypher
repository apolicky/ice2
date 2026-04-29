// R502 such results where the value of REFerenced is greater than the maximum of all DEPs in the reference group; within single table, hierarchical
MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c1)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
WHERE
  c1.infoType = "NUMBER"
  // impl detail of how the table is written to neo4j
  AND
  size(split(r.comparedAgainstMaximums, "~")) = 1
  // impl detail of how to get the maximum
  AND
  r.comparedAgainstMaximums STARTS WITH "[GREATER_THAN:"
RETURN t1.name, c1, r