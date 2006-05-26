package com.echomine.jabber;

/**
 * <p>Base message type that uses a string to store the message.  The message does not get parsed into XML DOM tree.  It simply
 * just sends the string as is.  This is useful for debugging and for simple messages to send.  This message is usually used
 * for outgoing message purpose.</p> <p>By default, the String messages will NOT support X Messages and encode them for
 * outputting purposes because the string is as-is.</p>
 */
public class JabberStringMessage extends JabberMessage {
    private String msgText;
    private int msgType;

    public JabberStringMessage(int msgType) {
        this(msgType, "");
    }

    public JabberStringMessage(int msgType, String msgText) {
        super();
        this.msgType = msgType;
        this.msgText = msgText;
    }

    /** sets the message to the passed in XML text. */
    public void setMessage(String msgText) {
        this.msgText = msgText;
    }

    /** @return the message contained by this object */
    public String getMessage() {
        return msgText;
    }

    public String encode() {
        return msgText;
    }

    public String toString() {
        return msgText;
    }

    public void setMessageType(int msgType) {
        this.msgType = msgType;
    }

    public int getMessageType() {
        return msgType;
    }
}
