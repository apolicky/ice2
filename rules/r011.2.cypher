// R011.2 columns with multiple types, i.e. typeDistribution has more than 1 element; skip NULL as type
// x-checked-rule
MATCH (c:Column)
WITH
  c,
  [
    x IN split(replace(replace(c.infoTypeDistribution, "[", ""), "]", ""), ",")
    WHERE substring(x, 0, 4) <> "NULL"
    | x
  ] AS typeDistr
WHERE size(typeDistr) > 1
RETURN c.name, typeDistr