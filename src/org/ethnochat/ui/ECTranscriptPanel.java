package org.ethnochat.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import org.ethnochat.application.EthnoChatApp;
import org.ethnochat.project.Note;
import org.ethnochat.project.Tag;
import org.ethnochat.project.Transcript;
import org.ethnochat.util.ResourceManager;

public class ECTranscriptPanel extends DockablePanel {

	private static final long serialVersionUID = 2649475126884608423L;

	private JComboBox tagComboBox;
    private JButton addTagButton;
    private JButton addNoteButton;
    private JTextPane transcriptPane;
    private JTextField searchTF;
    private JButton searchBeforeButton;
    private JButton searchAfterButton;
    private Transcript transcript;

    public ECTranscriptPanel(
            EthnoChatApp appInstance,
            Transcript transcript) {
        this.transcript = transcript;

        ResourceBundle res = ResourceManager.getControlResources();
        setName(res.getString("TRANSCRIPT_WND_TITLE"));

        javax.swing.Action a = new AddTagAction(res.getString("ADD_TAG_BTN"));
        tagComboBox = new JComboBox(
            new TagManagerComboBoxModelAdapter(
                appInstance.getCurrentProject().getTagManager()));
        addTagButton = new JButton(a);

        a = new AddNoteAction(res.getString("ADD_NOTE_BTN"));
        addNoteButton = new JButton(a);

        transcriptPane = new JTextPane();
        transcriptPane.setDocument(new TranscriptDocument(transcript, true));
        transcriptPane.setEditable(false);

        searchTF = new JTextField(15);
        searchBeforeButton = new JButton(res.getString("SEARCH_BEFORE_BTN"));
        searchAfterButton = new JButton(res.getString("SEARCH_AFTER_BTN"));

        layoutComponents();
        setControlState();
    }

    private void layoutComponents() {
        ResourceBundle res = ResourceManager.getControlResources();

        GridBagLayout gb = new GridBagLayout();
        setLayout(gb);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1.0;
        c.weighty = 0;

        JPanel tagControlPanel = new JPanel();
        tagControlPanel.add(tagComboBox);
        tagControlPanel.add(addTagButton);

        gb.setConstraints(tagControlPanel, c);
        add(tagControlPanel);

        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(addNoteButton, c);
        add(addNoteButton);

        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;

        JScrollPane scrollPane = new JScrollPane(transcriptPane);
        scrollPane.setVerticalScrollBarPolicy(
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(250, 145));
//        transcriptScrollPane.setMinimumSize(new Dimension(10, 10));
        gb.setConstraints(scrollPane, c);
        add(scrollPane);

        c.fill = GridBagConstraints.NONE;
        c.weighty = 0;

        JPanel searchPanel = new JPanel();
        JLabel searchLabel = new JLabel(res.getString("SEARCH_LBL"));
        searchPanel.add(searchLabel);
        searchPanel.add(searchTF);
        searchPanel.add(searchAfterButton);
        searchPanel.add(searchBeforeButton);
        gb.setConstraints(searchPanel, c);
        add(searchPanel);

        setMinimumSize(getPreferredSize());
    }

    private void setControlState() {
    }

    private class AddTagAction extends javax.swing.AbstractAction {

		private static final long serialVersionUID = 4864035109760890873L;

		public AddTagAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent event) {
            addTag();
        }
    }

    private void addTag() {
        Tag tag = (Tag)tagComboBox.getSelectedItem();
        if (tag != null) {
            int index = ((TranscriptDocument)transcriptPane.getDocument())
                .getMessageIndexByPosition(transcriptPane.getCaretPosition());
            transcript.addTagAt(index, tag);
        }
    }

    private class AddNoteAction extends javax.swing.AbstractAction {

		private static final long serialVersionUID = 4864035109760890873L;

		public AddNoteAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent event) {
            addNote();
        }
    }

    private void addNote() {
    	NoteDialog dialog = new NoteDialog(this.getParentFrame(), "Add Note");
    	dialog.setVisible(true);
        Note note = new Note(dialog.getText());
        if (note != null) {
            int index = ((TranscriptDocument)transcriptPane.getDocument())
                .getMessageIndexByPosition(transcriptPane.getCaretPosition());
            transcript.addNoteAt(index, note);
        }
    }
}
