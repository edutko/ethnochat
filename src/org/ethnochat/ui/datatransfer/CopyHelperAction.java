package org.ethnochat.ui.datatransfer;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.text.JTextComponent;

import org.ethnochat.util.ResourceManager;

class CopyHelperAction extends CCPHelperAction {

	private static final long serialVersionUID = -9110942825391487042L;

	public CopyHelperAction() {
        ResourceBundle res = ResourceManager.getControlResources();
        putValue(Action.NAME, res.getString("COPY"));
    }

    public void actionPerformed(ActionEvent event) {
        if (focusedComponent instanceof JTextComponent) {
            ((JTextComponent)focusedComponent).copy();
        }
    }
}
