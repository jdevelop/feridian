package com.echomine.jabber;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * <p>The interface is for instantiating a message object by looking at the internal elements.
 * Basically, the message builder will look at the elements, determine what type of message it is,
 * and then return an appropriate instance that is able to parse the data out of the elements.</p>
 */
public interface JabberMessageParser {
    /** instantiate a message object by looking at the DOM tree */
    JabberMessage createMessage(String qName, Namespace ns, Element msgTree) throws MessageNotSupportedException;
}
