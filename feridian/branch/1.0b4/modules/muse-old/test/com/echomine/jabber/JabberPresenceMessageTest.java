package com.echomine.jabber;

import junit.framework.TestCase;
import org.jdom.Element;

/**
 * Tests the jabber presence message class
 */
public class JabberPresenceMessageTest extends TestCase {
    DefaultMessageParser parser = new DefaultMessageParser();

    /**
     * a bug was introduced when the presence message was modified to conform
     * to the XMPP protocol specs.  The specs states that when available presence
     * is being sent or received, there is no need to include the "type" attribute.
     * It is by default available when the type attribute does not exist in the XML.
     * Thus, what happens when someone calls the message's isError(), NullPointerException
     * may occur if no type attribute exists.
     * This test case will check out this error.
     */
    public void testPresenceAvailableTypeIsNull() throws Exception {
        String streamXML = "<presence xmlns='jabber:client' from='abc@abc.com' to='def@abc.com'/>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        JabberPresenceMessage xmsg = new JabberPresenceMessage();
        xmsg.parse(parser, elem);
        assertNotNull(xmsg);
        //make sure the calling isError() will not cause a NPE
        assertTrue(!xmsg.isError());
        assertNull(xmsg.getType());
    }
}
