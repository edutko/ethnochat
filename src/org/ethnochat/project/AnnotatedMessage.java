package org.ethnochat.project;

import org.ethnochat.messaging.Contact;
import org.ethnochat.messaging.Message;

public class AnnotatedMessage extends Message {

    private java.util.ArrayList<Tag> tags;
    private java.util.ArrayList<Note> notes;

    public AnnotatedMessage(Contact from, String text) {
        super(from, text);

        tags = new java.util.ArrayList<Tag>();
        notes = new java.util.ArrayList<Note>();
    }

    public AnnotatedMessage(
            java.util.Date timestamp,
            Contact from,
            String text) {
        super(timestamp, from, text);

        tags = new java.util.ArrayList<Tag>();
        notes = new java.util.ArrayList<Note>();
    }

    public AnnotatedMessage(Message msg) {
        super(msg);

        tags = new java.util.ArrayList<Tag>();
        notes = new java.util.ArrayList<Note>();
    }

    public AnnotatedMessage(AnnotatedMessage msg) {
        super(msg);

        tags = new java.util.ArrayList<Tag>(msg.getTags());
        notes = new java.util.ArrayList<Note>(msg.getNotes());
    }

    public boolean addTag(Tag tag) {
        if (tags.contains(tag)) {
            return false;
        }
        return tags.add(tag);
    }

    public java.util.List<Tag> getTags() {
        return java.util.Collections.unmodifiableList(tags);
    }

    public int getTagCount() {
        return tags.size();
    }

    public boolean addNote(Note note) {
        return notes.add(note);
    }

    public java.util.List<Note> getNotes() {
        return java.util.Collections.unmodifiableList(notes);
    }

    public int getNoteCount() {
        return notes.size();
    }

    @Override public String toString() {
        String str = super.toString();
        str += " [tags: " + tags.get(0);
        for (int i = 1; i < tags.size(); i++) {
            str += ", " + tags.get(i);
        }
        str += "] [notes: " + notes.get(0);
        for (int i = 1; i < notes.size(); i++) {
            str += ", " + notes.get(i);
        }
        str += "]";
        return str;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof AnnotatedMessage)) {
            return false;
        }
        AnnotatedMessage m = (AnnotatedMessage)o;
        return (super.equals(m)
                && tags.equals(m.tags)
                && notes.equals(m.notes));
    }

    @Override public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 31 * hashCode + tags.hashCode();
        hashCode = 31 * hashCode + notes.hashCode();
        return hashCode;
    }
}
