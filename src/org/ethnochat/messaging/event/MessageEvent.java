package org.ethnochat.messaging.event;

import org.ethnochat.messaging.Message;

public class MessageEvent extends java.util.EventObject {

	private static final long serialVersionUID = 5635092158955385742L;

	private Message message;

    public MessageEvent(Object source, Message message) {
        super(source);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    @Override public String toString() {
        return message.toString();
    }
}
