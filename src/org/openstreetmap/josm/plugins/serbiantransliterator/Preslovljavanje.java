package org.openstreetmap.josm.plugins.serbiantransliterator;

import java.util.Arrays;


public class Preslovljavanje {
	static private String cirDvoslovaList[] = {"Љ", "Љ", "љ", "Њ", "Њ", "њ", "Џ", "џ", "Ђ", "ђ"};
	static private String latDvoslovaList[] = {"Lj","LJ","lj","Nj","NJ","nj","Dž","dž","Đ", "đ"};
	static private String celDvoslovaList[] = {"Lj","LJ","lj","Nj","NJ","nj","Dž","dž","Dj","dj"};
	
	static private String cirSlova = "АаБбВвГгДдЕеФфЖжЗзИиЈјКкЛлМмНнОоПпРрСсТтУуЋћХхЦцЧчШш";
	static private String latSlova = "AaBbVvGgDdEeFfŽžZzIiJjKkLlMmNnOoPpRrSsTtUuĆćHhCcČčŠš";
	static private String celSlova = "AaBbVvGgDdEeFfZzZzIiJjKkLlMmNnOoPpRrSsTtUuCcHhCcCcSs";

	static private String svaCirilica = cirSlova+Arrays.asList(cirDvoslovaList).toString();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Preslovljavanje preslovljavanje = new Preslovljavanje();
		System.out.println(preslovljavanje.cir2lat("ћирилицађшџж ")+Preslovljavanje.daLiImaCirilice(("ћирилицађшџж")));
		System.out.println(preslovljavanje.lat2cir("latinica ")+Preslovljavanje.daLiImaCirilice(("latinica")));
		System.out.println(preslovljavanje.cir2cel("ћирилица ")+Preslovljavanje.daLiImaCirilice(("ћирилицађшџж")));
		System.out.println(preslovljavanje.lat2cel("latinica ")+Preslovljavanje.daLiImaCirilice(("latinica")));
		
		System.out.println("\n\ndaLiJeSamoCirilica\n");
		System.out.println("ћирилицађшџж "+Preslovljavanje.daLiJeSamoCirilica(("ћирилицађшџж")));
		System.out.println("latinica "+Preslovljavanje.daLiJeSamoCirilica(("latinica")));
		System.out.println("ћирилица "+Preslovljavanje.daLiJeSamoCirilica(("ћирилицађш џж")));
		
	}
	
	/**
	 * Cirilica u latinicu
	 * @param input
	 * @return
	 */
    public String cir2lat(String string) {
    	// dvoznaci
    	for (int i = 0; i < cirDvoslovaList.length ; i++) {
    		string = string.replace(cirDvoslovaList[i], latDvoslovaList[i]);	
    	}
    	// slova
    	for (int i = 0; i < cirSlova.length() ; i++) {
    		string = string.replace(cirSlova.charAt(i), latSlova.charAt(i));	
    	}
		return string;
	}
    
    /**
     * Latinica u cirilicu
     * @param string
     * @return
     */
    public String lat2cir(String string) {
    	// dvoznaci
    	for (int i = 0; i < cirDvoslovaList.length ; i++) {
    		string = string.replace(latDvoslovaList[i], cirDvoslovaList[i]);
    	}
    	// slova
    	for (int i = 0; i < cirSlova.length() ; i++) {
    		string = string.replace(latSlova.charAt(i), cirSlova.charAt(i));
    	}
		return string;
	}
    
    
    public String cir2cel(String string) {
    	// dvoznaci
    	for (int i = 0; i < cirDvoslovaList.length ; i++) {
    		string = string.replace(cirDvoslovaList[i], celDvoslovaList[i]);	
    	}
    	// slova
    	for (int i = 0; i < cirSlova.length() ; i++) {
    		string = string.replace(cirSlova.charAt(i), celSlova.charAt(i));	
    	}
		return string;
	}
    
    public String lat2cel(String string) {
    	// dvoznaci
    	for (int i = 0; i < cirDvoslovaList.length ; i++) {
    		string = string.replace(latDvoslovaList[i], celDvoslovaList[i]);	
    	}
    	// slova
    	for (int i = 0; i < cirSlova.length() ; i++) {
    		string = string.replace(latSlova.charAt(i), celSlova.charAt(i));	
    	}
		return string;
	}
    
    /**
     * Proverava da li su samo cirilicni karakteri u stringu
     * @param string
     * @return
     */
    static public boolean daLiJeSamoCirilica(String string){

    	@SuppressWarnings("unused") // ne vidim razlog za ovime ?!?
		Character c =' ';
    	
    	// TODO ovo obavezno optimizovati
    	for (int i = 0; i < string.length() ; i++) {
    		c = string.charAt(i);
    		if(svaCirilica.indexOf(string.charAt(i))==-1){
    			return false;
    		}
    	}

		return true;
    }  
    
    
    
    /**
     * Proverava da li ima cirilicnih karaktera u stringu
     * @param string
     * @return
     */
    static public boolean daLiImaCirilice(String string){
    	// slova
    	for (int i = 0; i < cirSlova.length() ; i++) {
    		if(string.indexOf(cirSlova.charAt(i))>0)
    				return true;
    	}
		return false;
    }
    
    /**
     * Proverava da li ima cirilicnih karaktera u stringu
     * @param string
     * @return
     */
    static public boolean daLiJeLatinica(String string){
    	// slova
    	for (int i = 0; i < cirSlova.length() ; i++) {
    		if(string.indexOf(latSlova.charAt(i))>0)
    				return true;
    	}
		return false;
    }

}
