package org.ethnochat.messaging.event;

public interface ContactListener {

    public void contactListChanged(ContactEvent e);
    public void contactSignedOn(ContactEvent e);
    public void contactSignedOff(ContactEvent e);
    public void contactStatusChanged(ContactEvent e);
}
