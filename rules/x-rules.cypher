// R001, columns from table T
MATCH (c1:Column)-[FROM_TABLE]->(t:Table)
RETURN c1.name, t.name
// or only return `c` for the whole node representation

// R002 the number of tables in given dataset
// expects neo4j to only contain tables of the dataset
MATCH (t:Table)
RETURN count(t)

// R003 unique columns
MATCH (c:Column)-[FROM_TABLE]->(t:Table)
WHERE c.unique = true
RETURN c.name, t.name

// R004 columns of matching type
WITH 'NUMBER' AS searchedType
MATCH (c:Column)
WHERE c.infoType = searchedType
RETURN c.name

// R004.1 columns and their types
MATCH (c:Column)
RETURN c.name, c.infoType

// R005 columns with a relatively small domain
// x-checked-rule
MATCH (c:Column)
// or threshold
WHERE c.distinctValues <= 0.05 * c.count
RETURN c.name

// R006 columns with relatively large nullCount
MATCH (c:Column)
WITH c, (toFloat(c.nullCount) / c.count) AS nullCountPercentage
// or threshold
WHERE nullCountPercentage >= 0.5
RETURN c.name, nullCountPercentage

// R007 ID candidate columns
MATCH (c:Column)-[:FROM_TABLE]->(t:Table)
WHERE c.idCandidate OR (c.unique = true AND c.nullCount = 0)
RETURN c.name, t.name

// R007.1 ID candidate columns from the same table
// x-checked-rule
MATCH (c:Column)-[:FROM_TABLE]->(t:Table)
WHERE c.idCandidate OR (c.unique = true AND c.nullCount = 0)
RETURN collect(c.name), t.name, count(c) AS idCandidatesFromTheSameTable
ORDER BY idCandidatesFromTheSameTable DESC

// R008 codelist tables -- 2 columns, all having distinct values
MATCH (t:Table)
WHERE
  COUNT {
    MATCH (t)<-[:FROM_TABLE]-(c:Column)
    WHERE c.unique
  } =
  2
RETURN t.name

// R009 codelist tables -- 2 columns, both from the same table; they have only a single type in one column
MATCH (c:Column)-[:FROM_TABLE]->(t:Table)
WITH t, collect(c) AS tableColumns, count(c) AS numCols
WHERE numCols = 2
WITH
  t,
  [c IN tableColumns WHERE size(split(c.infoTypeDistribution, ',')) = 1] AS columnsWithSingleType
WHERE size(columnsWithSingleType) = numCols
RETURN t.name

// R010.1 columns with matching min values
MATCH (c1:Column)-[:FROM_TABLE]->(t1), (c2:Column)-[:FROM_TABLE]->(t2)
WHERE c1.infoType = c2.infoType AND c1.min = c2.min AND c1 <> c2
RETURN c1.name, t1.name, c2.name, t2.name, c1.min, c2.min

// R010.2 columns with matching max values
MATCH (c1:Column)-[:FROM_TABLE]->(t1), (c2:Column)-[:FROM_TABLE]->(t2)
WHERE c1.infoType = c2.infoType AND c1.max = c2.max AND c1 <> c2
RETURN c1.name, t1.name, c2.name, t2.name, c1.max, c2.max

// R011 columns with multiple types, i.e. typeDistribution has more than 1 element
// x-checked-rule
MATCH (c:Column)
WITH
  c,
  split(replace(replace(c.infoTypeDistribution, "[", ""), "]", ""), ",") AS typeDistr
WHERE size(typeDistr) > 1
RETURN c.name, typeDistr

// R012 columns with multiple types and no NULLs
MATCH (c:Column)
WITH
  c,
  split(replace(replace(c.infoTypeDistribution, "[", ""), "]", ""), ",") AS typeDistr
WITH
  c,
  typeDistr,
  [
    t IN typeDistr
    WHERE split(t, ":")[0] IN ["STRING", "NUMBER", "NUMBER_0_1", "DATETIME"]
    | t
  ] AS nonNullTypeDistr
WHERE size(nonNullTypeDistr) > 1
RETURN c.name, nonNullTypeDistr

// R013 columns having some type outliers
// x-checked-rule
WITH 0.05 AS percentageOfSmallCount
MATCH (c:Column)-[:FROM_TABLE]->(t:Table)
WITH
  c,
  t,
  split(replace(replace(c.infoTypeDistribution, "[", ""), "]", ""), ",") AS typeDistr,
  percentageOfSmallCount
WITH
  c,
  t,
  typeDistr,
  [
    t IN typeDistr
    WHERE toFloat(split(t, ":")[1]) < percentageOfSmallCount * c.count
    | t
  ] AS typesWithSmallCount
WHERE size(typesWithSmallCount) > 0
RETURN t.name, c.name, typesWithSmallCount, typeDistr

