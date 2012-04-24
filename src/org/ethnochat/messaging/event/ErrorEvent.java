package org.ethnochat.messaging.event;

public class ErrorEvent extends java.util.EventObject {

	private static final long serialVersionUID = -7755227868435719498L;

	private ErrorType type;
    private String message;

    public ErrorEvent(Object source, ErrorType type, String message) {
        super(source);
        this.type = type;
        this.message = message;
    }

    public ErrorType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
