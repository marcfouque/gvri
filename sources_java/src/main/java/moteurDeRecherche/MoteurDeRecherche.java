package moteurDeRecherche;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.query.text.TextDatasetFactory;
import org.apache.jena.query.text.TextIndexConfig;
import org.apache.jena.query.text.TextIndexLucene;
import org.apache.jena.query.text.assembler.TextDatasetAssembler;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.SyntaxError;
import org.apache.jena.tdb.TDBFactory;

public class MoteurDeRecherche {
	private Dataset d;
	private String prefix;
	
	public MoteurDeRecherche() {
		System.out.println("initialisation textdataset");
		d=TextDatasetFactory.create("./src/main/resources/base/assembleur.ttl");
		d.begin(ReadWrite.READ) ;
		System.out.println("#######");
		System.out.println(d.getContext());
		System.out.println("#######");
		
		System.out.println("initialisation des prefix pour les requetes");
		prefix = 	"PREFIX text: <http://jena.apache.org/text#> \n"+
	 			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"+
	 			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"+
	 			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n"+
	 			"PREFIX gvri: <https://inf203.isped.fr/gvri#> \n"+
				"PREFIX gvri_i: <https://inf203.isped.fr/gvri/instances#> \n";
		System.out.println(prefix);
	}
	
	public Dataset getDataset() {return d;}
	public Model getModel() {return d.getDefaultModel();}
	
	
	public ArrayList<Subject> requete(String req) {
		//methode permettant d'effectuer une requete
		//prend en entrée un string de requete sparql (avec une recherche de texte, text:query)
		//retourne un array contenant pour chaque  tuple de resultat un array avec en 0 la synthese du retour, puis chaque retour
		System.out.println(prefix+req);
		Query q = QueryFactory.create(prefix+req) ;
		ArrayList<Subject> resultat = new ArrayList<Subject>();
		try (QueryExecution qexec = QueryExecutionFactory.create(q, d)) {
		    ResultSet res = qexec.execSelect() ;
		    for ( ; res.hasNext() ; )
		    {
		      QuerySolution sol = res.nextSolution() ;
		      System.out.println(sol);
		      resultat.add(new SubjectRecherche(sol.get("s").toString(),sol.get("score").toString(),sol.get("literal").toString()));
		      
		      /*
		      ArrayList tempSol = new ArrayList();
		      tempSol.add(sol.toString());
		      Iterator<String> iter = sol.varNames();
		      while(iter.hasNext()){
		    	 tempSol.add(sol.get(iter.next()));
		  		}
		      resultat.add(tempSol);		  
		      */    
		      //RDFNode x = sol.get("varName") ;       // Get a result variable by name.
		      //Resource r = sol.getResource("VarR") ; // Get a result variable - must be a resource
		      //Literal l = sol.getLiteral("VarL") ;   // Get a result variable - must be a literal
		    }
			System.out.println("\n_\n");
		    return resultat;
		  }
	}
	public ArrayList<Subject> requete_STQ(String req) {
		//requete sans utilisation du text:query, donc sans l'index, simple requete sparql sur model rdf
		System.out.println(prefix+req);
		System.out.println(prefix+replacePrefix(req));
		Query q = QueryFactory.create(prefix+replacePrefix(req)) ;
		ArrayList<Subject> resultat = new ArrayList<Subject>();
		
		try (QueryExecution qexec = QueryExecutionFactory.create(q, d.getDefaultModel())) {
			System.out.println(qexec.toString());
			ResultSet res = qexec.execSelect() ;

		    for ( ; res.hasNext() ; )
		    {
				System.out.println("#####marco###");
		      QuerySolution sol = res.nextSolution() ;
		      System.out.println(sol);
		      resultat.add(new SubjectTriplet(sol.get("s").toString(),sol.get("p").toString(),sol.get("o").toString()));
		    }
			System.out.println("\n__\n");
		    return resultat;
		  }
	}
	private String replacePrefix(String req) {
		String requete = req;
		
		requete=req.replaceAll("https://inf203.isped.fr/gvri#", "gvri:");
		requete=req.replaceAll("https://inf203.isped.fr/gvri/instances#", "gvri_i:");
		return requete;
	}
	
	
	
	
	public String rechercheToTriplets(String uri) {
		String resultat = "";
		ArrayList<Subject> arr = requete_STQ("Select ?s ?p ?o "
				+ "where {"
				+ "{"
				+ " BIND ("+uri+" AS ?s) "
				+ uri+" ?p ?o"
				+ "}"
				+ "union"
				+ "{"
				+ " BIND ("+uri+" AS ?o) "
				+ "?s ?p "+uri
				+ "}"
				+ "}");
		for(Subject s : arr) {
			SubjectTriplet st = (SubjectTriplet)s;
			resultat+=st.toString()+"\n";
		}
		
		return resultat;
	}
	public String rechercheToTriplets(SubjectRecherche sr) {
		return rechercheToTriplets(sr.getSujet().getURI());
	}
	public String rechercheToTriplets(String valeur, String type, String keytype) {
		String uri = "";
		uri+=(true?"gvri_i:":"gvri:");
		uri+=type+valeur;
		String resultat = rechercheToTriplets(uri);
		System.out.println(resultat.length());
		if(resultat.length()==0)return "Il n'existe pas de "+keytype+" pour la valeur \""+valeur+"\"";
		return resultat;
	}

}
