import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class DiffSpliter {

	public static void main(String args[]){
		// Open the file  
		FileInputStream fstream;
		try {
			fstream = new FileInputStream("diff-1-2.osc");

			DataInputStream in = new DataInputStream(fstream);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			String buffer = "";
			int brojac = 0;

			while ((strLine = br.readLine()) != null)   {
				if(strLine.contains("<node ") | strLine.contains("<way ") ){  
					buffer = strLine+"\n";					
				}
				else{
					buffer += strLine+"\n";
				}
				if(strLine.contains("</node") | strLine.contains("</way") ){  
					FileWriter outFile= new FileWriter("izmena"+(brojac++)+"_s.osc");
					outFile.write("<?xml version='1.0' encoding='UTF-8'?>\n");
					outFile.write("<osmChange version=\"0.6\" generator=\"Preslovljavanje OSM\">\n");
					outFile.write("<modify>\n");
					outFile.write(buffer);
					outFile.write("</modify>\n");
					outFile.write("</osmChange>");

					outFile.close();
				}				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Gotovo");
	}

}
