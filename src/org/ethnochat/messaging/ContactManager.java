package org.ethnochat.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.ethnochat.messaging.event.ConnectionEvent;
import org.ethnochat.messaging.event.ConnectionListener;
import org.ethnochat.messaging.event.ContactEvent;
import org.ethnochat.messaging.event.ContactListener;
import org.ethnochat.messaging.event.ContactManagerEvent;
import org.ethnochat.messaging.event.ContactManagerEventType;
import org.ethnochat.messaging.event.ContactManagerListener;

public class ContactManager implements ConnectionListener, ContactListener{

    private TreeMap< MessagingService, ArrayList<ContactGroup> > serviceMap;
    private ArrayList<ContactManagerListener> contactManagerListeners;

    public ContactManager(MessagingServiceManager serviceManager) {

        serviceMap =
            new TreeMap< MessagingService, ArrayList<ContactGroup> >();
        contactManagerListeners = new ArrayList<ContactManagerListener>();

        Iterator<MessagingService> iter =
            serviceManager.getMessagingServices().iterator();
        while (iter.hasNext()) {
            addService(iter.next());
        }
    }

    public void addContactManagerListener(ContactManagerListener l) {
        contactManagerListeners.add(l);
    }

    public void removeContactManagerListener(ContactManagerListener l) {
        contactManagerListeners.remove(l);
    }

    public void addService(MessagingService service) {
        if (!serviceMap.containsKey(service)) {
            serviceMap.put(service, new ArrayList<ContactGroup>());
            service.addConnectionListener(this);
            service.addContactListener(this);
        }
    }

    public int getServiceCount() {
        return serviceMap.keySet().size();
    }

    public int getGroupCount() {
        int groupCount = 0;
        for (MessagingService svc : serviceMap.keySet()) {
            ArrayList<ContactGroup> groups = serviceMap.get(svc);
            groupCount += groups.size();
        }
        return groupCount;
    }

    public int getGroupCount(MessagingService service) {
        ArrayList<ContactGroup> groups = serviceMap.get(service);
        return groups.size();
    }

    public int getContactCount() {
        int contactCount = 0;
        for (MessagingService service : serviceMap.keySet()) {
            ArrayList<ContactGroup> groups = serviceMap.get(service);
            for (ContactGroup group : groups) {
                contactCount += group.size();
            }
        }
        return contactCount;
    }

    public int getContactCount(MessagingService service) {
        int contactCount = 0;
        ArrayList<ContactGroup> groups = serviceMap.get(service);
        for (ContactGroup group : groups) {
            contactCount += group.size();
        }
        return contactCount;
    }

    public int getContactCount(ContactGroup group) {
        return group.size();
    }

    public int getOnlineContactCount() {
        int contactCount = 0;
        for (MessagingService service : serviceMap.keySet()) {
            ArrayList<ContactGroup> groups = serviceMap.get(service);
            for (ContactGroup group : groups) {
                contactCount += group.getOnlineContacts().size();
            }
        }
        return contactCount;
    }

    public int getOnlineContactCount(MessagingService service) {
        int contactCount = 0;
        ArrayList<ContactGroup> groups = serviceMap.get(service);
        for (ContactGroup group : groups) {
            contactCount += group.getOnlineContacts().size();
        }
        return contactCount;
    }

    public int getOnlineContactCount(ContactGroup group) {
        return group.getOnlineContacts().size();
    }

    public List<MessagingService> getServices() {
        return Collections.unmodifiableList(
            new ArrayList<MessagingService>(serviceMap.keySet()));
    }

    public List<ContactGroup> getGroups(MessagingService service) {
        return Collections.unmodifiableList(serviceMap.get(service));
    }
/*
    public List<Contact> getContacts(ContactGroup group) {
        return ContactGroup.unmodifiableContactGroup(group);
    }
*/
    public List<Contact> getOnlineContacts(ContactGroup group) {
        return Collections.unmodifiableList(group.getOnlineContacts());
    }

    public ContactGroup getGroupForContact(Contact contact) {
        ContactGroup group = findGroupForContact(contact);
        return ContactGroup.unmodifiableContactGroup(group);
    }

    public void connected(ConnectionEvent event) {
        MessagingService service = (MessagingService)event.getSource();

        ArrayList<ContactGroup> newGroups =
            new ArrayList<ContactGroup>(service.getContactGroups());
        serviceMap.put(service, newGroups);

        fireMessagingServiceChanged(service);
    }

    public void disconnected(ConnectionEvent event) {
        MessagingService service = (MessagingService)event.getSource();
        ArrayList<ContactGroup> groups = serviceMap.get(service);
        for (ContactGroup group : groups) {
            group.setAllContactsOnline(false);
            fireContactGroupChanged(group);
        }
    }

    public void contactListChanged(ContactEvent event) {
        MessagingService service = (MessagingService)event.getSource();

        ArrayList<ContactGroup> newGroups =
            new ArrayList<ContactGroup>(service.getContactGroups());
       serviceMap.put(service, newGroups);

        fireMessagingServiceChanged(service);
    }

    public void contactSignedOn(ContactEvent event) {
        Contact contact = event.getContact();
        ContactGroup group = findGroupForContact(contact);
        group.setContactOnline(contact, true);
        fireContactChanged(contact, ContactManagerEventType.CONTACT_SIGNED_ON);
    }

    public void contactSignedOff(ContactEvent event) {
        Contact contact = event.getContact();
        ContactGroup group = findGroupForContact(contact);
        group.setContactOnline(contact, false);
        fireContactChanged(
            contact,
            ContactManagerEventType.CONTACT_SIGNED_OFF);
    }

    public void contactStatusChanged(ContactEvent event) {
        Contact contact = event.getContact();
        fireContactChanged(
            contact,
            ContactManagerEventType.CONTACT_STATUS_CHANGED);
    }

    public ContactGroup findGroupForContact(Contact contact) {
        ContactGroup group = null;
        ArrayList<ContactGroup> groups =
            serviceMap.get(contact.getMessagingService());
        for (ContactGroup g : groups) {
            if (g.contains(contact)) {
                group = g;
                break;
            }
        }
        return group;
    }

    private void fireContactChanged(
            Contact contact,
            ContactManagerEventType reason) {
        ContactGroup group = getGroupForContact(contact);
        ContactManagerEvent event =
            new ContactManagerEvent(
                this,
                contact,
                group,
                contact.getMessagingService(),
                reason);
        for (ContactManagerListener l : contactManagerListeners) {
            l.contactChanged(event);
        }
    }

    private void fireContactGroupChanged(ContactGroup group) {
        ContactManagerEvent event =
            new ContactManagerEvent(
                this,
                null,
                group,
                group.getMessagingService(),
                ContactManagerEventType.CONTACT_GROUP_CHANGED);
        for (ContactManagerListener l : contactManagerListeners) {
            l.contactGroupChanged(event);
        }
    }

    private void fireMessagingServiceChanged(MessagingService service) {
        ContactManagerEvent event =
            new ContactManagerEvent(
                this,
                null,
                null,
                service,
                ContactManagerEventType.MESSAGING_SERVICE_CHANGED);
        for (ContactManagerListener l : contactManagerListeners) {
            l.messagingServiceChanged(event);
        }
    }
}
