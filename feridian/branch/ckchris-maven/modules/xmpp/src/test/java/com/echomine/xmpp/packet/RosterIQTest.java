package com.echomine.xmpp.packet;

import java.io.Reader;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.XMPPTestCase;

/**
 * Tests the roster iq packet and roster item
 */
public class RosterIQTest extends XMPPTestCase {
    public void testMarshallRosterRequest() throws Exception {
        String inRes = "com/echomine/xmpp/data/RosterRequest.xml";
        Reader rdr = getResourceAsReader(inRes);
        RosterIQPacket packet = new RosterIQPacket();
        JiBXUtil.marshallIQPacket(writer, packet);
        compare(rdr);
    }

    public void testMarshallRosterRemove() throws Exception {
        String inRes = "com/echomine/xmpp/data/RosterRemove.xml";
        Reader rdr = getResourceAsReader(inRes);
        RosterIQPacket packet = new RosterIQPacket(IQPacket.TYPE_SET);
        packet.setFrom(JID.parseJID("juliet@example.com/balcony"));
        RosterItem item = new RosterItem();
        item.setJid(JID.parseJID("nurse@example.com"));
        item.setSubscription(RosterItem.SUBSCRIBE_REMOVE);
        packet.addItem(item);
        JiBXUtil.marshallIQPacket(writer, packet);
        compare(rdr);
    }

    public void testUnmarshallRosterNoneWithPending() throws Exception {
        String inRes = "com/echomine/xmpp/data/RosterNoneWithPending.xml";
        Reader rdr = getResourceAsReader(inRes);
        RosterIQPacket packet = (RosterIQPacket) JiBXUtil.unmarshallObject(rdr, IQPacket.class);
        assertEquals(IQPacket.TYPE_RESULT, packet.getType());
        assertNotNull(packet.getRosterItems());
        assertEquals(1, packet.getRosterItems().size());
        RosterItem item = (RosterItem) packet.getRosterItems().get(0);
        assertEquals("contact@example.org", item.getJid().toString());
        assertEquals(RosterItem.SUBSCRIBE_NONE, item.getSubscription());
        assertTrue(item.isPending());
        assertEquals("MyContact", item.getName());
        assertNotNull(item.getGroups());
        assertEquals(1, item.getGroups().size());
        assertEquals("MyBuddies", (String) item.getGroups().get(0));
    }

    public void testMarshallRosterResultNoChild() throws Exception {
        String inRes = "com/echomine/xmpp/data/RosterAdd.xml";
        Reader rdr = getResourceAsReader(inRes);
        RosterIQPacket packet = new RosterIQPacket(IQPacket.TYPE_SET);
        RosterItem item = new RosterItem();
        item.setJid(JID.parseJID("contact@example.org"));
        item.setName("MyContact");
        item.addGroup("MyBuddies");
        packet.addItem(item);
        JiBXUtil.marshallIQPacket(writer, packet);
        compare(rdr);
    }

    public void testUnmarshallRosterListResult() throws Exception {
        String inRes = "com/echomine/xmpp/data/RosterListResult.xml";
        Reader rdr = getResourceAsReader(inRes);
        RosterIQPacket packet = (RosterIQPacket) JiBXUtil.unmarshallObject(rdr, IQPacket.class);
        assertEquals(IQPacket.TYPE_RESULT, packet.getType());
        assertNotNull(packet.getRosterItems());
        assertEquals(2, packet.getRosterItems().size());
        RosterItem item = (RosterItem) packet.getRosterItems().get(0);
        assertEquals("romeo@example.org", item.getJid().toString());
        assertEquals(RosterItem.SUBSCRIBE_BOTH, item.getSubscription());
        assertNotNull(item.getGroups());
        assertEquals(1, item.getGroups().size());
        assertEquals("Friends", (String) item.getGroups().get(0));
        item = (RosterItem) packet.getRosterItems().get(1);
        assertEquals("mercutio@example.org", item.getJid().toString());
        assertEquals(RosterItem.SUBSCRIBE_FROM, item.getSubscription());
        assertNotNull(item.getGroups());
        assertEquals(1, item.getGroups().size());
        assertEquals("Friends", (String) item.getGroups().get(0));
    }
}
