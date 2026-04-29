// R005 columns with a relatively small domain
// x-checked-rule
MATCH (c:Column)
// or threshold
WHERE c.distinctValues <= 0.05 * c.count
RETURN c.name, c.distinctValues, c.count