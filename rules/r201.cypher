// R201 IND relevant column pairs, c1's domain is a subset of c2
MATCH (c1)-[r:IS_INCLUSION_DEPENDENT_ON]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t1)
MATCH (c2)-[:FROM_TABLE]->(t2)
RETURN c1.name, t1.name, c2.name, t2.name