
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import rs.iz.mpele.StringElemetnOSM;



public class OSMPregledPumpi 
{
	PumpaOSM pumpaOSM = new PumpaOSM();

	FileWriter mOutKmlFile;
	FileWriter mOutPregledFile;
	private String mNazivUlaznogFajla;
	static boolean mbSamoLPG = false;
	static boolean mbKml = false;

	public static void main(String args[])
	{

		int i = 0;
		String mUlazniFajl = null;
		String arg;
		OSMPregledPumpi parser = new OSMPregledPumpi();
		if(args.length==0)
		{

			System.out.println("-ulaz=");
			System.out.println("\tUlazni fajl");
			System.out.println("-lpg");
			System.out.println("-kml");

			System.out.println("\n\nPrimer za popunjavanje taga name:sr : \njava -cp serbiantransliterator.jar OSMParser -ulaz=serbia.osm -izlaz=rezultat.osm -azuriratiNameSr");

			return;
		}

		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

			if (arg.startsWith("-lpg")) {
				mbSamoLPG = true;
			}
			else if (arg.startsWith("-kml")) {
				mbKml = true;
			}
			else if (arg.startsWith("-ulaz=")) {
				mUlazniFajl = arg.substring(6); // od stringa 
				System.out.println("Ulazni fajl: "+mUlazniFajl);
			}
			else{
				System.out.println("\n!! Nepoznat argument !!");
			}

		}


