package com.echomine.xmpp;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;

import org.jibx.extras.DocumentComparator;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

import com.echomine.BasicTestCase;


/**
 * Tests the message packet
 */
public class PresencePacketTest extends BasicTestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUnmarshallInitialPresencePacket() throws Exception {
        String xml = "<presence xmlns='jabber:client'/>";
        StringReader rdr = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(PresencePacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        PresencePacket packet = (PresencePacket) ctx.unmarshalDocument(rdr);
        assertNull(packet.getType());
        assertNull(packet.getTo());
        assertNull(packet.getFrom());
        assertNull(packet.getShow());
        assertNull(packet.getStatus());
        assertEquals(0, packet.getPriority());
    }
    
    public void testUnmarshallPresenceWithChildren() throws Exception {
        String xml = "<presence xmlns='jabber:client'><show>xa</show><status>Lunch</status></presence>";
        StringReader rdr = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(PresencePacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        PresencePacket packet = (PresencePacket) ctx.unmarshalDocument(rdr);
        assertNull(packet.getType());
        assertEquals(PresencePacket.SHOW_XA, packet.getShow());
        assertEquals("Lunch", packet.getStatus());
        assertEquals(0, packet.getPriority());
    }

    public void testUnmarshallPresenceWithPriority() throws Exception {
        String xml = "<presence xmlns='jabber:client'><show>xa</show><status>Lunch</status><priority>20</priority></presence>";
        StringReader rdr = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(PresencePacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        PresencePacket packet = (PresencePacket) ctx.unmarshalDocument(rdr);
        assertNull(packet.getType());
        assertEquals(PresencePacket.SHOW_XA, packet.getShow());
        assertEquals("Lunch", packet.getStatus());
        assertEquals(20, packet.getPriority());
    }

    public void testMarshallInitialPresence() throws Exception {
        String xml = "<presence xmlns='jabber:client'/>";
        Reader rdr = new StringReader(xml);
        PresencePacket packet = new PresencePacket();
        IBindingFactory bfact = BindingDirectory.getFactory(PresencePacket.class);
        IMarshallingContext ctx = bfact.createMarshallingContext();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ctx.setOutput(bos, "UTF-8");
        ctx.marshalDocument(packet);
        String str = bos.toString();
        StringReader brdr = new StringReader(str);
        DocumentComparator comp = new DocumentComparator(System.err);
        assertTrue("Invalid Output String: " + str, comp.compare(rdr, brdr));
    }
    
    public void testMarshallPacketWithError() throws Exception {
        String inRes = "com/echomine/xmpp/data/PresenceWithError_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        PresencePacket packet = new PresencePacket();
        StanzaErrorPacket error = new StanzaErrorPacket();
        error.setCondition(ErrorCode.C_NOT_ALLOWED);
        error.setErrorType(StanzaErrorPacket.AUTH);
        packet.setError(error);
        IBindingFactory bfact = BindingDirectory.getFactory(PresencePacket.class);
        IMarshallingContext ctx = bfact.createMarshallingContext();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ctx.setOutput(bos, "UTF-8");
        ctx.marshalDocument(packet);
        String str = bos.toString();
        StringReader brdr = new StringReader(str);
        DocumentComparator comp = new DocumentComparator(System.err);
        assertTrue("Invalid Output String: " + str, comp.compare(rdr, brdr));
    }
}
