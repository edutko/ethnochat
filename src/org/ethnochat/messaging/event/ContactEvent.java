package org.ethnochat.messaging.event;

import org.ethnochat.messaging.Contact;

public class ContactEvent extends java.util.EventObject {

	private static final long serialVersionUID = -188535232570503550L;

	private Contact contact;

    public ContactEvent(Object source, Contact contact) {
        super(source);
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}
