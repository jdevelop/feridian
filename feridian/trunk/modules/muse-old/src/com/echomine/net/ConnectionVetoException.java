package com.echomine.net;

/** the exception exists to indicate that a connection is vetoed (or rejected) */
public class ConnectionVetoException extends Exception {
    public ConnectionVetoException() {
        super();
    }

    public ConnectionVetoException(String s) {
        super(s);
    }
}
