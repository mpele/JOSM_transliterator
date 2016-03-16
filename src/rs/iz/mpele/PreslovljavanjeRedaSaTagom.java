package rs.iz.mpele;
import org.openstreetmap.josm.plugins.serbiantransliterator.Preslovljavanje;


public class PreslovljavanjeRedaSaTagom extends Preslovljavanje{

	Character mNavodnikCharacter = '"'; // podrazumevano radi sa osm fajlom koji koristi navodnike (osmosis)
	

	public static void main(String[] args) {

		PreslovljavanjeRedaSaTagom preslovljavanje = new PreslovljavanjeRedaSaTagom();
		preslovljavanje.setmNavodnikCharacter('\'');

		System.out.println(("    <tag k='addr:street' v='Radnicka' />"));
		System.out.println(preslovljavanje.lat2cir("    <tag k='addr:street' v='Radnicka' />"));
		
		
}
    
    /**
     * Latinica u cirilicu u okviru reda gde je definisan i tag
     * @param string
     * @return
     */
    public String lat2cir(String strLine) {

		String k, v;
		int a = strLine.indexOf("<tag k=");
		if(a>0){
			int b = strLine.indexOf(" v=");
			int c = strLine.lastIndexOf(mNavodnikCharacter);

			k = strLine.substring(a+8, b-1);
			v = strLine.substring(b+4, c );
			
//			System.out.println("*" + k + "^" + v + " ");
//			System.out.println("*" + k + "^" + super.lat2cir(v) + " ");
		
			strLine = "    <tag k="+mNavodnikCharacter+k+mNavodnikCharacter+
					" v="+mNavodnikCharacter+super.lat2cir(v)+mNavodnikCharacter+" />";
		}
		
		
		return strLine;
	}
    
	public void setmNavodnikCharacter(Character navodnikCharacter) {
		this.mNavodnikCharacter = navodnikCharacter;
	}

    
}
