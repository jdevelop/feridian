package com.echomine.jabber;

import junit.framework.TestCase;
import org.jdom.Element;

public class JabberChatMessageTest extends TestCase {

    /**
     * Tests a previous glitch that was altering the thread IDs
     * of incoming chat messages.
     */
    public void testThreadID() throws Exception {
        final String TID = "chatNum01";
        JabberChatMessage message = new JabberChatMessage();
        message.setThreadID(TID);
        Element msgTree = message.getDOM();
        DefaultMessageParser parser = new DefaultMessageParser();
        message = (JabberChatMessage) parser.createMessage("message", JabberCode.XMLNS_CHAT, msgTree);
        assertEquals("The ThreadID has been changed.", TID, message.getThreadID());
    }
}

