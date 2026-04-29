// R503.1 such results where the value of REFerenced is greater than the maximum of all DEPs in the reference group; across tables
MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t2:Table)
WHERE
  t1 <> t2 AND
  c1.infoType = "NUMBER" AND
  c2.infoType = "NUMBER" AND
  size(split(r.comparedAgainstMaximums, "~")) = 1 AND
  r.comparedAgainstMaximums STARTS WITH "[GREATER_THAN:"
RETURN
  t1.name,
  c1.name,
  t2.name,
  c2.name,
  r.numberOfPairings,
  r.comparedAgainstMaximums