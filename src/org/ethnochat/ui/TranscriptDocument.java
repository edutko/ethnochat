package org.ethnochat.ui;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Position;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.ethnochat.messaging.Message;
import org.ethnochat.project.AnnotatedMessage;
import org.ethnochat.project.Note;
import org.ethnochat.project.Tag;
import org.ethnochat.project.Transcript;
import org.ethnochat.project.event.TranscriptEvent;
import org.ethnochat.project.event.TranscriptListener;

class TranscriptDocument extends DefaultStyledDocument
        implements TranscriptListener {

	private static final long serialVersionUID = 5476371581348169833L;
	private static ImageIcon tagIcon;
    private static ImageIcon noteIcon;
    private boolean showTagAndNoteText;

    private Transcript transcript;
    private ArrayList<Position> messageEndPositions;

    static {
        java.net.URL url =
            TranscriptDocument.class.getResource("/tag.gif");
        if (url == null) {
            throw new RuntimeException("Missing resource: tag.gif");
        }
        tagIcon = new ImageIcon(url);

        url = TranscriptDocument.class.getResource("/note.gif");
        if (url == null) {
            throw new RuntimeException("Missing resource: note.gif");
        }
        noteIcon = new ImageIcon(url);
    }

    TranscriptDocument(Transcript transcript, boolean showTagAndNoteText) {
        this.transcript = transcript;
        this.showTagAndNoteText = showTagAndNoteText;
        messageEndPositions = new ArrayList<Position>();

        Style DEFAULT = getStyle(StyleContext.DEFAULT_STYLE);
        addStyle("ME", DEFAULT);
        StyleConstants.setBold(getStyle("ME"), true);

        addStyle("YOU", DEFAULT);
        StyleConstants.setBold(getStyle("YOU"), true);
        StyleConstants.setForeground(
            getStyle("YOU"),
            new Color(0xcc, 0x00, 0x00));

        addStyle("TagText", DEFAULT);
        StyleConstants.setBackground(
            getStyle("TagText"),
            new Color(0x99, 0x99, 0xff));
        StyleConstants.setAlignment(
            getStyle("TagText"),
            StyleConstants.ALIGN_RIGHT);

        addStyle("NoteText", DEFAULT);
        StyleConstants.setBackground(
            getStyle("NoteText"),
            new Color(0xff, 0xff, 0x99));
        StyleConstants.setAlignment(
            getStyle("NoteText"),
            StyleConstants.ALIGN_RIGHT);

        addStyle("TagIcon", DEFAULT);
        StyleConstants.setIcon(getStyle("TagIcon"), tagIcon);

        addStyle("NoteIcon", DEFAULT);
        StyleConstants.setIcon(getStyle("NoteIcon"), noteIcon);

        int i = 0;
        for (AnnotatedMessage msg : transcript.getMessages()) {
            addMessage(msg);

            if (msg.getTagCount() > 0 && !showTagAndNoteText) {
                addIconAtMessageIndex(i, "TagIcon");
            }
            for (Tag tag : msg.getTags()) {
                addTag(msg, tag);
            }

            if (msg.getNoteCount() > 0 && !showTagAndNoteText) {
                addIconAtMessageIndex(i, "NoteIcon");
            }
            for (Note note : msg.getNotes()) {
                addNote(msg, note);
            }

            i++;
        }
        transcript.addTranscriptListener(this);
    }

    public int getMessageIndexByPosition(int offset) {
        int index = 0;
        while (offset > messageEndPositions.get(index).getOffset()
                && index < messageEndPositions.size()) {
            index++;
        }
        return index;
    }

    public void messageAdded(TranscriptEvent event) {
        Message msg = event.getMessage();
        addMessage(msg);
    }

    public void tagAdded(TranscriptEvent event) {
        AnnotatedMessage msg = event.getMessage();
        Tag tag = event.getTag();
        if (msg.getTagCount() == 1 && !showTagAndNoteText) {
            // This is the first tag
            addTagIconForMessage(msg);
        }
        addTag(msg, tag);
    }

    public void tagChanged(TranscriptEvent event) {
    }

    public void tagRemoved(TranscriptEvent event) {
    }

    public void noteAdded(TranscriptEvent event) {
        AnnotatedMessage msg = event.getMessage();
        Note note = event.getNote();
        if (msg.getNoteCount() == 1 && !showTagAndNoteText) {
            // This is the first note
            addNoteIconForMessage(msg);
        }
        addNote(msg, note);
    }

    public void noteChanged(TranscriptEvent event) {
    }

    public void noteRemoved(TranscriptEvent event) {
    }

    private void addMessage(Message msg) {
        Style style = getStyle("YOU");
        if (transcript.isMessageFromMe(msg)) {
            style = getStyle("ME");
        }
        try {
            insertString(
                getLength(),
                msg.getContact().getName() + ": ",
                style);
            insertString(
                getLength(),
                msg.getText() + "\n",
                getStyle(StyleContext.DEFAULT_STYLE));
            messageEndPositions.add(createPosition(getLength() - 1));
        } catch (javax.swing.text.BadLocationException ignored) {}
    }

    private int getMessageIndex(AnnotatedMessage msg) {
        java.util.List<AnnotatedMessage> messages =
            transcript.getMessages();
        int index = transcript.getMessageCount() - 1;
        for (int i = 0; i < transcript.getMessageCount(); i++) {
            if (messages.get(i) == msg) {
                index = i;
            }
        }
        return index;
    }

    private void addIconAtMessageIndex(int index, String iconStyleName) {
        try {
            insertString(
                messageEndPositions.get(index).getOffset(),
                " ",
                getStyle(StyleContext.DEFAULT_STYLE));
            insertString(
                messageEndPositions.get(index).getOffset(),
                " ",
                getStyle(iconStyleName));
        } catch (javax.swing.text.BadLocationException ignored) {}
    }

    private void addTextAtMessageIndex(int index, String text, String iconStyleName) {
        try {
            insertString(
                messageEndPositions.get(index).getOffset(),
                " ",
                getStyle(StyleContext.DEFAULT_STYLE));
            insertString(
                messageEndPositions.get(index).getOffset(),
                "[" + text + "]",
                getStyle(iconStyleName));
        } catch (javax.swing.text.BadLocationException ignored) {}
    }

    private void addTagIconForMessage(AnnotatedMessage msg) {
        int index = getMessageIndex(msg);
        addIconAtMessageIndex(index, "TagIcon");
    }

    private void addNoteIconForMessage(AnnotatedMessage msg) {
        int index = getMessageIndex(msg);
        addIconAtMessageIndex(index, "NoteIcon");
    }

    private void addTag(AnnotatedMessage msg, Tag tag) {
        if (showTagAndNoteText) {
            int index = getMessageIndex(msg);
            addTextAtMessageIndex(index, tag.getText(), "TagText");
        } else {
            // TO DO: put tag text somewhere (maybe in icon tooltip?)
        }
    }

    private void addNote(AnnotatedMessage msg, Note note) {
        // TO DO: put note text somewhere
        if (showTagAndNoteText) {
            int index = getMessageIndex(msg);
            addTextAtMessageIndex(index, note.getText(), "NoteText");
        } else {
            // TO DO: put tag text somewhere (maybe in icon tooltip?)
        }
    }
}
