// R017 pattern that were matched at least once
MATCH ()-[ph:PATTERN_HOLDS]->(p:Pattern)
RETURN DISTINCT (p.name)