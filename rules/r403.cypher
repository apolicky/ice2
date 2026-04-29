// R403 kolik hierarchickych paru bylo v jedne dane tabulce, napr employess.csv
// staci nam jeden sloupec, ve kterem se referencovane hodnoty porovnaji
MATCH (c1:Column)-[r:REFERENCE_COMPARISON_HOLDS]->(c1)
MATCH (c1)-[:FROM_TABLE]->(t:Table)
RETURN t.name, r.numberOfComparisons
// neni potreba pro danou tabulku resit vic nez 1 porovnavatelny sloupec.
LIMIT 1