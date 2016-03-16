package org.openstreetmap.josm.plugins.serbiantransliterator;

import rs.iz.mpele.StringElemetnOSM;


public class PreslovljavanjeOSM extends StringElemetnOSM{


	private String mName;
	private String mName_sr="";
	private String mName_sr_lat="";
	private String mOriginalName;
	private String mOriginalName_sr="";
	private String mOriginalName_sr_lat="";
	private String mStariTagovi="";
	
	private int mHashVrednost=0;
	private PodrazumevanoPismo emPodrazumevanoPismo = PodrazumevanoPismo.BEZ_PROMENE;

	private String mPrihvaceniNaziv="";
	private boolean mbDaLiJePrihvaceniNaziv_cirilica = false;
	private boolean mbDaLiImaTagovaZaNaziv = false;
	
	boolean mbAzuriratiName = false;
	boolean mbAzuriratiNameSr = false;
	boolean mbAzuriratiNameSrLatn = false;

	private Preslovljavanje preslovljavanje = new Preslovljavanje();

	public static void main(String[] args) {
		PreslovljavanjeOSM preslovljavanjeOsm = new PreslovljavanjeOSM();
		preslovljavanjeOsm.setPodrazumevanoPismo(PodrazumevanoPismo.LATINICA);
		preslovljavanjeOsm.setAzuriratiName(true);

		preslovljavanjeOsm.setNameKeyValue("name", "Аеродром Београд - Никола Тесла");
		preslovljavanjeOsm.setNameKeyValue("name:en", "Belgrade Nikola Tesla Airport");
		preslovljavanjeOsm.setNameKeyValue("name:ru", "Аеродром Београд - Никола Тесла");
		preslovljavanjeOsm.setNameKeyValue("name:sl", "Letališče Nikola Tesla");
		preslovljavanjeOsm.setNameKeyValue("name:sr" , "Аеродром Београд - Никола Тесла");
		preslovljavanjeOsm.setNameKeyValue("name:sr-Latn", "Aerodrom Beograd - Nikola Tesla");
		//preslovljavanjeOsm.setNameKeyValue("name:sr","Latinica");
		//		preslovljavanjeOsm.setName_sr_lat("Latinica");
		//		preslovljavanjeOsm.setName_en("Latinica");
		//preslovljavanjeOsm.setNameKeyValue("name","Латиница (Latinica)");
		//preslovljavanjeOsm.setNameKeyValue("name:sr","Латиница");
		//preslovljavanjeOsm.setNameKeyValue("name:sr-Latn","Latinica");
		//preslovljavanjeOsm.setNameKeyValue("name:en","Latinica");
		System.out.println("name="+preslovljavanjeOsm.getName());
		System.out.println("name:sr="+preslovljavanjeOsm.getName_sr());
		System.out.println("name:sr_lat="+preslovljavanjeOsm.getName_sr_lat());
		System.out.println("name:en="+preslovljavanjeOsm.getName_en());
		System.out.println(preslovljavanjeOsm.pregled());
		System.out.println(" izmene: "+preslovljavanjeOsm.daLiImaIzmena());

//		System.out.println("definisan hash ");
//		System.out.println("hash: "+preslovljavanjeOsm.getHashOriginala());
//		preslovljavanjeOsm.setNameKeyValue("serbianTransliteratorHash", "-1255948141");
//		System.out.println(" izmene: "+preslovljavanjeOsm.daLiImaIzmena());
//
//		System.out.println("promenjen tag");
//		System.out.println("hash: "+preslovljavanjeOsm.getHashOriginala());
//		preslovljavanjeOsm.setNameKeyValue("name:sr","123atinica sdfs df");
//		System.out.println(" izmene: "+preslovljavanjeOsm.daLiImaIzmena());
	}

