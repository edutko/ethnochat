package org.ethnochat.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.ethnochat.messaging.Contact;
import org.ethnochat.messaging.DummyMessagingService;
import org.ethnochat.messaging.Message;
import org.ethnochat.messaging.MessagingService;
import org.ethnochat.project.event.TranscriptEvent;
import org.ethnochat.project.event.TranscriptListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Transcript {

    private static final String ROOT_TAG = "EthnoChatTranscript";
    private static final String USER_NAME_TAG = "UserName";
    private static final String NOTE_TAG = "Note";
    private static final String TAG_TAG = "Tag";
    private static final String TAG_ID_ATTR = "id";
    private static final String MESSAGE_TAG = "Message";
    private static final String MESSAGE_TIMESTAMP_ATTR = "when";
    private static final String MESSAGE_FROM_TAG = "From";
    private static final String MESSAGE_TEXT_TAG = "Text";
    private static final String MESSAGING_SERVICE_TAG = "MessagingService";
    private static final String MESSAGING_SERVICE_ID_ATTR = "id";

    private LinkedList<TranscriptListener> transcriptListeners;
    private java.io.File transcriptFile;
    private String myUserName;
    private String msgSvcName;
    private UUID msgSvcID;
    private ArrayList<AnnotatedMessage> messages;
    private transient Project project;
    private transient boolean isDirty;
    private transient Timer autoSaveTimer;

    public Transcript(
            Project project,
            Contact contact) {
        this.project = project;

        org.ethnochat.messaging.MessagingService msgSvc =
            contact.getMessagingService();
        myUserName = msgSvc.getUserName();
        msgSvcName = msgSvc.getName();
        msgSvcID = msgSvc.getID();

        transcriptFile = new java.io.File(
            project.getConversationTranscriptDirectory(),
            contact.getQualifiedName());

        transcriptListeners = new LinkedList<TranscriptListener>();
        messages = new ArrayList<AnnotatedMessage>();
        autoSaveTimer = new Timer();
    }

    public Transcript(
            Project project,
            java.io.File transcriptFile) {
        this.project = project;
        this.transcriptFile = transcriptFile;
        transcriptListeners = new LinkedList<TranscriptListener>();
        messages = new ArrayList<AnnotatedMessage>();
        autoSaveTimer = new Timer();
    }

    public void addTranscriptListener(TranscriptListener l) {
        transcriptListeners.add(l);
    }

    public void removeTranscriptListener(TranscriptListener l) {
        transcriptListeners.remove(l);
    }

    public void addMessage(Message msg) {
        AnnotatedMessage aMsg = new AnnotatedMessage(msg);
        addMessage(aMsg);
        fireMessageAdded(aMsg);
    }

    public void addMessage(AnnotatedMessage msg) {
        isDirty = true;
        messages.add(msg);
    }

    public void addNoteAt(int index, Note note) {
        isDirty = true;
        AnnotatedMessage msg = messages.get(index);
        if (msg.addNote(note)) {
            fireNoteAdded(msg, note);
        }
    }

    public void addTagAt(int index, Tag tag) {
        isDirty = true;
        AnnotatedMessage msg = messages.get(index);
        if (msg.addTag(tag)) {
            fireTagAdded(msg, tag);
        }
    }

    public List<AnnotatedMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public int getMessageCount() {
        return messages.size();
    }

    public boolean isMessageFromMe(Message msg) {
        if (myUserName.compareToIgnoreCase(msg.getContact().getName()) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void save() {
        autoSaveTimer.schedule(new SaveTranscriptTimerTask(), 0);
    }

    public synchronized void loadFromFile()
            throws java.io.IOException, org.xml.sax.SAXException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //dbf.setValidating(true);
            DocumentBuilder parser = dbf.newDocumentBuilder();
            Document doc = parser.parse(transcriptFile);

            NodeList userNameNodes =
                doc.getElementsByTagName(USER_NAME_TAG);
            Node node = userNameNodes.item(0);
            myUserName = node.getTextContent();

            NodeList msgSvcNodes =
                doc.getElementsByTagName(MESSAGING_SERVICE_TAG);
            node = msgSvcNodes.item(0);
            msgSvcID =
                java.util.UUID.fromString(
                    node.getAttributes()
                        .getNamedItem(MESSAGING_SERVICE_ID_ATTR)
                            .getTextContent());
            msgSvcName = node.getTextContent();
            MessagingService svc = new DummyMessagingService(msgSvcID, msgSvcName);

            NodeList messageNodes = doc.getElementsByTagName(MESSAGE_TAG);
            for (int i = 0; i < messageNodes.getLength(); i++) {
                Element msgElement = (Element)messageNodes.item(i);

                String timestampStr =
                    msgElement.getAttributes()
                        .getNamedItem(MESSAGE_TIMESTAMP_ATTR)
                            .getTextContent();
                java.util.Date timestamp = new java.util.Date();
                try {
                    DatatypeFactory df = DatatypeFactory.newInstance();
                    timestamp = df
                        .newXMLGregorianCalendar(timestampStr)
                            .toGregorianCalendar()
                                .getTime();
                } catch (javax.xml.datatype.DatatypeConfigurationException e) {
                    // TO DO: handle exception
                }

                String fromStr =
                    msgElement.getElementsByTagName(MESSAGE_FROM_TAG)
                        .item(0).getTextContent();
                Contact from = new Contact(fromStr, svc);

                String messageText =
                    msgElement.getElementsByTagName(MESSAGE_TEXT_TAG)
                        .item(0).getTextContent();
                AnnotatedMessage msg =
                    new AnnotatedMessage(timestamp, from, messageText);

                NodeList tagNodes = msgElement.getElementsByTagName(TAG_TAG);
                for (int j = 0; j < tagNodes.getLength(); j++) {
                    Element tagElement = (Element)tagNodes.item(j);
                    String tagIdStr =
                        tagElement.getAttributes()
                            .getNamedItem(TAG_ID_ATTR)
                                .getTextContent();
                    Integer id = Integer.parseInt(tagIdStr);
                    msg.addTag(project.getTagManager().getTagByID(id));
                }

                NodeList noteNodes = msgElement.getElementsByTagName(NOTE_TAG);
                for (int j = 0; j < noteNodes.getLength(); j++) {
                    Element noteElement = (Element)noteNodes.item(j);
                    msg.addNote(new Note(noteElement.getTextContent()));
                }

                messages.add(msg);
            }

            isDirty = false;
            autoSaveTimer.schedule(new SaveTranscriptTimerTask(), 60000, 60000);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            // TO DO: handle exception
        }
    }

    protected void fireMessageAdded(AnnotatedMessage msg) {
        TranscriptEvent event = new TranscriptEvent(this, msg);
        for (TranscriptListener l : transcriptListeners) {
            l.messageAdded(event);
        }
    }

    protected void fireTagAdded(AnnotatedMessage msg, Tag tag) {
        TranscriptEvent event = new TranscriptEvent(this, msg, tag);
        for (TranscriptListener l : transcriptListeners) {
            l.tagAdded(event);
        }
    }

    protected void fireTagChanged(AnnotatedMessage msg, Tag tag) {
        TranscriptEvent event = new TranscriptEvent(this, msg, tag);
        for (TranscriptListener l : transcriptListeners) {
            l.tagChanged(event);
        }
    }

    protected void fireTagRemoved(AnnotatedMessage msg, Tag tag) {
        TranscriptEvent event = new TranscriptEvent(this, msg, tag);
        for (TranscriptListener l : transcriptListeners) {
            l.tagRemoved(event);
        }
    }

    protected void fireNoteAdded(AnnotatedMessage msg, Note note) {
        TranscriptEvent event = new TranscriptEvent(this, msg, note);
        for (TranscriptListener l : transcriptListeners) {
            l.noteAdded(event);
        }
    }

    protected void fireNoteChanged(AnnotatedMessage msg, Note note) {
        TranscriptEvent event = new TranscriptEvent(this, msg, note);
        for (TranscriptListener l : transcriptListeners) {
            l.noteChanged(event);
        }
    }

    protected void fireNoteRemoved(AnnotatedMessage msg, Note note) {
        TranscriptEvent event = new TranscriptEvent(this, msg, note);
        for (TranscriptListener l : transcriptListeners) {
            l.noteRemoved(event);
        }
    }

    private synchronized void storeToFile() throws java.io.IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        try {
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement(ROOT_TAG);
            doc.appendChild(root);

            Element element = doc.createElement(MESSAGING_SERVICE_TAG);
            element.setAttribute(
                MESSAGING_SERVICE_ID_ATTR,
                msgSvcID.toString());
            element.appendChild(doc.createTextNode(msgSvcName));
            root.appendChild(element);

            element = doc.createElement(USER_NAME_TAG);
            element.appendChild(doc.createTextNode(myUserName));
            root.appendChild(element);

            for (AnnotatedMessage msg : messages) {
                Element msgElement = doc.createElement(MESSAGE_TAG);

                String whenStr = "2008-01-01T00:00:00Z";
                java.util.GregorianCalendar cal =
                    new java.util.GregorianCalendar();
                cal.setTime(msg.getTimestamp());
                try {
                    DatatypeFactory df = DatatypeFactory.newInstance();
                    whenStr = df.newXMLGregorianCalendar(cal).toString();
                } catch (javax.xml.datatype.DatatypeConfigurationException e) {
                    // TO DO: handle exception
                }
                msgElement.setAttribute(
                    MESSAGE_TIMESTAMP_ATTR,
                    whenStr);

                Element fromElement = doc.createElement(MESSAGE_FROM_TAG);
                fromElement.appendChild(
                    doc.createTextNode(msg.getContact().getName()));
                msgElement.appendChild(doc.createTextNode("\n  "));
                msgElement.appendChild(fromElement);

                Element textElement = doc.createElement(MESSAGE_TEXT_TAG);
                textElement.appendChild(doc.createTextNode(msg.getText()));
                msgElement.appendChild(doc.createTextNode("\n  "));
                msgElement.appendChild(textElement);

                for (Tag tag : msg.getTags()) {
                    Element tagElement = doc.createElement(TAG_TAG);
                    tagElement.setAttribute(
                        TAG_ID_ATTR,
                        tag.getID().toString());
//                    tagElement.appendChild(
//                        doc.createTextNode(tag.getText()));
                    msgElement.appendChild(doc.createTextNode("\n  "));
                    msgElement.appendChild(tagElement);
                }

                for (Note note : msg.getNotes()) {
                    Element noteElement = doc.createElement(NOTE_TAG);
                    noteElement.appendChild(
                        doc.createTextNode(note.getText()));
                    msgElement.appendChild(noteElement);
                }

                root.appendChild(msgElement);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.METHOD, "xml");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            Source src = new DOMSource(doc);
            Result dest =
                new StreamResult(new java.io.FileOutputStream(transcriptFile));
            serializer.transform(src, dest);

            isDirty = false;
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            // TO DO: handle exception
        } catch (javax.xml.transform.TransformerConfigurationException e) {
            // TO DO: handle exception
        } catch (javax.xml.transform.TransformerException e) {
            // TO DO: handle exception
        }
    }

    private class SaveTranscriptTimerTask extends TimerTask {
        @Override
		public void run() {
            if (isDirty) {
                try {
                    storeToFile();
                } catch (java.io.IOException e) {
                    // TO DO: handle exception
                }
            }
        }
    }
}
