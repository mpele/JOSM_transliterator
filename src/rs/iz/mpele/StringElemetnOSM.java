package rs.iz.mpele;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class StringElemetnOSM {

	protected long id;
	protected double mLon, mLat;
	protected String mTipObjekta="";
	protected long mJedanOdNodeId = -1;

	protected Map<String, String> mSviOriginalniTagoviMap = new HashMap<String, String>();


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/** 
	 * Postavlja id objekta - moze da radi i bez nega
	 * @param ii
	 */
	public void setId(long ii){
		id = ii;
	}

	/**
	 * vraca long id
	 * @return
	 */
	public long getId(){
		// TODO potrebno ga je obezbediti
		return id;
	}

	public void setLon(double lon){
		mLon = lon;
	}

	public double getLon(){
		return mLon;
	}

	public void setLat(double lat){
		mLat = lat;
	}
	public double getLat(){
		return mLat;
	}	

	public void setTipObjekta(String tip){
		mTipObjekta = tip;
	}
	public String getTipObjekta(){
		return mTipObjekta;
	}


	public void setNameKeyValue(String key, String value){
		mSviOriginalniTagoviMap.put(key, value);
	}


	public String getTagValue(String key){
		String value;
		value = mSviOriginalniTagoviMap.get(key);
		if(value==null)
			return "";
		else
			return value;
	}


	/**
	 * Izdvaja vrednosti za tag i value iz teksta i cuva ih 
	 *   cuva i jednu vrednost za node
	 * @param strLine
	 */
	public void ucitajTagIzStringa(String strLine){
		String k, v;
		int a = strLine.indexOf("<tag k=");
		if(a>1){
			int b = strLine.indexOf(" v=");
			int c = strLine.lastIndexOf('"');
			//System.out.println(strLine);
			k = strLine.substring(a+8, b-1);
			v = strLine.substring(b+4, c);
			setNameKeyValue(k, v);
		}

		// ako ima neku referencu
		a = strLine.indexOf("<nd ref=");
		if(a>1){
			int b = strLine.lastIndexOf('"');
			mJedanOdNodeId=Long.parseLong( strLine.substring(a+9, b) );
		}		
	}


	public long getJedanOdNodeId(){
		return mJedanOdNodeId;
	}


	/**
	 * Izdvaja vrednosti za tag i value iz teksta i cuva ih 
	 * @param strLine
	 */
	public void ucitajZaglavljeTagovaIzStringa(String strLine){
		int pomA = strLine.indexOf(" id=");
		int pomB = strLine.indexOf('"', pomA+6);
		setId(Long.parseLong( strLine.substring(pomA+5, pomB) ));


		// TODO glupo je ovako ali me mrzi da razmisljam 
		if(strLine.contains("<node ")){
			setTipObjekta("node");

			// samo node ima lon - lat
			pomA = strLine.indexOf(" lat=");
			pomB = strLine.indexOf('"', pomA+7);
			setLat(Double.parseDouble( strLine.substring(pomA+6, pomB) ));

			pomA = strLine.indexOf(" lon=");
			pomB = strLine.indexOf('"', pomA+7);
			setLon(Double.parseDouble( strLine.substring(pomA+6, pomB) ));
		}

		if(	strLine.contains("<way "))
			setTipObjekta("way");

		if(strLine.contains("<relation ") )
			setTipObjekta("relation");


		// datum 
		//		pomA = strLine.indexOf(" timestamp=");
		//		pomB = strLine.indexOf('"', pomA+12);
		//
		//		
		//		objekatDate= new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH).parse(strLine.substring(pomA+12, pomB) );
		//		//if(!calendar.getTime().after(objekatDate))
		//			System.out.println(objekatDate+" "+calendar.getTime());

	}

	/**
	 * Vraca link koji direktno ucitava objekat u JOSM-u
	 * @return
	 */
	public String getHtmlLinkZaJOSM(){

		return "http://localhost:8111/import?url=http://api.openstreetmap.org/api/0.6/"+getTipObjekta()+"/"+getId()
				+ (getTipObjekta() == "node" ? "" :"/full");
	}

	/**
	 * Vraca link koji otvara stranicu Openstreetmap-u za odgovarajuci objekat
	 * @return
	 */
	public String getHtmlLinkZaBrowser(){
		return "http://www.openstreetmap.org/browse/"+getTipObjekta()+"/"+getId();
	}


	public void clear(){
		id=-1;
		mTipObjekta="";
		mLon = -999;
		mLat = -999;
		mSviOriginalniTagoviMap.clear();
		mJedanOdNodeId = -1;
	}


	public String getHtmlSviTagovi(){
		String izlaz="";
		Iterator<Entry<String, String>> it = mSviOriginalniTagoviMap.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			izlaz = izlaz + pairs.getKey() + " = " + pairs.getValue()+"<br/>";
		}
		return izlaz;

	}
}
