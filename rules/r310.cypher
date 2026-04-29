// R310 columns with 2 column comparison but no FD. FD check failed, there might be an outlier. If we took rows with unique values, for example, might we get some FD mapping function?
// they need to be from the same table
MATCH
  (lhsColumn)-[:FROM_TABLE]->(t)<-[:FROM_TABLE]-(rhs),
  // there was a 2 column comparison done
  (lhsColumn)-[:TWO_COLUMN_COMPARISON_HOLDS]-(rhs)
// distinct columns
WHERE
  rhs <> lhsColumn
  // don't have FD between them
  AND
  NOT (lhsColumn)-
  [:PART_OF_FUNC_DEP_DETERMINANT]->
  (:ColumnCombination)<-
  [:IS_FUNCTIONALLY_DEPENDENT_ON]-
  (rhs)
RETURN lhsColumn, rhs