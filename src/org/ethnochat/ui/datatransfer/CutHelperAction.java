package org.ethnochat.ui.datatransfer;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.text.JTextComponent;

import org.ethnochat.util.ResourceManager;

class CutHelperAction extends CCPHelperAction {

	private static final long serialVersionUID = 9099963868694500341L;

	public CutHelperAction() {
        ResourceBundle res = ResourceManager.getControlResources();
        putValue(Action.NAME, res.getString("CUT"));
    }

    public void actionPerformed(ActionEvent event) {
        if (focusedComponent instanceof JTextComponent) {
            ((JTextComponent)focusedComponent).cut();
        }
    }
}
