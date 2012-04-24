package org.ethnochat.messaging;

import org.ethnochat.util.NamedObject;

public class Contact implements Comparable<Contact>, NamedObject {

    private String name;
    private MessagingService service;
    private transient boolean isOnline;

    public Contact(String name, MessagingService service) {
        this.name = name;
        this.service = service;
        isOnline = false;
    }

    public String getName() {
        return name;
    }

    public String getQualifiedName() {
        return service.getName() + "." + name;
    }

    public static String[] splitQualifiedName(String qualifiedName) {
        return qualifiedName.split("\\.");
    }

    public MessagingService getMessagingService() {
        return service;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public boolean isOnline() {
        return isOnline;
    }

    @Override public String toString() {
        return name;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Contact)) {
            return false;
        }
        Contact c = (Contact)o;
        return (name.equals(c.name)
            && service.equals(c.service));
    }

    @Override public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + name.hashCode();
        hashCode = 31 * hashCode + service.hashCode();
        return hashCode;
    }

    public int compareTo(Contact c) {
        if (!service.equals(c.service)) {
            return service.compareTo(c.service);
        } else {
            return name.compareTo(c.name);
        }
    }
}
