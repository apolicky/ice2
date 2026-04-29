// R205 INDs in the same table
// x-checked-rule
MATCH (dependant:Column)-[:IS_INCLUSION_DEPENDENT_ON]->(referenced:Column)
MATCH (dependant)-[:FROM_TABLE]->(depT:Table)
MATCH (referenced)-[:FROM_TABLE]->(refT:Table)
WHERE depT = refT
RETURN referenced.name, dependant.name, refT.name AS table