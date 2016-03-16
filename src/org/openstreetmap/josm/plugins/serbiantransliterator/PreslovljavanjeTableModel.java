package org.openstreetmap.josm.plugins.serbiantransliterator;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

//import org.openstreetmap.josm.command.ChangePropertyCommand;


class PreslovljavanjeTableModel extends DefaultTableModel {
	ArrayList<String> mDodatniTagoviList = new ArrayList<String>();
	/**
	 * 
	 */
	private static final long serialVersionUID = -7915372361398091964L;
	PodrazumevanoPismo emPodrazumevanoPismo = PodrazumevanoPismo.BEZ_PROMENE;

	public PreslovljavanjeTableModel() {
		super();
	}



	public void setDodatniTagoviList(ArrayList<String> dodatniTagoviList) {
		this.mDodatniTagoviList.addAll(dodatniTagoviList);
	}


	/**
	 * Popunjava model sa odgovarajucim kolonama
	 */
	public void popuniModelSaKolonama(){
		// osnovni tagovi
		String[] columnNames = { "id", "tip", "izmena", "hash","name:sr", "name:sr-Latn", "name" };

		//Dodaje ostale tagove u zaglavlje
		List<String> lst = new ArrayList<String>(Arrays.asList(columnNames));
		lst.addAll(mDodatniTagoviList);
		Object[] finalColumnNames= lst.toArray();


		super.setColumnIdentifiers(finalColumnNames);

		//		PreslovljavanjeOSM preslovljavanjeOSM = new PreslovljavanjeOSM();
		//		preslovljavanjeOSM.setPodrazumevanoPismo(PodrazumevanoPismo.CIRILICA);
		//		preslovljavanjeOSM.setId(123456789);
		//		preslovljavanjeOSM.setNameKeyValue("name", "ToplĐica");
		//		preslovljavanjeOSM.setNameKeyValue("name:sr", "Топлица");
		//		preslovljavanjeOSM.setNameKeyValue("name:sr-Latn", "Toplica");
		//		addRed(preslovljavanjeOSM);
		//		PreslovljavanjeOSM preslovljavanjeOSM2 = new PreslovljavanjeOSM();
		//		preslovljavanjeOSM2.setPodrazumevanoPismo(PodrazumevanoPismo.CIRILICA);
		//		preslovljavanjeOSM2.setId(1234);
		//		preslovljavanjeOSM2.setNameKeyValue("name:sr", "Топлица2");
		//		preslovljavanjeOSM2.setNameKeyValue("name:sr-Latn", "Toplica");
		//		addRed(preslovljavanjeOSM2);
		//		PreslovljavanjeOSM preslovljavanjeOSM3 = new PreslovljavanjeOSM();
		//		preslovljavanjeOSM3.setPodrazumevanoPismo(PodrazumevanoPismo.CIRILICA);
		//		preslovljavanjeOSM3.setId(1234);
		//		preslovljavanjeOSM3.setNameKeyValue("name", "Toplica");
		//		preslovljavanjeOSM3.setNameKeyValue("name:sr", "Топлица2");
		//		preslovljavanjeOSM3.setNameKeyValue("name:sr-Latn", "Toplica");
		//		preslovljavanjeOSM3.setNameKeyValue(Tagovi.HASH.getTagKey(), "801209912");
		//		addRed(preslovljavanjeOSM3);

	}

	/**
	 * Za potrebe preslovaljavanja iz sr
	 * @param pismo
	 */
	public void setPodrazumevanoPismo(PodrazumevanoPismo pismo) {
		emPodrazumevanoPismo = pismo;
	}

