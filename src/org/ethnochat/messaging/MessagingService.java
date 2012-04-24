package org.ethnochat.messaging;

import org.ethnochat.messaging.event.*;

public interface MessagingService
        extends org.ethnochat.util.NamedObject,
            Comparable<MessagingService> {

    public java.util.UUID getID();
    public void addConnectionListener(ConnectionListener l);
    public void addContactListener(ContactListener l);
    public void addMessageListener(MessageListener l);
    public void removeConnectionListener(ConnectionListener l);
    public void removeContactListener(ContactListener l);
    public void removeMessageListener(MessageListener l);
    public void login(String userName, String password);
    public void logout();
    public String getUserName();
    public boolean isConnected();
    public java.util.Collection<ContactGroup> getContactGroups();
    public void addContactGroup(ContactGroup group);
    public void addContact(Contact contact);
    public void addContact(Contact contact, ContactGroup group);
    public void removeContact(Contact contact);
    public void removeGroup(ContactGroup group);
    public void sendMessage(Contact to, Message message);
}
