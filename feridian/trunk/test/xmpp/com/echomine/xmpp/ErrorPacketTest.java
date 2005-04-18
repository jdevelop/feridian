package com.echomine.xmpp;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import junit.framework.TestCase;

import org.jibx.extras.DocumentComparator;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * This will test both stanza and stream error packets.
 */
public class ErrorPacketTest extends TestCase {

    protected void setUp() throws Exception {
    }

    /**
     * tests the stream containing all the packet data
     */
    public void testUnmarshallAllData() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "\n\t<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>diagnostic</text>" + "\n\t<escape-your-data xmlns='application-ns'/></stream:error>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(ErrorPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        ErrorPacket msg = (ErrorPacket) ctx.unmarshalDocument(reader);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertEquals("diagnostic", msg.getText());
        assertEquals("escape-your-data", msg.getApplicationCondition().getName());
    }

    public void testUnmarshallWithNoApplicationCondition() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>diagnostic</text>" + "</stream:error>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(ErrorPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        ErrorPacket msg = (ErrorPacket) ctx.unmarshalDocument(reader);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertEquals("diagnostic", msg.getText());
        assertNull(msg.getApplicationCondition());
    }

    public void testUnmarshallConditionOnly() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(ErrorPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        ErrorPacket msg = (ErrorPacket) ctx.unmarshalDocument(reader);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertNull(msg.getText());
        assertNull(msg.getApplicationCondition());
    }

    public void testUnmarshallNoText() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "<escape-your-data xmlns='application-ns'/></stream:error>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(ErrorPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        ErrorPacket msg = (ErrorPacket) ctx.unmarshalDocument(reader);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertNull(msg.getText());
        assertEquals("escape-your-data", msg.getApplicationCondition().getName());
    }

    public void testMarshallAllData() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "<text xmlns='urn:ietf:params:xml:ns:xmpp-streams'>diagnostic</text>" + "<escape-your-data xmlns='application-ns'/></stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket packet = new ErrorPacket();
        packet.setCondition(ErrorCode.S_XML_NOT_WELL_FORMED);
        packet.setApplicationCondition(new NSI("escape-your-data", "application-ns"));
        packet.setText("diagnostic");
        IBindingFactory bfact = BindingDirectory.getFactory(ErrorPacket.class);
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
