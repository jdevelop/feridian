package com.echomine.xmpp.packet;

import java.io.Reader;
import java.util.Locale;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPTestCase;

/**
 * Tests the message packet
 */
public class MessagePacketTest extends XMPPTestCase {

    public void testUnmarshallNormalPacket() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageNormal_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = (MessagePacket) JiBXUtil.unmarshallObject(rdr, MessagePacket.class);
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
        MessagePacket packet = (MessagePacket) JiBXUtil.unmarshallObject(rdr, MessagePacket.class);
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
        MessagePacket packet = (MessagePacket) JiBXUtil.unmarshallObject(rdr, MessagePacket.class);
        assertNotNull(packet.getError());
        assertEquals(ErrorCode.C_NOT_ALLOWED, packet.getError().getCondition());
        assertEquals(StanzaErrorPacket.AUTH, packet.getError().getErrorType());
    }

    public void testUnmarshallWithUnknownStanzas() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageWithUnknown_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = (MessagePacket) JiBXUtil.unmarshallObject(rdr, PresencePacket.class);
        assertEquals("chat", packet.getType());
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
        JiBXUtil.marshallObject(writer, packet);
        compare(rdr);
    }

    public void testMarshallWithExtensions() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageWithExtensions.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = new MessagePacket();
        packet.setSubject("test subject");
        packet.setBody("test body");
        packet.setThreadID("test-thread");
        packet.setTo(JID.parseJID("romeo@shakespeare.com"));
        packet.setFrom(JID.parseJID("juliet@shakespeare.com"));
        packet.setType(MessagePacket.TYPE_CHAT);
        packet.setId("id_0001");
        RosterIQPacket ext = new RosterIQPacket();
        RosterItem item = new RosterItem();
        item.setJid(JID.parseJID("contact@example.org"));
        item.setName("MyContact");
        item.addGroup("MyBuddies");
        ext.addItem(item);
        packet.addExtension(XMPPConstants.NS_IQ_ROSTER, ext);
        JiBXUtil.marshallObject(writer, packet);
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
        JiBXUtil.marshallObject(writer, packet);
        compare(rdr);
    }

    public void testMarshallInternationalPacket() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageInternational.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = new MessagePacket();
        packet.setLocale(Locale.CANADA);
        packet.setSubject("hello!");
        packet.setSubject("ciao!", Locale.ITALIAN);
        packet.setBody("good day!");
        packet.setBody("buon giorno!", Locale.ITALIAN);
        packet.setThreadID("test-thread");
        packet.setTo(JID.parseJID("romeo@shakespeare.com"));
        packet.setFrom(JID.parseJID("juliet@shakespeare.com"));
        packet.setType(MessagePacket.TYPE_CHAT);
        packet.setId("id_0001");
        JiBXUtil.marshallObject(writer, packet);
        compare(rdr);
    }

    public void testUnmarshallInternationalPacket() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageInternational.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = (MessagePacket) JiBXUtil.unmarshallObject(rdr, MessagePacket.class);
        assertEquals(Locale.CANADA, packet.getLocale());
        assertEquals("hello!", packet.getSubject());
        assertEquals("ciao!", packet.getSubject(Locale.ITALIAN));
        assertEquals("good day!", packet.getBody());
        assertEquals("buon giorno!", packet.getBody(Locale.ITALIAN));
        assertEquals("test-thread", packet.getThreadID());
        assertEquals(MessagePacket.TYPE_CHAT, packet.getType());
        assertEquals("id_0001", packet.getId());
    }
}
