package org.ethnochat.ui;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JTextArea;

public class NoteDialog extends JDialog {

	private static final long serialVersionUID = -4335126852085135695L;

	private JTextArea textArea;

	public NoteDialog(Frame owner, String title) {
        super(owner, title);

        textArea = new JTextArea();
        getContentPane().add(textArea);

        pack();
        setLocationRelativeTo(owner);
        setSize(getPreferredSize());
    }

	public String getText() {
		return textArea.getText();
	}
}
