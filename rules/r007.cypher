// R007 ID candidate columns
MATCH (c:Column)-[:FROM_TABLE]->(t:Table)
WHERE c.idCandidate OR (c.unique = true AND c.nullCount = 0)
RETURN c.name, t.name