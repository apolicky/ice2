// R105 not-implemented: such columns that have been mapped by the following FD mapping function (assuming there are a lot of them)
WITH ["mappingConvertingEmailToNameByRemovingAtAndSplittingByComma"] AS searchedForFunctions
MATCH (rhs:Column)-[fd:IS_FUNCTIONALLY_DEPENDENT_ON]->(lhs:ColumnCombination)
WITH
  rhs,
  lhs,
  [
    fdFunction IN keys(properties(fd))
    WHERE
      properties(fd)[fdFunction] = true AND fdFunction IN searchedForFunctions
    | fdFunction
  ] AS trueMappingFunctions
WHERE size(trueMappingFunctions) > 0
RETURN lhs.name, rhs.name, trueMappingFunctions