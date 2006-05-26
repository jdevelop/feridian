package com.echomine.net;

/** the exception exists to indicate that a connection is vetoed (or rejected) */
public class ConnectionVetoException extends Exception {
    private static final long serialVersionUID = -6610075920160732670L;

    public ConnectionVetoException() {
        super();
    }

    public ConnectionVetoException(String s) {
        super(s);
    }
}
