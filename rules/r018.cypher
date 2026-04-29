// R018 matching patterns for columns, provide the percentage of the match
MATCH (c:Column)-[ph:PATTERN_HOLDS]->(p:Pattern)
MATCH (c)-[:FROM_TABLE]->(t:Table)
RETURN c.name, t.name, p.name, toFloat(ph.count) / c.count AS matchPercentage