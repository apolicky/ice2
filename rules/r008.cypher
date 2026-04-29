// R008 codelist tables -- 2 columns, all having distinct values
MATCH (t:Table)
WHERE
  COUNT {
    MATCH (t)<-[:FROM_TABLE]-(c:Column)
    WHERE c.unique
  } =
  2
RETURN t.name