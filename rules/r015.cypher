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