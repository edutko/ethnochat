package org.ethnochat.plugin;

public class ManifestMissingException extends Exception {

	private static final long serialVersionUID = 1798434898393699003L;

	public ManifestMissingException() {
        super();
    }

    public ManifestMissingException(String message) {
        super(message);
    }
}
