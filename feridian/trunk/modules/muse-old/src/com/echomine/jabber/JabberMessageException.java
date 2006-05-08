package com.echomine.jabber;

/**
 * Any problems when authenticating or registering or anything related to the User Service will throw this exception.  This
 * exception contains the Error Message returned by the server that you can use to find out more information.
 */
public class JabberMessageException extends Exception {
    private ErrorMessage msg;

    public JabberMessageException(ErrorMessage msg) {
        super("User Service Exception");
        this.msg = msg;
    }

    public ErrorMessage getError() {
        return msg;
    }

    public String getErrorMessage() {
        return msg.getMessage();
    }

    public int getErrorCode() {
        return msg.getCode();
    }

    public String toString() {
        return msg.getMessage();
    }
}
