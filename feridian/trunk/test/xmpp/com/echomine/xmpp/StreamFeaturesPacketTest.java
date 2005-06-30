package com.echomine.xmpp;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import junit.framework.TestCase;

import org.jibx.extras.DocumentComparator;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.stream.XMPPClientHandshakeStream;
import com.echomine.xmpp.stream.XMPPConnectionContext;

/**
 * Tests the stream features packet
 */
public class StreamFeaturesPacketTest extends TestCase implements XMPPConstants {
    UnmarshallingContext uctx;
    XMPPConnectionContext ctx;
    XMPPStreamWriter writer;
    XMPPClientHandshakeStream stream;

    protected void setUp() throws Exception {
        ctx = new XMPPConnectionContext();
        uctx = new UnmarshallingContext();
        writer = new XMPPStreamWriter(STREAM_URIS);
        stream = new XMPPClientHandshakeStream();
    }

    public void testUnmarshallRequiredTLS() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>\n\t<required/></starttls></stream:features>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(StreamFeaturesPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        StreamFeaturesPacket packet = (StreamFeaturesPacket) ctx.unmarshalDocument(reader);
        assertTrue(packet.isTLSSupported());
        assertTrue(packet.isTLSRequired());
    }

    public void testUnmarshallOptionalTLS() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/></stream:features>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(StreamFeaturesPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        StreamFeaturesPacket packet = (StreamFeaturesPacket) ctx.unmarshalDocument(reader);
        assertTrue(packet.isTLSSupported());
        assertFalse(packet.isTLSRequired());
    }

    public void testUnmarshallNoTLS() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t</stream:features>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(StreamFeaturesPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        StreamFeaturesPacket packet = (StreamFeaturesPacket) ctx.unmarshalDocument(reader);
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
        IBindingFactory bfact = BindingDirectory.getFactory(StreamFeaturesPacket.class);
        IMarshallingContext ctx = bfact.createMarshallingContext();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ctx.setOutput(bos, "UTF-8");
        ctx.marshalDocument(packet);
        String str = bos.toString();
        StringReader brdr = new StringReader(str);
        DocumentComparator comp = new DocumentComparator(System.err);
        assertTrue("Invalid Output String: " + str, comp.compare(reader, brdr));
    }

    public void testMarshallOptionalTLS() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/></stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = new StreamFeaturesPacket();
        packet.setTLSSupported(true);
        packet.setTLSRequired(false);
        IBindingFactory bfact = BindingDirectory.getFactory(StreamFeaturesPacket.class);
        IMarshallingContext ctx = bfact.createMarshallingContext();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ctx.setOutput(bos, "UTF-8");
        ctx.marshalDocument(packet);
        String str = bos.toString();
        StringReader brdr = new StringReader(str);
        DocumentComparator comp = new DocumentComparator(System.err);
        assertTrue("Invalid Output String: " + str, comp.compare(reader, brdr));
    }

    public void testMarshallNoTLS() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>" + "</stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = new StreamFeaturesPacket();
        packet.setTLSSupported(false);
        packet.setTLSRequired(false);
        IBindingFactory bfact = BindingDirectory.getFactory(StreamFeaturesPacket.class);
        IMarshallingContext ctx = bfact.createMarshallingContext();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ctx.setOutput(bos, "UTF-8");
        ctx.marshalDocument(packet);
        String str = bos.toString();
        StringReader brdr = new StringReader(str);
        DocumentComparator comp = new DocumentComparator(System.err);
        assertTrue("Invalid Output String: " + str, comp.compare(reader, brdr));
    }

    public void testUnmarshallSessionAndBinding() throws Exception {
        String xml = "<stream:features xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/>\n\t<session xmlns='urn:ietf:params:xml:ns:xmpp-session'/>\n\t</stream:features>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(StreamFeaturesPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        StreamFeaturesPacket packet = (StreamFeaturesPacket) ctx.unmarshalDocument(reader);
        assertTrue(packet.isBindingRequired());
        assertTrue(packet.isSessionRequired());
    }
    
    public void testMarshallSessionAndBinding() throws Exception {
        String xml = "<stream:features xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/>\n\t<session xmlns='urn:ietf:params:xml:ns:xmpp-session'/>\n\t</stream:features>";
        StringReader reader = new StringReader(xml);
        StreamFeaturesPacket packet = new StreamFeaturesPacket();
        packet.setBindingRequired(true);
        packet.setSessionRequired(true);
        IBindingFactory bfact = BindingDirectory.getFactory(StreamFeaturesPacket.class);
        IMarshallingContext ctx = bfact.createMarshallingContext();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ctx.setOutput(bos, "UTF-8");
        ctx.marshalDocument(packet);
        String str = bos.toString();
        StringReader brdr = new StringReader(str);
        DocumentComparator comp = new DocumentComparator(System.err);
        assertTrue("Invalid Output String: " + str, comp.compare(reader, brdr));
    }
}
