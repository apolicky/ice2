// R309 columns that wouldn't need to be even compared: A.min >> B.max
MATCH (c1:Column)-[:FROM_TABLE]->(t:Table)
MATCH (c2:Column)-[:FROM_TABLE]->(t)
WHERE c1.infoType = "NUMBER" AND c1.infoType = c2.infoType AND c1.min > c2.max
RETURN c1, c2, t