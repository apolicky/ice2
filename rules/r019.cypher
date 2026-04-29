// R019 personal contact information found
// x-checked-rule
MATCH (c:Column)-[ph:PATTERN_HOLDS]->(p:Pattern)
MATCH (c)-[:FROM_TABLE]->(t:Table)
WHERE p.name IN ["Email", "PhoneNumber"]
RETURN c.name, t.name, p.name, ph.count AS numberOfOccurrences