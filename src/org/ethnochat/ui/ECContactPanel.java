package org.ethnochat.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.ethnochat.application.EthnoChatApp;
import org.ethnochat.messaging.Contact;
import org.ethnochat.messaging.MessagingService;
import org.ethnochat.messaging.event.ConnectionEvent;
import org.ethnochat.messaging.event.ConnectionListener;
import org.ethnochat.util.ResourceManager;

public class ECContactPanel extends DockablePanel
        implements ConnectionListener {

	private static final long serialVersionUID = 3736905112866389311L;

	private final EthnoChatApp appInstance;

    private ContactManagerTreeModelAdapter contactListTreeModel;
    private JTree contactListTree;
    private JButton addContactButton;
    private JButton removeContactButton;
    private JButton addGroupButton;
    private JButton removeGroupButton;

    public ECContactPanel(EthnoChatApp appInstance) {
        this.appInstance = appInstance;

        ResourceBundle res = ResourceManager.getControlResources();
        setName(res.getString("CONTACT_WND_TITLE"));

        addContactButton = new JButton("+");
        removeContactButton = new JButton("-");
        addGroupButton = new JButton("+");
        removeGroupButton = new JButton("-");

        contactListTreeModel =
            new ContactManagerTreeModelAdapter(appInstance.getContactManager());
        contactListTree = new JTree(contactListTreeModel);
        contactListTree.setRootVisible(false);
        contactListTree.addTreeSelectionListener(
            new ContactTreeSelectionListener());
        contactListTree.addMouseListener(new ContactTreeMouseListener());

        layoutComponents();
        setControlState();

        appInstance.getMessagingServiceManager()
            .registerConnectionListener(this);
    }

    public void connected(ConnectionEvent event) {
        setControlState();
    }

    public void disconnected(ConnectionEvent event) {
        setControlState();
    }

    private void layoutComponents() {
        ResourceBundle res = ResourceManager.getControlResources();

        GridBagLayout gb = new GridBagLayout();
        setLayout(gb);

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 1.0;
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(contactListTree);
        gb.setConstraints(scrollPane, c);
        add(scrollPane);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 0.0;
        JLabel contactButtonsLabel = new JLabel(res.getString("CONTACTS_LBL"));
        gb.setConstraints(contactButtonsLabel, c);
        add(contactButtonsLabel);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 0.0;
        JLabel groupButtonsLabel = new JLabel(res.getString("GROUPS_LBL"));
        gb.setConstraints(groupButtonsLabel, c);
        add(groupButtonsLabel);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;

        gb.setConstraints(addContactButton, c);
        add(addContactButton);

        gb.setConstraints(removeContactButton, c);
        add(removeContactButton);

        gb.setConstraints(addGroupButton, c);
        add(addGroupButton);

        gb.setConstraints(removeGroupButton, c);
        add(removeGroupButton);

        setMinimumSize(getPreferredSize());
    }

    private void setControlState() {
        MessagingService service = null;

        Object selectedNode = contactListTree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            service =
                contactListTreeModel.getMessagingServiceForNode(selectedNode);
        }
        if (service == null
                || !service.isConnected()) {
            addContactButton.setEnabled(false);
            removeContactButton.setEnabled(false);
            addGroupButton.setEnabled(false);
            removeGroupButton.setEnabled(false);
        } else {
            addContactButton.setEnabled(true);
            removeContactButton.setEnabled(true);
            addGroupButton.setEnabled(true);
            removeGroupButton.setEnabled(true);
        }
    }

    private class ContactTreeSelectionListener
            implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent event) {
            setControlState();
        }
    }

    private class ContactTreeMouseListener extends MouseAdapter {

        @Override
		public void mousePressed(MouseEvent event) {
            if (event.getClickCount() == 2) {
                TreePath selectedPath = contactListTree.getPathForLocation(
                    event.getX(),
                    event.getY());
                if (selectedPath != null
                        && selectedPath.getLastPathComponent() instanceof Contact) {
                    appInstance.getConversationManager().startConversation(
                        (Contact)selectedPath.getLastPathComponent());
                }
             }
         }
     };
}
