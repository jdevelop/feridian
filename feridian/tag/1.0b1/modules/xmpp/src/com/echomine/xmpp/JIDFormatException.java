package com.echomine.xmpp;

/**
 * any problem while parsing anything can throw this exception. This is a
 * runtime exception
 */
public class JIDFormatException extends RuntimeException {
    private static final long serialVersionUID = -5791502096643888504L;

    public JIDFormatException() {
    }

    public JIDFormatException(String message) {
        super(message);
    }

    public JIDFormatException(Throwable cause) {
        super(cause);
    }

    public JIDFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
