// R207 not-implemented: INDs of type one-to-one, many-to-one, ...
MATCH (dependant:Column)-[i:IS_INCLUSION_DEPENDENT_ON]->(referenced:Column)
WHERE i.generalizedCardinality = 'many-to-one'
RETURN referenced, dependant, i.generalizedCardinality