package org.ethnochat.messaging.event;

public class ConnectionEvent extends java.util.EventObject {

	private static final long serialVersionUID = 1465531208698207027L;

	boolean isConnected;

    public ConnectionEvent(Object source) {
        super(source);
    }

    public boolean isConnected() {
        return isConnected;
    }
}
