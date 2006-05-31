package com.echomine.jabber.msg;

import com.echomine.jabber.*;
import junit.framework.TestCase;
import org.jdom.Element;

/**
 * tests the OOB message classes.
 */
public class OOBMessageTest extends TestCase {
    private DefaultMessageParser parser = new DefaultMessageParser();

    /**
     * Tests the incoming OOB IQ Message to make sure that the data are parsed out and can be retrieved correctly.
     * Also, this tests to make sure that the message is a request message.
     */
    public void testOOBIQIncomingMessage() throws Exception {
        String streamXML = "<iq type='set' from='abc@jabber.org/work' to='def@jabber.org/home' id='oob1'>" +
                "<query xmlns='jabber:iq:oob'><url>http://www.testing.com/test.zip</url>" +
                "<desc>Jabber Description</desc></query></iq>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        OOBIQMessage msg = new OOBIQMessage();
        msg.parse(parser, elem);
        //make sure the message defaults to a set type
        assertEquals(JabberIQMessage.TYPE_SET, msg.getType());
        assertEquals("http://www.testing.com/test.zip", msg.getUrl());
        assertEquals("Jabber Description", msg.getDescription());
    }

    /**
     * Tests that the incoming result message can be parsed properly.  Since it's just an IQ message, it's
     * really not that much.  It doesn't even contain an internal query element.
     */
    public void testOOBIQIncomingResultMessage() throws Exception {
        String streamXML = "<iq type='result' from='abc@jabber.org/home' to='def@jabber.org/work' id='oob1'/>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        OOBIQMessage msg = new OOBIQMessage();
        msg.parse(parser, elem);
        assertEquals(JabberIQMessage.TYPE_RESULT, msg.getType());
        assertEquals("abc@jabber.org/home", msg.getFrom().toString());
        assertEquals("def@jabber.org/work", msg.getTo().toString());
    }

    /**
     * Tests that the error message when file is not found is properly composed.
     */
    public void testOOBIQIncomingFileNotFoundErrorMessage() throws Exception {
        String streamXML = "<iq type='error' from='abc@jabber.org/home' to='def@jabber.org/work' id='oob1'>" +
                "<query xmlns='jabber:iq:oob'><url>http://www.testing.com/test.zip</url>" +
                "<desc>Jabber Description</desc></query><error code='404'>Not Found</error></iq>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        OOBIQMessage msg = new OOBIQMessage();
        msg.parse(parser, elem);
        assertEquals(JabberIQMessage.TYPE_ERROR, msg.getType());
        assertTrue(msg.isError());
        assertEquals("abc@jabber.org/home", msg.getFrom().toString());
        assertEquals("def@jabber.org/work", msg.getTo().toString());
        assertEquals("http://www.testing.com/test.zip", msg.getUrl());
        assertEquals("Jabber Description", msg.getDescription());
        ErrorMessage emsg = msg.getErrorMessage();
        assertEquals(404, emsg.getCode());
        assertEquals("Not Found", emsg.getMessage());
    }

    /**
     * Tests that the error message for rejecting files is properly composed
     */
    public void testOOBIQIncomingNotAcceptableErrorMessage() throws Exception {
        String streamXML = "<iq type='error' from='abc@jabber.org/home' to='def@jabber.org/work' id='oob1'>" +
                "<query xmlns='jabber:iq:oob'><url>http://www.testing.com/test.zip</url>" +
                "<desc>Jabber Description</desc></query><error code='406'>Not Acceptable</error></iq>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        OOBIQMessage msg = new OOBIQMessage();
        msg.parse(parser, elem);
        assertEquals(JabberIQMessage.TYPE_ERROR, msg.getType());
        assertTrue(msg.isError());
        assertEquals("abc@jabber.org/home", msg.getFrom().toString());
        assertEquals("def@jabber.org/work", msg.getTo().toString());
        assertEquals("http://www.testing.com/test.zip", msg.getUrl());
        assertEquals("Jabber Description", msg.getDescription());
        ErrorMessage emsg = msg.getErrorMessage();
        assertEquals(406, emsg.getCode());
        assertEquals("Not Acceptable", emsg.getMessage());
    }

    /**
     * Tests the convenience method creation methods to make sure that it is conforming to the
     * specs.
     */
    public void testOOBIQCreateSendUrlMessage() throws Exception {
        OOBIQMessage msg = OOBIQMessage.createSendUrlMessage(new JID("def@jabber.org/work"), "http://www.testing.com/test.zip", "Jabber Description");
        //make sure the message created conforms to the specs
        assertEquals(JabberIQMessage.TYPE_SET, msg.getType());
        assertEquals("def@jabber.org/work", msg.getTo().toString());
        assertEquals("http://www.testing.com/test.zip", msg.getUrl());
        assertEquals("Jabber Description", msg.getDescription());
        assertTrue(msg.isReplyRequired());
    }

