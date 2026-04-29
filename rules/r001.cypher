// R001, columns from table T
MATCH (c1:Column)-[FROM_TABLE]->(t:Table)
RETURN c1.name, t.name
// or only return `c` for the whole node representation