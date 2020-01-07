package GVRI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
//import javafx.scene.control.TextFlow;
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
	private TextArea zoneInfo;
	
	
	public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("VueGVRI.fxml"));
            

            primaryStage.setTitle("Projet Recherche d'Information - Marc Fouqu�");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            
           // listeElements = new ListView<String>();
            
            
            //listeElements.getSelectionModel().selectedItemProperty().addListener(observable -> System.out.printf("Valeur s�lectionn�e: %d", listeElements.getSelectionModel().getSelectedItem()).println());

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
            	zoneInfo.setText(mdr.rechercheToTriplets((SubjectRecherche)newValue));
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
                zoneInfo.setText(mdr.rechercheToTriplets(newValue.split(" : ")[1],corresp.get(newValue.split(" : ")[0]),newValue.split(" : ")[0]));
            	//zoneInfo.setText(mdr.rechercheToTriplets((SubjectRecherche)newValue));
            	listeElements.getSelectionModel().clearSelection();;
            }
        });
        
		System.out.println("___");
	}
	
	@FXML
	private void rechercher() {
		System.out.println("lancement recherche");
		String req = "Select ?s ?score ?literal ?label ?type "
				+ "where {"
						+ "(?s ?score ?literal) text:query (rdfs:label \"�\")."
						//+ "?s ?p ?o."
						//+ "?s rdfs:label ?label."
						//+ "?s rdf:type ?type"
				+ "} limit 30";
		/*
		req="SELECT ?s\r\n" + 
				"{ ?s text:query (rdfs:label '�' 10) ; \r\n" + 
				//"     rdfs:label ?label ;\r\n" + 
				//"     rdf:type   ?type \r\n" + 
				"}";
		*/
		
		ArrayList<Subject> resultat = mdr.requete(req.replaceAll("�", this.requete.getText()));
		//ArrayList<Subject> resultat = mdr.requete_STQ(req.replaceAll("�", this.requete.getText()));
		//this.reponse.setText(resultat.toString());
		int nbres = resultat.size();
		nb_res.setText((nbres==30?"Plus de ":"")+nbres+(nbres<2?" r�sultat trouv�":" r�sultats trouv�s")+ " pour la recherche \""+this.requete.getText()+"\"");
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
		ps.put("num�ro patient",Pattern.compile("[0-9]{6}"));//numpatient
		ps.put("morpho cimo3",Pattern.compile("[0-9]{4}"));//morpho cimo3
		//ps.put("s�jour",Pattern.compile("[0-9]{1,5}"));//sejour
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
		corresp.put("num�ro patient","patient_");//numpatient
		corresp.put("morpho cimo3","morphoCimo_");//morpho cimo3
		corresp.put("s�jour","sejour_");//sejour
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
