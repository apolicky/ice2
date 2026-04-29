// R004 columns of matching type
WITH 'NUMBER' AS searchedType
MATCH (c:Column)
WHERE c.infoType = searchedType
RETURN c.name