// R208 not-implemented: INDs with up to 5 occurrences of the referenced column (up to 5 items within an order)
MATCH (dependant:Column)-[i:IS_INCLUSION_DEPENDENT_ON]->(referenced:Column)
WHERE i.cardinalityDependant.max <= 5
RETURN referenced, dependant, i.cardinalityDependant