		if(mUlazniFajl==null)
			parser.run("serbia.osm");
		else
			parser.run(mUlazniFajl);
	}



	/**
	 * Ako izlazni fajl nije definisan i ne generise ga 
	 * @param nazivIzlaznogFajla
	 */

	public void setIzlazniKmlFajl(String nazivIzlaznogFajla){


		// ako ne pravi kml i ne kreira ga
		if(!mbKml)
			return;
		
		
		String lpgString = "http://maps.google.com/mapfiles/kml/shapes/caution.png";
		String pumpaString = "http://maps.google.com/mapfiles/kml/shapes/gas_stations.png";
		
		String znakZaPumpu;
		
		if(mbSamoLPG)
			znakZaPumpu = lpgString;
		else
			znakZaPumpu = pumpaString;
			
			
		try {
			mOutKmlFile = new FileWriter(nazivIzlaznogFajla);
			mOutKmlFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">"+
					"<Document>"+
					"<Style id=\"sn_gas_stations\">"+
					"<IconStyle>"+
					"<scale>1.2</scale>"+
					"<Icon>"+
					"<href>"+znakZaPumpu+"</href>"+
					"</Icon>"+
					"<hotSpot x=\"0.5\" y=\"0\" xunits=\"fraction\" yunits=\"fraction\"/>"+
					"</IconStyle>"+
					"<ListStyle>"+
					"</ListStyle>"+
					"</Style>"+
					"<StyleMap id=\"msn_gas_stations\">"+
					"<Pair>"+
					"<key>normal</key>"+
					"<styleUrl>#sn_gas_stations</styleUrl>"+
					"</Pair>"+
					"<Pair>"+
					"<key>highlight</key>"+
					"<styleUrl>#sh_gas_stations</styleUrl>"+
					"</Pair>"+
					"</StyleMap>"+
					"<Style id=\"sh_gas_stations\">"+
					"<IconStyle>"+
					"<scale>1.4</scale>"+
					"<Icon>"+
					"<href>"+znakZaPumpu+"</href>"+
					"</Icon>"+
					"<hotSpot x=\"0.5\" y=\"0\" xunits=\"fraction\" yunits=\"fraction\"/>"+
					"</IconStyle>"+
					"<ListStyle>"+
					"</ListStyle>"+
					"</Style>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public void closeKmlFile(){

		// ako ne generise kml fajl onda ga i ne zatvara
		if(!mbKml)
			return;

		try {
			mOutKmlFile.write("</Document></kml>");
			mOutKmlFile.close();
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

		// za pumpe koje nisu definisane kao node vec kao way - da pamti za drugi krug
		List<PomKmlBlokKadaNijeNode> lPumpeWay=new ArrayList<PomKmlBlokKadaNijeNode>();

		//izdvaja sam naziv fajla bez putanje i ekstenzije
		mNazivUlaznogFajla=ulazniFajl.replace(".osm", "");// brise ekstenziju - osm
		mNazivUlaznogFajla=mNazivUlaznogFajla.substring(mNazivUlaznogFajla.lastIndexOf('/')+1); //brise putanju

		mNazivUlaznogFajla=mNazivUlaznogFajla+"_pumpe"+(mbSamoLPG?"_lpg":"");
		setIzlazniKmlFajl(mNazivUlaznogFajla+".kml");

		try{
			// Open the file  


			mOutPregledFile= new FileWriter(mNazivUlaznogFajla+".html");

			FileInputStream fstream = new FileInputStream(ulazniFajl);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;

			mOutPregledFile.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
					//za vertikalan tekst u zaglavlju
					"<style> th { background-color: grey; color: white; text-align: center;vertical-align: bottom; height: 150px; padding-bottom: 3px; padding-left: 5px; padding-right: 5px; }"+
					" .verticalText { text-align: center; vertical-align: middle; width: 20px; margin: 0px; padding: 0px; padding-left: 3px; padding-right: 3px; padding-top: 10px; white-space: nowrap; -webkit-transform: rotate(-90deg); -moz-transform: rotate(-90deg);  }; </style>"+
					"</head><body>");
			mOutPregledFile.write("<table  border=1> <tr> <th>ID</th> <th>JOSM</th> <th>name</th> <th>brand</th> <th><div class=\"verticalText\">opening_hours</div></th> <th><div class=\"verticalText\">fuel:lpg</div></th> <th><div class=\"verticalText\">octane_95</div></th> <th><div class=\"verticalText\">fuel:diesel</div></th> </tr>");


			//String newline = System.getProperty("line.separator");


			// Glavna petlja
			while ((strLine = br.readLine()) != null) {
				if(strLine.contains("<node ") | strLine.contains("<way ")| strLine.contains("<relation ") ){  // ako je pocetak novog node
					if(pumpaOSM.daLiJePumpa()){
						if (mbSamoLPG == false || (mbSamoLPG == true && pumpaOSM.daLiTociLPG() == true))
						{
							mOutPregledFile.write(
									"<tr> <td><a href=\""+pumpaOSM.getHtmlLinkZaBrowser()+ "\" >"+pumpaOSM.getId()+"</a></td><td>"+
											"<a href=\""+pumpaOSM.getHtmlLinkZaJOSM()+"\">JOSM</a></td><td>"+
											pumpaOSM.getTagValue("name")+"</td><td>"+
											pumpaOSM.getTagValue("brand")+"</td><td>"+
											pumpaOSM.getTagValue("opening_hours")+"</td><td>"+
											pumpaOSM.getTagValue("fuel:lpg")+"</td><td>"+
											pumpaOSM.getTagValue("fuel:octane_95")+"</td><td>"+
											pumpaOSM.getTagValue("fuel:diesel")+"<td/></tr> "										
									);

							// ako se pravi kml fajl onda ga i popunjava
							if(mbKml){
								if(!pumpaOSM.getTipObjekta().equals("node")){ 
									// ako nije node pamti ga za drugi krug da bi mu se nasle koordinate
									System.out.println("nije node "+pumpaOSM.getJedanOdNodeId());
									lPumpeWay.add(new PomKmlBlokKadaNijeNode(pumpaOSM.getJedanOdNodeId(), pumpaOSM.getKmlPlacemark()));
								}
								else // ako je node upisuje u kml fajl
									mOutKmlFile.write(pumpaOSM.getKmlPlacemark());
							}
						}
					}
					pumpaOSM.clear();

					// za pocetak novog zapisa
					pumpaOSM.ucitajZaglavljeTagovaIzStringa(strLine);
				}

				// dodaje sve tagove u preslovljavanje
				pumpaOSM.ucitajTagIzStringa(strLine);
			}



			// drugi krug - ako ima pumpi koje nisu definisane kao node onda trazi jednu od njihovih referenci
			if(lPumpeWay.size()>0){
				fstream.close();
				fstream = new FileInputStream(ulazniFajl);
				in = new DataInputStream(fstream);
				br = new BufferedReader(new InputStreamReader(in));
				System.out.println("drugi krug..."+br.readLine());
				while ((strLine = br.readLine()) != null) {
					if(strLine.contains("<node") ){  
						int pomA = strLine.indexOf(" id=");
						int pomB = strLine.indexOf('"', pomA+6);

						if(Long.parseLong( strLine.substring(pomA+5, pomB) ) == lPumpeWay.get(0).getId()){
							//System.out.println("nasao ");
							mOutKmlFile.write(lPumpeWay.get(0).getKmlString(strLine));

							lPumpeWay.remove(0);
							if(lPumpeWay.size()==0)
								break;
						}
					}
				}


			}


			//Close the input stream
			in.close();
			closeKmlFile();
			mOutPregledFile.close();



		}catch (Exception e){//Catch exception if any
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("Gotovo");
	}


}







////////////////////////////////////////////////////////////////////////////////////////////////////////////

class PumpaOSM extends StringElemetnOSM{

	/**
	 * Proverava da li je to pumpa
	 * @return
	 */
	public Boolean daLiJePumpa(){
		if(getTagValue("amenity").equals("fuel"))
			return true;
		else
			return false;
	}


	/**
	 * Proverava da li toci LPG
	 * @return
	 */
	public Boolean daLiTociLPG(){
		if(getTagValue("fuel:lpg").equals("yes"))
			return true;
		else
			return false;
	}

	public String getKmlPlacemark(){
		String izlaz = 
				"	<Placemark>\n"+
						"		<name>"+getTagValue("name")+"</name>\n"+
						"		<description><![CDATA[\n"+
						" Linkovi za obradu:" +
						"<a href=\""+getHtmlLinkZaBrowser()+ "\" >"+getId()+"</a> - "+
						"<a href=\""+getHtmlLinkZaJOSM()+"\">JOSM</a><br/>\n"+ 
						getHtmlSviTagovi()+
						" \n"+
						" ]]>\n"+
						"		</description>"+
						"		<styleUrl>#msn_gas_stations</styleUrl>\n"+
						"		<Point>\n"+
						"			<coordinates>"+getLon()+","+getLat()+",0</coordinates>\n"+
						"		</Point>\n"+
						"	</Placemark>\n";
		return izlaz;
	}

}







////////////////////////////////////////////////////////////////////////////////////////////////////////////

class PomKmlBlokKadaNijeNode{
	long mId;
	String mKmlString;

	PomKmlBlokKadaNijeNode(long id, String kmlString){
		mId = id;
		mKmlString = kmlString;
	}


	public long getId(){
		return mId;
	}

/**
 * Menja koordinate 
 * @param strLine
 * @return
 */
	public String getKmlString(String strLine){
		int pomA = strLine.indexOf(" lat=");
		int pomB = strLine.indexOf('"', pomA+7);
		String lat = strLine.substring(pomA+6, pomB) ;

		pomA = strLine.indexOf(" lon=");
		pomB = strLine.indexOf('"', pomA+7);
		String lon = strLine.substring(pomA+6, pomB) ;		

		return mKmlString.replace("<coordinates>-999.0,-999.0,0</coordinates>", "<coordinates>"+lon+","+lat+",0</coordinates>");
	}
}