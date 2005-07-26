package com.echomine.xmpp;

import com.echomine.xmpp.packet.StanzaErrorPacket;

public class StanzaPacketBaseTest extends XMPPTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Setting the error to null should reset both the error and the type to
     * null.
     */
    public void testSetErrorToNull() throws Exception {
        StanzaPacketBase packet = new StanzaPacketBase();
        StanzaErrorPacket errPacket = new StanzaErrorPacket();
        packet.setError(errPacket);
        assertEquals(errPacket, packet.getError());
        assertEquals(StanzaPacketBase.TYPE_ERROR, packet.getType());
        packet.setError(null);
        assertNull(packet.getError());
        assertNull(packet.getType());
    }
}
