package com.echomine.jabber.compat.packet;

import java.io.Reader;
import java.io.StringReader;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.XMPPTestCase;

/**
 * Test for gateway interaction (JID escaping) packets
 */
public class GatewayIQPacketTest extends XMPPTestCase {

    private static final String DEFAULT_JID = "FooBar@aim.jabber.org";

    private static final String DEFAULT_PROMPT = "Screen Name";

    private static final String DEFAULT_DESCRIPTION = "Please enter the AOL Screen Name of the"
            + "person you would like to contact.";

    public void testMarshallGatewayIQPacket() throws Exception {
        // TODO implement marshalling tests
    }

    public void testUnmarshallGatewayIQPacket() throws Exception {
        Reader reader = new StringReader("<query xmlns='jabber:iq:gateway'/>");
        GatewayIQPacket gatewayIQPacket = (GatewayIQPacket) JiBXUtil
                .unmarshallObject(reader, GatewayIQPacket.class);
        assertNotNull(gatewayIQPacket);
        assertNull(gatewayIQPacket.getDesc());
        assertNull(gatewayIQPacket.getPrompt());
        assertNull(gatewayIQPacket.getJid());
        reader.close();
        reader = new StringReader("<query xmlns='jabber:iq:gateway'>"
                + "<desc>" + DEFAULT_DESCRIPTION + "</desc>" + "<prompt>"
                + DEFAULT_PROMPT + "</prompt>" + "</query>");
        gatewayIQPacket = (GatewayIQPacket) JiBXUtil.unmarshallObject(reader,
                GatewayIQPacket.class);
        assertNotNull(gatewayIQPacket);
        assertEquals(DEFAULT_PROMPT, gatewayIQPacket.getPrompt());
        assertEquals(DEFAULT_DESCRIPTION, gatewayIQPacket.getDesc());
        assertNull(gatewayIQPacket.getJid());
        reader.close();
        reader = new StringReader("<query xmlns='jabber:iq:gateway'>" + "<jid>"
                + DEFAULT_JID + "</jid>" + "</query>");
        gatewayIQPacket = (GatewayIQPacket) JiBXUtil.unmarshallObject(reader,
                GatewayIQPacket.class);
        assertNotNull(gatewayIQPacket);
        assertNull(gatewayIQPacket.getDesc());
        assertNull(gatewayIQPacket.getPrompt());
        assertEquals(JID.parseJID(DEFAULT_JID), gatewayIQPacket.getJid());
    }
}
