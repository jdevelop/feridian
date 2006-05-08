package com.echomine.jabber;

import org.jdom.Element;

/**
 * The iq message contains the parsed <iq> tag plus whatever content is sent inside that tag.
 * The message inside the <iq> tag is in a DOM tree and can be parsed further for more
 * information, such as query, register, etc.  The IQ message by default requires a reply.
 */
public class JabberIQMessage extends AbstractJabberMessage {
    public final static String TYPE_GET = "get";
    public final static String TYPE_SET = "set";
    public final static String TYPE_RESULT = "result";

    /**
     * this constructor is for creating outgoing messages.  It is here to be used by
     * subclasses.  The constructor simply creates a default element tree with the
     * <iq> as the top top level tag, and then sets the message to use that tree.
     */
    public JabberIQMessage(String type) {
        super(type, new Element("iq", JabberCode.XMLNS_IQ));
        setReplyRequired(true);
    }

    /** constructs an iq message with default iq type of "get" */
    public JabberIQMessage() {
        this(TYPE_GET);
    }

    public int getMessageType() {
        return JabberCode.MSG_IQ;
    }
}
