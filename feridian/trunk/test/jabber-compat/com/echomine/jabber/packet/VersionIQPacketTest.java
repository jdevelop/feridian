package com.echomine.jabber.packet;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.XMPPTestCase;
import com.echomine.xmpp.packet.IQPacket;

import java.io.StringReader;

/**
 * This tests the version IQ message to see if it gets marshalls and unmarshalls properly.
 */
public class VersionIQPacketTest extends XMPPTestCase {
    public void testUnmarshall() throws Exception {
        String xml = "<iq xmlns='jabber:client'><query xmlns='jabber:iq:version'><name>Exodus</name><version>1.0</version><os>Windows XP</os></query></iq>";
        StringReader reader = new StringReader(xml);
        VersionIQPacket msg = (VersionIQPacket) JiBXUtil.unmarshallObject(reader, IQPacket.class);
        assertEquals("Exodus", msg.getName());
        assertEquals("1.0", msg.getVersion());
        assertEquals("Windows XP", msg.getOS());
    }

    public void testUnmarshallWithNoOS() throws Exception {
        String xml = "<iq xmlns='jabber:client'><query xmlns='jabber:iq:version'><name>Exodus</name><version>1.0</version></query></iq>";
        StringReader reader = new StringReader(xml);
        VersionIQPacket msg = (VersionIQPacket) JiBXUtil.unmarshallObject(reader, IQPacket.class);
        assertEquals("Exodus", msg.getName());
        assertEquals("1.0", msg.getVersion());
        assertNull(msg.getOS());
    }

    public void testMarshall() throws Exception {
        String xml = "<iq xmlns='jabber:client'><query xmlns='jabber:iq:version'><name>Exodus</name><version>1.0</version><os>Windows XP</os></query></iq>";
        StringReader reader = new StringReader(xml);
        VersionIQPacket msg = new VersionIQPacket();
        msg.setName("Exodus");
        msg.setVersion("1.0");
        msg.setOS("Windows XP");
        JiBXUtil.marshallIQPacket(writer, msg);
        compare(reader);
    }

    public void testMarshallQuery() throws Exception {
        String xml = "<iq xmlns='jabber:client' type='get'><query xmlns='jabber:iq:version'/>";
        StringReader reader = new StringReader(xml);
        VersionIQPacket msg = new VersionIQPacket();
        JiBXUtil.marshallIQPacket(writer, msg);
        compare(reader);
    }
}
