package org.ethnochat.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import org.ethnochat.application.EthnoChatApp;
import org.ethnochat.messaging.Contact;
import org.ethnochat.messaging.Message;
import org.ethnochat.messaging.MessagingService;
import org.ethnochat.messaging.event.ConnectionEvent;
import org.ethnochat.messaging.event.ConnectionListener;
import org.ethnochat.messaging.event.ContactEvent;
import org.ethnochat.messaging.event.ContactListener;
import org.ethnochat.project.Conversation;
import org.ethnochat.project.Tag;
import org.ethnochat.util.ResourceManager;

public class ECMessagingPanel extends DockablePanel
        implements ConnectionListener,
        ContactListener {

	private static final long serialVersionUID = 2378088335412544949L;

	private static final Style DEFAULT = StyleContext
        .getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

    private JTextPane conversationPane;
    private JTextPane composeMessagePane;
    private JComboBox tagComboBox;
    private JButton addTagButton;
    private JButton addNoteButton;
    private JButton sendButton;
    private MessagingService msgSvc;
    private Conversation conversation;
    private Contact self;

    public ECMessagingPanel(
            EthnoChatApp appInstance,
            Conversation conversation) {
        this.conversation = conversation;
        msgSvc = conversation.getContact().getMessagingService();
        self = new Contact(msgSvc.getUserName(), msgSvc);

        ResourceBundle res = ResourceManager.getControlResources();
        setName(res.getString("MESSAGE_WND_TITLE")
            + ": " + conversation.getContact().getName());

        conversationPane = new JTextPane();
        conversationPane.setDocument(
            new TranscriptDocument(conversation.getTranscript(), false));
        conversationPane.setEditable(false);

        javax.swing.Action a = new SendMessageAction(res.getString("SEND"));
        composeMessagePane = new JTextPane();
        composeMessagePane.getInputMap().put(
            KeyStroke.getKeyStroke("ENTER"), "sendMessage");
        composeMessagePane.getActionMap().put("sendMessage", a);
        composeMessagePane.getDocument().addDocumentListener(
            new MessagingPanelDocumentListener());

        sendButton = new JButton(a);

        a = new AddTagAction(res.getString("ADD_TAG_BTN"));
        tagComboBox = new JComboBox(
            new TagManagerComboBoxModelAdapter(
                appInstance.getCurrentProject().getTagManager()));
        addTagButton = new JButton(a);
        addNoteButton = new JButton(res.getString("ADD_NOTE_BTN"));

        layoutComponents();
        setControlState();
        composeMessagePane.requestFocusInWindow();
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void insertComposeText(String text) {
        javax.swing.text.Document doc = composeMessagePane.getDocument();
        try {
            doc.insertString(
                doc.getLength(),
                text,
                DEFAULT);
        } catch (javax.swing.text.BadLocationException ignored) {}
    }

    public void connected(ConnectionEvent event) {
        if (msgSvc.equals(event.getSource())) {

        }
    }

    public void disconnected(ConnectionEvent event) {
        if (msgSvc.equals(event.getSource())) {

        }
    }

    public void contactListChanged(ContactEvent e) {}

    public void contactSignedOn(ContactEvent event) {
        if (msgSvc.equals(event.getSource())
                && conversation.getContact().equals(event.getContact())) {

        }
    }

    public void contactSignedOff(ContactEvent event) {
        if (msgSvc.equals(event.getSource())
                && conversation.getContact().equals(event.getContact())) {

        }
    }

    public void contactStatusChanged(ContactEvent event) {}

    private void layoutComponents() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        GridBagLayout gb = new GridBagLayout();
        JPanel conversationPanel = new JPanel(gb);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weighty = 1.0;
        c.weightx = 1.0;

        JScrollPane conversationScrollPane = new JScrollPane(conversationPane);
        conversationScrollPane.setVerticalScrollBarPolicy(
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        conversationScrollPane.setPreferredSize(new Dimension(250, 145));
        //conversationScrollPane.setMinimumSize(new Dimension(10, 10));
        gb.setConstraints(conversationScrollPane, c);
        conversationPanel.add(conversationScrollPane);

        c.gridwidth = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.NONE;
        c.weighty = 0;

        JPanel tagControlPanel = new JPanel();

        tagControlPanel.add(tagComboBox);
        tagControlPanel.add(addTagButton);

        gb.setConstraints(tagControlPanel, c);
        conversationPanel.add(tagControlPanel);

        c.gridwidth = 1;
        gb.setConstraints(addNoteButton, c);
        conversationPanel.add(addNoteButton);

        splitPane.add(conversationPanel);
        splitPane.setResizeWeight(1.0);

        gb = new GridBagLayout();
        JPanel composePanel = new JPanel(gb);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;

        JScrollPane msgCompScrollPane = new JScrollPane(composeMessagePane);
        msgCompScrollPane.setVerticalScrollBarPolicy(
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        msgCompScrollPane.setPreferredSize(new Dimension(200, 50));
        //msgCompScrollPane.setMinimumSize(new Dimension(10, 10));
        c.insets = new Insets(4, 4, 4, 0);
        c.weightx = 1.0;
        gb.setConstraints(msgCompScrollPane, c);
        composePanel.add(msgCompScrollPane);

        c.insets = new Insets(4, 4, 4, 4);
        c.weightx = 0;
        gb.setConstraints(sendButton, c);
        composePanel.add(sendButton);

        splitPane.add(composePanel);
        add(splitPane);

        setMinimumSize(getPreferredSize());
    }

    private void setControlState() {
        if (composeMessagePane.getText().length() == 0) {
            sendButton.setEnabled(false);
        } else {
            sendButton.setEnabled(true);
        }
    }

    private void addTag() {
        Tag tag = (Tag)tagComboBox.getSelectedItem();
        if (tag != null) {
            int index = ((TranscriptDocument)conversationPane.getDocument())
                .getMessageIndexByPosition(conversationPane.getCaretPosition());
            conversation.addTagAt(index, tag);
        }
    }

    private void sendMessage() {
        String messageText = composeMessagePane.getText();
        if (messageText.length() > 0) {
            Message message = new Message(self, messageText);
            composeMessagePane.setText("");
            msgSvc.sendMessage(conversation.getContact(), message);
            conversation.addMessage(message);
        }
    }

    private class MessagingPanelDocumentListener implements DocumentListener {

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

    private class SendMessageAction extends javax.swing.AbstractAction {

		private static final long serialVersionUID = -2322609582024750830L;

		public SendMessageAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent event) {
            sendMessage();
        }
    }

    private class AddTagAction extends javax.swing.AbstractAction {

		private static final long serialVersionUID = -1930030511547044667L;

		public AddTagAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent event) {
            addTag();
        }
    }
}
