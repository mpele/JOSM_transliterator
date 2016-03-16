
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.openstreetmap.josm.plugins.serbiantransliterator.PodrazumevanoPismo;
import org.openstreetmap.josm.plugins.serbiantransliterator.PreslovljavanjeOSM;


public class PregledKorisnika 
{

	FileWriter mOutPregledFile;
	//	FileWriter mOutPregledZaMesecFile;

	PreslovljavanjeOSM preslovljavanjeOSM = new PreslovljavanjeOSM();

	private String mNazivUlaznogFajla;

	private HashMap<String, Integer> mBrojAktivnihIzmenaKorisnika = new HashMap<String, Integer>();
	private HashMap<String, Integer> mBrojAktivnihIzmenaZaDan= new HashMap<String, Integer>();
	private HashMap<String, String> mDatumPrvi= new HashMap<String, String>();
	private HashMap<String, String> mDatumPoslednji= new HashMap<String, String>();
	ValueComparator comparatorInteger=  new ValueComparator(mBrojAktivnihIzmenaKorisnika);
	ValueComparatorString comparatorStringDatumPrvi =  new ValueComparatorString(mDatumPrvi);
	ValueComparatorString comparatorStringDatumPoslednji =  new ValueComparatorString(mDatumPoslednji);
	TreeMap<String,Integer> sortiraniBrojAktinihIzmena = new TreeMap<String,Integer>(comparatorInteger);
	TreeMap<String,String> sortiraniPrviDatum= new TreeMap<String,String>(comparatorStringDatumPrvi);
	TreeMap<String,String> sortiraniPoslednjiDatum= new TreeMap<String,String>(comparatorStringDatumPoslednji);


	public static void main(String args[])
	{

		int i = 0;
		String ulazniFajl = null;
		String arg;
		PregledKorisnika parser = new PregledKorisnika();


		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

			if (arg.startsWith("-ulaz=")) {
				ulazniFajl = arg.substring(6); // od stringa 
				System.out.println("Ulazni fajl: "+ulazniFajl);
			}

		}