	public void addRed(PreslovljavanjeOSM preslovljavanjeOSM) {

		// definise vrednosti za osnovne tagove
		Object[] osnovniPodaci = new Object[] { preslovljavanjeOSM, 
				preslovljavanjeOSM.getTipObjekta(),
				new Boolean(false), 
				preslovljavanjeOSM.daLiImaHash(),
				preslovljavanjeOSM.getOriginalName_sr(),
				preslovljavanjeOSM.getOriginalName_sr_lat(),
				preslovljavanjeOSM.getOriginalName() 
		};

		// definise vrednosti za dopunske tagove
		List<Object> podaci = new ArrayList<Object>(Arrays.asList(osnovniPodaci));
		for(String tag:mDodatniTagoviList)
			podaci.add(preslovljavanjeOSM.getTagValue(tag));

		super.addRow(podaci.toArray());
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	public Class getColumnClass(int c) {
		///////////////////////////////////////////////////////////////////////////
		// TODO krpljenje za dodavanje kolone
		if(c>5)
			return String.class;
		///////////////////////////////////////////////////////////////////////////


		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		if (col < 2) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		//		System.out.println("Setting value at " + row + "," + col + " to "
		//				+ value + " (an instance of " + value.getClass() + ")");
		super.setValueAt(value, row, col);
		fireTableCellUpdated(row, col);
	}

	/**
	 * Preslovljava na osnovu polja name:sr
	 * 
	 * @param row
	 */
	public void presloviRedIzSr(int row) {
		String osnova = (String) getValueAt(row, EIdKoloneUTabeli.CIRILICA.getKolona());
		Preslovljavanje presl = new Preslovljavanje();
		setValueAt(presl.cir2lat(osnova), row, EIdKoloneUTabeli.LATINICA.getKolona()); // latinica

		// preslovljava tag name u zavisnosti sta je podeseno u dropboxu
		switch (emPodrazumevanoPismo) {
		case CIRILICA:
			setValueAt(osnova, row, EIdKoloneUTabeli.NAME.getKolona());
			//			System.out.println("cirilica");
			break;
		case LATINICA:
			setValueAt(presl.cir2lat(osnova), row, EIdKoloneUTabeli.NAME.getKolona());
			//			System.out.println("latinica");
			break;
		default:
			//			System.out.println("bez promene");
			break;
		}

		// TODO provera da li treba da snima izmene
		setValueAt(new Boolean(true), row, 2); // izmena - da se sacuvava
		fireTableRowsUpdated(row, row);
	}

	/**
	 * Preslvoljava na osnovu PreslovljavanjeOSM
	 * 
	 * @param row
	 */
	public void presloviRedAuto(int row) {
		PreslovljavanjeOSM preslovljavanjeOsm = (PreslovljavanjeOSM) getValueAt(
				row, 0);
		setValueAt(preslovljavanjeOsm.getName_sr(), row, EIdKoloneUTabeli.CIRILICA.getKolona()); // name:sr
		setValueAt(preslovljavanjeOsm.getName_sr_lat(), row, EIdKoloneUTabeli.LATINICA.getKolona()); // name:sr-Latn
		setValueAt(preslovljavanjeOsm.getName(), row, EIdKoloneUTabeli.NAME.getKolona()); // name
		setValueAt(daLiImaIzmena(row, preslovljavanjeOsm), row, 2); // izmene - da se sacuvava
		fireTableRowsUpdated(row, row);
	}

	private boolean daLiImaIzmena(int row, PreslovljavanjeOSM preslovljavanjeOsm) {
		String nazivSr = (String) getValueAt(row, EIdKoloneUTabeli.CIRILICA.getKolona());
		String nazivSrLatn = (String) getValueAt(row, EIdKoloneUTabeli.LATINICA.getKolona());
		String naziv = (String) getValueAt(row, EIdKoloneUTabeli.NAME.getKolona());
		if ((nazivSr.equals(preslovljavanjeOsm.getOriginalName_sr())
				&& (nazivSrLatn.equals(preslovljavanjeOsm.getOriginalName_sr_lat())) 
				&& (naziv.equals(preslovljavanjeOsm.getOriginalName()))))
			return false;
		else
			return true;
	}

	/**
	 * Vraca na originalno stanje
	 * 
	 * @param row
	 */
	public void presloviRedOriginal(int row) {
		PreslovljavanjeOSM preslovljavanjeOsm = (PreslovljavanjeOSM) getValueAt(
				row, 0);
		setValueAt(new Boolean(false), row, 2); // izmene - da se sacuvava
		setValueAt(preslovljavanjeOsm.getOriginalName_sr(), row, EIdKoloneUTabeli.CIRILICA.getKolona()); // name:sr
		setValueAt(preslovljavanjeOsm.getOriginalName_sr_lat(), row, EIdKoloneUTabeli.LATINICA.getKolona()); // name:sr-Latn
		setValueAt(preslovljavanjeOsm.getOriginalName(), row, EIdKoloneUTabeli.NAME.getKolona()); // name
		fireTableRowsUpdated(row, row); 
	}

}


/**
 * Render za polja sa nazivima
 * @author mpele
 *
 */

final class StringRenderer extends DefaultTableCellRenderer implements
TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6864140087397816821L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		//System.out.println(value + " " + row + " " + column);
		if (value instanceof String && column > 2) {
			row = table.convertRowIndexToModel(row);
			String s = table.getModel().getValueAt(row, column).toString();

			PreslovljavanjeOSM preslovljavanjeOsm = (PreslovljavanjeOSM) table
					.getModel().getValueAt(row, 0);


			if(column==EIdKoloneUTabeli.CIRILICA.getKolona()) { // name:sr
				if (s.contentEquals(preslovljavanjeOsm.getOriginalName_sr())) {
					// setBackground(Color.RED);
					setForeground(null);
				} else if (s.contentEquals(preslovljavanjeOsm.getName_sr())) {
					setForeground(Color.BLUE);
				} else {
					setForeground(Color.RED);
				}
				String tip = preslovljavanjeOsm.getOriginalName_sr();
				if (tip.isEmpty())
					tip = "-";
				setToolTipText(tip);
			}
			else if(column==EIdKoloneUTabeli.LATINICA.getKolona()) { // name:sr-Latn
				if (s.contentEquals(preslovljavanjeOsm.getOriginalName_sr_lat())) {
					// setBackground(Color.RED);
					setForeground(null);
				} else if (s.contentEquals(preslovljavanjeOsm.getName_sr_lat())) {
					setForeground(Color.BLUE);
				} else {
					setForeground(Color.RED);
				}
				String tip = preslovljavanjeOsm.getOriginalName_sr_lat();
				if (tip.isEmpty())
					tip = "-";
				setToolTipText(tip);
			}
			else if(column==EIdKoloneUTabeli.NAME.getKolona())  { // name
				if (s.contentEquals(preslovljavanjeOsm.getOriginalName())) {
					// setBackground(Color.RED);
					setForeground(null);
				} else if (s.contentEquals(preslovljavanjeOsm.getName())) {
					setForeground(Color.BLUE);
				} else {
					setForeground(Color.RED);
				}
				String tip = preslovljavanjeOsm.getOriginalName();
				if (tip.isEmpty())
					tip = "-";
				setToolTipText(tip);
			}
			else if(column > EIdKoloneUTabeli.NAME.getKolona())  { // za sve kolone posele Name
				// pretpostavka je da je kolona NAME poslednja od podrazumevanih

				//ucitava key na osnovu naziva kolone
				String key = table.getModel().getColumnName(column);

				if (s.contentEquals(preslovljavanjeOsm.getTagValue(key))) {
					// setBackground(Color.RED);
					setForeground(null);
				} else {
					setForeground(Color.RED);
				}
				String tip = preslovljavanjeOsm.getTagValue(key);
				if (tip.isEmpty())
					tip = "-";
				setToolTipText(tip);
			}
			else
				setForeground(Color.YELLOW);

			return super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);

		} else {
			setForeground(null);
			setToolTipText(null);
			return super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
		}
	}
}


