package com.echomine.xmpp.packet;

import java.io.Reader;
import java.io.StringReader;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.XMPPTestCase;

/**
 * Tests the iq privacy packet and its associated functions
 */
public class PrivacyIQTest extends XMPPTestCase {
    public void testMarshallPrivacyListRequest() throws Exception {
        String xml = "<iq xmlns='jabber:client' type='get'><query xmlns='jabber:iq:privacy'/></iq>";
        StringReader rdr = new StringReader(xml);
        PrivacyIQPacket packet = new PrivacyIQPacket();
        JiBXUtil.marshallIQPacket(writer, packet);
        compare(rdr);
    }

    /**
     * When setting the default name to null after setting name to something
     * else, the marshalled xml must not include an empty "default" element.
     */
    public void testMarshallPrivacyListAfterSecondTimeSettingToNull() throws Exception {
        String xml = "<iq xmlns='jabber:client' type='get'><query xmlns='jabber:iq:privacy'/></iq>";
        StringReader rdr = new StringReader(xml);
        PrivacyIQPacket packet = new PrivacyIQPacket();
        packet.setDefaultName("test");
        packet.setDefaultName(null);
        packet.setActiveName("test");
        packet.setActiveName(null);
        JiBXUtil.marshallIQPacket(writer, packet);
        compare(rdr);
    }

    /**
     * Tests the setting for removing a default name
     */
    public void testMarshallDefaultNameRemove() throws Exception {
        String xml = "<iq xmlns='jabber:client' type='set'><query xmlns='jabber:iq:privacy'><default/></query></iq>";
        StringReader rdr = new StringReader(xml);
        PrivacyIQPacket packet = new PrivacyIQPacket(IQPacket.TYPE_SET);
        packet.setDefaultName("");
        assertNull(packet.getDefaultName());
        JiBXUtil.marshallIQPacket(writer, packet);
        compare(rdr);
    }

    /**
     * Tests the setting for removing an active name
     */
    public void testMarshallActiveNameRemove() throws Exception {
        String xml = "<iq xmlns='jabber:client' type='set'><query xmlns='jabber:iq:privacy'><active/></query></iq>";
        StringReader rdr = new StringReader(xml);
        PrivacyIQPacket packet = new PrivacyIQPacket(IQPacket.TYPE_SET);
        packet.setActiveName("");
        assertNull(packet.getDefaultName());
        JiBXUtil.marshallIQPacket(writer, packet);
        compare(rdr);
    }

    public void testMarshallListRequest() throws Exception {
        String xml = "<iq xmlns='jabber:client' type='get'><query xmlns='jabber:iq:privacy'><list name='public'/></query></iq>";
        StringReader rdr = new StringReader(xml);
        PrivacyIQPacket packet = new PrivacyIQPacket();
        packet.addPrivacyList(new PrivacyList("public"));
        assertNull(packet.getDefaultName());
        JiBXUtil.marshallIQPacket(writer, packet);
        compare(rdr);
    }

    public void testMarshallDenyIQRequest() throws Exception {
        String inRes = "com/echomine/xmpp/data/PrivacySetDenyIQ.xml";
        Reader rdr = getResourceAsReader(inRes);
        PrivacyIQPacket packet = new PrivacyIQPacket(IQPacket.TYPE_SET);
        PrivacyItem item = new PrivacyItem();
        item.setDenyIQ(true);
        item.setOrder(6);
        PrivacyList list = new PrivacyList("iq-global-example");
        list.addItem(item);
        packet.addPrivacyList(list);
        JiBXUtil.marshallIQPacket(writer, packet);
        compare(rdr);
    }

    public void testUnmarshallPrivacyListWithItemResult() throws Exception {
        String inRes = "com/echomine/xmpp/data/PrivacyListWithItemResult.xml";
        Reader rdr = getResourceAsReader(inRes);
        PrivacyIQPacket packet = (PrivacyIQPacket) JiBXUtil.unmarshallObject(rdr, IQPacket.class);
        assertEquals(IQPacket.TYPE_RESULT, packet.getType());
        assertNull(packet.getActiveName());
        assertNull(packet.getDefaultName());
        assertEquals(1, packet.getPrivacyLists().size());
        PrivacyList list = packet.getPrivacyList(0);
        assertEquals(2, list.getItems().size());
        PrivacyItem item = list.getItem(0);
        assertEquals(PrivacyItem.TYPE_JID, item.getType());
        assertEquals("tybalt@example.com", item.getValue());
        assertFalse(item.isAllow());
        assertEquals(1, item.getOrder());
        item = list.getItem(1);
        assertTrue(item.isAllow());
        assertNull(item.getType());
        assertNull(item.getValue());
        assertEquals(2, item.getOrder());
    }

    public void testUnmarshallPrivacyListResult() throws Exception {
        String inRes = "com/echomine/xmpp/data/PrivacyListResult.xml";
        Reader rdr = getResourceAsReader(inRes);
        PrivacyIQPacket packet = (PrivacyIQPacket) JiBXUtil.unmarshallObject(rdr, IQPacket.class);
        assertEquals(IQPacket.TYPE_RESULT, packet.getType());
        assertEquals("private", packet.getActiveName());
        assertEquals("public", packet.getDefaultName());
        assertEquals(3, packet.getPrivacyLists().size());
        assertEquals("public", ((PrivacyList) packet.getPrivacyLists().get(0)).getName());
        assertEquals("private", ((PrivacyList) packet.getPrivacyLists().get(1)).getName());
        assertEquals("special", ((PrivacyList) packet.getPrivacyLists().get(2)).getName());
    }

}
