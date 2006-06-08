package com.echomine.xmpp;

/**
 * Main exception class for all XMPP related errors. This is the exception for
 * almost all exceptions thrown by the API.
 */
public class XMPPException extends Exception {
    private static final long serialVersionUID = 567800071603787344L;

    public XMPPException() {
        super();
    }

    public XMPPException(String message) {
        super(message);
    }

    public XMPPException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMPPException(Throwable cause) {
        super(cause);
    }
}
