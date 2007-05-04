package com.echomine.jabber.packet;

import java.io.Reader;
import java.io.StringReader;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.XMPPTestCase;
import com.echomine.xmpp.packet.IQPacket;

/**
 * Tests unmarchalling of packets for JEP-0030 discovery services 
 * (currently only 2 namespaces are available)
 */
public class DiscoveryQueryIQPacketTest extends XMPPTestCase {

    public void testUnmarshallDiscoveryInfoPackets() throws Exception {
        Reader reader = new StringReader("<query xmlns='"
                + DiscoveryInfoIQPacket.NAMESPACE + "'/>");
        DiscoveryInfoIQPacket discovery = (DiscoveryInfoIQPacket) JiBXUtil
                .unmarshallObject(reader, DiscoveryInfoIQPacket.class);
        assertNotNull(discovery);
        assertNull(discovery.getFeatures());
        assertNull(discovery.getIdentities());
        reader.close();
        reader = new StringReader(
                "<query xmlns='"
                        + DiscoveryInfoIQPacket.NAMESPACE
                        + "'>"
                        + "<identity category='conference' type='text' name='Play-Specific Chatrooms'/>"
                        + "<identity category='directory' type='chatroom' name='Play-Specific Chatrooms'/>"
                        + "<feature var='http://jabber.org/protocol/disco#info'/>"
                        + "<feature var='http://jabber.org/protocol/disco#items'/>"
                        + "<feature var='http://jabber.org/protocol/muc'/>"
                        + "<feature var='jabber:iq:register'/>"
                        + "<feature var='jabber:iq:search'/>"
                        + "<feature var='jabber:iq:time'/>"
                        + "<feature var='jabber:iq:version'/>" + "</query>");
        discovery = (DiscoveryInfoIQPacket) JiBXUtil.unmarshallObject(reader,
                DiscoveryInfoIQPacket.class);
        assertNotNull(discovery);
        assertNotNull(discovery.getFeatures());
        assertEquals(7, discovery.getFeatures().size());
        assertNotNull(discovery.getIdentities());
        assertEquals(2, discovery.getIdentities().size());
        reader.close();
        reader = new StringReader(
                "<iq xmlns='jabber:client' type='result' ><query xmlns='"
                        + DiscoveryInfoIQPacket.NAMESPACE
                        + "'>"
                        + "<identity category='conference' type='text' name='Play-Specific Chatrooms'/>"
                        + "<identity category='directory' type='chatroom' name='Play-Specific Chatrooms'/>"
                        + "<feature var='http://jabber.org/protocol/disco#info'/>"
                        + "<feature var='http://jabber.org/protocol/disco#items'/>"
                        + "<feature var='http://jabber.org/protocol/muc'/>"
                        + "<feature var='jabber:iq:register'/>"
                        + "<feature var='jabber:iq:search'/>"
                        + "<feature var='jabber:iq:time'/>"
                        + "<feature var='jabber:iq:version'/>"
                        + "</query></iq>");
        discovery = (DiscoveryInfoIQPacket) JiBXUtil.unmarshallObject(reader,
                IQPacket.class);
        assertNotNull(discovery);
        assertNotNull(discovery.getFeatures());
        assertEquals(7, discovery.getFeatures().size());
        assertNotNull(discovery.getIdentities());
        assertEquals(2, discovery.getIdentities().size());
        reader.close();
    }

    public void testMarshallDiscoveryInfoIQPacket() throws Exception {
        DiscoveryInfoIQPacket discovery = new DiscoveryInfoIQPacket();
        writer.flush();
        JiBXUtil.marshallIQPacket(writer, discovery);
        compare(new StringReader("<iq xmlns='jabber:client' type='get'><query xmlns='"
                + DiscoveryInfoIQPacket.NAMESPACE + "' /></iq>"));
    }

    public void testUnmarshallDiscovertyItemsPacket() throws Exception {
        Reader reader = new StringReader("<query xmlns='"
                + DiscoveryItemIQPacket.NAMESPACE + "'/>");
        DiscoveryItemIQPacket discovery = (DiscoveryItemIQPacket) JiBXUtil
                .unmarshallObject(reader, DiscoveryItemIQPacket.class);
        assertNotNull(discovery);
        assertNull(discovery.getItems());
        reader.close();
        reader = new StringReader("<query xmlns='"
                + DiscoveryItemIQPacket.NAMESPACE + "'>"
                + "<item jid='people.shakespeare.lit'"
                + "name='Directory of Characters'/>"
                + "<item jid='plays.shakespeare.lit'"
                + "name='Play-Specific Chatrooms'/>"
                + "<item jid='mim.shakespeare.lit'"
                + "name='Gateway to Marlowe IM'/>"
                + "<item jid='words.shakespeare.lit'"
                + "name='Shakespearean Lexicon'/>"
                + "<item jid='globe.shakespeare.lit'"
                + "name='Calendar of Performances'/>"
                + "<item jid='headlines.shakespeare.lit'"
                + "name='Latest Shakespearean News'/>"
                + "<item jid='catalog.shakespeare.lit'"
                + "name='Buy Shakespeare Stuff!'/>"
                + "<item jid='en2fr.shakespeare.lit'"
                + "name='French Translation Service'/>" + "</query>");
        discovery = (DiscoveryItemIQPacket) JiBXUtil.unmarshallObject(reader,
                DiscoveryItemIQPacket.class);
        assertNotNull(discovery);
        assertNotNull(discovery.getItems());
        assertEquals(8, discovery.getItems().size());
        reader.close();
    }
}
