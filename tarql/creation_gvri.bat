chcp 65001
IF %1==effacer  GOTO si
GOTO sinon

:si
  del ".\tarql\gvri.ttl"
  type ".\tarql\StructureTurtle.rdf">>".\tarql\gvri.ttl"
  GOTO sinon

:sinon
	(
	tarql -d "|" tarql/sparql/create_ths_grp_iacr_topo.sparql
	tarql -d "|" tarql/sparql/create_ths_iacr_topo.sparql
	tarql -d "|" tarql/sparql/create_ths_cimo3_topo.sparql
	tarql -d "|" tarql/sparql/create_ths_grp_iacr_morpho.sparql
	tarql -d "|" tarql/sparql/create_ths_cimo3_morpho.sparql
	
	tarql -d "|" tarql/sparql/create_map_morpho_adicap_cimo3.sparql
	tarql -d "|" tarql/sparql/create_map_topo_adicap_cimo3.sparql
	tarql -d "|" tarql/sparql/create_ths_cim10.sparql
	tarql -d "|" tarql/sparql/create_ths_sejour.sparql
	tarql -d "|" tarql/sparql/create_ths_patient.sparql

	
	
	
	tarql -d "|" tarql/sparql/cimo3_cui_umls.sparql
	)>>tarql/gvri.ttl