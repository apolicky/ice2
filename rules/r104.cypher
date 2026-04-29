// R104 not-implemented: FDs with a FD mapping function
// -- on example dataset scammer-info.csv can be done by running the following cypher statement
// MATCH (gn: Column {name: 'givenName'})-[fd:IS_FUNCTIONALLY_DEPENDENT_ON ]->(email:ColumnCombination {name: 'email'})
// SET fd.mappingConvertingEmailToNameByRemovingAtAndSplittingByComma = true
// RETURN gn, fd, email
// x-checked-rule
MATCH (rhs:Column)-[fd:IS_FUNCTIONALLY_DEPENDENT_ON]->(lhs:ColumnCombination)
WITH
  rhs,
  lhs,
  [
    fdFunction IN keys(properties(fd))
    WHERE properties(fd)[fdFunction] = true
    | fdFunction
  ] AS trueMappingFunctions
WHERE size(trueMappingFunctions) > 0
RETURN lhs.name, rhs.name, trueMappingFunctions