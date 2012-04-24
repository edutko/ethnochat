package org.ethnochat.ui;

import java.awt.Container;

import javax.swing.JInternalFrame;

public class ECInternalFrame extends JInternalFrame {

	private static final long serialVersionUID = 5700720136452140434L;

	public ECInternalFrame(Container content) {
        super(content.getName());

        setContentPane(content);

        setSize(getPreferredSize());
//        setMinimumSize(getPreferredSize());
        pack();
    }
}
