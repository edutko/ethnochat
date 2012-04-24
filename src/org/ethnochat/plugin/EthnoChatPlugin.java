package org.ethnochat.plugin;

import java.util.UUID;

import org.ethnochat.util.MutableNamedObject;

public interface EthnoChatPlugin
        extends MutableNamedObject,
            Comparable<EthnoChatPlugin> {

    public UUID getID();
    public ECPluginType getType();
    public void onLoad();
    public void onUnload();
}
