// R103 such FDs that have more than x LHS columns, useful for marking fake FDs.
WITH 3 AS fdMinLhsCount
MATCH (rhs:Column)-[:IS_FUNCTIONALLY_DEPENDENT_ON]->(lhs:ColumnCombination)
MATCH (lhs)<-[:PART_OF_FUNC_DEP_DETERMINANT]-(c:Column)
MATCH (c)-[:FROM_TABLE]->(t:Table)
WITH rhs, split(lhs.name, ',') AS lhsCols
// or as a variable
WHERE size(lhsCols) >= fdMinLhsCount
RETURN lhsCols, collect(DISTINCT rhs.name)
ORDER BY size(lhsCols) ASC