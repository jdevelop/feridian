package com.echomine.jabber;

import com.echomine.common.ParseException;

/**
 * This message represents a null message that does absolutely nothing.
 * The reason it is here is to provide a mechanism for sending a NOOP message
 * that can be used to provide keepalive pings.
 * Because the Jabber communication is based on XML, spaces and newlines
 * doesn't affect the output or processing of the message.  In this message,
 * we use a space instead.
 */
public class NullMessage extends JabberMessage {
    static final String NULLSTRING = " ";

    public int getMessageType() {
        return JabberCode.MSG_UNKNOWN;
    }

    public String encode() throws ParseException {
        return NULLSTRING;
    }
}
