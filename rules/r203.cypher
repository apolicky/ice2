// R203 the number of INDs across distinct tables
MATCH (c1:Column)-[r:IS_INCLUSION_DEPENDENT_ON]->(c2:Column)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t2:Table)
WHERE t1 <> t2
RETURN count(r)