package org.ethnochat.ui;

import java.awt.Cursor;
import java.awt.Frame;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import org.ethnochat.messaging.MessagingService;
import org.ethnochat.util.ResourceManager;

public class BackgroundLoginTask implements Runnable {

    private Frame owner;
    private ProgressDialog progressDlg;
    private MessagingService service;
    private String userName;
    private char[] password;

    public BackgroundLoginTask(Frame owner,
            MessagingService service,
            String userName,
            char[] password) {
        ResourceBundle res = ResourceManager.getControlResources();
        String title = res.getString("LOGIN_WAIT_WND_TITLE");
        progressDlg = new ProgressDialog(owner, title);
        this.owner = owner;
        this.service = service;
        this.userName = userName;
        this.password = password;
    }

    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                owner.setCursor(
                    Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
        });
        progressDlg.setVisibleAfterDelay(500);

        service.login(userName, new String(password));
        for (int i = 0; i < password.length; i++) {
            password[i] = 0;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                owner.setCursor(Cursor.getDefaultCursor());
                progressDlg.dispose();
            }
        });
    }
}
