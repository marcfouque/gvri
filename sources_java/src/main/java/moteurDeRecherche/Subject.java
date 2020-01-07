package moteurDeRecherche;

public class Subject {
	private ElementRDF sujet;
	
	public Subject(String s) {
		sujet= new ElementRDF(s);
	}
	
	public ElementRDF getSujet() {return sujet;}
	
	@Override
	public String toString() {
		return sujet.getValeur();
	}
}