/**
 * Render za checkbox koji pokazuje da li se snima
 *  - menja boju pozadine u zavisnosti da li PreslovljavanjeOSM predlaze promenu
 * @author mpele
 *
 */
final class CheckBoxRenderer extends DefaultTableCellRenderer implements
TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2029038748444962113L;
	private JCheckBox box = new JCheckBox();

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if(column==2){ // izmene
			box.setSelected(((Boolean) value).booleanValue());
			row = table.convertRowIndexToModel(row);

			PreslovljavanjeOSM preslovljavanjeOsm = (PreslovljavanjeOSM) table
					.getModel().getValueAt(row, 0);
			if (preslovljavanjeOsm.daLiImaIzmena()) {
				box.setBackground(Color.RED);
			} else {
				box.setBackground(null);
			}
		} else if(column==EIdKoloneUTabeli.HASH.getKolona()){ //hash
			box.setSelected(((Boolean) value).booleanValue());
			// definise pozadinu chechboxa za hash u zavisnosti da li je hash suma indenticna 
			row = table.convertRowIndexToModel(row);

			PreslovljavanjeOSM preslovljavanjeOsm = (PreslovljavanjeOSM) table
					.getModel().getValueAt(row, 0);

			String pomString=table.getModel().getValueAt(row, EIdKoloneUTabeli.NAME.getKolona()).toString()
					+table.getModel().getValueAt(row, EIdKoloneUTabeli.CIRILICA.getKolona()).toString()
					+table.getModel().getValueAt(row, EIdKoloneUTabeli.LATINICA.getKolona()).toString();

			//System.out.println(preslovljavanjeOsm.getTagValue(Tagovi.HASH.getTagKey()) +"**"+	Integer.toString(PreslovljavanjeOSM.getHash(pomString)));

			if (preslovljavanjeOsm.daLiImaHash() 
					&& !preslovljavanjeOsm.getTagValue(Tagovi.HASH.getTagKey()).equals(Integer.toString(PreslovljavanjeOSM.getHash(pomString)))) {
				box.setBackground(Color.RED);
			} else {
				box.setBackground(null);
			}
		}


		return box;
	}

}


/**
 * Render za id
 *  - postavlja toolTip za id polje
 * @author mpele
 *
 */
final class IdRender extends DefaultTableCellRenderer implements TableCellRenderer {
	private static final long serialVersionUID = -1035628079745182301L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		row = table.convertRowIndexToModel(row);
		PreslovljavanjeOSM preslovljavanjeOsm = (PreslovljavanjeOSM) table.getModel().getValueAt(row, 0);

		setToolTipText("<html>"+preslovljavanjeOsm.getStariTagovi().replace("|", "<br/>")+"</html>");
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}