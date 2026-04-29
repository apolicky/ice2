// R009 codelist tables -- 2 columns, both from the same table; they have only a single type in one column
MATCH (c:Column)-[:FROM_TABLE]->(t:Table)
WITH t, collect(c) AS tableColumns, count(c) AS numCols
WHERE numCols = 2
WITH
  t,
  [c IN tableColumns WHERE size(split(c.infoTypeDistribution, ',')) = 1] AS columnsWithSingleType
WHERE size(columnsWithSingleType) = numCols
RETURN t.name