package com.echomine.net;

/** exception thrown when anything goes wrong with connection-related stuff. */
public class ConnectionException extends Exception {
    public ConnectionException() {
        super();
    }

    public ConnectionException(String par1) {
        super(par1);
    }
}
