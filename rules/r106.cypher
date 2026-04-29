// R106 not-implemented: FDs with more than one FD mapping function
WITH 2 AS atLeastXMappingFunctions
MATCH (rhs:Column)-[fd:IS_FUNCTIONALLY_DEPENDENT_ON]->(lhs:ColumnCombination)
WITH
  rhs,
  lhs,
  [
    fdFunction IN keys(properties(fd))
    WHERE properties(fd)[fdFunction] = true
    | fdFunction
  ] AS trueMappingFunctions
// or some other number
WHERE size(trueMappingFunctions) >= atLeastXMappingFunctions
RETURN lhs.name, rhs.name, trueMappingFunctions