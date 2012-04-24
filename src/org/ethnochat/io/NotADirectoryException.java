package org.ethnochat.io;

public class NotADirectoryException extends Exception {

	private static final long serialVersionUID = -8218398578449371597L;

	public NotADirectoryException() {
        super();
    }

    public NotADirectoryException(String message) {
        super(message + " is not a directory.");
    }
}
