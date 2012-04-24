package org.ethnochat.plugin;

import java.util.UUID;

import org.ethnochat.messaging.MessagingService;

public abstract class DefaultMessagingServicePlugin
        implements MessagingServicePlugin {

    private final UUID id;
    private String name;

    protected DefaultMessagingServicePlugin(UUID id) {
    	this.id = id;
    }

    public UUID getID() {
    	return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ECPluginType getType() {
        return ECPluginType.MESSAGING_SERVICE;
    }

    public void onLoad() {}
    public void onUnload() {}

    public MessagingService getMessagingService() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getID() + " (" + getName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MessagingServicePlugin)) {
            return false;
        }
        MessagingServicePlugin p = (MessagingServicePlugin)o;
        return (getType().equals(p.getType())
            && getID().equals(p.getID())
            && getName().equals(p.getName()));
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + getType().hashCode();
        hashCode = 31 * hashCode + getID().hashCode();
        hashCode = 31 * hashCode + getName().hashCode();
        return hashCode;
    }

    public int compareTo(Plugin plugin) {
        Enum<ECPluginType> type = getType();
        if (!type.equals(plugin.getType())) {
            return type.compareTo(plugin.getType());
        }
        if (!getID().equals(plugin.getID())) {
            return getID().compareTo(plugin.getID());
        }
        return getName().compareTo(plugin.getName());
    }
}
