package com.echomine.jabber;

import com.echomine.common.ParseException;
import junit.framework.TestCase;
import org.jdom.Element;

/**
 * Tests the default message parser to check whether the clases are being properly parsed
 */
public class DefaultMessageParserTest extends TestCase {
    /** Tests that the setting of a proper parser will work correctly. */
    public void testSetProperParser() throws Exception {
        DefaultMessageParser parser = new DefaultMessageParser();
        parser.setParser("x", TestMessage.XMLNS, "com.echomine.jabber.TestMessage");
        assertTrue(parser.supportsParsingFor("x", TestMessage.XMLNS));
    }

    /** tests that the setting of a improper parser will throw an error */
    public void testSetNonJabberMessageParsableParser() throws Exception {
        DefaultMessageParser parser = new DefaultMessageParser();
        try {
            parser.setParser("x", TestMessage.XMLNS, "java.lang.Object");
            fail("Setting a non JabberMessageParsable class as a parser should throw an exception");
        } catch (ParseException ex) {
            //success
        }
    }

    /** tests the creation of a jabber message */
    public void testCreateMessage() throws Exception {
        DefaultMessageParser parser = new DefaultMessageParser();
        parser.setParser("test", TestMessage.XMLNS, "com.echomine.jabber.TestMessage");
        assertTrue(parser.supportsParsingFor("test", TestMessage.XMLNS));
        String xmlStr = "<test xmlns='" + TestMessage.XMLNS.getURI() + "'>yabba yabba</test>";
        Element elem = JabberUtil.parseXmlStringToDOM(xmlStr);
        TestMessage msg = (TestMessage) parser.createMessage("test", TestMessage.XMLNS, elem);
        assertEquals("yabba yabba", msg.getText());
    }
}
