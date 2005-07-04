package com.echomine.xmpp;

import java.io.StringReader;

import com.echomine.XMPPTestCase;
import com.echomine.jibx.JiBXUtil;

/**
 * Base class containing convenience methods to test IQ packets
 */
public class IQStanzaTest extends XMPPTestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIQResultWithNoChild() throws Exception {
        String xml = "<iq xmlns='jabber:client' type='result' id='iq_001'/>";
        StringReader rdr = new StringReader(xml);
        IQPacket packet = (IQPacket) JiBXUtil.unmarshallObject(rdr, IQPacket.class);
        assertEquals(IQPacket.TYPE_RESULT, packet.getType());
        assertEquals(IQPacket.class, packet.getClass());
        assertEquals("iq_001", packet.getId());
    }
}
