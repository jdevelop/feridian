package com.echomine.net;

/** exception thrown when anything goes wrong with connection-related stuff. */
public class ConnectionException extends Exception {
    private static final long serialVersionUID = 3273882575798588837L;

    public ConnectionException() {
        super();
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);

    }

    public ConnectionException(Throwable cause) {
        super(cause);

    }
}
