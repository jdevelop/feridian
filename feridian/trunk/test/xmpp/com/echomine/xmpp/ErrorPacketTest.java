package com.echomine.xmpp;

import java.io.StringReader;

import com.echomine.XMPPTestCase;

/**
 * This will test both stanza and stream error packets.
 */
public class ErrorPacketTest extends XMPPTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * tests the stream containing all the packet data
     */
    public void testUnmarshallAllData() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "\n\t<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>diagnostic</text>" + "\n\t<escape-your-data xmlns='application-ns'/></stream:error>";
        StringReader reader = new StringReader(xml);        
        ErrorPacket msg = (ErrorPacket) unmarshallObject(reader, ErrorPacket.class);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertEquals("diagnostic", msg.getText());
        assertEquals("escape-your-data", msg.getApplicationCondition().getName());
    }

    public void testUnmarshallWithNoApplicationCondition() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>diagnostic</text>" + "</stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket msg = (ErrorPacket) unmarshallObject(reader, ErrorPacket.class);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertEquals("diagnostic", msg.getText());
        assertNull(msg.getApplicationCondition());
    }

    public void testUnmarshallConditionOnly() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket msg = (ErrorPacket) unmarshallObject(reader, ErrorPacket.class);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertNull(msg.getText());
        assertNull(msg.getApplicationCondition());
    }

    public void testUnmarshallNoText() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "<escape-your-data xmlns='application-ns'/></stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket msg = (ErrorPacket) unmarshallObject(reader, ErrorPacket.class);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertNull(msg.getText());
        assertEquals("escape-your-data", msg.getApplicationCondition().getName());
    }

    public void testMarshallAllData() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "<text xmlns='urn:ietf:params:xml:ns:xmpp-streams'>diagnostic</text>" + "<escape-your-data xmlns='application-ns'/></stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket packet = new ErrorPacket();
        packet.setCondition(ErrorCode.S_XML_NOT_WELL_FORMED);
        packet.setApplicationCondition(new NSI("escape-your-data", "application-ns"));
        packet.setText("diagnostic");
        marshallObject(packet, ErrorPacket.class);
        compare(reader);
    }
}
