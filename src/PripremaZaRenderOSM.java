

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.serbiantransliterator.Preslovljavanje;

import rs.iz.mpele.StringElemetnOSM;


public class PripremaZaRenderOSM extends StringElemetnOSM{
	
	Preslovljavanje preslovljavanje = new Preslovljavanje();

	List<String> mNameTagovi = new ArrayList<String>();
	List<String> mNameSrLatTagovi = new ArrayList<String>();


	public static void main(String[] args) {
		PripremaZaRenderOSM pripremaZaRenderOSM = new PripremaZaRenderOSM();

		pripremaZaRenderOSM.setNameKeyValue("name", "Аеродром Београд - Никола Тесла");
		pripremaZaRenderOSM.setNameKeyValue("name:en", "Belgrade Nikola Tesla Airport");
		pripremaZaRenderOSM.setNameKeyValue("name:ru", "Аеродром Београд - Никола Тесла");
		pripremaZaRenderOSM.setNameKeyValue("name:sl", "Letališče Nikola Tesla");
		pripremaZaRenderOSM.setNameKeyValue("name:sr" , "Аеродром Београд - Никола Тесла");
		pripremaZaRenderOSM.setNameKeyValue("name:sr-Latn", "Aerodrom Beograd - Nikola Tesla");
		
		pripremaZaRenderOSM.setNameTagovi(Arrays.asList("name:sl", "name:sr-Latn"));
		pripremaZaRenderOSM.setNameSrLatTagovi(Arrays.asList("Pname:sr", "name:sr-Latn"));
		
		System.out.println(pripremaZaRenderOSM.pregled());
		
		System.out.println(pripremaZaRenderOSM.getName());
		System.out.println(pripremaZaRenderOSM.getName_srLatn());
	}
	
	public void clear(){
		super.clear();
	}
	
	public String pregled(){
		String pom = "";

		for(String tmp : mSviOriginalniTagoviMap.keySet()){
			System.out.println(tmp +" "+mSviOriginalniTagoviMap.get(tmp));
		}
		return pom;
	}

	public String getName() {
		for(String tag: mNameTagovi){
			if(mSviOriginalniTagoviMap.containsKey(tag)){
				return mSviOriginalniTagoviMap.get(tag);
			}
		}
		
		return null;
	}

	public String getName_srLatn() {
		boolean bPreslovljavanje = false;
		for(String tag: mNameSrLatTagovi){
			if(tag.startsWith("P")){
				tag = tag.substring(1);
				bPreslovljavanje = true;
			}
			
			if(mSviOriginalniTagoviMap.containsKey(tag)){
				if(bPreslovljavanje){
					return preslovljavanje.cir2lat(mSviOriginalniTagoviMap.get(tag));
				}
				else{
					return mSviOriginalniTagoviMap.get(tag);					
				}
			}
		}
		
		return null;
	}

	public void setNameTagovi(List<String> nameTagovi) {
		this.mNameTagovi = nameTagovi;
	}

	public void setNameSrLatTagovi(List<String> nameSrLatTagovi) {
		this.mNameSrLatTagovi = nameSrLatTagovi;
	}
}
