// R504 how many reference groups were there? within single table, hierarchical
MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c1)
MATCH (c1)-[:FROM_TABLE]->(t:Table)
RETURN t.name, max(r.numberOfPairings)