package org.ethnochat.plugin;

import org.ethnochat.messaging.MessagingService;

public interface MessagingServicePlugin extends Plugin {

    public MessagingService getMessagingService();
}
