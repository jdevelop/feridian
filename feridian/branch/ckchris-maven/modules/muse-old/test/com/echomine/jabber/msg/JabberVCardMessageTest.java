package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.DefaultMessageParser;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberUtil;
import junit.framework.TestCase;
import org.jdom.Element;

/**
 * tests vcard message
 */
public class JabberVCardMessageTest extends TestCase {
    DefaultMessageParser parser = new DefaultMessageParser();

    protected void setUp() throws Exception {
    }

    public void testParserSupportsVCardMessage() {
        assertTrue(parser.supportsParsingFor("vCard", JabberCode.XMLNS_IQ_VCARD));
    }

    /**
     * Tests a bug where when the JID is null, null pointer
     * exception is thrown.  This should not be the case.
     */
    public void testJIDNullDoesNotThrowNPE() throws ParseException {
        JabberVCardMessage msg = new JabberVCardMessage();
        msg.setJID(null);
        //the following should not thrown a NPE
        msg.encode();
    }

    public void testParsingWhenJIDIsNull() throws Exception {
        JabberVCardMessage msg = new JabberVCardMessage();
        msg.setJID(null);
        String xml = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(xml);
        msg = new JabberVCardMessage();
        //this should parse fine.
        try {
            msg.parse(parser, elem);
        } catch (ParseException ex) {
            fail("Parsing of message with null JID should not throw exception");
        }
    }

    public void testExternalPhotoType() throws Exception {
        JabberVCardMessage msg = new JabberVCardMessage();
        msg.setPhoto("http://www.photo.com/");
        String xml = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(xml);
        msg = new JabberVCardMessage();
        msg.parse(parser, elem);
        assertEquals("http://www.photo.com/", msg.getPhoto());
        assertNull(msg.getPhotoType());
    }

    public void testBinaryPhotoType() throws Exception {
        JabberVCardMessage msg = new JabberVCardMessage();
        msg.setPhoto("BASE64DataWhichWeWillMock");
        msg.setPhotoType("image/jpeg");
        String xml = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(xml);
        msg = new JabberVCardMessage();
        msg.parse(parser, elem);
        assertEquals("BASE64DataWhichWeWillMock", msg.getPhoto());
        assertEquals("image/jpeg", msg.getPhotoType());
    }
}
