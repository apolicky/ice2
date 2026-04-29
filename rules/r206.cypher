// R206 INDs in the same table + the referenced column is an ID candidate -- possible hierarchical relationship
MATCH (dependant:Column)-[:IS_INCLUSION_DEPENDENT_ON]->(referenced:Column)
MATCH (dependant)-[:FROM_TABLE]->(depT:Table)
MATCH (referenced)-[:FROM_TABLE]->(refT:Table)
WHERE depT = refT AND referenced.idCandidate = true
RETURN referenced.name, dependant.name, refT.name AS table