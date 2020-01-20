<p align="center">
  <a href="http://www.isped.u-bordeaux.fr/" target="_blank">
    <img src="http://www.isped.u-bordeaux.fr/Portals/0/ISPED-UBX_2019CJMN.jpg?ver=2019-03-27-141509-167" alt="isped-ub logo">
   </a>
</p>

# gvri
moteur de recherche à travers des données PMSI - Java/Jena/Lucene
Projet dans le cadre de l'UE 203 - M2 SITIS - ISPED

### Prérequis
 - JDK >7<br/>
 - Maven >3<br/>
 💻
 Si volonté de refaire tout le processus de création de fichier RDF + création TDB + indexation/indexage
 - Tarql
 - Fuseki2
 
## Installation

Cloner le projet 
```bash
git clone https://github.com/marcfouque/gvri.git
```
Installation et compilation des dépendances (avec maven)
```bash
mvn compile
```
Compilation et execution du projet
```bash
javac sources_java/src/main/java/GVRI/GVRI.java
java sources_java/src/main/java/GVRI/GVRI
```
L'interface mettra ~5 secondes à apparaitre (chargement TDB,...)

Si volonté de lancer la contruction d'un TDB et une indexation alors lancer le script <i>./init.bat</i> (pas traduit en shell déso...), les chemins ne puvant etre définis qu'en absolu dans le fichier assembleur (<i>./sources_java/src/resources/base/assembleur.ttl</i>) nécéssité de les modifier (de même que les chemins dans le script <i>init.bat</i>).
