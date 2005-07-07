package com.echomine.xmpp;

/**
 * any problem while parsing anything can throw this exception.
 */
public class ParseException extends XMPPException {
    private static final long serialVersionUID = -5791502096643888504L;

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
