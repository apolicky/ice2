// R013 columns having some type outliers
// x-checked-rule
WITH 0.05 AS percentageOfSmallCount
MATCH (c:Column)-[:FROM_TABLE]->(t:Table)
WITH
  c,
  t,
  split(replace(replace(c.infoTypeDistribution, "[", ""), "]", ""), ",") AS typeDistr,
  percentageOfSmallCount
WITH
  c,
  t,
  typeDistr,
  [
    t IN typeDistr
    WHERE toFloat(split(t, ":")[1]) < percentageOfSmallCount * c.count
    | t
  ] AS typesWithSmallCount
WHERE size(typesWithSmallCount) > 0
RETURN t.name, c.name, typesWithSmallCount, typeDistr