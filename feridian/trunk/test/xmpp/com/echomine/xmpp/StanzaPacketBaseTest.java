package com.echomine.xmpp;

import junit.framework.TestCase;


public class StanzaPacketBaseTest extends TestCase {
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