// R014 same name columns from distinct tables
MATCH (c1:Column)-[:FROM_TABLE]->(t1:Table)
MATCH (c2:Column)-[:FROM_TABLE]->(t2:Table)
WHERE t1 <> t2 AND c1.name = c2.name
RETURN c1, t1, c2, t2

// R015 NUMBER type columns: such columns having min-avg or avg-max relatively close
MATCH (c:Column)
WHERE
  c.infoType = "NUMBER" AND
  c.max <> 0 AND
  ((c.average - c.min) / (c.max - c.min) < 0.2 OR
    (c.average - c.min) / (c.max - c.min) > 0.8)
RETURN
  c,
  (c.average - c.min) / (c.max - c.min) AS closerToMinOrMax___close0_min___close1_max

// R016 NUMBER type columns: such columns having relatively small difference between the number of positive and negative values
MATCH (c:Column)
WITH c, c.count - c.positiveCount - c.zeroCount AS negativeCount
WITH
  c,
  negativeCount,
  toFloat(abs(c.positiveCount - negativeCount)) / c.count AS distNegativePositive
WHERE
  c.infoType = "NUMBER" AND
  (distNegativePositive > 0.4 AND // can be variable
    distNegativePositive < 0.6) // can be variable
RETURN c, negativeCount, c.positiveCount, distNegativePositive

// R017 pattern that were matched at least once
MATCH ()-[ph:PATTERN_HOLDS]->(p:Pattern)
RETURN DISTINCT (p.name)

// R018 matching patterns for columns, provide the percentage of the match
MATCH (c:Column)-[ph:PATTERN_HOLDS]->(p:Pattern)
MATCH (c)-[:FROM_TABLE]->(t:Table)
RETURN c.name, t.name, p.name, toFloat(ph.count) / c.count AS matchPercentage

// R019 personal contact information found
// x-checked-rule
MATCH (c:Column)-[ph:PATTERN_HOLDS]->(p:Pattern)
MATCH (c)-[:FROM_TABLE]->(t:Table)
WHERE ph.name IN ["Email", "PhoneNumber"]
RETURN c.name, t.name, p.name, ph.count AS numberOfOccurrences

// R101 functionally dependant columns
WITH 3 AS maxLHSCount
MATCH (rhs:Column)-[:IS_FUNCTIONALLY_DEPENDENT_ON]->(lhs:ColumnCombination)
WHERE size(split(lhs.name, ",")) <= maxLHSCount
RETURN lhs.name, rhs.name
ORDER BY size(split(lhs.name, ",")) ASC

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

// R201 IND relevant column pairs, c1's domain is a subset of c2
MATCH (c1)-[r:IS_INCLUSION_DEPENDENT_ON]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t1)
MATCH (c2)-[:FROM_TABLE]->(t2)
RETURN c1.name, t1.name, c2.name, t2.name

// R202 the number of INDs found
MATCH ()-[r:IS_INCLUSION_DEPENDENT_ON]->()
RETURN count(r)

// R203 the number of INDs across distinct tables
MATCH (c1:Column)-[r:IS_INCLUSION_DEPENDENT_ON]->(c2:Column)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t2:Table)
WHERE t1 <> t2
RETURN count(r)

// R204 INDs regarding ID candidate columns -- possible foreign key referencing
MATCH
  (dependant:Column)-[:IS_INCLUSION_DEPENDENT_ON]->
  (referenced:Column {idCandidate: true})
MATCH (dependant)-[:FROM_TABLE]->(td)
MATCH (referenced)-[:FROM_TABLE]->(tr)
WHERE td <> tr
RETURN referenced.name, tr.name, dependant.name, td.name

// R205 INDs in the same table
// x-checked-rule
MATCH (dependant:Column)-[:IS_INCLUSION_DEPENDENT_ON]->(referenced:Column)
MATCH (dependant)-[:FROM_TABLE]->(depT:Table)
MATCH (referenced)-[:FROM_TABLE]->(refT:Table)
WHERE depT = refT
RETURN referenced.name, dependant.name, refT.name AS table

// R206 INDs in the same table + the referenced column is an ID candidate -- possible hierarchical relationship
MATCH (dependant:Column)-[:IS_INCLUSION_DEPENDENT_ON]->(referenced:Column)
MATCH (dependant)-[:FROM_TABLE]->(depT:Table)
MATCH (referenced)-[:FROM_TABLE]->(refT:Table)
WHERE depT = refT AND referenced.idCandidate = true
RETURN referenced.name, dependant.name, refT.name AS table

// R207 not-implemented: INDs of type one-to-one, many-to-one, ...
MATCH (dependant:Column)-[i:IS_INCLUSION_DEPENDENT_ON]->(referenced:Column)
WHERE i.generalizedCardinality = 'many-to-one'
RETURN referenced, dependant, i.generalizedCardinality

