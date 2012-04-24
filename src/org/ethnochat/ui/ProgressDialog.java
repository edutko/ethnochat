package org.ethnochat.ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class ProgressDialog extends JDialog implements ProgressIndicator,
        ActionListener {

	private static final long serialVersionUID = 8415330208769816871L;

	private JProgressBar progressBar;
    Timer timer;

    public ProgressDialog(Frame owner, String title) {
        super(owner, title);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        getContentPane().add(progressBar);

        pack();
        setLocationRelativeTo(owner);
        setSize(getPreferredSize());
    }

    public void setVisibleAfterDelay(int delay) {
        timer = new Timer(delay, this);
        timer.setRepeats(false);
        timer.start();
    }

    public void setMinimum(int value) {
        progressBar.setMinimum(value);
    }

    public void setMaximum(int value) {
        progressBar.setMaximum(value);
    }

    public void setValue(int value) {
        progressBar.setIndeterminate(false);
        progressBar.setValue(value);
    }

    public void actionPerformed(ActionEvent e) {
        if (isDisplayable()) {
            setVisible(true);
        }
    }
}
