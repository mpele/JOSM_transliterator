import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.File;

import rs.iz.mpele.PreslovljavanjeRedaSaTagom;

public class ProstoPreslovljavanjeFajla 
{

	PreslovljavanjeRedaSaTagom preslovljavanje = new PreslovljavanjeRedaSaTagom();

	private String mNazivUlaznogFajla;
	ArrayList<String> mTagArrayList = new ArrayList<String>();
	boolean mbLatinica;
	Character mNavodnikCharacter;


	public static void main(String args[])
	{

		int i = 0;
		String ulazniFajl = null;
		String arg;
		boolean bLatinica = false;
		Character navodnikCharacter = '"';

		ArrayList<String> tagArrayList = new ArrayList<String>();


		while (i < args.length ) {
			arg = args[i++];

			if (arg.startsWith("-ulaz=")) {
				ulazniFajl = arg.substring(6); // od stringa 
				System.out.println("Ulazni fajl: "+ulazniFajl);
			}
			else if (arg.startsWith("-latinica")) {
				bLatinica = true;
			}
			else if (arg.startsWith("-cirilica")) {
				bLatinica = false;
			}
			else if (arg.startsWith("-josm")) {
				navodnikCharacter = '\'';
			}
			else{
				tagArrayList.add(arg);
			}
			
		}

		if(bLatinica)
			System.out.println("Preslovljava tagove u latinicu. ");
		else
			System.out.println("Preslovljava tagove u cirilicu. ");

		
		System.out.println("Tagovi koji se preslovljavaju: "+tagArrayList);
		System.out.println("Navodnik: "+ navodnikCharacter);
		
//		tagArrayList.add("addr:city");
//		tagArrayList.add("addr:street");
		
		ProstoPreslovljavanjeFajla parser = new ProstoPreslovljavanjeFajla();
		parser.setTagovi(tagArrayList);
		parser.setPismo(bLatinica);

		if(ulazniFajl==null)
			parser.run("serbia.osm", navodnikCharacter);
		else
			parser.run(ulazniFajl, navodnikCharacter);

	}


	
	public void run(String ulazniFajl, Character navodnikCharacter){
 
		mNavodnikCharacter = navodnikCharacter;
		
		//izdvaja sam naziv fajla bez putanje i ekstenzije
		mNazivUlaznogFajla=ulazniFajl.replace(".osm", "");// brise ekstenziju - osm
		mNazivUlaznogFajla=mNazivUlaznogFajla.substring(mNazivUlaznogFajla.lastIndexOf('/')+1); //brise putanju


		try{
			PrintWriter out1 = new PrintWriter(new File("_"+ulazniFajl), "UTF-8");
			// Open the file  
			FileInputStream fstream = new FileInputStream(ulazniFajl);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF8"));

			String strLine;
			boolean boolNasaoTag;

			preslovljavanje.setmNavodnikCharacter(mNavodnikCharacter);
			
			// glavna petlja
			while ((strLine = br.readLine()) != null) {
				boolNasaoTag = false;
				
				for (int i=0; i < mTagArrayList.size(); i++){
//	System.out.println("<tag k="+mNavodnikCharacter+mTagArrayList.get(i).toString()+mNavodnikCharacter);
					
					if(strLine.contains("<tag k="+mNavodnikCharacter+mTagArrayList.get(i).toString()+mNavodnikCharacter)) {
						boolNasaoTag = true;
						break;
					}
				}	
				
				if(boolNasaoTag == true){
	System.out.print(".");
//	System.out.println(strLine);
//	System.out.println(preslovljavanje.cir2lat(strLine));
						
					if(mbLatinica)
						out1.write(preslovljavanje.cir2lat(strLine)+"\n");
					else 
						out1.write(preslovljavanje.lat2cir(strLine)+"\n");
				}
				else {
					out1.write(strLine+"\n");
				}
				boolNasaoTag = false;
			}
			
			in.close();
			out1.flush();
			out1.close();	

		}catch (Exception e){//Catch exception if any
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("\nGotovo");
	}
	
	public void setTagovi(ArrayList<String> pTagArrayList){
		mTagArrayList = pTagArrayList;
	}
	
	public void setPismo(boolean pbLatinica){
		mbLatinica = pbLatinica;
	}

}