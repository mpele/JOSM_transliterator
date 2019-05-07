
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class OSMParser2 
{
	FileWriter mOutFile;

	PripremaZaRenderOSM pripremaZaRenderOSM = new PripremaZaRenderOSM();

	boolean mbGenerisanjeNovOSMFajl = false; // menja se ako se definise izlazni fajl

	private String mNazivUlaznogFajla;
	public static void main(String args[])
	{
		String izlazniFajl = null;
		String mUlazniFajl = null;
		String arg;
		OSMParser2 parser = new OSMParser2();
		if(args.length==0)
		{
			System.out.println("-izlaz=");
			System.out.println("\tIzlazni fajl");
			System.out.println("-ulaz=");
			System.out.println("\tUlazni fajl");

			System.out.println("-forsirajZamenuTagaName");
			System.out.println("\tAzurira tag name sa prvim dostupnm tagom iz liste koja sledi.");

			System.out.println("\n\nPrimer za popunjavanje taga name:sr : \njava -cp serbiantransliterator.jar OSMParser -ulaz=serbia.osm -izlaz=rezultat.osm -azuriratiNameSr");

			System.out.println("Primer za popunjavanje taga name sa drugim tagovima: \njava -cp serbiantransliterator.jar OSMParser -ulaz=serbia.osm -izlaz=rezultat.osm -forsirajZamenuTagaName name:sr name:sr-Latn");

			return;
		}

		int i = 0;
		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

			if (arg.equals("-forsirajZamenuTagaName")) { // kada je forsirajZamenuTagaName ne obazire se na ostale parametre 
				List<String> listaNameTagova = new ArrayList<String>();
				while (i < args.length) {					
					listaNameTagova.add(args[i++]);
				}
				//parser.setTagoveZaForsirajZamenuTagaName(listaNameTagova);
				System.out.println("forsirana zamena tagova: " + listaNameTagova);
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

	/**
	 * Ako izlazni fajl nije definisan i ne generise ga 
	 * @param nazivIzlaznogFajla
	 */
	public void setIzlazniFajl(String nazivIzlaznogFajla){
		mbGenerisanjeNovOSMFajl = true;
		// izlazni fajl
		try {
			mOutFile = new FileWriter(nazivIzlaznogFajla);
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

		// TODO u parametre !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// TODO u parametre !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// TODO u parametre !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// TODO u parametre !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// TODO u parametre !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		pripremaZaRenderOSM.setNameTagovi(Arrays.asList("name:sr", "name:sr-Latn", "name"));
		pripremaZaRenderOSM.setNameTagovi(Arrays.asList("name:sr-Latn", "Pname:sr", "name"));

		
		
		//izdvaja sam naziv fajla bez putanje i ekstenzije
		mNazivUlaznogFajla=ulazniFajl.replace(".osm", "");// brise ekstenziju - osm
		mNazivUlaznogFajla=mNazivUlaznogFajla.substring(mNazivUlaznogFajla.lastIndexOf('/')+1); //brise putanju

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

					elementString = srediElement(element, pripremaZaRenderOSM);
					ispis(elementString);

					element.clear();
					pripremaZaRenderOSM.clear();
					elementString="";
				}


				element.add(strLine);
				// dodaje sve tagove u preslovljavanje	
				pripremaZaRenderOSM.ucitajTagIzStringa(strLine);
			}

			// kopija koda iz glavne petlje

			// TODO staviti kopiju koda iz glavne petlje - za poslednji element !!!!!!!!!!!!!!!!!!!


			//Close the input stream
			in.close();
			//Close the output streams
			mOutFile.close();
		}catch (Exception e){//Catch exception if any
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("Gotovo");
	}


	private String srediElement(List<String> element, PripremaZaRenderOSM pripremaZaRenderOSM2) {
		String rezultat = "";
		for (String s : element)
		{
			if(s.contains("<tag k=\"name\"")){
				//System.out.println("name");
				rezultat += "\t\t<tag k=\"name\" v=\""+pripremaZaRenderOSM2.getName() + "\" />\n";
			}
			else if(s.contains("<tag k=\"name:sr-Latn\"")){
				//System.out.println("name sr");
				rezultat += "\t\t<tag k=\"name:sr-Latn\" v=\""+pripremaZaRenderOSM2.getName_srLatn()+ "\" />\n";
			}
			else{
				rezultat += s + "\n";
			}
		}
		
		return rezultat;
	}
}