	public void setNameKeyValue(String key, String value){
		mSviOriginalniTagoviMap.put(key, value);
		mStariTagovi+=key+"="+value+"|";
		if(key.equals(Tagovi.NAME.getTagKey())){
			setName(value);
			mOriginalName=value;
			mbDaLiImaTagovaZaNaziv = true;
		}
		else if(key.equals(Tagovi.CIRILICA.getTagKey())){
			setName_sr(value);
			mOriginalName_sr=value;
			mbDaLiImaTagovaZaNaziv = true;
		}
		else if(key.equals(Tagovi.LATINICA.getTagKey())){
			setName_sr_lat(value);
			mOriginalName_sr_lat=value;
			mbDaLiImaTagovaZaNaziv = true;
		}
		else if(key.equals(Tagovi.HASH.getTagKey())){
			 mHashVrednost = Integer.parseInt(value);
		}
		//		else if(key.equals(Tagovi.NAME.getTagKey())){
		//			setName_en(value);
		//			mbDaLiImaTagovaZaNaziv = true;
		//		}
	}
	


	@Override
	public String toString(){
		return String.valueOf(getId());
	}
	
	
	/**
	 * Postavlja podrazumevano pismo
	 * @param ppismo
	 */
	public void setPodrazumevanoPismo(PodrazumevanoPismo ppismo){
		emPodrazumevanoPismo = ppismo;
	}

	private void setName(String name){ 
		// ako je u tagu name cirilica onda je dalje sve jasno
		if(Preslovljavanje.daLiImaCirilice(name)){
			if(Preslovljavanje.daLiJeLatinica(name)){ // ako je i cirilica i latinica onda ide razdvajanje
				//razdvajanje naziva
				// pretpostavka da je odvojeno zagradama

				int pom1 = name.lastIndexOf(')');
				if(pom1 == -1){  // ako nije odvojeno zagradom
					mbDaLiJePrihvaceniNaziv_cirilica = true;
					mPrihvaceniNaziv = name;
					return;
				}
				int pom2 = name.lastIndexOf('(');

				String naziv1 = name.substring(pom2+1,pom1);

				if(Preslovljavanje.daLiImaCirilice(naziv1)){
					setName(naziv1);
				}
				else{
					setName(name.substring(0, pom2));
				}
			}
			else{			
				mbDaLiJePrihvaceniNaziv_cirilica = true;
				mPrihvaceniNaziv = name;
			}
		}
		// 
		if(!mbDaLiJePrihvaceniNaziv_cirilica){ // ako je vec bila cirilica onda nema sta dalje
			if(Preslovljavanje.daLiJeLatinica(name)){ // ako vec nije bila cirilica onda je latinica prihvacen naziv
				mbDaLiJePrihvaceniNaziv_cirilica = false;
				mPrihvaceniNaziv = name;
			}		
		}
		mName = name; // name se ne menja
	}

	public void setName_sr(String name){
		mName_sr = name;
		mPrihvaceniNaziv = name;
		mbDaLiJePrihvaceniNaziv_cirilica = true;
	}

	public void setName_sr_lat(String name){
		mName_sr_lat = name;
	}



	/**
	 *  vraca podrazumevani naziv 
	 */
	public String getName(){
		if(mOriginalName ==null)
			return "";
		String name="";
		switch(emPodrazumevanoPismo){
		case BEZ_PROMENE:
			name = mName;
			break;
		case CIRILICA:
			name = getName_sr();
			break;
		case LATINICA:
			name = getName_sr_lat();
			break;
		}
		return name.trim();
	}

	/**
	 *  vraca cirilicni naziv
	 */
	public String getName_sr(){
		return preslovljavanje.lat2cir(mPrihvaceniNaziv).trim();
	}

	public String getName_sr_lat(){
		if(mbDaLiJePrihvaceniNaziv_cirilica==false)
			return mPrihvaceniNaziv;
		else
			return preslovljavanje.cir2lat(mPrihvaceniNaziv).trim();
	}

	public String getName_en(){
		if(mbDaLiJePrihvaceniNaziv_cirilica==true)
			return preslovljavanje.cir2cel(mPrihvaceniNaziv).trim();
		else
			return preslovljavanje.lat2cel(mPrihvaceniNaziv).trim();
	}    

