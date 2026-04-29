// R101 functionally dependant columns
WITH 3 AS maxLHSCount
MATCH (rhs:Column)-[:IS_FUNCTIONALLY_DEPENDENT_ON]->(lhs:ColumnCombination)
WHERE size(split(lhs.name, ",")) <= maxLHSCount
RETURN lhs.name, rhs.name
ORDER BY size(split(lhs.name, ",")) ASC