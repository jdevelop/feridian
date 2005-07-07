package com.echomine.xmpp;

import java.io.Reader;

import com.echomine.XMPPTestCase;

/**
 * Tests the roster iq packet and roster item
 */
public class IQRosterTest extends XMPPTestCase {
    public void testRosterRequest() throws Exception {
        String inRes = "com/echomine/xmpp/data/RosterRequest.xml";
        Reader rdr = getResourceAsReader(inRes);
        IQRosterPacket packet = new IQRosterPacket();
        marshallObject(packet, IQRosterPacket.class);
        compare(rdr);
    }

    public void testRosterRemove() throws Exception {
        String inRes = "com/echomine/xmpp/data/RosterRemove.xml";
        Reader rdr = getResourceAsReader(inRes);
        IQRosterPacket packet = new IQRosterPacket();
        packet.setFrom(JID.parseJID("juliet@example.com/balcony"));
        RosterItem item = new RosterItem();
        item.setJid(JID.parseJID("nurse@example.com"));
        item.setSubscription(RosterItem.SUBSCRIBE_REMOVE);
        packet.addItem(item);
        marshallObject(packet, IQRosterPacket.class);
        compare(rdr);
    }

    public void testRosterNoneWithPending() throws Exception {
        String inRes = "com/echomine/xmpp/data/RosterNoneWithPending.xml";
        Reader rdr = getResourceAsReader(inRes);
        IQRosterPacket packet = (IQRosterPacket) unmarshallObject(rdr, IQRosterPacket.class);
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

    public void testRosterResultNoChild() throws Exception {
        String inRes = "com/echomine/xmpp/data/RosterAdd.xml";
        Reader rdr = getResourceAsReader(inRes);
        IQRosterPacket packet = new IQRosterPacket();
        RosterItem item = new RosterItem();
        item.setJid(JID.parseJID("contact@example.org"));
        item.setName("MyContact");
        item.addGroup("MyBuddies");
        packet.addItem(item);
        marshallObject(packet, IQRosterPacket.class);
        compare(rdr);
    }

    public void testRosterListResult() throws Exception {
        String inRes = "com/echomine/xmpp/data/RosterListResult.xml";
        Reader rdr = getResourceAsReader(inRes);
        IQRosterPacket packet = (IQRosterPacket) unmarshallObject(rdr, IQRosterPacket.class);
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
