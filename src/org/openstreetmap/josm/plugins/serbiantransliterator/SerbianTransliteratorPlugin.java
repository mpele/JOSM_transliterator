package org.openstreetmap.josm.plugins.serbiantransliterator;

import static org.openstreetmap.josm.tools.I18n.tr;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class SerbianTransliteratorPlugin extends Plugin {

    LaunchAction action;
    
    /**
     * constructor 
     */
    public SerbianTransliteratorPlugin(PluginInformation info) {
        super(info);
        action = new LaunchAction();
        MainMenu.add(Main.main.menu.editMenu, action);
    }
}
