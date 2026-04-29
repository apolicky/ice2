// R505 how many reference groups were there? across tables
// x-checked-rule
MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c2:Column)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t2:Table)
WHERE t1 <> t2
RETURN
  t1.name,
  t2.name,
  max(r.numberOfPairings) AS distinctValuesThatWereReferenced