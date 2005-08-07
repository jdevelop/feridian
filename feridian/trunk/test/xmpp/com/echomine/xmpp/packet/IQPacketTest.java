package com.echomine.xmpp.packet;

import java.io.StringReader;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.XMPPTestCase;
import com.echomine.xmpp.packet.IQPacket;

/**
 * Base class containing convenience methods to test IQ packets
 */
public class IQPacketTest extends XMPPTestCase {
    public void testUnmarshallIQResultWithNoChild() throws Exception {
        String xml = "<iq xmlns='jabber:client' type='result' id='iq_001'/>";
        StringReader rdr = new StringReader(xml);
        IQPacket packet = (IQPacket) JiBXUtil.unmarshallObject(rdr, IQPacket.class);
        assertEquals(IQPacket.TYPE_RESULT, packet.getType());
        assertEquals(IQPacket.class, packet.getClass());
        assertEquals("iq_001", packet.getId());
    }
}
