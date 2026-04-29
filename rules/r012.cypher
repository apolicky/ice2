// R012 columns with multiple types and no NULLs
MATCH (c:Column)
WITH
  c,
  split(replace(replace(c.infoTypeDistribution, "[", ""), "]", ""), ",") AS typeDistr
WITH
  c,
  typeDistr,
  [
    t IN typeDistr
    WHERE split(t, ":")[0] IN ["STRING", "NUMBER", "NUMBER_0_1", "DATETIME"]
    | t
  ] AS nonNullTypeDistr
WHERE size(nonNullTypeDistr) > 1
RETURN c.name, nonNullTypeDistr