package org.ethnochat.messaging;

import java.util.LinkedList;
import java.util.UUID;
import org.ethnochat.messaging.event.*;

public abstract class DefaultMessagingService
        implements MessagingService {

    private static final java.util.UUID DEFAULT_UUID =
        UUID.fromString("00000000-0000-0000-0000-000000000000");

    private String name;

    private LinkedList<ConnectionListener> connectionListeners;
    private LinkedList<ContactListener> contactListeners;
    private LinkedList<ErrorListener> errorListeners;
    private LinkedList<MessageListener> messageListeners;

    public DefaultMessagingService() {
        connectionListeners = new LinkedList<ConnectionListener>();
        contactListeners = new LinkedList<ContactListener>();
        errorListeners = new LinkedList<ErrorListener>();
        messageListeners = new LinkedList<MessageListener>();
    }

    public UUID getID() {
        return DEFAULT_UUID;
    }

    public String getName() {
        return name;
    }

    public boolean isConnected() {
        return false;
    }

    public void addConnectionListener(ConnectionListener l) {
        connectionListeners.add(l);
    }

    public void addContactListener(ContactListener l) {
        contactListeners.add(l);
    }

    public void addErrorListener(ErrorListener l) {
        errorListeners.add(l);
    }

    public void addMessageListener(MessageListener l) {
        messageListeners.add(l);
    }

    public String getUserName() {
        throw new UnsupportedOperationException();
    }

    public void addContactGroup(ContactGroup group) {
        throw new UnsupportedOperationException();
    }

    public void addContact(Contact contact) {
        throw new UnsupportedOperationException();
    }

    public void addContact(Contact contact, ContactGroup group) {
        throw new UnsupportedOperationException();
    }

    public void removeContact(Contact contact) {
        throw new UnsupportedOperationException();
    }

    public void removeGroup(ContactGroup group) {
        throw new UnsupportedOperationException();
    }

    public java.util.Collection<ContactGroup> getContactGroups() {
        return new java.util.ArrayList<ContactGroup>();
    }

    public void sendMessage(Contact to, Message message) {
        throw new UnsupportedOperationException();
    }

    public void login(String userName, String password) {
        throw new UnsupportedOperationException();
    }

    public void logout() {
        throw new UnsupportedOperationException();
    }

    public void removeConnectionListener(ConnectionListener l) {
        connectionListeners.remove(l);
    }

    public void removeContactListener(ContactListener l) {
        contactListeners.remove(l);
    }

    public void removeErrorListener(ErrorListener l) {
        errorListeners.remove(l);
    }

    public void removeMessageListener(MessageListener l) {
        messageListeners.remove(l);
    }

    @Override public String toString() {
        return getName();
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof MessagingService)) {
            return false;
        }
        MessagingService s = (MessagingService)o;
        return getID().equals(s.getID());
    }

    @Override public int hashCode() {
        return getID().hashCode();
    }

    public int compareTo(MessagingService service) {
        return getID().compareTo(service.getID());
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void fireErrorOccurred(ErrorEvent event) {
        for (ErrorListener l : errorListeners) {
            l.errorOccurred(event);
        }
    }

    protected void fireContactListChanged(ContactEvent event) {
        for (ContactListener l : contactListeners) {
            l.contactListChanged(event);
        }
    }

    protected void fireContactSignedOn(ContactEvent event) {
        for (ContactListener l : contactListeners) {
            l.contactSignedOn(event);
        }
    }

    protected void fireContactSignedOff(ContactEvent event) {
        for (ContactListener l : contactListeners) {
            l.contactSignedOff(event);
        }
    }

    protected void fireContactStatusChanged(ContactEvent event) {
        for (ContactListener l : contactListeners) {
            l.contactStatusChanged(event);
        }
    }

    protected void fireConnected(ConnectionEvent event) {
        for (ConnectionListener l : connectionListeners) {
            l.connected(event);
        }
    }

    protected void fireDisconnected(ConnectionEvent event) {
        for (ConnectionListener l : connectionListeners) {
            l.disconnected(event);
        }
    }

    protected void fireMessageReceived(MessageEvent event) {
        for (MessageListener l : messageListeners) {
            l.messageReceived(event);
        }
    }
}
