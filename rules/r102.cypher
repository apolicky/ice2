// R102 ID candidates based on FDs: LHS column functionally determines every other column from the same table
// ugly but seems to work
MATCH (rhs:Column)-[:IS_FUNCTIONALLY_DEPENDENT_ON]->(lhs:ColumnCombination)
MATCH (lhs)<-[:PART_OF_FUNC_DEP_DETERMINANT]-(c:Column)
MATCH (c)-[:FROM_TABLE]->(t:Table)
WHERE size(split(lhs.name, ',')) = 1
WITH
  c AS singleColLhs,
  t,
  collect(DISTINCT rhs.name) AS rhsCollected,
  count(DISTINCT rhs.name) AS rhsCount
WHERE
  COUNT {
    MATCH (t)<-[:FROM_TABLE]-(c1:Column)
    RETURN c1
  } =
  rhsCount + 1
RETURN singleColLhs