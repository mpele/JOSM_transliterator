
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import org.openstreetmap.josm.plugins.serbiantransliterator.PodrazumevanoPismo;
import org.openstreetmap.josm.plugins.serbiantransliterator.PreslovljavanjeOSM;


public class PregledNaziva 
{

	FileWriter mOutPregledFile;
//	FileWriter mOutPregledZaMesecFile;

	PreslovljavanjeOSM preslovljavanjeOSM = new PreslovljavanjeOSM();

	boolean mbAzuriratiName = false;
	boolean mbAzuriratiNameSr = false;
	boolean mbAzuriratiNameSrLatn = false;

	int mBrojacDiffFajlova = 0;
	int mBrojacFajlovaZaLekturu = 1;
	int mBrojacElemenataZaLekturu = 0;

	private String mNazivUlaznogFajla;

	public static void main(String args[])
	{

		int i = 0;
		String ulazniFajl = null;
		String arg;
		PregledNaziva parser = new PregledNaziva();
		if(args.length==0)
		{
			//			System.out.println("-izlaz=");
			//			System.out.println("\tIzlazni fajl");
			System.out.println("-ulaz=");
			System.out.println("\tUlazni fajl");
			//
			//			System.out.println("-lektura");
			//			System.out.println("\tGenerise fajlove za lekturu.");
			//			System.out.println("-diff");
			//			System.out.println("\tGenerise diff fajlove.");
			//
			//			System.out.println("-name=bezpromene");
			//			System.out.println("\tNe menja tag name.");
			//			System.out.println("-name=cirilica");
			//			System.out.println("\tPodrazumevano pismo za tag name je ćirilica.");
			//			System.out.println("-name=latinica");
			//			System.out.println("\tPodrazumevano pismo za tag name je latinica.");
			//			System.out.println("-azuriratiName");
			//			System.out.println("\tAzurira tag name.");
			//			System.out.println("-azuriratiNameSr");
			//			System.out.println("\tAzurira tag name:sr.");
			//			System.out.println("-azuriratiNameSrLatn");
			//			System.out.println("\tAzurira tag name:sr-Latn.");			
			//
			//
			//			System.out.println("\n\nPrimer za popunjavanje taga name:sr : \njava -cp serbiantransliterator.jar OSMParser -ulaz=serbia.osm -izlaz=rezultat.osm -azuriratiNameSr");
			//
			return;
		}

		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

			// podrazumevano pismo za tag name
			if (arg.equals("-name=bezpromene")) {
				System.out.println("*Ne menja tag name.");
			}			
			else if (arg.startsWith("-ulaz=")) {
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

			mOutPregledFile= new FileWriter(mNazivUlaznogFajla+".html");
//			mOutPregledZaMesecFile= new FileWriter(mNazivUlaznogFajla+"_30dana.html");

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;

			mOutPregledFile.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head><body>");
			mOutPregledFile.write("<table  border=1> <tr> <th>ID</th> <th>JOSM</th> <th>Tip</th> <th>name:sr</th> <th>name:sr-Latn</th> <th>name</th> </tr>");

			//mOutPregledZaMesecFile.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head><body>");
			//mOutPregledZaMesecFile.write("<table  border=1> <tr> <th>ID</th> <th>Tip</th> <th>name:sr</th> <th>name:sr-Latn</th> <th>name</th> </tr>");


			//String newline = System.getProperty("line.separator");

			while ((strLine = br.readLine()) != null) {
				if(strLine.contains("<node ") | strLine.contains("<way ")| strLine.contains("<relation ") ){  // ako je pocetak novog node
					if(preslovljavanjeOSM.daLiImaTagovaZaNaziv() && !preslovljavanjeOSM.daLiImaIzmena()){ //  ako nema izmena
						mOutPregledFile.write(
								"<tr> <td><a href=\""+preslovljavanjeOSM.getHtmlLinkZaBrowser()+ "\" >"+preslovljavanjeOSM.getId()+"</a></td><td>"+
										"<a href=\""+preslovljavanjeOSM.getHtmlLinkZaJOSM()+"\">JOSM</a></td><td>"+
										preslovljavanjeOSM.getTipObjekta()+"</td><td>"+
										preslovljavanjeOSM.getOriginalName_sr()+"</td><td>"+
										preslovljavanjeOSM.getOriginalName_sr_lat()+"</td><td>"+
										preslovljavanjeOSM.getOriginalName()+"<td/></tr> "										
								);
					}
					preslovljavanjeOSM.clear();

					
					// za pocetak novog zapisa
					preslovljavanjeOSM.ucitajZaglavljeTagovaIzStringa(strLine);
				}

				// dodaje sve tagove u preslovljavanje
				preslovljavanjeOSM.ucitajTagIzStringa(strLine);
			}


			//Close the input stream
			in.close();

			mOutPregledFile.close();
//			mOutPregledZaMesecFile.close();


		}catch (Exception e){//Catch exception if any
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("Gotovo");
	}


}
