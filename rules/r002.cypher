// R002 the number of tables in given dataset
// expects neo4j to only contain tables of the dataset
MATCH (t:Table)
RETURN count(t)