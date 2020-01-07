chcp 65001
IF %1==effacer  GOTO si
GOTO sinon

:si
  del "gvri.rdf"
  type StructureTurtle.rdf>>gvri.rdf
GOTO sinon

:sinon
	(
	tarql -d "|" sparql/create_ths_grp_iacr_topo.sparql
	tarql -d "|" sparql/create_ths_iacr_topo.sparql
	tarql -d "|" sparql/create_ths_cimo3_topo.sparql
	tarql -d "|" sparql/create_ths_grp_iacr_morpho.sparql
	tarql -d "|" sparql/create_ths_cimo3_morpho.sparql
	
	tarql -d "|" sparql/create_map_morpho_adicap_cimo3.sparql
	tarql -d "|" sparql/create_map_topo_adicap_cimo3.sparql
	tarql -d "|" sparql/create_ths_cim10.sparql
	tarql -d "|" sparql/create_ths_sejour.sparql
	tarql -d "|" sparql/create_ths_patient.sparql

	
	
	
	tarql -d "|" sparql/cimo3_cui_umls.sparql
	)>>gvri.rdf