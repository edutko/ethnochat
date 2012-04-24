package org.ethnochat.project;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import org.ethnochat.messaging.Contact;
import org.ethnochat.messaging.Message;
import org.ethnochat.messaging.MessagingService;
import org.ethnochat.messaging.MessagingServiceManager;
import org.ethnochat.messaging.event.MessageEvent;
import org.ethnochat.messaging.event.MessageListener;
import org.ethnochat.project.event.ConversationManagerEvent;
import org.ethnochat.project.event.ConversationManagerListener;

public class ConversationManager implements MessageListener {

    private MessagingServiceManager serviceManager;
    private Project project;
    private HashMap<Contact, Conversation> conversationMap;
    private LinkedList<ConversationManagerListener> conversationManagerListeners;

    public ConversationManager(
            MessagingServiceManager serviceManager,
            Project project) {
        this.serviceManager = serviceManager;
        this.project = project;
        conversationMap = new HashMap<Contact, Conversation>();
        conversationManagerListeners =
            new LinkedList<ConversationManagerListener>();

        serviceManager.registerMessageListener(this);
    }

    public void addConversationManagerListener(ConversationManagerListener l) {
        conversationManagerListeners.add(l);
    }

    public void removeConversationManagerListener(
            ConversationManagerListener l) {
        conversationManagerListeners.remove(l);
    }

    public Conversation startConversation(Contact contact) {
        Conversation conversation =
            Conversation.create(project, contact);
        try {
            conversation.loadFromFile();
        } catch (java.io.IOException ignored) {
        } catch (org.xml.sax.SAXException e) {
            // TO DO: handle exception
        }
        conversationMap.put(contact, conversation);
        fireConversationStarted(conversation);
        return conversation;
    }

    public void endConversation(Conversation conversation) {
        Contact contact = conversation.getContact();
        conversation.save();
        fireConversationEnded(conversation);
        conversationMap.remove(contact);
    }

    public boolean isConversationActive(Contact contact) {
        return conversationMap.containsKey(contact);
    }

    public MessagingService getMessagingService(java.util.UUID id) {
        return serviceManager.getMessagingService(id);
    }

    public void setProject(Project newProject) {
        endAllConversations();
        project = newProject;
    }

    public void endAllConversations() {
        ArrayList<Contact> keys =
            new ArrayList<Contact>(conversationMap.keySet());
        for (Contact contact : keys) {
            endConversation(conversationMap.get(contact));
        }
    }

    public void messageReceived(MessageEvent event) {
        Contact contact = event.getMessage().getContact();
        Conversation conversation = conversationMap.get(contact);
        if (conversation == null) {
            conversation = startConversation(contact);
        }
        deliverMessage(conversation, event.getMessage());
    }

    MessagingServiceManager getMessagingServiceManager() {
        return serviceManager;
    }

    private void fireConversationStarted(Conversation conversation) {
        ConversationManagerEvent event =
            new ConversationManagerEvent(this, conversation);
        for (ConversationManagerListener l : conversationManagerListeners) {
            l.conversationStarted(event);
        }
    }

    private void fireConversationEnded(Conversation conversation) {
        ConversationManagerEvent event =
            new ConversationManagerEvent(this, conversation);
        for (ConversationManagerListener l : conversationManagerListeners) {
            l.conversationEnded(event);
        }
    }

    private void deliverMessage(Conversation conversation, Message msg) {
        conversation.addMessage(msg);
    }
}
