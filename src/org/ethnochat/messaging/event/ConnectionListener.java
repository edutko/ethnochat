package org.ethnochat.messaging.event;

public interface ConnectionListener {

    public void connected(ConnectionEvent e);
    public void disconnected(ConnectionEvent e);
}
