/**
 * Ovaj fajl se koristi za definisanje taga name i name:sr-Latn
 * 
 */


import java.util.List;


public class OSMParser_SredjivanjeGranice extends OSMParser_Name_NameSrLatn
{
	public static void main(String args[])
	{
		String izlazniFajl = null;
		String mUlazniFajl = null;
		String arg;
		OSMParser_SredjivanjeGranice parser = new OSMParser_SredjivanjeGranice();
		if(args.length==0)
		{
			System.out.println("Sredjuje granice");

			System.out.println("-izlaz=");
			System.out.println("\tIzlazni fajl. Ako nije definisan izlazni fajl ispisuje na StdOut");
			System.out.println("-ulaz=");
			System.out.println("\tUlazni fajl");
			return;
		}

		int i = 0;
		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

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


		if(izlazniFajl!=null){
			System.out.println("Sredjuje granice");
			System.out.println("Ulazni fajl: "+mUlazniFajl);
			System.out.println("Izlazni fajl: "+izlazniFajl);
		}

		if(mUlazniFajl==null)
			parser.run("serbia.osm");
		else
			parser.run(mUlazniFajl);
	}

	@Override
	protected String srediElement(List<String> element, PripremaZaRenderOSM pripremaZaRenderOSM2) {
		String rezultat = "";

		String id_elementa = "";

		if(element.isEmpty()){
			return "";
		}

		int pomA = element.get(0).indexOf(" id=");
		int pomB = element.get(0).indexOf('"', pomA+6);
		id_elementa = element.get(0).substring(pomA+5, pomB);
		
		for (String s : element)
		{
			rezultat += s + "\n";
		}


		if(id_elementa.equals("2088990")) {
			System.out.println("Pronasao granicu Kosova !!!");
			//granica Kosova se stavalja da bude admin_level=4
			rezultat = rezultat.replace("<tag k=\"admin_level\" v=\"2\"/>", "<tag k=\"admin_level\" v=\"4\"/>");
		}

		if(id_elementa.equals("1741311")) {
			System.out.println("Pronasao granicu Srbije !!!");
			// brise trenutnu granicu Srbije
			rezultat = "";
		}

		if(id_elementa.equals("9088937")) {
			System.out.println("Pronasao claimed granicu Srbije !!!");
			// postavlja da claimed bude vazeca
			rezultat = rezultat.replace("claimed_administrative", "administrative");
			rezultat = rezultat.replace("claimed:admin_level", "admin_level");
		}

		return rezultat;
	}
}
