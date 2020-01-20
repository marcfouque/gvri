package moteurDeRecherche;

public class SubjectRecherche extends Subject {
	private String scoreURI;
	private float scoreNb;
	private String literal;
	
	public SubjectRecherche(String sub, String scr, String lit) {
		super(sub);
		scoreURI=scr;
		scoreNb = Float.parseFloat(scr.split("\\^\\^")[0]);
		literal=lit;
	}
	


	
	@Override
	public String toString() {
		return super.toString()+"\n"+literal+"\nscore : "+scoreNb;
	}
}
