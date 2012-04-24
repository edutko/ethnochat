package org.ethnochat.messaging.event;

public interface ContactManagerListener {

    public void contactAdded(ContactManagerEvent event);
    public void contactChanged(ContactManagerEvent event);
    public void contactRemoved(ContactManagerEvent event);
    public void contactGroupAdded(ContactManagerEvent event);
    public void contactGroupChanged(ContactManagerEvent event);
    public void contactGroupRemoved(ContactManagerEvent event);
    public void messagingServiceChanged(ContactManagerEvent event);
}
