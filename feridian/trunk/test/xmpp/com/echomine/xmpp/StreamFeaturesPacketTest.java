package com.echomine.xmpp;

import java.io.StringReader;

import com.echomine.XMPPTestCase;

/**
 * Tests the stream features packet
 */
public class StreamFeaturesPacketTest extends XMPPTestCase implements XMPPConstants {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUnmarshallRequiredTLS() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>\n\t<required/></starttls></stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = (StreamFeaturesPacket) unmarshallObject(reader, StreamFeaturesPacket.class);
        assertTrue(packet.isTLSSupported());
        assertTrue(packet.isTLSRequired());
    }

    public void testUnmarshallOptionalTLS() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/></stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = (StreamFeaturesPacket) unmarshallObject(reader, StreamFeaturesPacket.class);
        assertTrue(packet.isTLSSupported());
        assertFalse(packet.isTLSRequired());
    }

    public void testUnmarshallNoTLS() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t</stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = (StreamFeaturesPacket) unmarshallObject(reader, StreamFeaturesPacket.class);
        assertFalse(packet.isTLSSupported());
        assertFalse(packet.isTLSRequired());
    }

    public void testMarshallRequireTLS() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>"
                + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>\n\t<required/></starttls></stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = new StreamFeaturesPacket();
        packet.setTLSSupported(true);
        packet.setTLSRequired(true);
        marshallObject(packet, StreamFeaturesPacket.class);
        compare(reader);
    }

    public void testMarshallOptionalTLS() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/></stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = new StreamFeaturesPacket();
        packet.setTLSSupported(true);
        packet.setTLSRequired(false);
        marshallObject(packet, StreamFeaturesPacket.class);
        compare(reader);
    }

    public void testMarshallNoTLS() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>" + "</stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = new StreamFeaturesPacket();
        packet.setTLSSupported(false);
        packet.setTLSRequired(false);
        marshallObject(packet, StreamFeaturesPacket.class);
        compare(reader);
    }

    public void testUnmarshallSessionAndBinding() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>"
                + "\n\t<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/>\n\t<session xmlns='urn:ietf:params:xml:ns:xmpp-session'/>\n\t</stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = (StreamFeaturesPacket) unmarshallObject(reader, StreamFeaturesPacket.class);
        assertTrue(packet.isBindingRequired());
        assertTrue(packet.isSessionRequired());
    }

    public void testMarshallSessionAndBinding() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>"
                + "\n\t<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/>\n\t<session xmlns='urn:ietf:params:xml:ns:xmpp-session'/>\n\t</stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = new StreamFeaturesPacket();
        packet.setBindingRequired(true);
        packet.setSessionRequired(true);
        marshallObject(packet, StreamFeaturesPacket.class);
        compare(reader);
    }
}
