package com.echomine.xmpp.packet;

import com.echomine.xmpp.XMPPTestCase;
import com.echomine.xmpp.packet.StanzaErrorPacket;
import com.echomine.xmpp.packet.StanzaPacketBase;

public class StanzaPacketBaseTest extends XMPPTestCase {
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
