rem call ./tarql/creation_gvri.bat effacer
rd /s /q "jena/tdb/tdb_gvri"
md "jena/tdb/tdb_gvri"
java -cp "D:\marco\Jena\fuseki\apache-jena-fuseki-3.13.1\fuseki-server.jar" tdb2.tdbloader --loc ./jena/tdb/tdb_gvri ./tarql/gvri.ttl
java -Xmx16G -cp "D:\marco\Jena\fuseki\apache-jena-fuseki-3.13.1\fuseki-server.jar" jena.textindexer --desc=./sources_java/src/main/resources/base/assembleur.ttl
