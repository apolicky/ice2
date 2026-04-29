// R010.1 columns with matching min values
MATCH (c1:Column)-[:FROM_TABLE]->(t1), (c2:Column)-[:FROM_TABLE]->(t2)
WHERE c1.infoType = c2.infoType AND c1.min = c2.min AND c1 <> c2
RETURN c1.name, t1.name, c2.name, t2.name, c1.min, c2.min