package org.ethnochat.project.event;

public interface TagManagerListener {

    public void tagAdded(TagManagerEvent event);
    public void tagChanged(TagManagerEvent event);
    public void tagRemoved(TagManagerEvent event);
}
