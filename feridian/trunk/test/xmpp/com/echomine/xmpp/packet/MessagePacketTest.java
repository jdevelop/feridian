package com.echomine.xmpp.packet;

import java.io.Reader;

import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.XMPPTestCase;

/**
 * Tests the message packet
 */
public class MessagePacketTest extends XMPPTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUnmarshallNormalPacket() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageNormal_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = (MessagePacket) unmarshallObject(rdr, MessagePacket.class);
        assertEquals("test subject", packet.getSubject());
        assertEquals("test body", packet.getBody());
        assertEquals("test-thread", packet.getThreadID());
        assertEquals("romeo@shakespeare.com", packet.getTo().toString());
        assertEquals("juliet@shakespeare.com", packet.getFrom().toString());
        assertEquals(MessagePacket.TYPE_CHAT, packet.getType());
    }

    public void testUnmarshallPacketWithNoSubject() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageBodyOnly_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = (MessagePacket) unmarshallObject(rdr, MessagePacket.class);
        assertNull(packet.getSubject());
        assertEquals("test body", packet.getBody());
        assertNull(packet.getThreadID());
        assertEquals("romeo@shakespeare.com", packet.getTo().toString());
        assertEquals("juliet@shakespeare.com", packet.getFrom().toString());
        assertEquals(MessagePacket.TYPE_CHAT, packet.getType());
    }

    public void testUnmarshallPacketWithError() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageWithError_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = (MessagePacket) unmarshallObject(rdr, MessagePacket.class);
        assertNotNull(packet.getError());
        assertEquals(ErrorCode.C_NOT_ALLOWED, packet.getError().getCondition());
        assertEquals(StanzaErrorPacket.AUTH, packet.getError().getErrorType());
    }

    public void testMarshallNormalPacket() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageNormal_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = new MessagePacket();
        packet.setSubject("test subject");
        packet.setBody("test body");
        packet.setThreadID("test-thread");
        packet.setTo(JID.parseJID("romeo@shakespeare.com"));
        packet.setFrom(JID.parseJID("juliet@shakespeare.com"));
        packet.setType(MessagePacket.TYPE_CHAT);
        packet.setId("id_0001");
        marshallObject(packet, MessagePacket.class);
        compare(rdr);
    }

    public void testMarshallPacketWithError() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageWithError_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = new MessagePacket();
        packet.setBody("test body");
        packet.setTo(JID.parseJID("romeo@shakespeare.com"));
        packet.setFrom(JID.parseJID("juliet@shakespeare.com"));
        packet.setType(MessagePacket.TYPE_CHAT);
        packet.setId("id_0001");
        StanzaErrorPacket error = new StanzaErrorPacket();
        error.setCondition(ErrorCode.C_NOT_ALLOWED);
        error.setErrorType(StanzaErrorPacket.AUTH);
        packet.setError(error);
        marshallObject(packet, MessagePacket.class);
        compare(rdr);
    }
}
