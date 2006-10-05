package com.echomine.jabber.packet;

import java.io.StringReader;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.XMPPTestCase;
import com.echomine.xmpp.packet.IQPacket;

/**
 * Tests the packet for marshalling and unmarshalling properness.
 */
public class AuthIQPacketTest extends XMPPTestCase {
    /**
     * A bug was reported where a server was sending out of order xmls by
     * outputting the digest element before the password element.  This
     * should not be the case, but Feridian might as well workaround this
     * issue by being more relaxed about the schema checking.  This test
     * case will test to make sure that out of order xml is ok.
     */
    public void testOutOfOrderUnmarshall() throws Exception {
        String xml = "<iq id='frdn_0' xmlns='jabber:client' type='result'><query xmlns='jabber:iq:auth'><username>feridian</username><digest/><password/><resource/></query></iq>";
        StringReader reader = new StringReader(xml);
        AuthIQPacket msg = (AuthIQPacket) JiBXUtil.unmarshallObject(reader, IQPacket.class);
        assertEquals(IQPacket.TYPE_RESULT, msg.getType());
        assertEquals("feridian", msg.getUsername());
        assertEquals("", msg.getDigest());
        assertEquals("", msg.getPassword());
        assertEquals("", msg.getResource());
    }

    public void testMarshall() throws Exception {
        String xml = "<iq xmlns='jabber:client' type='set'><query xmlns='jabber:iq:auth'><username>feridian</username><password>blah</password></query></iq>";
        StringReader reader = new StringReader(xml);
        AuthIQPacket msg = new AuthIQPacket(IQPacket.TYPE_SET);
        msg.setUsername("feridian");
        msg.setPassword("blah");
        JiBXUtil.marshallIQPacket(writer, msg);
        compare(reader);
    }
    
    public void testMarshallQuery() throws Exception {
        String xml = "<iq xmlns='jabber:client' type='get'><query xmlns='jabber:iq:auth'><username>feridian</username></query></iq>";
        StringReader reader = new StringReader(xml);
        AuthIQPacket msg = new AuthIQPacket();
        msg.setUsername("feridian");
        JiBXUtil.marshallIQPacket(writer, msg);
        compare(reader);
    }
}
