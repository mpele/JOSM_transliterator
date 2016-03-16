import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class AdaptiraKmlZaUnosJOSM {

	/**
	 * Pronalazi koordinate tacaka i 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {


		FileInputStream fstream = new FileInputStream("LPGSerbia.kml");


		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		FileWriter fOutputFile = new FileWriter("LPGSerbia_adaptiran.kml");


		String s, lon= "", lat="";
		String pomocniBuffer = "";

		while((s = br.readLine()) != null) { 
			if(s.contains("</Placemark>") ){  
				pomocniBuffer = pomocniBuffer.replace("]]></description>", " http://localhost:8111/add_node?lon="+lon+"&lat="+lat+" ]]></description>");
				fOutputFile.write(pomocniBuffer);
				pomocniBuffer="";
				lon = lat = "";
			}

			if(s.contains("<coordinates>") ){  
				try {
					int pomA = s.indexOf("<coordinates>");
					int pomB = s.indexOf(',', pomA+14);
					lon = s.substring(pomA+13, pomB) ;
					

					int pomC = s.indexOf(',', pomB+2);
					lat = s.substring(pomB+1, pomC) ;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			pomocniBuffer += s+"\n";

		} 


		fOutputFile.write(pomocniBuffer);


		in.close();
		fOutputFile.close();
		System.out.println("Gotovo");
	}

}
