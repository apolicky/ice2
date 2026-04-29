// R003 unique columns
MATCH (c:Column)-[FROM_TABLE]->(t:Table)
WHERE c.unique = true
RETURN c.name, t.name