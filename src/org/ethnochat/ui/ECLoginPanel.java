package org.ethnochat.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ethnochat.application.EthnoChatApp;
import org.ethnochat.messaging.MessagingService;
import org.ethnochat.messaging.MessagingServiceManager;
import org.ethnochat.messaging.event.ConnectionEvent;
import org.ethnochat.messaging.event.ConnectionListener;
import org.ethnochat.util.ObjectDescriptor;
import org.ethnochat.util.ResourceManager;

public class ECLoginPanel extends DockablePanel
        implements ConnectionListener {

	private static final long serialVersionUID = -9201623935302051201L;

	private final EthnoChatApp appInstance;

    private MessagingServiceManager serviceManager;
    private JComboBox serviceCB;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;

    public ECLoginPanel(EthnoChatApp appInstance) {
        this.appInstance = appInstance;
        serviceManager = appInstance.getMessagingServiceManager();
        ResourceBundle res = ResourceManager.getControlResources();
        setName(res.getString("LOGIN_WND_TITLE"));
        layoutComponents();
        setControlState();
        populateServiceList();
    }

    public void connected(ConnectionEvent event) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                appInstance.getMainWindow().showLoginPanel(false);
            }
        });
    }
    public void disconnected(ConnectionEvent event) {}

    private void layoutComponents() {

        ResourceBundle res = ResourceManager.getControlResources();

        GridBagLayout gb = new GridBagLayout();
        setLayout(gb);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel(res.getString("SERVICE_LBL"));
        c.insets = new Insets(4, 4, 0, 0);
        c.weightx = 0;
        gb.setConstraints(label, c);
        add(label);

        serviceCB = new JComboBox();
        c.insets = new Insets(4, 4, 0, 4);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        gb.setConstraints(serviceCB, c);
        add(serviceCB);

        label = new JLabel(res.getString("USERNAME_LBL"));
        c.insets = new Insets(4, 4, 0, 0);
        c.gridwidth = GridBagConstraints.RELATIVE;;
        c.weightx = 0;
        gb.setConstraints(label, c);
        add(label);

        LoginPanelKeyListener kl = new LoginPanelKeyListener();
        LoginPanelDocumentListener dl = new LoginPanelDocumentListener();
        userNameField = new JTextField();
        userNameField.addKeyListener(kl);
        userNameField.getDocument().addDocumentListener(dl);
        userNameField.setColumns(15);
        c.insets = new Insets(4, 4, 0, 4);
        c.gridwidth = GridBagConstraints.REMAINDER;;
        c.weightx = 1.0;
        gb.setConstraints(userNameField, c);
        add(userNameField);

        label = new JLabel(res.getString("PASSWORD_LBL"));
        c.insets = new Insets(4, 4, 4, 0);
        c.gridwidth = GridBagConstraints.RELATIVE;;
        c.weightx = 0;
        gb.setConstraints(label, c);
        add(label);

        passwordField = new JPasswordField();
        passwordField.addKeyListener(kl);
        passwordField.getDocument().addDocumentListener(dl);
        passwordField.setColumns(15);
        c.insets = new Insets(4, 4, 4, 4);
        c.gridwidth = GridBagConstraints.REMAINDER;;
        c.weightx = 1.0;
        gb.setConstraints(passwordField, c);
        add(passwordField);

        JPanel buttonPanel = new JPanel();
        //buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

        LoginPanelActionListener al = new LoginPanelActionListener();
        loginButton = new JButton(res.getString("LOGIN"));
        loginButton.addActionListener(al);

        cancelButton = new JButton(res.getString("CANCEL"));
        cancelButton.addActionListener(al);

        //buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(loginButton);
        //buttonPanel.add(Box.createRigidArea(new Dimension(4, 0)));
        buttonPanel.add(cancelButton);

        c.insets = new Insets(4, 4, 4, 4);
        c.gridwidth = 2;
        c.weightx = 0;
        gb.setConstraints(buttonPanel, c);
        add(buttonPanel);

        setMinimumSize(getPreferredSize());
    }

    private void populateServiceList() {
        serviceCB.removeAllItems();
        Iterator<MessagingService> iter =
            serviceManager.getMessagingServices().iterator();
        while (iter.hasNext()) {
            MessagingService svc = iter.next();
            serviceCB.addItem(new ObjectDescriptor<MessagingService>(svc));
        }
    }

    private void setControlState() {
        if (userNameField.getText().length() == 0
                || passwordField.getPassword().length == 0) {
            loginButton.setEnabled(false);
        } else {
            loginButton.setEnabled(true);
        }
    }

    private void login() {
        MessagingService service =
            ((ObjectDescriptor<MessagingService>)
		    serviceCB.getSelectedItem()).getObject();
        service.addConnectionListener(this);
        new Thread(
            new BackgroundLoginTask(getParentFrame(),
                service,
                userNameField.getText(),
                passwordField.getPassword())).start();
        // TO DO: handle error cases (e.g. authorization failure)

        userNameField.setText("");
        passwordField.setText("");
    }

    private class LoginPanelActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == loginButton) {
                login();
            } else if (event.getSource() == cancelButton) {
                userNameField.setText("");
                passwordField.setText("");
                ECParentFrame parent = appInstance.getMainWindow();
                parent.showLoginPanel(false);
            }
        }
    }

    private class LoginPanelDocumentListener implements DocumentListener {

        public void changedUpdate(DocumentEvent event) {
            setControlState();
        }

        public void insertUpdate(DocumentEvent event) {
            setControlState();
        }

        public void removeUpdate(DocumentEvent event) {
            setControlState();
        }
     }

    private class LoginPanelKeyListener implements KeyListener {

        public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    if (loginButton.isEnabled()) {
                        login();
                    }
                    break;
            }
        }

        public void keyReleased(KeyEvent event) {}
        public void keyTyped(KeyEvent event) {}
    }
}
