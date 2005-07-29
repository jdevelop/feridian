package com.echomine.xmpp.packet;

import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.XMPPTestCase;

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

    public void testUnmarshallPresenceWithChildren() throws Exception {
        String xml = "<presence xmlns='jabber:client'><show>xa</show><status>Lunch</status></presence>";
        StringReader rdr = new StringReader(xml);
        PresencePacket packet = (PresencePacket) JiBXUtil.unmarshallObject(rdr, PresencePacket.class);
        assertNull(packet.getType());
        assertEquals(PresencePacket.SHOW_XA, packet.getShow());
        assertEquals("Lunch", packet.getStatus());
        assertEquals(0, packet.getPriority());
    }

    public void testUnmarshallPresenceWithPriority() throws Exception {
        String xml = "<presence xmlns='jabber:client'><show>xa</show><status>Lunch</status><priority>20</priority></presence>";
        StringReader rdr = new StringReader(xml);
        PresencePacket packet = (PresencePacket) JiBXUtil.unmarshallObject(rdr, PresencePacket.class);
        assertNull(packet.getType());
        assertEquals(PresencePacket.SHOW_XA, packet.getShow());
        assertEquals(20, packet.getPriority());
        assertEquals("Lunch", packet.getStatus());
    }

    public void testUnmarshallWithUnknownStanzas() throws Exception {
        String inRes = "com/echomine/xmpp/data/PresenceWithUnknownStanzas_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        PresencePacket packet = (PresencePacket) JiBXUtil.unmarshallObject(rdr, PresencePacket.class);
        assertNull(packet.getType());
    }

    public void testMarshallInitialPresence() throws Exception {
        String xml = "<presence xmlns='jabber:client'/>";
        Reader rdr = new StringReader(xml);
        PresencePacket packet = new PresencePacket();
        JiBXUtil.marshallObject(writer, packet);
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
        JiBXUtil.marshallObject(writer, packet);
        compare(rdr);
    }

    public void testMarshallInternationalPacket() throws Exception {
        String inRes = "com/echomine/xmpp/data/PresenceInternational.xml";
        Reader rdr = getResourceAsReader(inRes);
        PresencePacket packet = new PresencePacket();
        packet.setLocale(Locale.CANADA);
        packet.setShow(PresencePacket.SHOW_AWAY);
        packet.setStatus("dinner");
        packet.setStatus("cenare", Locale.ITALY);
        JiBXUtil.marshallObject(writer, packet);
        compare(rdr);
    }
    
    public void testUnmarshallInternationalPacket() throws Exception {
        String inRes = "com/echomine/xmpp/data/PresenceInternational.xml";
        Reader rdr = getResourceAsReader(inRes);
        PresencePacket packet = (PresencePacket) JiBXUtil.unmarshallObject(rdr, PresencePacket.class);
        assertEquals(Locale.CANADA, packet.getLocale());
        assertEquals("away", packet.getShow());
        assertEquals("dinner", packet.getStatus());
        assertEquals("cenare", packet.getStatus(Locale.ITALY));
    }
}
