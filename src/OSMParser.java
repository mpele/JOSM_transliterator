
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.plugins.serbiantransliterator.PodrazumevanoPismo;
import org.openstreetmap.josm.plugins.serbiantransliterator.PreslovljavanjeOSM;


public class OSMParser 
{

	FileWriter mOutFile;
	FileWriter mOutPregledFile;
	FileWriter mOutZaLekturu;

	PreslovljavanjeOSM preslovljavanjeOSM = new PreslovljavanjeOSM();

	boolean mbGenerisanjeDiffFajlova = false;
	boolean mbGenerisanjeLekturaFajlova = false;
	boolean mbGenerisanjeNovOSMFajl = false; // menja se ako se definise izlazni fajl


	boolean mbAzuriratiName = false;
	boolean mbAzuriratiNameSr = false;
	boolean mbAzuriratiNameSrLatn = false;

	private PodrazumevanoPismo emPodrazumevanoPismo = PodrazumevanoPismo.BEZ_PROMENE;

	int mBrojacDiffFajlova = 0;
	int mBrojacFajlovaZaLekturu = 1;
	int mBrojacElemenataZaLekturu = 0;

	private String mNazivUlaznogFajla;

	public static void main(String args[])
	{

		int i = 0;
		String izlazniFajl = null;
		String mUlazniFajl = null;
		String arg;
		OSMParser parser = new OSMParser();
		if(args.length==0)
		{
			System.out.println("-izlaz=");
			System.out.println("\tIzlazni fajl");
			System.out.println("-ulaz=");
			System.out.println("\tUlazni fajl");

			System.out.println("-lektura");
			System.out.println("\tGenerise fajlove za lekturu.");
			System.out.println("-diff");
			System.out.println("\tGenerise diff fajlove.");

			System.out.println("-name=bezpromene");
			System.out.println("\tNe menja tag name.");
			System.out.println("-name=cirilica");
			System.out.println("\tPodrazumevano pismo za tag name je ćirilica.");
			System.out.println("-name=latinica");
			System.out.println("\tPodrazumevano pismo za tag name je latinica.");
			System.out.println("-azuriratiName");
			System.out.println("\tAzurira tag name.");
			System.out.println("-azuriratiNameSr");
			System.out.println("\tAzurira tag name:sr.");
			System.out.println("-azuriratiNameSrLatn");
			System.out.println("\tAzurira tag name:sr-Latn.");			


			System.out.println("\n\nPrimer za popunjavanje taga name:sr : \njava -cp serbiantransliterator.jar OSMParser -ulaz=serbia.osm -izlaz=rezultat.osm -azuriratiNameSr");

			return;
		}

		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

			// podrazumevano pismo za tag name
			if (arg.equals("-name=bezpromene")) {
				parser.setPodrazumevanoPismo(PodrazumevanoPismo.BEZ_PROMENE);
				System.out.println("*Ne menja tag name.");
			}
			else if (arg.equals("-name=cirilica")) {
				parser.setPodrazumevanoPismo(PodrazumevanoPismo.CIRILICA);
				parser.setAzuriratiName(true);
				System.out.println("Podrazumevano pismo za tag name je ćirilica.");
			}
			else if (arg.equals("-name=latinica")) {
				parser.setPodrazumevanoPismo(PodrazumevanoPismo.LATINICA);
				parser.setAzuriratiName(true);
				System.out.println("Podrazumevano pismo za tag name je latinica.");
			}


			// sta se menja
			else if (arg.equals("-azuriratiName")) {
				parser.setAzuriratiName(true);
				System.out.println("Azurira tag name.");
			}
			else if (arg.equals("-azuriratiNameSr")) {
				parser.setAzuriratiNameSr(true);
				System.out.println("Azurira tag name:sr.");
			}
			else if (arg.equals("-azuriratiNameSrLatn")) {
				parser.setAzuriratiNameSrLatn(true);
				System.out.println("Azurira tag name:sr-Latn.");			
			}

			// da li se prave lekturisani fajlovi
			else if (arg.equals("-lektura")) {
				parser.setGenerisanjeLekturisaniFajlovi(true);
				System.out.println("Generise fajlove za lekturu.");
			}
			// da li se generisu diff fajlovi
			else if (arg.equals("-diff")) {
				parser.setGenerisanjeDiffFajlovi(true);
				System.out.println("Generise diff fajlove.");
			}


			// izlazni fajl sa primenjenim svim izmenama
			else if (arg.startsWith("-izlaz=")) {
				izlazniFajl = arg.substring(7); // od stringa 
				System.out.println("Izlazni fajl: "+izlazniFajl);
			}
			// izlazni fajl sa primenjenim svim izmenama
			else if (arg.startsWith("-ulaz=")) {
				mUlazniFajl = arg.substring(6); // od stringa 
				System.out.println("Ulazni fajl: "+mUlazniFajl);
			}
			else{
				System.out.println("\n!! Nepoznat argument !!");
			}

		}


