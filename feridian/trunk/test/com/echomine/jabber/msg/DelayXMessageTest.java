package com.echomine.jabber.msg;

import com.echomine.jabber.DefaultMessageParser;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberUtil;
import junit.framework.TestCase;
import org.jdom.Element;

public class DelayXMessageTest extends TestCase {
    DefaultMessageParser parser = new DefaultMessageParser();

    /** tests the delay x message chat parsing */
    public void testDelayXMessageForChat() throws Exception {
        String streamXML = "<x from='juliet@capulet.com' stamp='20030911T20:30:40' xmlns='jabber:x:delay'>Offline Storage</x>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        DelayXMessage xmsg = new DelayXMessage();
        xmsg.parse(parser, elem);
        assertNotNull(xmsg);
        assertEquals("20030911T20:30:40", xmsg.getTime());
        assertEquals("Offline Storage", xmsg.getText());
        assertEquals("juliet@capulet.com", xmsg.getFrom());
    }

    /**
     * this tests that the parser has the message registered to parse the namespace
     */
    public void testParserSupportsMessage() throws Exception {
        assertTrue(parser.supportsParsingFor("x", JabberCode.XMLNS_X_DELAY));
    }

    /**
     * Test to make sure that the message type is set properly
     */
    public void testMessageType() throws Exception {
        DelayXMessage msg = new DelayXMessage();
        assertEquals(JabberCode.MSG_X_DELAY, msg.getMessageType());
    }
}
