package org.ethnochat.messaging;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;

public class ContactGroup extends AbstractList<Contact>
        implements org.ethnochat.util.MutableNamedObject {

    private String name;
    private MessagingService service;
    private ArrayList<Contact> allContacts;
    private transient ArrayList<Contact> onlineContacts;

    public static ContactGroup unmodifiableContactGroup(ContactGroup src) {
        return new ContactGroup.ReadOnlyContactGroup(src);
    }

    public ContactGroup(String name, MessagingService service) {
        this.name = name;
        this.service = service;
        allContacts = new ArrayList<Contact>();
        onlineContacts = new ArrayList<Contact>();
    }

    public java.util.List<Contact> getOnlineContacts() {
        return Collections.unmodifiableList(onlineContacts);
    }

    public void setContactOnline(Contact c, boolean isOnline) {
        if (allContacts.contains(c)) {
            c.setOnline(true);
            onlineContacts.add(c);
        }
    }

    public void setAllContactsOnline(boolean isOnline) {
        onlineContacts.clear();
        if (isOnline) {
            onlineContacts.addAll(allContacts);
        } else {
            onlineContacts.clear();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MessagingService getMessagingService() {
        return service;
    }

    @Override public Contact get(int index) {
        return allContacts.get(index);
    }

    @Override public int size() {
        return allContacts.size();
    }

    @Override public boolean add(Contact c) {
        boolean added = allContacts.add(c);
        if (added && c.isOnline()) {
            onlineContacts.add(c);
        }
        return added;
    }

    @Override public Contact remove(int index) {
        onlineContacts.remove(allContacts.get(index));
        return allContacts.remove(index);
    }

    public boolean remove(Contact c) {
        onlineContacts.remove(c);
        return allContacts.remove(c);
    }

    @Override public String toString() {
        return name;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof ContactGroup)) {
            return false;
        }
        ContactGroup g = (ContactGroup)o;
        if (!service.equals(g.service) || !name.equals(g.name)) {
            return false;
        }
        return allContacts.equals(g.allContacts);
    }

    @Override public int hashCode() {
        int hashCode = 13;
        hashCode = 31 * hashCode + allContacts.hashCode();
        hashCode = 31 * hashCode + name.hashCode();
        hashCode = 31 * hashCode + service.hashCode();
        return hashCode;
    }

    protected ArrayList<Contact> getContacts() {
        return allContacts;
    }

    protected void setContacts(ArrayList<Contact> contacts) {
        allContacts = contacts;
    }

    protected void setOnlineContacts(ArrayList<Contact> contacts) {
        onlineContacts = contacts;
    }

    private static class ReadOnlyContactGroup extends ContactGroup {

        ReadOnlyContactGroup(ContactGroup g) {
            super(g.getName(), g.getMessagingService());
            setContacts(
                new ArrayList<Contact>(
                    Collections.unmodifiableList(g.getContacts())));
            setOnlineContacts(
                new ArrayList<Contact>(
                    Collections.unmodifiableList(g.getOnlineContacts())));
        }

        @Override public void setName(String name) {
            throw new UnsupportedOperationException();
        }

        @Override public void setContactOnline(Contact c, boolean isOnline) {
            throw new UnsupportedOperationException();
        }

        @Override public void setAllContactsOnline(boolean isOnline) {
            throw new UnsupportedOperationException();
        }

        @Override public boolean add(Contact q) {
            throw new UnsupportedOperationException();
        }

        @Override public Contact remove(int index) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Contact q) {
            throw new UnsupportedOperationException();
        }
    }
}
