package com.echomine.xmpp.packet;

import java.io.Reader;
import java.io.StringReader;

import com.echomine.XMPPTestCase;
import com.echomine.xmpp.ErrorCode;

/**
 * Tests the message packet
 */
public class PresencePacketTest extends XMPPTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUnmarshallInitialPresencePacket() throws Exception {
        String xml = "<presence xmlns='jabber:client'/>";
        StringReader rdr = new StringReader(xml);
        PresencePacket packet = (PresencePacket) unmarshallObject(rdr, PresencePacket.class);
        assertNull(packet.getType());
        assertNull(packet.getTo());
        assertNull(packet.getFrom());
        assertNull(packet.getShow());
        assertNull(packet.getStatus());
        assertEquals(0, packet.getPriority());
    }

    public void testUnmarshallPresenceWithChildren() throws Exception {
        String xml = "<presence xmlns='jabber:client'><show>xa</show><status>Lunch</status></presence>";
        StringReader rdr = new StringReader(xml);
        PresencePacket packet = (PresencePacket) unmarshallObject(rdr, PresencePacket.class);
        assertNull(packet.getType());
        assertEquals(PresencePacket.SHOW_XA, packet.getShow());
        assertEquals("Lunch", packet.getStatus());
        assertEquals(0, packet.getPriority());
    }

    public void testUnmarshallPresenceWithPriority() throws Exception {
        String xml = "<presence xmlns='jabber:client'><show>xa</show><status>Lunch</status><priority>20</priority></presence>";
        StringReader rdr = new StringReader(xml);
        PresencePacket packet = (PresencePacket) unmarshallObject(rdr, PresencePacket.class);
        assertNull(packet.getType());
        assertEquals(PresencePacket.SHOW_XA, packet.getShow());
        assertEquals("Lunch", packet.getStatus());
        assertEquals(20, packet.getPriority());
    }

    public void testMarshallInitialPresence() throws Exception {
        String xml = "<presence xmlns='jabber:client'/>";
        Reader rdr = new StringReader(xml);
        PresencePacket packet = new PresencePacket();
        marshallObject(packet, PresencePacket.class);
        compare(rdr);
    }

    public void testMarshallPacketWithError() throws Exception {
        String inRes = "com/echomine/xmpp/data/PresenceWithError_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        PresencePacket packet = new PresencePacket();
        StanzaErrorPacket error = new StanzaErrorPacket();
        error.setCondition(ErrorCode.C_NOT_ALLOWED);
        error.setErrorType(StanzaErrorPacket.AUTH);
        packet.setError(error);
        marshallObject(packet, PresencePacket.class);
        compare(rdr);
    }
}