	public String getStariTagovi(){
		// TODO mozda izbaciti promenivu mStariTagovi i koristiti mSviOriginalniTagoviMap
		return mStariTagovi;
	}
	
	/**
	 * Vraca originalnu vrednost za tag
	 * @return
	 */
	public String getOriginalName(){
		if(mOriginalName ==null)
			return "";
		else
			return mOriginalName;
	}
	
	/**
	 * Vraca originalnu vrednost za tag
	 * @return
	 */
	public String getOriginalName_sr(){
		return mOriginalName_sr;
	}
	
	/**
	 * Vraca originalnu vrednost za tag
	 * @return
	 */
	public String getOriginalName_sr_lat(){
		return mOriginalName_sr_lat;
	}

	public boolean daLiImaTagovaZaNaziv(){
		return mbDaLiImaTagovaZaNaziv;
	}
	
	public boolean daLiImaIzmena(){
		if(mbDaLiImaTagovaZaNaziv == false)
			return false;

		if(mName ==null)
			return false;
		// TODO Staviti exception !!!!!!!!!!!!!!!!!!!!!!!!!!!!1

		if(mHashVrednost == getHashOriginala())
			return false;

		if((mName.equals(getName())|!mbAzuriratiName) 
				&& (mName_sr.equals(getName_sr())|!mbAzuriratiNameSr)
				&& (mName_sr_lat.equals(getName_sr_lat())|!mbAzuriratiNameSrLatn)
				//		&& mName_en.equals(getName_en()())
				)
			return false;
		else
			return true;
	}

	
	/**
	 * vraca da je bezbedno ako je name cirilicno, 
	 * 						//ako je definisan name:sr i on se poklapa sa cirilicnom verzijom
	 * @return
	 */
	public boolean daLiJeBezbednoPreslovljavanje(){
		
		// ako se radi preslovljavanje samo glavnog taga onda je sve bezbedno
		if(mbAzuriratiName==true && mbAzuriratiNameSr==false && mbAzuriratiNameSrLatn==false)
			return true;
		
		if(Preslovljavanje.daLiJeSamoCirilica(mName))
			return true;
		
		return false;
	}
	

	public void clear(){
		super.clear();
		
		mName="";
		mName_sr="";
		mName_sr_lat="";
		mStariTagovi="";
		mOriginalName="";
		mOriginalName_sr="";
		mOriginalName_sr_lat="";
		mStariTagovi="";

		mHashVrednost=0;
		//	 emPodrazumevanoPismo = PodrazumevanoPismo.BEZ_PROMENE;

		mPrihvaceniNaziv="";
		mbDaLiJePrihvaceniNaziv_cirilica = false;
		mbDaLiImaTagovaZaNaziv = false;
	}
	
	
	public String pregled(){
		String pom = "";
		pom += mName;
		if(mbAzuriratiName)
			pom += "-"+getName();
		if(mbAzuriratiNameSr)
			pom += " |Sr: "+ mName_sr+"-"+getName_sr();
		if(mbAzuriratiNameSrLatn)
			pom += " |SrLatn: "+ mName_sr_lat+"-"+getName_sr_lat();
		pom += "\n";
		return pom;
	}
	
	public void setAzuriratiName(boolean bool){		
		mbAzuriratiName = bool;
	}
	
	public void setAzuriratiNameSr(boolean bool){		
		mbAzuriratiNameSr = bool;
	}
	
	public void setAzuriratiNameSrLatn(boolean bool){		
		mbAzuriratiNameSrLatn = bool;
	}


	public int getHashOriginala() {
		String string = getOriginalName()+getOriginalName_sr()+getOriginalName_sr_lat();
		return getHash(string);
	}
	
	public static int getHash(String string){
	    int h = 0;
	    int len = string.length();
	    for (int i = 0; i < len; i++) {
	        h = 31 * h + string.charAt(i);
	    }
	    //System.out.println(string+"*"+h);
	    return h;
	}
	
	public boolean daLiImaHash(){
		if(mHashVrednost == 0)
			return false;
		else
			return true;
	}
}
