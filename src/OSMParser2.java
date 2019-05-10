
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

	public PripremaZaRenderOSM pripremaZaRenderOSM = new PripremaZaRenderOSM();

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

			System.out.println("prva grupa tagova (razdvojenih sa '@') je za name a druga za name:sr-Latn ");
			System.out.println("ako za name:sr-Latn tag pocinje sa 'P' radi se preslovljavanje u latinicu ");

			System.out.println("Primer za popunjavanje taga name sa drugim tagovima: \njava -cp serbiantransliterator.jar OSMParser2 -ulaz=serbia.osm -izlaz=rezultat.osm name:sr;name name:sr-Latn;Pname:sr");

			return;
		}

		int i = 0;
		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

			// izlazni fajl sa primenjenim svim izmenama
			if (arg.startsWith("-izlaz=")) {
				izlazniFajl = arg.substring(7); // od stringa 
				System.out.println("Izlazni fajl: "+izlazniFajl);
			}
			// izlazni fajl sa primenjenim svim izmenama
			else if (arg.startsWith("-ulaz=")) {
				mUlazniFajl = arg.substring(6); // od stringa 
				System.out.println("Ulazni fajl: "+mUlazniFajl);
			}
		}
		
		parser.pripremaZaRenderOSM.setNameTagovi(Arrays.asList(args[i++].split("@")));
		parser.pripremaZaRenderOSM.setNameSrLatTagovi(Arrays.asList(args[i++].split("@")));


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
		
		//izdvaja sam naziv fajla bez putanje i ekstenzije
		mNazivUlaznogFajla=ulazniFajl.replace(".osm", "");// brise ekstenziju - osm
		mNazivUlaznogFajla=mNazivUlaznogFajla.substring(mNazivUlaznogFajla.lastIndexOf('/')+1); //brise putanju

		try{
			// Open the file  
			FileInputStream fstream = new FileInputStream(ulazniFajl);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			String elementString = null;

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

			// poslednji element i zavrsetak fajla
			elementString = srediElement(element, pripremaZaRenderOSM);
			ispis(elementString);


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
				if(!pripremaZaRenderOSM2.daLiJeDefinisanTag("name:sr-Latn")){
					rezultat += "\t\t<tag k=\"name:sr-Latn\" v=\""+pripremaZaRenderOSM2.getName_srLatn()+ "\" />\n";
				}
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
