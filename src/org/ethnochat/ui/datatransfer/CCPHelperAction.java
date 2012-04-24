package org.ethnochat.ui.datatransfer;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

abstract class CCPHelperAction extends AbstractAction
        implements PropertyChangeListener {

	private static final long serialVersionUID = -2598631330761771943L;

	protected Component focusedComponent;

    public CCPHelperAction() {
        KeyboardFocusManager
            .getCurrentKeyboardFocusManager()
                .addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent event) {
        focusedComponent = KeyboardFocusManager
            .getCurrentKeyboardFocusManager()
                .getPermanentFocusOwner();
    }
}
