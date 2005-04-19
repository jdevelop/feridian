package com.echomine.xmpp;

/**
 * any problem while parsing anything can throw this exception.
 */
public class ParseException extends XMPPException {
    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
