// R406 what are the distributions of RBCCs
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2:Column)
RETURN c1.name, c2.name, r.comparisonResults