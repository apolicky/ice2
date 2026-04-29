// R006 columns with relatively large nullCount
MATCH (c:Column)
WITH c, (toFloat(c.nullCount) / c.count) AS nullCountPercentage
// or threshold
WHERE nullCountPercentage >= 0.5
RETURN c.name, nullCountPercentage