		//		parser.setGenerisanjeDiffFajlovi(true);
		//		parser.setGenerisanjeLekturisaniFajlovi(true);
		//		parser.setIzlazniFajl("rezultat.osm");
		//parser.setPodrazumevanoPismo(PodrazumevanoPismo.LATINICA);

		//za upload
		//		parser.setPodrazumevanoPismo(PodrazumevanoPismo.BEZ_PROMENE);
		//		parser.setAzuriratiName(false);
		//		parser.setAzuriratiNameSr(true);
		//		parser.setAzuriratiNameSrLatn(true);

		if(izlazniFajl!=null)
			parser.setIzlazniFajl(izlazniFajl);
		else
			parser.setIzlazniFajl("rezultat.osm");

		if(mUlazniFajl==null)
			parser.run("serbia.osm");
		else
			parser.run(mUlazniFajl);
	}


	public OSMParser(){

	}

	public void setGenerisanjeLekturisaniFajlovi(Boolean bool){
		mbGenerisanjeLekturaFajlova = bool;
	}
	public void setGenerisanjeDiffFajlovi(Boolean bool){
		mbGenerisanjeDiffFajlova = bool;
	}



	private void ispis(String string){
		if(mbGenerisanjeNovOSMFajl){ 
			try {
				mOutFile.write(string);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void ispisZaLekturu(String string){
		if(mbGenerisanjeLekturaFajlova){ 
			try {
				if(mBrojacElemenataZaLekturu == 0){
					mOutZaLekturu.write("<?xml version='1.0' encoding='UTF-8'?>\n<osm version='0.6' generator='JOSM'>\n");
				}
				mOutZaLekturu.write(string);
				mBrojacElemenataZaLekturu++;
				if(mBrojacElemenataZaLekturu >100){
					mOutZaLekturu.write("</osm>");
					mOutZaLekturu.close();
					mOutZaLekturu = new FileWriter("lektura_"+mNazivUlaznogFajla+"_"+(mBrojacFajlovaZaLekturu++)+".osm");
					mBrojacElemenataZaLekturu = 0;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	private void generisiDiffFajl(String buffer){
		try {
			FileWriter outFile;
			outFile = new FileWriter("izmena"+(mBrojacDiffFajlova++)+"_d.osc");
			outFile.write("<?xml version='1.0' encoding='UTF-8'?>\n");
			outFile.write("<osmChange version=\"0.6\" generator=\"Preslovljavanje OSM\">\n");
			outFile.write("<modify>\n");
			outFile.write(buffer);
			outFile.write("</modify>\n");
			outFile.write("</osmChange>");
			outFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Problem sa generisanjem diff fajla.");
		}
	}

	/**
	 * Ako izlazni fajl nije definisan i ne generise ga 
	 * @param nazivIzlaznogFajla
	 */

	public void setIzlazniFajl(String nazivIzlaznogFajla){
		mbGenerisanjeNovOSMFajl = true;
		// izlazni fajl
		try {
			mOutFile = new FileWriter(nazivIzlaznogFajla);
			mOutPregledFile= new FileWriter("pregled.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	/**
	 * Startuje parser sa ulaznim fajlom
	 * @param nazivUlaznogFajla
	 */
	public void run(String ulazniFajl){

		//izdvaja sam naziv fajla bez putanje i ekstenzije
		mNazivUlaznogFajla=ulazniFajl.replace(".osm", "");// brise ekstenziju - osm
		mNazivUlaznogFajla=mNazivUlaznogFajla.substring(mNazivUlaznogFajla.lastIndexOf('/')+1); //brise putanju

		//TODO smestiti ovo negde drugde !!!!!!!!!!!!!!!
		if(mbGenerisanjeLekturaFajlova){ 
			try {
				mOutZaLekturu = new FileWriter("lektura_"+mNazivUlaznogFajla+".osm");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// !!!!!!!!!!!!!!!!!!!!!!

		try{
			// Open the file  
			FileInputStream fstream = new FileInputStream(ulazniFajl);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			String elementString;

			List<String> element = new ArrayList<String>();

			//String newline = System.getProperty("line.separator");

			while ((strLine = br.readLine()) != null) {
				if(strLine.contains("<node ") | strLine.contains("<way ")| strLine.contains("<relation ") ){  // ako je pocetak novog node
					if(preslovljavanjeOSM.daLiImaIzmena()){ //  ako ima izmena
						if(preslovljavanjeOSM.daLiJeBezbednoPreslovljavanje()){ // i ako je bezbedno
							elementString = srediElement(element, preslovljavanjeOSM);
							ispis(elementString);
							//				System.out.println("--------------------"+preslovljavanjeOSM.pregled());
							mOutPregledFile.write(preslovljavanjeOSM.pregled());
							
							// Ako ima izmena generise diff fajl ili upisuje u lekturu 
							if(mbGenerisanjeDiffFajlova){
								generisiDiffFajl(elementString);
							}
							else {
								ispisZaLekturu(elementToString(element));								
							}
							
						}
						else{ // ako nije bezbedno - za lektorisanje
							ispisZaLekturu(elementToString(element));
						}
					}
					else {
						ispis(elementToString(element));
					}
					element.clear();
					preslovljavanjeOSM.clear();
					elementString="";
				}


				element.add(strLine);
				// dodaje sve tagove u preslovljavanje	
				preslovljavanjeOSM.ucitajTagIzStringa(strLine);
			}

			// kopija koda iz glavne petlje
			if(preslovljavanjeOSM.daLiImaIzmena() // stampa ako ima izmena
					&& preslovljavanjeOSM.daLiJeBezbednoPreslovljavanje()){ // i ako je bezbedno
				elementString = srediElement(element, preslovljavanjeOSM);
				ispis(elementString);
				//				System.out.println("--------------------"+preslovljavanjeOSM.pregled());
				mOutPregledFile.write(preslovljavanjeOSM.pregled());
				if(mbGenerisanjeDiffFajlova){
					generisiDiffFajl(elementString);
				}
			}
			else {
				ispis(elementToString(element));
			}


			//Close the input stream
			in.close();
			//Close the output streams
			mOutFile.close();
			mOutPregledFile.close();
			
			try {
				mOutZaLekturu.write("</osm>");
				mOutZaLekturu.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// ako ne moze da ga zatvori i ne treba da ista radi
			}

		}catch (Exception e){//Catch exception if any
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("Gotovo");
	}


	private String elementToString(List<String> element){
		String rezultat = "";
		for (String s : element)
		{
			rezultat += s+"\n";
		}
		return rezultat;

	}

	/**
	 * Vraca string sa korigovanim vrednostima tagova
	 * @param element
	 * @param preslovljavanje
	 * @return
	 */
	private String srediElement(List<String> element, PreslovljavanjeOSM preslovljavanje){
		String rezultat = "";
		boolean bName = false, 
				bName_sr = false, 
				bName_sr_latn = false;
		for (String s : element)
		{
			if(s.contains("<tag k=\"name\"") && mbAzuriratiName){
				//System.out.println("name");
				rezultat += "\t\t<tag k=\"name\" v=\""+preslovljavanje.getName() + "\" />\n";
				bName = true;
			}
			else if(s.contains("<tag k=\"name:sr\"") && mbAzuriratiNameSr){
				//System.out.println("name sr");
				rezultat += "\t\t<tag k=\"name:sr\" v=\""+preslovljavanje.getName_sr()+ "\" />\n";
				bName_sr = true;
			}			
			else if(s.contains("<tag k=\"name:sr-Latn\"") && mbAzuriratiNameSrLatn){
				//System.out.println("name sr");
				rezultat += "\t\t<tag k=\"name:sr-Latn\" v=\""+preslovljavanje.getName_sr_lat()+ "\" />\n";
				bName_sr_latn = true;
			}
			else{
				rezultat += s + "\n";
			}
		}
		// dodaje stringove koji fale
		if(bName == false && mbAzuriratiName){
			rezultat = rezultat.replaceFirst("<tag k", "<tag k=\"name\" v=\""+preslovljavanje.getName_sr()+ "\" />\n\t\t<tag k");
		}
		if(bName_sr == false && mbAzuriratiNameSr){
			//System.out.println("*name sr");
			rezultat = rezultat.replaceFirst("<tag k", "<tag k=\"name:sr\" v=\""+preslovljavanje.getName_sr()+ "\" />\n\t\t<tag k");
		}
		if(bName_sr_latn == false && mbAzuriratiNameSrLatn){
			rezultat = rezultat.replaceFirst("<tag k", "<tag k=\"name:sr-Latn\" v=\""+preslovljavanje.getName_sr_lat()+ "\" />\n\t\t<tag k");
		}
		return rezultat;
	}


	/**
	 * Postavlja podrazumevano pismo
	 * @param ppismo
	 */
	public void setPodrazumevanoPismo(PodrazumevanoPismo ppismo){
		emPodrazumevanoPismo = ppismo;
		preslovljavanjeOSM.setPodrazumevanoPismo(emPodrazumevanoPismo);
	}

	public void setAzuriratiName(boolean bool){		
		mbAzuriratiName = bool;
		preslovljavanjeOSM.setAzuriratiName(mbAzuriratiName);
	}

	public void setAzuriratiNameSr(boolean bool){		
		mbAzuriratiNameSr = bool;
		preslovljavanjeOSM.setAzuriratiNameSr(mbAzuriratiNameSr);
	}

	public void setAzuriratiNameSrLatn(boolean bool){		
		mbAzuriratiNameSrLatn = bool;
		preslovljavanjeOSM.setAzuriratiNameSrLatn(mbAzuriratiNameSrLatn);
	}

}
