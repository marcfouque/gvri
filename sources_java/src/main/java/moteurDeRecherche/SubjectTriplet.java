package moteurDeRecherche;

public class SubjectTriplet extends Subject {
	private ElementRDF objet;
	private ElementRDF predicat;
	
	public SubjectTriplet(String s, String p, String o) {
		super(s);
		predicat= new ElementRDF(p);
		objet= new ElementRDF(o);
	}
	
	public ElementRDF getObjet() {return objet;}
	public ElementRDF getPredicat() {return predicat;}
	


	@Override
	public String toString() {
		return super.toString()+" _ "+predicat.getValeur()+" _ "+objet.getValeur();
	}
}
