package org.ethnochat.project;

import java.util.List;

import org.ethnochat.messaging.Contact;
import org.ethnochat.messaging.Message;
import org.ethnochat.project.event.TranscriptListener;

public class Conversation
        implements org.ethnochat.messaging.event.MessageListener {

    private Transcript transcript;
    private transient Contact contact;

    public static Conversation create(
            Project project,
            Contact contact) {
        return new Conversation(project, contact);
    }

    public void addTranscriptListener(TranscriptListener l) {
        transcript.addTranscriptListener(l);
    }

    public void removeTranscriptListener(TranscriptListener l) {
        transcript.removeTranscriptListener(l);
    }

    public void addMessage(Message msg) {
        transcript.addMessage(msg);
    }

    public void addTagAt(int index, Tag tag) {
        transcript.addTagAt(index, tag);
    }

    public Contact getContact() {
        return contact;
    }

    public List<AnnotatedMessage> getMessages() {
        return transcript.getMessages();
    }

    public int getMessageCount() {
        return transcript.getMessageCount();
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public void save() {
        transcript.save();
    }

    public void loadFromFile()
            throws java.io.IOException, org.xml.sax.SAXException {
        transcript.loadFromFile();
    }

    public void messageReceived(
            org.ethnochat.messaging.event.MessageEvent event) {
        addMessage(event.getMessage());
    }

    private Conversation(
            Project project,
            Contact contact) {
        this.contact = contact;
        transcript = new Transcript(project, contact);
    }
}
