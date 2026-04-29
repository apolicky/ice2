// R007.1 ID candidate columns from the same table
// x-checked-rule
MATCH (c:Column)-[:FROM_TABLE]->(t:Table)
WHERE c.idCandidate OR (c.unique = true AND c.nullCount = 0)
RETURN collect(c.name), t.name, count(c) AS idCandidatesFromTheSameTable
ORDER BY idCandidatesFromTheSameTable DESC