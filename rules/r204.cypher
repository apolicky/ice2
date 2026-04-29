// R204 INDs regarding ID candidate columns -- possible foreign key referencing
MATCH
  (dependant:Column)-[:IS_INCLUSION_DEPENDENT_ON]->
  (referenced:Column {idCandidate: true})
MATCH (dependant)-[:FROM_TABLE]->(td)
MATCH (referenced)-[:FROM_TABLE]->(tr)
WHERE td <> tr
RETURN referenced.name, tr.name, dependant.name, td.name