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