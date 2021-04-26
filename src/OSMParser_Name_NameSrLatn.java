/**
 * Ovaj fajl se koristi za definisanje taga name i name:sr-Latn
 * 
 */


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class OSMParser_Name_NameSrLatn
{
	
	OutputStreamWriter mOutFile;

	public PripremaZaRenderOSM pripremaZaRenderOSM = new PripremaZaRenderOSM();

	boolean mbIspisUNovOSMFajl = false; // menja se ako se definise izlazni fajl
	boolean debug = false;

	private String mNazivUlaznogFajla;
	public static void main(String args[])
	{
		String izlazniFajl = null;
		String mUlazniFajl = null;
		String arg;
		OSMParser_Name_NameSrLatn parser = new OSMParser_Name_NameSrLatn();
		if(args.length==0)
		{
			System.out.println("Cita stdin i obradjuje \n Koristiti parametar -h za pomoc");
		}

		int i = 0;
		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

			if (arg.startsWith("-h")) {
				parser.porurkaZaHelp();
			}
			// izlazni fajl sa primenjenim svim izmenama
			if (arg.startsWith("-izlaz=")) {
				izlazniFajl = arg.substring(7); // od stringa 
				parser.setIzlazniFajl(izlazniFajl);
			}
			// izlazni fajl sa primenjenim svim izmenama
			else if (arg.startsWith("-ulaz=")) {
				mUlazniFajl = arg.substring(6); // od stringa 
			}
		}

		parser.pripremaZaRenderOSM.setNameTagovi(Arrays.asList(args[i++].split("@")));
		parser.pripremaZaRenderOSM.setNameSrLatTagovi(Arrays.asList(args[i++].split("@")));
		if(args.length > i){
			parser.setDebug(true);
		}

		System.out.println("Ulazni fajl: "+mUlazniFajl);
		System.out.println("Izlazni fajl: "+izlazniFajl);

		parser.run(mUlazniFajl);
	}

	protected void porurkaZaHelp() {
		System.out.println("-h");
		System.out.println("\tispisuje ovu informaciju");
		System.out.println("-izlaz=");
		System.out.println("\tIzlazni fajl. Ako nije definisan izlazni fajl ispisuje na StdOut");
		System.out.println("-ulaz=");
		System.out.println("\tUlazni fajl");
	}

	private void setDebug(boolean b) {
		debug = b;
	}


	private void ispisUFajl(String string){
		if(mbIspisUNovOSMFajl){ 
			try {
				mOutFile.write(string);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			System.out.println(string);
		}
	}

	/**
	 * Ako izlazni fajl nije definisan i ne generise ga 
	 * @param nazivIzlaznogFajla
	 */
	public void setIzlazniFajl(String nazivIzlaznogFajla){
		mbIspisUNovOSMFajl = true;
		// izlazni fajl
		try {
			mOutFile = new OutputStreamWriter(new FileOutputStream(nazivIzlaznogFajla), StandardCharsets.UTF_8);
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
		InputStreamReader inputStreamReader;
		
		try{
			
			if(ulazniFajl != null) { // definisan naziv fajla
				//izdvaja sam naziv fajla bez putanje i ekstenzije
				mNazivUlaznogFajla=ulazniFajl.replace(".osm", "");// brise ekstenziju - osm
				mNazivUlaznogFajla=mNazivUlaznogFajla.substring(mNazivUlaznogFajla.lastIndexOf('/')+1); //brise putanju
				
				// Open the file  
				FileInputStream fstream = new FileInputStream(ulazniFajl);

				DataInputStream in = new DataInputStream(fstream);
				inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
			}
			else { // cita iz stdin
				inputStreamReader = new InputStreamReader(System.in);
			}

			BufferedReader br = new BufferedReader(inputStreamReader);;

			String strLine;
			String elementString = null;

			List<String> element = new ArrayList<String>();

			//String newline = System.getProperty("line.separator");

			boolean poceoDaObradjuje = false; // da preskoci zaglavlje
			while ((strLine = br.readLine()) != null) {

				if(strLine.contains("<node ") | strLine.contains("<way ")| strLine.contains("<relation ") ){  // ako je pocetak novog node
					elementString = srediElement(element, pripremaZaRenderOSM);
					ispisUFajl(elementString);

					element.clear();
					pripremaZaRenderOSM.clear();
					elementString="";
					poceoDaObradjuje = true;
					element.add(strLine);						
				}
				else{
					if(!poceoDaObradjuje){
						ispisUFajl(strLine+"\n");
					}
					else{
						element.add(strLine);						
						// dodaje sve tagove u preslovljavanje	
						pripremaZaRenderOSM.ucitajTagIzStringa(strLine);
					}
				}
			}

			// poslednji element i zavrsetak fajla
			elementString = srediElement(element, pripremaZaRenderOSM);
			ispisUFajl(elementString);

			//Close the output streams
			if(mbIspisUNovOSMFajl){				
				mOutFile.close();
			}
		}catch (Exception e){//Catch exception if any
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
		}
		if(mbIspisUNovOSMFajl){
			System.out.println("Gotovo");
		}
	}


	protected String srediElement(List<String> element, PripremaZaRenderOSM pripremaZaRenderOSM2) {
		String rezultat = "";
		String debugString = "";

		String id_elementa = "";

		if(element.isEmpty()){
			return "";
		}

		if(debug){
			int pomA = element.get(0).indexOf(" id=");
			int pomB = element.get(0).indexOf('"', pomA+6);
			id_elementa = element.get(0).substring(pomA+5, pomB);
			debugString = " " + id_elementa;
		}


		boolean foundName = false;
		boolean foundNameLatn = false;
		for (String s : element)
		{
			if (s.trim().startsWith("</")) {
				// Node/way/relation tag is closing, add missing
				if (!foundName) {
					String generatedName = pripremaZaRenderOSM2.getName();
					if (generatedName != null) {
						rezultat += "\t\t<tag k=\"name\" v=\"" + generatedName + debugString + "\" />\n";
					}
				}

				if (!foundNameLatn) {
					String generatedNameLatn = pripremaZaRenderOSM2.getName_srLatn();
					if (generatedNameLatn != null) {
						rezultat += "\t\t<tag k=\"name:sr-Latn\" v=\""+ generatedNameLatn + debugString + "\" />\n";
					}
				}
			}

			if(s.contains("<tag k=\"name\"")){
				foundName = true;
				//System.out.println("name");
				rezultat += "\t\t<tag k=\"name\" v=\""+pripremaZaRenderOSM2.getName() + debugString + "\" />\n";
			}
			else if(s.contains("<tag k=\"name:sr-Latn\"")){
				foundNameLatn = true;
				//System.out.println("name sr");
				rezultat += "\t\t<tag k=\"name:sr-Latn\" v=\""+pripremaZaRenderOSM2.getName_srLatn() + debugString + "\" />\n";
			}
			else{
				rezultat += s + "\n";
			}
		}

		return rezultat;
	}
}
