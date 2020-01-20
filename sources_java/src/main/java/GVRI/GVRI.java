package GVRI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.*;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
//import javafx.scene.control.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

//import com.gluonhq.charm-glisten.control.CharmListView; 


import moteurDeRecherche.*;


public class GVRI extends Application{
	
	private MoteurDeRecherche mdr ;
	private HashMap<String,Pattern> motifs;
	private HashMap<String,String> corresp;
	
	
    
	//private ArrayList<Pattern> motifs;
	/*
	Variables de javaFX permettant  un lien entre le controleur  et la  vue
	*/
	@FXML
	private TextField requete;
	@FXML
	private Label nb_res;
	@FXML
	private ListView<Subject> listeElements;
	/*@FXML
	private CharmListView listeRegexMatch;*/
	@FXML
	private ListView listeRegexMatch;
	@FXML
	private WebView zoneInfo;
	
	
	public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("VueGVRI.fxml"));
            

            primaryStage.setTitle("Projet Recherche d'Information - Marc Fouqué");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            
           // listeElements = new ListView<String>();
            
            
            //listeElements.getSelectionModel().selectedItemProperty().addListener(observable -> System.out.printf("Valeur sélectionnée: %d", listeElements.getSelectionModel().getSelectedItem()).println());

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
	@FXML
	private void initialize() {
		System.out.println("initialisation");
        mdr=new MoteurDeRecherche();
        motifs=initPattern();
        
        //listener sur clic elements liste
        listeElements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        listeElements.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Subject>() {
        	 
            @Override
            public void changed(ObservableValue<? extends Subject> observable, Subject oldValue, Subject newValue) {
                System.out.println("selection element");
                
            	//zoneInfo.setText(newValue.toString());
                zoneInfo.getEngine().loadContent(toHTML(mdr.rechercheToTriplets((SubjectRecherche)newValue)));
            	//zoneInfo.setText(mdr.rechercheToTriplets((SubjectRecherche)newValue));
            	listeRegexMatch.getSelectionModel().clearSelection();;
            }
        });
        
      //listener sur clic elements liste regex
        listeRegexMatch.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        listeRegexMatch.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
        	 
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("selection element");
              
            	//zoneInfo.setText(newValue.toString());
                String temp = newValue.split(" : ")[1];
                zoneInfo.getEngine().loadContent(toHTML(mdr.rechercheToTriplets(newValue.split(" : ")[1],corresp.get(newValue.split(" : ")[0]),newValue.split(" : ")[0])));
                //zoneInfo.setText(mdr.rechercheToTriplets(newValue.split(" : ")[1],corresp.get(newValue.split(" : ")[0]),newValue.split(" : ")[0]));
            	//zoneInfo.setText(mdr.rechercheToTriplets((SubjectRecherche)newValue));
            	listeElements.getSelectionModel().clearSelection();;
            }
        });
        
		System.out.println("___");
	}
	
	@FXML
	private void rechercher() {
		System.out.println("lancement recherche");
		/*
		String req = "Select distinct ?s ?score ?literal "
				+ "where {"
						+ "(?s ?score ?literal) text:query (gvri:recherche \"§\")."
						+ "OPTIONAL { ?s ?p ((?ps?score ?literal) text:query (gvri:recherche \"§\")) } ."
						//+ "union"
						//+ "{"
						//+ "bind(?score1+?score2 AS ?score)"
						//+ "bind(concat(?score1,?score2) AS ?score)"
						//+ "}"
						//+ "bind(?score1+?score2+?score0 AS ?score)"
						//+ "bind(concat(?literal,?literal2) AS ?literal)"
						//+ "?s rdfs:label ?label."
						//+ "?s rdf:type ?type"
				+ "} ORDER BY DESC(?score) ";
		*/
		
		String req = "Select distinct ?s ?score ?literal "
				+ "where {"
						//+ "(?s ?score ?literal) text:query (rdfs:label \"§\")."
						+ "(?s ?score ?literal) text:query (gvri:recherche \"§\")."
						//+ "(?s ?score2) text:query (gvri:recherche \"§~\")."
						//+ "(?s ?score2 ?literal2) text:query (rdfs:label \"§~\")"
						//+ "bind(?score1+?score2 AS ?score)"
						//+ "bind(concat(concat(concat(?literal1,str(?score1)),\"_\"),str(?score2)) AS ?literal)"
				+ "} ";
		
		/*
		req="SELECT ?s\r\n" + 
				"{ ?s text:query (rdfs:label '§' 10) ; \r\n" + 
				//"     rdfs:label ?label ;\r\n" + 
				//"     rdf:type   ?type \r\n" + 
				"}";
		*/
		
		ArrayList<Subject> resultat = mdr.requete(req.replaceAll("§", this.requete.getText()));
		//ArrayList<Subject> resultat = mdr.requete_STQ(req.replaceAll("§", this.requete.getText()));
		//this.reponse.setText(resultat.toString());
		int nbres = resultat.size();
		nb_res.setText((nbres==30?"Plus de ":"")+nbres+(nbres<2?" résultat trouvé":" résultats trouvés")+ " pour la recherche \""+this.requete.getText()+"\"");
		initListe(resultat);
		initListeRegex(this.requete.getText());

	}
	
	
	private void initListe(ArrayList<Subject> al) {
		System.out.println("initialisation liste");
		//ObservableList<String> elements = FXCollections.observableArrayList(al);
		listeElements.getItems().clear();
		for(int i=0;i<al.size();i++) {
			listeElements.getItems().add(al.get(i));
		}
		//System.out.println(listeElements.getItems());
		System.out.println("liste remplie");
	}
	
	
	private HashMap<String,Pattern> initPattern() {
		HashMap<String,Pattern> ps = new HashMap<String,Pattern>();
		
		//differents pattern du plus precis au plus general
		//ps.put("code cimo3",Pattern.compile("M-[0-9]{4}/[0-9]"));//cimo3
		ps.put("cui UMLS",Pattern.compile("C0[0-9]{6}"));//cuiumls
		ps.put("topo cimo3",Pattern.compile("C[0-9]{3}"));//topo cimo3
		ps.put("lesion Adicap",Pattern.compile("[A-Za-z]{1}[0-9]{1}[A-Za-z]{1}[0-9]{1}"));//lesion adicap
		ps.put("diagnostique",Pattern.compile("[A-Za-z]{1}[0-9]{1,5}\\+?[0-9]?"));//diagnostiques
		ps.put("numéro patient",Pattern.compile("[0-9]{6}"));//numpatient
		ps.put("morpho cimo3",Pattern.compile("[0-9]{4}"));//morpho cimo3
		//ps.put("séjour",Pattern.compile("[0-9]{1,5}"));//sejour
		//ps.put("morhpo IACR",Pattern.compile("1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17"));//morphoIACR
		//ps.put("organe Adicap",Pattern.compile("[A-Za-z]{2}"));//organe adicap
		//ps.put("topo Adicap",Pattern.compile("[A-Za-z]{2}|[0-9]{1,2}"));//topo adicap
		//ps.put("topo IACR",Pattern.compile("[0-9]{2}"));//topoIACR

		
		corresp = new HashMap<String,String>();
		//corresp.put("code cimo3","");//cimo3
		corresp.put("cui UMLS","umls_");//cuiumls
		corresp.put("topo cimo3","topoCimo_");//topo cimo3
		corresp.put("lesion Adicap","adicapLesion_");//lesion adicap
		corresp.put("diagnostique (code cim10)","cim10_");//diagnostiques
		corresp.put("numéro patient","patient_");//numpatient
		corresp.put("morpho cimo3","morphoCimo_");//morpho cimo3
		corresp.put("séjour","sejour_");//sejour
		corresp.put("morhpo IACR","morphoGrpIacr_");//morphoIACR
		corresp.put("organe Adicap","adicapOrgane_");//organe adicap
		corresp.put("topo Adicap","adicapTopo_");//topo adicap
		corresp.put("topo IACR","topoGrpIacr_");//topoIACR
		return ps;
	}
	
	private void initListeRegex(String req) {
		System.out.println("Recherche pattern dans la requete");
		listeRegexMatch.getItems().clear();
		for(String p : motifs.keySet()) {
			String resultat =p+" : ";
			Matcher m = motifs.get(p).matcher(req);
			while(m.find()) {
				listeRegexMatch.getItems().add(p+" : "+m.group());
	        }
			
		}
		
		//Matcher m =  
	}
	
	private String toHTML(String s) {
		//String res = s;
		String res = "";
		System.out.println("Marcopolo\n\n");
		System.out.println(s);
		String[] arrS = s.split(" _§£_ ");
		HashMap<String,ArrayList<String>> dico=new HashMap<String,ArrayList<String>>();

		System.out.println(arrS[0]);
		res+="<html><body>";
		res+="<h1>"+arrS[0]+"</h1>";
		//res=res.replaceAll(arrS[0], "<b>"+arrS[0]+"</b>");
		//arrS = s.split(arrS[0]);
		
		for(int i=0;i<arrS.length-3;i=i+3) {
			//res+=arrS[i]+"MARCO";
			//String[] tempS = arrS[i].split(" _§£_ ");
			System.out.println(dico.toString());
			if(dico.containsKey(arrS[i+1])) {
				ArrayList<String> als = dico.get(arrS[i+1]);
				if(arrS[i+2].trim().equals(arrS[0].trim())) {
					System.out.println(arrS[i+2].trim());
					als.add(arrS[i]);
					}
				else als.add(arrS[i+2]);
				dico.put(arrS[i+1],als);
			}
			else {
				ArrayList<String> als = new ArrayList<String>();
				if(arrS[i+2].trim().equals(arrS[0].trim())) {
					System.out.println(arrS[i+2].trim());
					als.add(arrS[i]);
					}
				else als.add(arrS[i+2]);
				dico.put(arrS[1+i],als);
			}
			//dico.put(arrS[i].split(" _§£_ ")[1], arrS[i].split(" _§£_ ")[2]);
			//for
		}
		res+="<ul>";
		
		Iterator<String> clefsDico = dico.keySet().iterator();
		while(clefsDico.hasNext()) {
			String cd = clefsDico.next();
			res+="<li><h3>"+cd+" :</h3><ul>";
			for(String v:dico.get(cd)) {
				res+="<li>"+v+"</li>";
			}
			res+="</ul></li>";
		}
		res+="</ul>";
		//res="<html>"+res+"</html>";
		res+="</body></html>";
		System.out.println(res);
		return res;
	}
	
	public static void main(String[] args) {
		System.out.println("Lancement GVRI");
		//initialisation tdb/index
		//MoteurDeRecherche mdr = new MoteurDeRecherche();
		
		//mdr.requete("Select ?s where {?s text:query (rdfs:label \"Autres\")} limit 10");
		//mdr.requete_STQ("Select ?s ?p ?o where {?s ?p rdfs:Class}");
		
		System.out.println("################");
		
		launch(args);

	}

}
