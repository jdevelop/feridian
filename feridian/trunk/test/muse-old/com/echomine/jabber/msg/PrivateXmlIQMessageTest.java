package com.echomine.jabber.msg;

import com.echomine.jabber.DefaultMessageParser;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberUtil;
import junit.framework.TestCase;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Tests the jabber:iq:private message object.  It checks to make sure that the object conforms to the specs
 */
public class PrivateXmlIQMessageTest extends TestCase {
    DefaultMessageParser parser = new DefaultMessageParser();

    /**
     * Tests that the parsing of the message is done properly and it allows us to retrieve the
     * private namespace that we desire
     * @throws java.lang.Exception
     */
    public void testRetrievePrivateData() throws Exception {
        String streamXML = "<iq xmlns='jabber:client' to='abc@abc.com'><query xmlns='jabber:iq:private'><prefs xmlns='my:prefs'><test/></prefs></query></iq>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        PrivateXmlIQMessage msg = new PrivateXmlIQMessage();
        msg.parse(parser, elem);
        assertNotNull(msg);
        //retrieve the private message namespace
        Element prefs = msg.getPrivateData("prefs", "my:prefs");
        assertNotNull(prefs);
        assertNotNull(prefs.getChild("test", Namespace.getNamespace("my:prefs")));
    }

    /**
     * Tests that setting the private data works the way it should.  Note that only one private data may be set
     * and there should be no other private data namespaces.
     * @throws java.lang.Exception
     */
    public void testSetPrivateDataAsDOM() throws Exception {
        String streamXML = "<prefs xmlns=\"my:prefs\"><test/></prefs>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        PrivateXmlIQMessage msg = new PrivateXmlIQMessage();
        msg.setPrivateData(elem);
        Element data = msg.getPrivateData("prefs", "my:prefs");
        assertNotNull(data);
        assertEquals("prefs", data.getName());
        assertEquals("my:prefs", data.getNamespace().getURI());
    }

    /**
     * Tests that setting the private data works the way it should.  Note that only one private data may be set
     * and there should be no other private data namespaces.
     * @throws java.lang.Exception
     */
    public void testSetPrivateDataAsString() throws Exception {
        String streamXML = "<prefs xmlns=\"my:prefs\"><test/></prefs>";
        PrivateXmlIQMessage msg = new PrivateXmlIQMessage();
        msg.setPrivateData(streamXML);
        String encodeStr = msg.encode();
        assertTrue(encodeStr.indexOf("<prefs xmlns=\"my:prefs\">") > 0);
        Element data = msg.getPrivateData("prefs", "my:prefs");
        assertNotNull(data);
        assertEquals("prefs", data.getName());
        assertEquals("my:prefs", data.getNamespace().getURI());
    }

    /**
     * this tests that the parser has the message registered to parse the namespace
     */
    public void testParserSupportsMessage() throws Exception {
        assertTrue(parser.supportsParsingFor("query", JabberCode.XMLNS_IQ_PRIVATE));
    }

    /**
     * Test to make sure that the message type is set properly
     * @throws java.lang.Exception
     */
    public void testMessageType() throws Exception {
        PrivateXmlIQMessage msg = new PrivateXmlIQMessage();
        assertEquals(JabberCode.MSG_IQ_PRIVATE, msg.getMessageType());
    }
}
