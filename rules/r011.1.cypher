// R011.1 columns with multiple types, i.e. typeDistribution has more than 1 element
// x-checked-rule
MATCH (c:Column)
WITH
  c,
  split(replace(replace(c.infoTypeDistribution, "[", ""), "]", ""), ",") AS typeDistr
WHERE size(typeDistr) > 1
RETURN c.name, typeDistr