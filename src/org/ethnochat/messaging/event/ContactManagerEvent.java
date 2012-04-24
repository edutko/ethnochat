package org.ethnochat.messaging.event;

import org.ethnochat.messaging.Contact;
import org.ethnochat.messaging.ContactGroup;
import org.ethnochat.messaging.MessagingService;

public class ContactManagerEvent extends java.util.EventObject {

	private static final long serialVersionUID = -3534630658311581125L;

	private Contact contact;
    private ContactGroup group;
    private MessagingService service;
    private ContactManagerEventType type;

    public ContactManagerEvent(Object source,
            Contact contact,
            ContactGroup group,
            MessagingService service,
            ContactManagerEventType type) {
        super(source);
        this.contact = contact;
        this.group = group;
        this.service = service;
        this.type = type;
    }

    public Contact getContact() {
        return contact;
    }

    public ContactGroup getContactGroup() {
        return group;
    }

    public MessagingService getMessagingService() {
        return service;
    }

    public ContactManagerEventType getType() {
        return type;
    }
}
