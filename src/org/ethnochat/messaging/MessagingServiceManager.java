package org.ethnochat.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ethnochat.messaging.event.*;
import org.ethnochat.plugin.*;

public class MessagingServiceManager {

    private PluginManager pluginMgr;

    public MessagingServiceManager(PluginManager pluginManager) {
        this.pluginMgr = pluginManager;
    }

    public Collection<MessagingService> getMessagingServices() {
        ArrayList<MessagingService> list = new ArrayList<MessagingService>();
        Iterator<Plugin> iter =
            pluginMgr.getPlugins(ECPluginType.MESSAGING_SERVICE).iterator();
        while (iter.hasNext()) {
            MessagingServicePlugin p = (MessagingServicePlugin)iter.next();
            list.add(p.getMessagingService());
        }
        return list;
    }

    public MessagingService getMessagingService(java.util.UUID id) {
        MessagingService service = null;
        Iterator<Plugin> iter =
            pluginMgr.getPlugins(ECPluginType.MESSAGING_SERVICE).iterator();
        while (iter.hasNext() && service == null) {
            MessagingServicePlugin p = (MessagingServicePlugin)iter.next();
            service = p.getMessagingService();
            if (!id.equals(service.getID())) {
                service = null;
            }
        }
        return service;
    }

    public void registerConnectionListener(ConnectionListener l) {
        java.util.Iterator<MessagingService> iter =
            getMessagingServices().iterator();
        while (iter.hasNext()) {
            iter.next().addConnectionListener(l);
        }
    }

    public void registerContactListener(ContactListener l) {
        java.util.Iterator<MessagingService> iter =
            getMessagingServices().iterator();
        while (iter.hasNext()) {
            iter.next().addContactListener(l);
        }
    }

    public void registerMessageListener(MessageListener l) {
        java.util.Iterator<MessagingService> iter =
            getMessagingServices().iterator();
        while (iter.hasNext()) {
            iter.next().addMessageListener(l);
        }
    }
}
