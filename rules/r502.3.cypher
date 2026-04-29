MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t2:Table)
WHERE
  t1 <> t2 AND
  c1.infoType = "NUMBER"
  // impl detail of how the table is written to neo4j
  AND
  size(split(r.comparedAgainstMaximums, "~")) >= 1
  // impl detail of how to get the maximum
  AND
  (NOT r.comparedAgainstMaximums CONTAINS "LESS" AND
    NOT r.comparedAgainstMaximums CONTAINS "UNKNOWN")
RETURN t1.name, c1.name, t2.name, c2.name, r