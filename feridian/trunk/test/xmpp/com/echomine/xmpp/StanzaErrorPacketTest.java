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
public class StanzaErrorPacketTest extends TestCase {

    protected void setUp() throws Exception {
    }

    /**
     * tests the stream containing all the packet data
     */
    public void testUnmarshallAllData() throws Exception {
        String xml = "<error type='continue' xmlns='jabber:client'>" + "\n\t<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>"
                + "\n\t<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>diagnostic</text>" + "\n\t<escape-your-data xmlns='application-ns'/></error>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(StanzaErrorPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        StanzaErrorPacket msg = (StanzaErrorPacket) ctx.unmarshalDocument(reader);
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertEquals("diagnostic", msg.getText());
        assertEquals("continue", msg.getErrorType());
        assertEquals("escape-your-data", msg.getApplicationCondition().getName());
    }

    public void testUnmarshallWithNoApplicationCondition() throws Exception {
        String xml = "<error type='continue' xmlns='jabber:client'>\n\t" + "<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>"
                + "\n\t<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>diagnostic</text>" + "</error>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(StanzaErrorPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        StanzaErrorPacket msg = (StanzaErrorPacket) ctx.unmarshalDocument(reader);
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertEquals("diagnostic", msg.getText());
        assertNull(msg.getApplicationCondition());
        assertEquals("continue", msg.getErrorType());
    }

    public void testUnmarshallConditionOnly() throws Exception {
        String xml = "<error type='continue' xmlns='jabber:client'>\n\t" + "<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" + "</error>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(StanzaErrorPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        StanzaErrorPacket msg = (StanzaErrorPacket) ctx.unmarshalDocument(reader);
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertNull(msg.getText());
        assertNull(msg.getApplicationCondition());
        assertEquals("continue", msg.getErrorType());
    }

    public void testUnmarshallNoText() throws Exception {
        String xml = "<error type='continue' xmlns='jabber:client'>\n\t" + "<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>"
                + "<escape-your-data xmlns='application-ns'/></error>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(StanzaErrorPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        StanzaErrorPacket msg = (StanzaErrorPacket) ctx.unmarshalDocument(reader);
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertNull(msg.getText());
        assertEquals("continue", msg.getErrorType());
        assertEquals("escape-your-data", msg.getApplicationCondition().getName());
    }

    public void testMarshallAllData() throws Exception {
        String xml = "<error xmlns='jabber:client' type='cancel'>\n\t" + "<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>"
                + "<text xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>diagnostic</text>" + "<escape-your-data xmlns='application-ns'/></error>";
        StringReader reader = new StringReader(xml);
        StanzaErrorPacket packet = new StanzaErrorPacket();
        packet.setCondition(ErrorCode.C_FORBIDDEN);
        packet.setApplicationCondition(new NSI("escape-your-data", "application-ns"));
        packet.setErrorType(StanzaErrorPacket.CANCEL);
        packet.setText("diagnostic");
        IBindingFactory bfact = BindingDirectory.getFactory(StanzaErrorPacket.class);
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
