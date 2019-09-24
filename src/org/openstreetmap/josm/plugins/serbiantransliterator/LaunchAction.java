package org.openstreetmap.josm.plugins.serbiantransliterator;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Set;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.DataSelectionListener;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.event.SelectionEventManager;
import org.openstreetmap.josm.tools.Shortcut;

public class LaunchAction extends JosmAction implements DataSelectionListener  {

	private static final long serialVersionUID = 1L;

	public LaunchAction()  {

		super(
				tr("Edit nazivi"),
				(String) null, //TODO: set "tag-editor" and add /images/tag-editor.png to distrib
				tr("Launches the tag editor dialog - kada je mis iznad menija"),
				Shortcut.registerShortcut("edit:launchserbiantransliterator", tr("Launches the dialog preslovljavanje"), KeyEvent.VK_2,Shortcut.ALT_SHIFT)
				, true, "serbiantransliterator/launch", true);

		SelectionEventManager.getInstance().addSelectionListener(this);
		setEnabled(false);
	}

	/**
	 * launch the editor
	 */
	protected void launchEditor() {
		if (!isEnabled())
			return;
		StartPreslovljavanjeDialog dialog = new StartPreslovljavanjeDialog();
		//PreslovljavanjeDialog dialog = new PreslovljavanjeDialog();
		dialog.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		launchEditor();
	}

	@Override
	public void selectionChanged(SelectionChangeEvent event) {
		Set<OsmPrimitive> newSelection = event.getSelection();
		setEnabled(newSelection != null && newSelection.size() > 0);
	}

}

//