package org.openstreetmap.josm.plugins.serbiantransliterator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.awt.Dimension;

import javax.swing.JCheckBox;

import java.awt.Component;
/**
 * Startuje PreslovljavanjeDijalog sa pocetnim parametrima
 * 
 * @author mpele
 */
public class StartPreslovljavanjeDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3845789593282565609L;
	private final JPanel contentPanel = new JPanel();
	private JTextField mtxtDodatniParametri;
	private JComboBox<Object> mComboBox;
	private JCheckBox chckbxRelacije;

	public static void main(String[] args) {
		try {
			StartPreslovljavanjeDialog dialog = new StartPreslovljavanjeDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public StartPreslovljavanjeDialog() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		{
			Box verticalBox = Box.createVerticalBox();
			contentPanel.add(verticalBox);
			{
				Box horizontalBox = Box.createHorizontalBox();
				horizontalBox.setMaximumSize(new Dimension(400, 0));
				verticalBox.add(horizontalBox);
				{
					JLabel lblPodrazumevanoPismo = new JLabel("Podrazumevano pismo: ");
					horizontalBox.add(lblPodrazumevanoPismo);
				}
				{
					mComboBox = new JComboBox<Object>();
					mComboBox.setModel(new DefaultComboBoxModel<Object>(PodrazumevanoPismo.values()));
					mComboBox.setSelectedIndex(1); 
					horizontalBox.add(mComboBox);
				}
			}
			{
				Box horizontalBox = Box.createHorizontalBox();
				verticalBox.add(horizontalBox);
				{
					JLabel lblSpisakTagova = new JLabel("Spisak dodatnih tagova: ");
					horizontalBox.add(lblSpisakTagova);
				}
				{
					mtxtDodatniParametri = new JTextField();
					mtxtDodatniParametri.setText("highway note fixme");
					horizontalBox.add(mtxtDodatniParametri);
					mtxtDodatniParametri.setColumns(15);
				}
			}
			{
				Component verticalStrut = Box.createVerticalStrut(20);
				verticalStrut.setMinimumSize(new Dimension(0, 100));
				verticalBox.add(verticalStrut);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(20);
				verticalStrut.setMinimumSize(new Dimension(0, 100));
				verticalBox.add(verticalStrut);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(20);
				verticalStrut.setMinimumSize(new Dimension(0, 100));
				verticalBox.add(verticalStrut);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(20);
				verticalStrut.setMinimumSize(new Dimension(0, 100));
				verticalBox.add(verticalStrut);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(20);
				verticalStrut.setMinimumSize(new Dimension(0, 100));
				verticalBox.add(verticalStrut);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(20);
				verticalStrut.setMinimumSize(new Dimension(0, 100));
				verticalBox.add(verticalStrut);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(20);
				verticalStrut.setMinimumSize(new Dimension(0, 100));
				verticalBox.add(verticalStrut);
			}
			{
				chckbxRelacije = new JCheckBox("expert mod (Radi sa relacijama)");
				verticalBox.add(chckbxRelacije);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					/**
					 * Startuje PreslovljavanjeDialog sa parametrima i gasi sebe
					 */
					public void actionPerformed(ActionEvent arg0) {
						// deli string iz txtField-a i dodaje kao dodatna polja
						ArrayList<String> dodatniTagovi = new ArrayList<String>();
						dodatniTagovi.addAll(new ArrayList<String>(Arrays.asList(mtxtDodatniParametri.getText().split(" "))));
						PreslovljavanjeDialog dialog = new PreslovljavanjeDialog(dodatniTagovi,(PodrazumevanoPismo) mComboBox.getSelectedItem(),chckbxRelacije.isSelected());
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						
						StartPreslovljavanjeDialog.this.dispose();
						dialog.setVisible(true);
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
						StartPreslovljavanjeDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
