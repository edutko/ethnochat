package org.ethnochat.plugin;

import java.util.UUID;

import org.ethnochat.util.MutableNamedObject;

public interface Plugin
        extends MutableNamedObject,
            Comparable<Plugin> {

    public UUID getID();
    public ECPluginType getType();
    public void onLoad();
    public void onUnload();
}
