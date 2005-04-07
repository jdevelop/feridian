package com.echomine.jabber;

import com.echomine.common.ParseException;
import org.jdom.Element;

/**
 * All messages that is used to parse incoming messages must implement this method.  Since the parse relies on the JDOM, you
 * will need to use JDOM even if your message does not extend JabberJDOMMessage, or you will need
 * to implement your own Message Parser.
 */
public interface JabberMessageParsable {
    /**
     * parse element/incoming message into a message object. Note that the parsing is unique in that
     * during normal behavior, you will return "this" at the end of the method.  However, for those
     * message that require "morphing" into another message type (ie. IQ Messages), the method can
     * return a new instance of another JabberMessage to replace the current one.
     */
    JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException;
}
