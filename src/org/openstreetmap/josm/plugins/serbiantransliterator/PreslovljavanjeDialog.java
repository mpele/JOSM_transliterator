package org.openstreetmap.josm.plugins.serbiantransliterator;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

enum EIdKoloneUTabeli{
	NAME(6), CIRILICA(4), LATINICA(5), ENGLESKI(7), HASH(3);
	private int kolona;

	private EIdKoloneUTabeli(int col) {
		this.setKolona(col);
	}

	public int getKolona() {
		return kolona;
	}

	public void setKolona(int kolona) {
		this.kolona = kolona;
	}
}

/**
 * @author mpele
 *
 */
public class PreslovljavanjeDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4717990196196081917L;
	private final JPanel contentPanel = new JPanel();
	private boolean mbExpert = false;
	private boolean mbLatinicaSeMenja;
	private JTable mTable;
	JComboBox<Object> mComboBox;

	PreslovljavanjeTableModel mModel;
	PodrazumevanoPismo mPodrazumevanoPismoAutoPreslovljavanje;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PreslovljavanjeDialog dialog = new PreslovljavanjeDialog();

			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

			dialog.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Konstruktor bez dodatnih tagova
	 */
	public PreslovljavanjeDialog() {
		this(new ArrayList<String>(),PodrazumevanoPismo.BEZ_PROMENE, false, true);
	}


	/**
	 * Glavni konstruktor
	 * Konstruktor sa dodatnim tagovima i da li je ekspert (da li radi sa relacijama)
	 */		
	public PreslovljavanjeDialog(ArrayList<String> dodatniTagovi, PodrazumevanoPismo podrazumevanoPismo, boolean bExpert, boolean bLatinica) {
		setModal(true);
		setTitle("Preslovljavanje");
		setBounds(100, 100, 1100, 700);
		// setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			// mModel = new DefaultTableModel();
			mModel = new PreslovljavanjeTableModel();
			mModel.setDodatniTagoviList(dodatniTagovi);
			mModel.popuniModelSaKolonama();
			mTable = new JTable(mModel);
			mTable.setAutoCreateRowSorter(true);

			mbExpert = bExpert;
			mbLatinicaSeMenja = bLatinica;

			ucitajSveTagoveIzSelektovanihObjekata(podrazumevanoPismo);
			mModel.setPodrazumevanoPismo(podrazumevanoPismo);

			// Sirina kolona
			TableColumn column = mTable.getColumnModel().getColumn(0);
			column.setMinWidth(100);
			column.setMaxWidth(100);
			column = mTable.getColumnModel().getColumn(1);
			column.setMinWidth(50);
			column.setMaxWidth(50);
			column = mTable.getColumnModel().getColumn(2);
			column.setMinWidth(50);
			column.setMaxWidth(50);
			column = mTable.getColumnModel().getColumn(EIdKoloneUTabeli.HASH.getKolona());
			column.setMinWidth(50);
			column.setMaxWidth(50);

			// definise render
			mTable.setDefaultRenderer(String.class, new StringRenderer());
			mTable.setDefaultRenderer(Boolean.class, new CheckBoxRenderer());
			mTable.setDefaultRenderer(PreslovljavanjeOSM.class, new IdRender());


			// definise precice sa tastature
			InputMap inputMap = mTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			ActionMap actionMap = mTable.getActionMap();

			// pritisnuto Ctrl-S - preslovi iz sr
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "ControlS");
			actionMap.put("ControlS", new AbstractAction() {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					presloviSelektovanoIzSr();
				}
			});

			// pritisnuto Ctrl-D - Presovi auto
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "ControlD");
			actionMap.put("ControlD", new AbstractAction() {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					presloviSelektovanoAuto();
				}
			});

			// pritisnuto Ctrl-F - original
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "ControlF");
			actionMap.put("ControlF", new AbstractAction() {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					vratiSelektovaneNaOriginale();
				}
			});

			// dupli klik
			mTable.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					if (e.getClickCount() == 2){
						try {
							JTable target = (JTable)e.getSource();
							int row = target.getSelectedRow();
							int col = target.getSelectedColumn();
							PreslovljavanjeOSM tmpPres = (PreslovljavanjeOSM)target.getValueAt(row, 0) ;
							if(col==0)
								Desktop.getDesktop().browse(new URI("http://www.openstreetmap.org/browse/"+tmpPres.getTipObjekta()+"/"+tmpPres.getId()));
							else
								Desktop.getDesktop().browse(new URI("http://www.openstreetmap.org/?"+tmpPres.getTipObjekta()+"="+tmpPres.getId()));

						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			} );


			JScrollPane scroller = new JScrollPane(mTable);
			contentPanel.add(scroller, BorderLayout.CENTER);
		}
		{
			final JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JLabel lblPodrazumevanoPismo = new JLabel("Podrazumevano pismo");
				panel.add(lblPodrazumevanoPismo);
			}
			{
				mComboBox = new JComboBox<Object>();
				mComboBox.setModel(new DefaultComboBoxModel<Object>(PodrazumevanoPismo.values()));
				mComboBox.setSelectedIndex(1);
				mComboBox.setToolTipText("Za preslovljavanje iz sr");

				mComboBox.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						mModel.setPodrazumevanoPismo((PodrazumevanoPismo) mComboBox.getSelectedItem());
					}
				});
				panel.add(mComboBox);
			}
			{
				JButton btnRemove = new JButton("Uklnoni redove");
				panel.add(btnRemove);
				btnRemove.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { // brise
						// oznacene
						// redove
						int[] rows = mTable.getSelectedRows();
						for (int i = 0; i < rows.length; i++) {
							mModel.removeRow(rows[i] - i);
						}
					}
				});
			}
			{
				JButton btnPreslovi = new JButton("Preslovi iz sr");
				panel.add(btnPreslovi);
				btnPreslovi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { 
						presloviSelektovanoIzSr();
					}
				});
			}
			{
				JButton btnPresloviAuto = new JButton("Preslovi auto");
				panel.add(btnPresloviAuto);
				btnPresloviAuto.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { 
						presloviSelektovanoAuto();
					}
				});
			}
			{
				// vraca originalne zapise
				JButton btnOriginal = new JButton("Original");
				panel.add(btnOriginal);
				btnOriginal.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) { 
						vratiSelektovaneNaOriginale();
					}
				});
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						updateJOSMSelection();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PreslovljavanjeDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}



	private void presloviSelektovanoIzSr(){
		int[] rows = mTable.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			// da prebaci broj reda iz sortirane tabele u red po
			// modelu
			int row = mTable.convertRowIndexToModel(rows[i]);
			mModel.presloviRedIzSr(row, mbLatinicaSeMenja);
		}
	}

	private void presloviSelektovanoAuto(){
		int[] rows = mTable.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			// da prebaci broj reda iz sortirane tabele u red po
			// modelu
			int row = mTable.convertRowIndexToModel(rows[i]);
			mModel.presloviRedAuto(row, mbLatinicaSeMenja);
		}
	}

	private void vratiSelektovaneNaOriginale(){
		// vraca originalne zapise
		int[] rows = mTable.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			// da prebaci broj reda iz sortirane tabele u red po
			// modelu
			int row = mTable.convertRowIndexToModel(rows[i]);
			mModel.presloviRedOriginal(row);
		}		
	}

	public void ucitajSveTagoveIzSelektovanihObjekata(PodrazumevanoPismo podrazumevanoPismo) {


		// zastita ako se ne pokrece iz JOSM-a vec iz Eclipse-a
		try{
			MainApplication.getLayerManager().getEditDataSet().getSelected();
		}
		catch(Exception e){
			return;
		}


		Collection<OsmPrimitive> selection = MainApplication.getLayerManager().getEditDataSet().getSelected();

		for (OsmPrimitive element : selection) {
			PreslovljavanjeOSM preslovljavanjeOSM = new PreslovljavanjeOSM();
			preslovljavanjeOSM.setPodrazumevanoPismo(podrazumevanoPismo);
			preslovljavanjeOSM.setTipObjekta(element.getType().toString());			
			preslovljavanjeOSM.setId(element.getId());
			for (String key : element.keySet()) {
				String value = element.get(key);
				preslovljavanjeOSM.setNameKeyValue(key, value);
			}

			// ako ima tagova za naziv upisuje u tabelu
			if (preslovljavanjeOSM.daLiImaTagovaZaNaziv() ) {
				// ne upisuje relacije ako nije expert
				System.out.println(preslovljavanjeOSM.getTipObjekta());
				System.out.println(mbExpert);
				if( (mbExpert && preslovljavanjeOSM.getTipObjekta().equals("relation") ) ||
						! preslovljavanjeOSM.getTipObjekta().equals("relation")){
					System.out.println("Upisuje");
					insert(preslovljavanjeOSM);
				}
			}
		}
	}

	/**
	 * Ubacuje red u tabelu na osnovu svih tagova
	 * 
	 * @param preslovljavanjeOSM
	 */
	public void insert(PreslovljavanjeOSM preslovljavanjeOSM) {
		//		System.out.println(preslovljavanjeOSM.daLiImaIzmena()
		//				+ preslovljavanjeOSM.pregled());
		//		System.out.println("upis u tabelu" + preslovljavanjeOSM.getId() + " "
		//				+ preslovljavanjeOSM.getName() + " "
		//				+ preslovljavanjeOSM.getName_sr() + " "
		//				+ preslovljavanjeOSM.getName_sr_lat() + " "
		//				+ preslovljavanjeOSM.getName_en());
		mModel.addRed(preslovljavanjeOSM);
	}

	public void updateJOSMSelection() {
		int numRow;
		int brojKolona = mModel.getColumnCount();

		// //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		Collection<Command> c = new LinkedList<Command>();
		Collection<OsmPrimitive> selection = MainApplication.getLayerManager().getEditDataSet().getSelected();
		for (OsmPrimitive element : selection) {
			numRow = getRowZapisa(element.getId()); // vraca -1 ako nije stiklirano da treba da se menja
			if (numRow >= 0) {

				// kreira JOSM komande za azuriranje tagova 
				c.add(new ChangePropertyCommand(element, Tagovi.NAME.getTagKey(), 
						mModel.getValueAt(numRow, EIdKoloneUTabeli.NAME.getKolona()).toString()));
				c.add(new ChangePropertyCommand(element, Tagovi.CIRILICA.getTagKey(), 
						mModel.getValueAt(numRow, EIdKoloneUTabeli.CIRILICA.getKolona()).toString()));
				c.add(new ChangePropertyCommand(element, Tagovi.LATINICA.getTagKey(), mModel.getValueAt(numRow, EIdKoloneUTabeli.LATINICA.getKolona()).toString()));

				// prati hash sumu
				if((Boolean) mModel.getValueAt(numRow, EIdKoloneUTabeli.HASH.getKolona())){ // ako je oznacen chechkbox
					String pomString=mModel.getValueAt(numRow, EIdKoloneUTabeli.NAME.getKolona()).toString()
							+mModel.getValueAt(numRow, EIdKoloneUTabeli.CIRILICA.getKolona()).toString()
							+mModel.getValueAt(numRow, EIdKoloneUTabeli.LATINICA.getKolona()).toString();

					c.add(new ChangePropertyCommand(element, Tagovi.HASH.getTagKey(), 
							Integer.toString(PreslovljavanjeOSM.getHash(pomString))));
				} else { // ako nije stiklirano a postoji hash onda ga brise
					if(((PreslovljavanjeOSM) mModel.getValueAt(numRow, 0)).daLiImaHash()){
						c.add(new ChangePropertyCommand(element, Tagovi.HASH.getTagKey(), ""));
					}
				}

				// za kolone sa dodatnim tagovima
				for(int i = EIdKoloneUTabeli.NAME.getKolona()+1; i< brojKolona;i++){
					String key = mModel.getColumnName(i);
					String vrednostUTabeli = mModel.getValueAt(numRow, i).toString();
					c.add(new ChangePropertyCommand(element, key, vrednostUTabeli));	
				}
			}
		}

	      SequenceCommand command = new SequenceCommand(
	    		      //trn("Updating properties of up to {0} object", "Updating properties of up to {0} objects", selection.size(), selection.size()),
	    		  "Promena naziva",
	    		  	                c
	    		          );
		 // executes the commands and adds them to the undo/redo chains
		UndoRedoHandler.getInstance().add(command);

		// //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		// zatvara dijalog
		PreslovljavanjeDialog.this.dispose();
	}

	/**
	 * Vraca id reda u kome se nalaze podaci za objekat ili -1 ako ga nema ili
	 * nije stikliran da snimi izmene
	 * 
	 * @param id
	 * @return
	 */
	private int getRowZapisa(long id) {
		for (int i = 0; i < mModel.getRowCount(); i++) {
			// System.out.println(id+" "+i+" "+mModel.getRowCount());
			if (id == Long.parseLong(mModel.getValueAt(i, 0).toString())) {
				// System.out.println("i - "+i+" "+mModel.getValueAt(i,
				// 3).toString());
				Boolean bIzmena = (Boolean) mModel.getValueAt(i, 2);

				// ako nije stiklirano da se menja ne menja
				if (bIzmena == true)
					return i;
				else
					return -1; // ne snima

			}
		}
		return -1;
	}
}

