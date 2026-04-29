// R301 comparable columns
WITH [
  {first: "BOOLEAN", second: "BOOLEAN"},
  {first: "BOOLEAN", second: "NUMBER_0_1"},
  {first: "BOOLEAN", second: "NUMBER"},
  {first: "NUMBER_0_1", second: "NUMBER_0_1"},
  {first: "NUMBER_0_1", second: "NUMBER"},
  {first: "NUMBER", second: "NUMBER"},
  {first: "NUMBER", second: "STRING"},
  {first: "STRING", second: "STRING"}
] AS comparableInfoTypes
UNWIND comparableInfoTypes AS comparableInfoTypePair
MATCH (c1:Column)-[:FROM_TABLE]->(t:Table)<-[:FROM_TABLE]-(c2:Column)
WHERE
  c1 <> c2 AND
  (c1.infoType = comparableInfoTypePair.first AND
    c2.infoType = comparableInfoTypePair.second)
RETURN
  t.name,
  c1.name AS leftColumn,
  c2.name AS rightColumn,
  c1.infoType AS leftColumnInfoType,
  c2.infoType AS rightColumnInfoType