package org.ethnochat.project.event;

import org.ethnochat.project.AnnotatedMessage;
import org.ethnochat.project.Note;
import org.ethnochat.project.Tag;

public class TranscriptEvent extends java.util.EventObject {

	private static final long serialVersionUID = -4397158588030456856L;

	private AnnotatedMessage message;
    private Tag tag;
    private Note note;

    public TranscriptEvent(Object source, AnnotatedMessage message) {
        super(source);
        this.message = message;
    }

    public TranscriptEvent(
            Object source,
            AnnotatedMessage message,
            Tag tag) {
        super(source);
        this.message = message;
        this.tag = tag;
    }

    public TranscriptEvent(
            Object source,
            AnnotatedMessage message,
            Note note) {
        super(source);
        this.message = message;
        this.note = note;
    }

    public AnnotatedMessage getMessage() {
        return message;
    }

    public Tag getTag() {
        return tag;
    }

    public Note getNote() {
        return note;
    }
}