// R208 not-implemented: INDs with up to 5 occurrences of the referenced column (up to 5 items within an order)
MATCH (dependant:Column)-[i:IS_INCLUSION_DEPENDENT_ON]->(referenced:Column)
WHERE i.cardinalityDependant.max <= 5
RETURN referenced, dependant, i.cardinalityDependant

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

// R302 columns that have been compared and at least one value was over the threshold
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WHERE size(r.comparisonIdsOverThreshold) > 0
RETURN c1, r, c2

// R303.1 columns with comparison X over threshold
// x-checked-rule
WITH 'string-substring-strict' AS operation
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
// any of 'string-length-greater-than', 'arithm-less-than', 'arithm-greater-than', ...
WHERE operation IN r.comparisonIdsOverThreshold
RETURN c1, r, c2

// R303.2 columns with comparison X over threshold
WITH 'arithm-greater-than' AS operation
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
// any of 'string-length-greater-than', 'arithm-less-than', 'arithm-greater-than', ...
WHERE operation IN r.comparisonIdsOverThreshold
RETURN c1, r, c2

// R303.3 columns with comparison X over threshold
WITH 'arithm-less-than' AS operation
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
// any of 'string-length-greater-than', 'arithm-less-than', 'arithm-greater-than', ...
WHERE operation IN r.comparisonIdsOverThreshold
RETURN c1, r, c2

// R303.4 columns with comparison X over threshold
WITH 'string-substring-case-insensitive' AS operation
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
// any of 'string-length-greater-than', 'arithm-less-than', 'arithm-greater-than', ...
WHERE operation IN r.comparisonIdsOverThreshold
RETURN c1, r, c2

// R303.5 columns with comparison X over threshold
WITH 'string-length-greater-than' AS operation
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
// any of 'string-length-greater-than', 'arithm-less-than', 'arithm-greater-than', ...
WHERE operation IN r.comparisonIdsOverThreshold
RETURN c1, r, c2

// R304 columns with more than one comparison over threshold
// x-checked-rule
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WHERE size(r.comparisonIdsOverThreshold) > 1
RETURN c1, r, c2

// R305.1 columns compared with at least 50\% success rate for a comparison
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WITH
  c1,
  c2,
  r,
  [
    comparisonResult IN r.comparisonResults
    WHERE toFloat(split(comparisonResult, "~")[1]) > (toFloat(c1.count) * 0.5)
    | comparisonResult
  ] AS comparisonResults
WHERE size(comparisonResults) > 0
RETURN c1.name, c2.name, c1.count AS numberOfComparisons, comparisonResults

// R305.2 columns compared with at least 50\% success rate for a comparison; disregard string-length comparisons
// x-checked-rule
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WITH
  c1,
  c2,
  r,
  [
    comparisonResult IN r.comparisonResults
    WHERE
      toFloat(split(comparisonResult, "~")[1]) > (toFloat(c1.count) * 0.5) AND
      substring(split(comparisonResult, "~")[0], 0, 14) <> 'string-length-'
    | comparisonResult
  ] AS comparisonResults
WHERE size(comparisonResults) > 0
RETURN c1.name, c2.name, c1.count AS numberOfComparisons, comparisonResults

// R306 columns with both arithmetic '<' and string length '<". Indicates the columns are of different orders of magnitude
// x-checked-rule
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WHERE
  ('arithm-less-than' IN r.comparisonIdsOverThreshold) AND
  ('string-length-less-than' IN r.comparisonIdsOverThreshold)
RETURN c1, r, c2

// R307 columns with 2 distinct arithmetic comparison results, i.e. <: 98\%, >=: 2\%
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WITH
  c1,
  c2,
  r,
  [a IN r.comparisonResults WHERE a STARTS WITH "arithm" | a] AS arithmResults
WHERE size(arithmResults) > 1
RETURN c1, r, c2, arithmResults

// R308 outlier columns with 2 distinct arithmetic comparison results, i.e. <: 98\%, >=: 2\%. Here threshold max 10\%
// x-checked-rule
MATCH (c1:Column)-[r:TWO_COLUMN_COMPARISON_HOLDS]->(c2:Column)
WITH
  c1,
  c2,
  r,
  [a IN r.comparisonResults WHERE a STARTS WITH "arithm" | a] AS arithmResults
WHERE size(arithmResults) = 2
// only such pairs that have 2 distinct 'arithm' results
WITH
  c1,
  c2,
  r,
  arithmResults,
  toFloatList([split(arithmResults[0], '~')[1], split(arithmResults[1], '~')[1]]) AS countsFirstAndSecond
// parse the map from weird format
UNWIND countsFirstAndSecond AS c
WITH c1, c2, r, arithmResults, min(c) / sum(c) AS percentualCountOfLessFrequent
WHERE percentualCountOfLessFrequent < 0.1
RETURN c1, r, c2, percentualCountOfLessFrequent, arithmResults

