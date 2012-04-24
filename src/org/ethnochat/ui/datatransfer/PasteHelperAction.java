package org.ethnochat.ui.datatransfer;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.text.JTextComponent;

import org.ethnochat.util.ResourceManager;

class PasteHelperAction extends CCPHelperAction {

	private static final long serialVersionUID = -1154294218555824348L;

	public PasteHelperAction() {
        ResourceBundle res = ResourceManager.getControlResources();
        putValue(Action.NAME, res.getString("PASTE"));
    }

    public void actionPerformed(ActionEvent event) {
        if (focusedComponent instanceof JTextComponent) {
            ((JTextComponent)focusedComponent).paste();
        }
    }
}
