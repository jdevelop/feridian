package com.echomine.xmpp;

import com.echomine.xmpp.packet.ErrorPacket;

/**
 * This exception class is specifically to indicate that the exception stems
 * from seeing an error stanza. It does not mean there is some sort of general
 * xmpp exception (message sending failed, etc). The communication is perfect,
 * but the reply from the server indicates the request itself has an error.
 */
public class XMPPStanzaErrorException extends XMPPException {
    private static final long serialVersionUID = -3783848222282547179L;
    private ErrorPacket errorPacket;

    /**
     * @param packet
     */
    public XMPPStanzaErrorException(ErrorPacket packet) {
        super();
        this.errorPacket = packet;
    }

    /**
     * @param message
     * @param packet
     */
    public XMPPStanzaErrorException(String message, ErrorPacket packet) {
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