// R309 columns that wouldn't need to be even compared: A.min >> B.max
MATCH (c1:Column)-[:FROM_TABLE]->(t:Table)
MATCH (c2:Column)-[:FROM_TABLE]->(t)
WHERE c1.infoType = "NUMBER" AND c1.infoType = c2.infoType AND c1.min > c2.max
RETURN c1, c2, t

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

// R401 are there even any RBCCs
MATCH p = ()-[r:REFERENCE_COMPARISON_HOLDS]->()
// any will do
RETURN p

// R402 how many row pairs have there been in a singe table
// x-checked-rule
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t:Table)
MATCH (c2)-[:FROM_TABLE]->(t)
RETURN t.name, r.numberOfComparisons

// R403 how many row pairs kolik hierarchickych paru bylo v jedne dane tabulce, napr employess.csv
// staci nam jeden sloupec, ve kterem se referencovane hodnoty porovnaji
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c1)
MATCH (c1)-[:FROM_TABLE]->(t:Table)
RETURN t.name, r.numberOfComparisons
// neni potreba pro danou tabulku resit vic nez 1 porovnavatelny sloupec.
LIMIT 1

// R404 how many row pairs have there been diffenent tables
// x-checked-rule
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2:Column)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t2:Table)
WHERE t1 <> t2
RETURN max(r.numberOfComparisons), t1.name, t2.name

// R405 are there any rules that have been over the threshold
// x-checked-rule
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2:Column)
WHERE size(r.comparisonIdsOverThreshold) > 0
RETURN c1.name, c2.name, r.comparisonIdsOverThreshold

// R405.1 are there any other rules than string-length-< (most common) that have been over the threshold
// x-checked-rule
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2:Column)
WHERE
  size(r.comparisonIdsOverThreshold) > 0 AND
  NOT isEmpty(
    [x IN r.comparisonIdsOverThreshold WHERE x <> 'string-length-less-than' | x])
RETURN c1.name, c2.name, r.comparisonIdsOverThreshold

// R406 what are the distributions of RBCCs
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c2:Column)
RETURN c1.name, c2.name, r.comparisonResults

// R407 what is the highest number of row pair that have been linked togehter
MATCH (dep:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(ref:Column)
RETURN ref.name, dep.name, r.numberOfComparisons
ORDER BY r.numberOfComparisons DESC
LIMIT 1

// R501 are there even any RBCACs
MATCH p = ()-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->()
RETURN p

// R502 such results where the value of REFerenced is greater than the maximum of all DEPs in the reference group; within single table, hierarchical
MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c1)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
WHERE
  c1.infoType = "NUMBER"
  // impl detail of how the table is written to neo4j
  AND
  size(split(r.comparedAgainstMaximums, "~")) = 1
  // impl detail of how to get the maximum
  AND
  r.comparedAgainstMaximums STARTS WITH "[GREATER_THAN:"
RETURN t1.name, c1, r

// R503.1 such results where the value of REFerenced is greater than the maximum of all DEPs in the reference group; across tables
MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t2:Table)
WHERE
  t1 <> t2 AND
  c1.infoType = "NUMBER" AND
  c2.infoType = "NUMBER" AND
  size(split(r.comparedAgainstMaximums, "~")) = 1 AND
  r.comparedAgainstMaximums STARTS WITH "[GREATER_THAN:"
RETURN
  t1.name,
  c1.name,
  t2.name,
  c2.name,
  r.numberOfPairings,
  r.comparedAgainstMaximums

// R503.2 such results where the value of REFerenced is greater than the sum of all DEPs in the reference group; across tables
MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c2)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t2:Table)
WHERE
  t1 <> t2 AND
  c1.infoType = "NUMBER" AND
  c2.infoType = "NUMBER" AND
  size(split(r.comparedAgainstSums, "~")) = 1 AND
  r.comparedAgainstSums STARTS WITH "[GREATER_THAN:"
RETURN
  t1.name,
  c1.name,
  t2.name,
  c2.name,
  r.numberOfPairings,
  r.comparedAgainstSums

// R504 how many reference groups were there? within single table, hierarchical
MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c1)
MATCH (c1)-[:FROM_TABLE]->(t:Table)
RETURN t.name, max(r.numberOfPairings)

// R505 how many reference groups were there? across tables
// x-checked-rule
MATCH (c1:Column)-[r:AGGREGATED_REFERENCE_COMPARISON_HOLDS]->(c2:Column)
MATCH (c1)-[:FROM_TABLE]->(t1:Table)
MATCH (c2)-[:FROM_TABLE]->(t2:Table)
WHERE t1 <> t2
RETURN
  t1.name,
  t2.name,
  max(r.numberOfPairings) AS distinctValuesThatWereReferenced