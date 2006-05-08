package com.echomine.jabber;

import com.echomine.common.ParseException;
import org.jdom.Namespace;
import org.jdom.Element;

/**
 * test message object used solely to do simple testing
 * The format of the test message XML is as follows:
 * <code>
 * &lt;test>test&lt;test>
 * </code>
 */
public class TestMessage extends JabberJDOMMessage implements JabberMessageParsable {
    public static final Namespace XMLNS = Namespace.getNamespace("parser:test:x");
    private String text;

    public TestMessage() {
    }

    /** @return the sample text */
    public String getText() {
        return text;
    }

    /** sets the sample text */
    public void setText(String text) {
        this.text = text;
    }

    /** @return a message type code that is unique to the message (for this class, it returns 0) */
    public int getMessageType() {
        return 0;
    }

    /**
     * encodes the data into an XML string that is ready to be sent out to the network.
     * This method is only used for outgoing messages.
     */
    public String encode() throws ParseException {
        return null;
    }

    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        super.parse(parser, msgTree);
        text = msgTree.getText();
        return this;
    }
}
