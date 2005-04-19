package com.echomine.xmpp;


/**
 * base exception class for all XMPP related errors.
 */
public class XMPPException extends Exception {
    ErrorPacket errorPacket;

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

    public XMPPException(ErrorPacket packet) {
        super();
        this.errorPacket = packet;
    }

    public XMPPException(String message, ErrorPacket packet) {
        super(message);
        this.errorPacket = packet;
    }

    /**
     * @return the error packet associated with this exception, or null if none
     */
    public ErrorPacket getErrorPacket() {
        return errorPacket;
    }

    /**
     * @return the error packet's condition string, or null if none
     */
    public String getErrorCondition() {
        if (errorPacket == null)
            return null;
        return errorPacket.getCondition();
    }

    /**
     * 
     * @return the descriptive text (or null) from the error packet
     */
    public String getErrorText() {
        if (errorPacket == null)
            return null;
        return errorPacket.getText();
    }
}
