package com.echomine.xmpp.packet;

import java.io.StringReader;

import com.echomine.XMPPTestCase;
import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.NSI;

/**
 * This will test both stanza and stream error packets.
 */
public class StanzaErrorPacketTest extends XMPPTestCase {

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
        String xml = "<error type='continue' xmlns='jabber:client'>" + "\n\t<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>"
                + "\n\t<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>diagnostic</text>" + "\n\t<escape-your-data xmlns='application-ns'/></error>";
        StringReader reader = new StringReader(xml);
        StanzaErrorPacket msg = (StanzaErrorPacket) unmarshallObject(reader, StanzaErrorPacket.class);
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertEquals("diagnostic", msg.getText());
        assertEquals("continue", msg.getErrorType());
        assertEquals("escape-your-data", msg.getApplicationCondition().getName());
    }

    public void testUnmarshallWithNoApplicationCondition() throws Exception {
        String xml = "<error type='continue' xmlns='jabber:client'>\n\t" + "<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>"
                + "\n\t<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>diagnostic</text>" + "</error>";
        StringReader reader = new StringReader(xml);
        StanzaErrorPacket msg = (StanzaErrorPacket) unmarshallObject(reader, StanzaErrorPacket.class);
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertEquals("diagnostic", msg.getText());
        assertNull(msg.getApplicationCondition());
        assertEquals("continue", msg.getErrorType());
    }

    public void testUnmarshallConditionOnly() throws Exception {
        String xml = "<error type='continue' xmlns='jabber:client'>\n\t" + "<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" + "</error>";
        StringReader reader = new StringReader(xml);
        StanzaErrorPacket msg = (StanzaErrorPacket) unmarshallObject(reader, StanzaErrorPacket.class);
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertNull(msg.getText());
        assertNull(msg.getApplicationCondition());
        assertEquals("continue", msg.getErrorType());
    }

    public void testUnmarshallNoText() throws Exception {
        String xml = "<error type='continue' xmlns='jabber:client'>\n\t" + "<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" + "<escape-your-data xmlns='application-ns'/></error>";
        StringReader reader = new StringReader(xml);
        StanzaErrorPacket msg = (StanzaErrorPacket) unmarshallObject(reader, StanzaErrorPacket.class);
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertNull(msg.getText());
        assertEquals("continue", msg.getErrorType());
        assertEquals("escape-your-data", msg.getApplicationCondition().getName());
    }

    public void testMarshallAllData() throws Exception {
        String xml = "<error xmlns='jabber:client' type='cancel'>\n\t" + "<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>"
                + "<text xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>diagnostic</text>" + "<escape-your-data xmlns='application-ns'/></error>";
        StringReader reader = new StringReader(xml);
        StanzaErrorPacket packet = new StanzaErrorPacket();
        packet.setCondition(ErrorCode.C_FORBIDDEN);
        packet.setApplicationCondition(new NSI("escape-your-data", "application-ns"));
        packet.setErrorType(StanzaErrorPacket.CANCEL);
        packet.setText("diagnostic");
        marshallObject(packet, StanzaErrorPacket.class);
        compare(reader);
    }
}
