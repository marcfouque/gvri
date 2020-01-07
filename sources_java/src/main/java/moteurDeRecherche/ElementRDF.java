package moteurDeRecherche;

public class ElementRDF {
	private String uri;
	private String valeur;
	
	public ElementRDF(String u,String v) {
		uri=u;
		valeur=v;
	}
	public ElementRDF(String u) {
		String[] temp = u.split("#");
		String v = "";
		if(temp.length==1)v=u;
		else for(int i=1;i<temp.length;i++)v+=temp[i];
		uri=u;
		valeur=v;
	}
	public String getURI() {return uri;}
	public String getValeur() {return valeur;}
	
	@Override
	public String toString() {
		return uri;
	}
}
