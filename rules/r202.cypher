// R202 the number of INDs found
MATCH ()-[r:IS_INCLUSION_DEPENDENT_ON]->()
RETURN count(r)