    /**
     * Tests the convenience method that creates a success message to be sent to the remote user
     * is conforming to the specs.
     */
    public void testOOBIQCreateSuccessMessage() throws Exception {
        OOBIQMessage msg = OOBIQMessage.createSuccessMessage(new JID("def@jabber.org/work"), "oob1");
        assertEquals(JabberIQMessage.TYPE_RESULT, msg.getType());
        assertEquals("def@jabber.org/work", msg.getTo().toString());
        assertEquals("oob1", msg.getMessageID());
    }

    /** tests that the error message conforms to the specs */
    public void testOOBIQCreateNotFoundErrorMessage() throws Exception {
        OOBIQMessage msg = OOBIQMessage.createNotFoundErrorMessage(new JID("def@jabber.org/work"), "oob1");
        assertEquals(JabberIQMessage.TYPE_ERROR, msg.getType());
        assertEquals("def@jabber.org/work", msg.getTo().toString());
        assertEquals("oob1", msg.getMessageID());
        assertTrue(msg.isError());
        ErrorMessage emsg = msg.getErrorMessage();
        assertEquals(404, emsg.getCode());
        assertEquals("Not Found", emsg.getMessage());
    }

    /** tests that the error message conforms to the specs */
    public void testOOBIQCreateNotAcceptableErrorMessage() throws Exception {
        OOBIQMessage msg = OOBIQMessage.createNotAcceptableErrorMessage(new JID("def@jabber.org/work"), "oob1");
        assertEquals(JabberIQMessage.TYPE_ERROR, msg.getType());
        assertEquals("def@jabber.org/work", msg.getTo().toString());
        assertEquals("oob1", msg.getMessageID());
        assertTrue(msg.isError());
        ErrorMessage emsg = msg.getErrorMessage();
        assertEquals(406, emsg.getCode());
        assertEquals("Not Acceptable", emsg.getMessage());
    }

    /**
     * tests whether the encoding of the message class is working properly
     */
    public void testOOBIQMessageEncode() throws Exception {
        OOBIQMessage msg = OOBIQMessage.createSendUrlMessage(new JID("def@jabber.org/work"), "http://www.testing.com/test.zip", "Jabber Description");
        String xml = msg.encode();
        //now reparse it
        Element elem = JabberUtil.parseXmlStringToDOM(xml);
        msg = new OOBIQMessage();
        msg.parse(parser, elem);
        assertEquals("def@jabber.org/work", msg.getTo().toString());
        assertEquals("http://www.testing.com/test.zip", msg.getUrl());
        assertEquals("Jabber Description", msg.getDescription());
    }

    /**
     * Tests the jabber:x:oob message to make sure it is parsing the incoming data properly
     */
    public void testOOBXIncomingMessage() throws Exception {
        String streamXML = "<x xmlns='jabber:x:oob'><url>http://www.testing.com/test.zip</url>" +
                "<desc>Jabber Description</desc></x>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        OOBXMessage msg = new OOBXMessage();
        msg.parse(parser, elem);
        assertEquals("http://www.testing.com/test.zip", msg.getUrl());
        assertEquals("Jabber Description", msg.getDescription());
    }

    /**
     * tests whether the encoding of the message class is working properly
     */
    public void testOOBXMessageEncode() throws Exception {
        OOBXMessage msg = new OOBXMessage();
        msg.setUrl("http://www.testing.com/test.zip");
        msg.setDescription("Jabber Description");
        String xml = msg.encode();
        //now reparse it
        Element elem = JabberUtil.parseXmlStringToDOM(xml);
        msg = new OOBXMessage();
        msg.parse(parser, elem);
        assertEquals("http://www.testing.com/test.zip", msg.getUrl());
        assertEquals("Jabber Description", msg.getDescription());
    }

    /**
     * this tests that the parser has the message registered to parse the namespace
     */
    public void testParserSupportsMessage() throws Exception {
        assertTrue(parser.supportsParsingFor("query", JabberCode.XMLNS_IQ_OOB));
        assertTrue(parser.supportsParsingFor("x", JabberCode.XMLNS_X_OOB));
    }

    /**
     * Test to make sure that the message type is set properly
     */
    public void testMessageType() throws Exception {
        OOBIQMessage msg = new OOBIQMessage();
        assertEquals(JabberCode.MSG_IQ_OOB, msg.getMessageType());
        OOBXMessage xmsg = new OOBXMessage();
        assertEquals(JabberCode.MSG_X_OOB, xmsg.getMessageType());
    }
}
