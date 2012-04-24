package org.ethnochat.ui;

import java.awt.Container;
import java.awt.Frame;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

class DockablePanel extends JPanel {

	private static final long serialVersionUID = 3501973251500750565L;

	public boolean isDocked() {
        return (getParentInternalFrame() == null);
    }

    public JInternalFrame getParentInternalFrame() {
        Container ancestor = getParent();
        while (ancestor != null
                && !(ancestor instanceof JInternalFrame)) {
            ancestor = ancestor.getParent();
        }
        if (ancestor != null) {
            return (JInternalFrame)ancestor;
        }
        return null;
    }

    protected Frame getParentFrame() {
        Container ancestor = getParent();
        while (ancestor != null
                && !(ancestor instanceof Frame)) {
            ancestor = ancestor.getParent();
        }
        return (Frame)ancestor;
    }
}