		if(ulazniFajl==null)
			parser.run("serbia.osm");
		else
			parser.run(ulazniFajl);

	}




	public void run(String ulazniFajl){

		//izdvaja sam naziv fajla bez putanje i ekstenzije
		mNazivUlaznogFajla=ulazniFajl.replace(".osm", "");// brise ekstenziju - osm
		mNazivUlaznogFajla=mNazivUlaznogFajla.substring(mNazivUlaznogFajla.lastIndexOf('/')+1); //brise putanju

		preslovljavanjeOSM.setPodrazumevanoPismo(PodrazumevanoPismo.BEZ_PROMENE);
		preslovljavanjeOSM.setAzuriratiName(true);
		preslovljavanjeOSM.setAzuriratiNameSrLatn(true);
		preslovljavanjeOSM.setAzuriratiNameSrLatn(true);

		try{
			// Open the file  
			FileInputStream fstream = new FileInputStream(ulazniFajl);



			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;

			int pomA, pomB, pomC, pomD;

			//mOutPregledZaMesecFile.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head><body>");
			//mOutPregledZaMesecFile.write("<table  border=1> <tr> <th>ID</th> <th>Tip</th> <th>name:sr</th> <th>name:sr-Latn</th> <th>name</th> </tr>");


			//String newline = System.getProperty("line.separator");

			while ((strLine = br.readLine()) != null) {
				if(strLine.contains("<node ") | strLine.contains("<way ")| strLine.contains("<relation ") ){  // ako je pocetak novog node
					// za pocetak novog zapisa

					// user
					pomA = strLine.indexOf(" user=");
					pomB = strLine.indexOf('"', pomA+7);

					String strUser= strLine.substring(pomA+7, pomB);

					// datum 
					pomC = strLine.indexOf(" timestamp=");
					pomD = strLine.indexOf('"', pomC+12);

					String strDatum = strLine.substring(pomC+12, pomD);


					//System.out.println(strLine);
					//System.out.println(strUser + " "+ strDatum);

					// azurira vrednosti
					dodajAktivnuIzmenuKorisniku(strUser);
					//dodajAktivnuIzmenuZaDan(strDatum.substring(0, 10)); // za pregled po danima
					dodajAktivnuIzmenuZaDan(strDatum.substring(0, 7)); // za pregled po mesecima
					
					proveriDatume(strUser, strDatum);


				}
			}

			
			//System.out.println(mBrojAktivnihIzmenaZaDan);
			printStatistikaPoDanu();
			
			//System.out.println(mBrojAktivnihIzmenaKorisnika);
			sortiraniBrojAktinihIzmena.putAll(mBrojAktivnihIzmenaKorisnika);
			//System.out.println(sortiraniBrojAktinihIzmena);
			//System.out.println(mDatumPrvi);
			sortiraniPrviDatum.putAll(mDatumPrvi);
			//System.out.println(sortiraniPrviDatum);
			//System.out.println(mDatumPoslednji);
			sortiraniPoslednjiDatum.putAll(mDatumPoslednji);
			//System.out.println(sortiraniPoslednjiDatum);

			printMap(sortiraniPoslednjiDatum, "_PregledKorisnika_PoslednjiDatum.html");
			printMap(sortiraniPrviDatum, "_PregledKorisnika_PrviDatum.html");
			printMapB(sortiraniBrojAktinihIzmena, "_PregledKorisnika_BrojAktivnihIzmena.html");

			//Close the input stream
			in.close();


			//			mOutPregledZaMesecFile.close();


		}catch (Exception e){//Catch exception if any
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("Gotovo");
	}


	/**
	 * Vodi racuna o datumima
	 * @param korisnik
	 */
	private void proveriDatume(String korisnik, String datum){
		String min_value = mDatumPrvi.get(korisnik);
		if(min_value == null){
			mDatumPrvi.put(korisnik, datum);
		}
		else{
			if(min_value.compareTo(datum)>0)
				mDatumPrvi.put(korisnik, datum);
		}

		String max_value = mDatumPoslednji.get(korisnik);
		if(max_value == null){
			mDatumPoslednji.put(korisnik, datum);
		}
		else{
			if(max_value.compareTo(datum)<0)
				mDatumPoslednji.put(korisnik, datum);
		}
	}

	/**
	 * Broji pojavljivanja korisnika
	 * @param korisnik
	 */
	private void dodajAktivnuIzmenuKorisniku(String korisnik){
		Integer value = mBrojAktivnihIzmenaKorisnika.get(korisnik);
		if(value == null){
			mBrojAktivnihIzmenaKorisnika.put(korisnik, 1);
		}
		else{
			mBrojAktivnihIzmenaKorisnika.put(korisnik, value+1);
		}
	}

	/**
	 * Broji pojavljivanja dana
	 * @param datumString
	 */
	private void dodajAktivnuIzmenuZaDan(String datumString){
		Integer value = mBrojAktivnihIzmenaZaDan.get(datumString);
		if(value == null){
			mBrojAktivnihIzmenaZaDan.put(datumString, 1);
		}
		else{
			mBrojAktivnihIzmenaZaDan.put(datumString, value+1);
		}
	}
	

	@SuppressWarnings("rawtypes")
	public void printMap(Map<String, String> map, String nazivFajla){

		int j = 0;
		try {
			mOutPregledFile= new FileWriter(mNazivUlaznogFajla+nazivFajla);
			mOutPregledFile.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head><body>");
			mOutPregledFile.write("<table  border=1> <tr> <th>br.</th> <th>korisnik</th> <th>Br. aktivnih izmena</th> <th>Prva izmena</th> <th>Poslednja izmena</th> </tr>");


			for (Map.Entry entry : map.entrySet()) {
//				System.out.println(
//						entry.getKey() 
//						+ " |\t " + mBrojAktivnihIzmenaKorisnika.get(entry.getKey())
//						+ " |\t " + mDatumPrvi.get(entry.getKey())
//						+ " |\t " + mDatumPoslednji.get(entry.getKey())
//						);

				mOutPregledFile.write(
						"<td> "+ j++ +"</td>"+
								"<td><a href=\""+"http://www.openstreetmap.org/user/"+ entry.getKey()+ "\" >"+entry.getKey()+"</a></td><td>"+
								mBrojAktivnihIzmenaKorisnika.get(entry.getKey())+"</td><td>"+
								mDatumPrvi.get(entry.getKey())+"</td><td>"+
								mDatumPoslednji.get(entry.getKey())+"</td><td></tr> "
						);


			}	
			mOutPregledFile.close();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printMapB(Map<String, Integer> map, String nazivFajla){

		int j = 0;
		try {
			mOutPregledFile= new FileWriter(mNazivUlaznogFajla+nazivFajla);
			mOutPregledFile.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head><body>");
			mOutPregledFile.write("<table  border=1> <tr> <th>br.</th> <th>korisnik</th> <th>Br. aktivnih izmena</th> <th>Prva izmena</th> <th>Poslednja izmena</th> </tr>");


			for(Map.Entry entry : map.entrySet()) {
//				System.out.println(
//						entry.getKey() 
//						+ " |\t " + mBrojAktivnihIzmenaKorisnika.get(entry.getKey())
//						+ " |\t " + mDatumPrvi.get(entry.getKey())
//						+ " |\t " + mDatumPoslednji.get(entry.getKey())
//						);

				mOutPregledFile.write(
						"<td> "+ j++ +"</td>"+
								"<td><a href=\""+"http://www.openstreetmap.org/user/"+ entry.getKey()+ "\" >"+entry.getKey()+"</a></td><td>"+
								mBrojAktivnihIzmenaKorisnika.get(entry.getKey())+"</td><td>"+
								mDatumPrvi.get(entry.getKey())+"</td><td>"+
								mDatumPoslednji.get(entry.getKey())+"</td><td></tr> "
						);


			}	
			mOutPregledFile.close();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




public void printStatistikaPoDanu(){


	try {
		mOutPregledFile= new FileWriter("statistikPoDanu.txt");
		mOutPregledFile.write("Dan\tBroj izmena\n");


		for (Map.Entry entry : mBrojAktivnihIzmenaZaDan.entrySet()) {
//		    System.out.print("key,val: " + entry.getKey() + "," + entry.getValue());
			mOutPregledFile.write(entry.getKey() + "\t" + entry.getValue()+"\n");
		}
		
		mOutPregledFile.close();	
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}




// za sortiranje vrednosti u HashMap - Integer (za broj aktivnih izmena)
class ValueComparator implements Comparator<String> {

	Map<String, Integer> base;
	public ValueComparator(HashMap<String, Integer> mBrojAktivnihIzmena) {
		this.base = mBrojAktivnihIzmena;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.    
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}



//za sortiranje vrednosti u HashMap - String
class ValueComparatorString implements Comparator<String> {

	Map<String, String> base;
	public ValueComparatorString(HashMap<String, String> map) {
		this.base = map;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.    
	public int compare(String a, String b) {
		if (base.get(a).compareTo(base.get(b)) > 0) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}