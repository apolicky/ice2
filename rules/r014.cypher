// R014 same name columns from distinct tables
MATCH (c1:Column)-[:FROM_TABLE]->(t1:Table)
MATCH (c2:Column)-[:FROM_TABLE]->(t2:Table)
WHERE t1 <> t2 AND c1.name = c2.name
RETURN c1, t1, c2, t2