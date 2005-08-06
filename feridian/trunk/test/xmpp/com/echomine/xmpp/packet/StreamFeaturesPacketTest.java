package com.echomine.xmpp.packet;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPTestCase;

/**
 * Tests the stream features packet
 */
public class StreamFeaturesPacketTest extends XMPPTestCase implements XMPPConstants {
    public void testUnmarshallRequiredTLS() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>\n\t<required/></starttls></stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeatures packet = (StreamFeatures) JiBXUtil.unmarshallObject(reader, StreamFeatures.class);
        assertTrue(packet.isTLSSupported());
        assertTrue(packet.isTLSRequired());
    }

    public void testUnmarshallOptionalTLS() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/></stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeatures packet = (StreamFeatures) JiBXUtil.unmarshallObject(reader, StreamFeatures.class);
        assertTrue(packet.isTLSSupported());
        assertFalse(packet.isTLSRequired());
    }

    public void testUnmarshallNoTLS() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t</stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeatures packet = (StreamFeatures) JiBXUtil.unmarshallObject(reader, StreamFeatures.class);
        assertFalse(packet.isTLSSupported());
        assertFalse(packet.isTLSRequired());
    }

    public void testMarshallRequireTLS() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>"
                + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>\n\t<required/></starttls></stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeatures packet = new StreamFeatures();
        packet.setTLSRequired(true);
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
    }

    public void testMarshallOptionalTLS() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/></stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeatures packet = new StreamFeatures();
        packet.addFeature(NS_STREAM_TLS, "starttls", null);
        packet.setTLSRequired(false);
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
    }

    public void testMarshallNoTLS() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>" + "</stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeatures packet = new StreamFeatures();
        packet.setTLSRequired(false);
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
    }

    public void testUnmarshallSessionAndBinding() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>"
                + "\n\t<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/>\n\t<session xmlns='urn:ietf:params:xml:ns:xmpp-session'/>\n\t</stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeatures packet = (StreamFeatures) JiBXUtil.unmarshallObject(reader, StreamFeatures.class);
        assertTrue(packet.isBindingSupported());
        assertTrue(packet.isSessionSupported());
    }

    public void testMarshallSessionAndBinding() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>"
                + "\n\t<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/>\n\t<session xmlns='urn:ietf:params:xml:ns:xmpp-session'/>\n\t</stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeatures packet = new StreamFeatures();
        packet.addFeature(NS_STREAM_BINDING, "bind", null);
        packet.addFeature(NS_STREAM_SESSION, "session", null);
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
    }

    public void testUnmarshallAll() throws Exception {
        String inRes = "com/echomine/xmpp/data/StreamFeatures.xml";
        Reader reader = getResourceAsReader(inRes);
        StreamFeatures packet = (StreamFeatures) JiBXUtil.unmarshallObject(reader, StreamFeatures.class);
        assertTrue(packet.isTLSSupported());
        assertTrue(packet.isTLSRequired());
        assertTrue(packet.isSaslSupported());
        assertTrue(packet.isSaslMechanismSupported("DIGEST-MD5"));
        assertTrue(packet.isSaslMechanismSupported("PLAIN"));
        assertTrue(packet.isBindingSupported());
        assertTrue(packet.isSessionSupported());
        assertTrue(packet.isFeatureSupported(NS_STREAM_IQ_AUTH));
        assertTrue(packet.isFeatureSupported(NS_STREAM_IQ_REGISTER));
    }

    public void testMarshallAll() throws Exception {
        String inRes = "com/echomine/xmpp/data/StreamFeatures.xml";
        Reader reader = getResourceAsReader(inRes);
        StreamFeatures packet = new StreamFeatures();
        packet.setTLSRequired(true);
        ArrayList list = new ArrayList(5);
        list.add("DIGEST-MD5");
        list.add("PLAIN");
        packet.addFeature(NS_STREAM_SASL, "mechanisms", list);
        packet.addFeature(NS_STREAM_BINDING, "bind", null);
        packet.addFeature(NS_STREAM_SESSION, "session", null);
        packet.addFeature(NS_STREAM_IQ_AUTH, "iq-stream", null);
        packet.addFeature(NS_STREAM_IQ_REGISTER, "iq-register", null);
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
    }
}
