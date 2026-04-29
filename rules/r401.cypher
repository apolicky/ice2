// R401 are there even any RBCCs
MATCH p = ()-[r:REFERENCE_COMPARISON_HOLDS]->()
// any will do
RETURN p