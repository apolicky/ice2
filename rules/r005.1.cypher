// R005 columns with a relatively small domain
// x-checked-rule
MATCH (c:Column)
// or threshold
WHERE c.distinctValues <= 20
RETURN c.name, c.distinctValues, c.count