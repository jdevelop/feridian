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
public class MessagePacketTest extends BasicTestCase {

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

    public void testUnmarshallNormalPacket() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageNormal_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        IBindingFactory bfact = BindingDirectory.getFactory(MessagePacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        MessagePacket packet = (MessagePacket) ctx.unmarshalDocument(rdr);
        assertEquals("test subject", packet.getSubject());
        assertEquals("test body", packet.getBody());
        assertEquals("test-thread", packet.getThreadID());
        assertEquals("romeo@shakespeare.com", packet.getTo().toString());
        assertEquals("juliet@shakespeare.com", packet.getFrom().toString());
        assertEquals(MessagePacket.TYPE_CHAT, packet.getType());
    }
    
    public void testUnmarshallPacketWithNoSubject() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageBodyOnly_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        IBindingFactory bfact = BindingDirectory.getFactory(MessagePacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        MessagePacket packet = (MessagePacket) ctx.unmarshalDocument(rdr);
        assertNull(packet.getSubject());
        assertEquals("test body", packet.getBody());
        assertNull(packet.getThreadID());
        assertEquals("romeo@shakespeare.com", packet.getTo().toString());
        assertEquals("juliet@shakespeare.com", packet.getFrom().toString());
        assertEquals(MessagePacket.TYPE_CHAT, packet.getType());
    }
    
    public void testUnmarshallPacketWithError() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageWithError_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        IBindingFactory bfact = BindingDirectory.getFactory(MessagePacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        MessagePacket packet = (MessagePacket) ctx.unmarshalDocument(rdr);
        assertNotNull(packet.getError());
        assertEquals(ErrorCode.C_NOT_ALLOWED, packet.getError().getCondition());
        assertEquals(StanzaErrorPacket.AUTH, packet.getError().getErrorType());
    }
    
    public void testMarshallNormalPacket() throws Exception {
        String inRes = "com/echomine/xmpp/data/MessageNormal_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = new MessagePacket();
        packet.setSubject("test subject");
        packet.setBody("test body");
        packet.setThreadID("test-thread");
        packet.setTo(JID.parseJID("romeo@shakespeare.com"));
        packet.setFrom(JID.parseJID("juliet@shakespeare.com"));
        packet.setType(MessagePacket.TYPE_CHAT);
        packet.setId("id_0001");
        IBindingFactory bfact = BindingDirectory.getFactory(MessagePacket.class);
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
        String inRes = "com/echomine/xmpp/data/MessageWithError_in.xml";
        Reader rdr = getResourceAsReader(inRes);
        MessagePacket packet = new MessagePacket();
        packet.setBody("test body");
        packet.setTo(JID.parseJID("romeo@shakespeare.com"));
        packet.setFrom(JID.parseJID("juliet@shakespeare.com"));
        packet.setType(MessagePacket.TYPE_CHAT);
        packet.setId("id_0001");
        StanzaErrorPacket error = new StanzaErrorPacket();
        error.setCondition(ErrorCode.C_NOT_ALLOWED);
        error.setErrorType(StanzaErrorPacket.AUTH);
        packet.setError(error);
        IBindingFactory bfact = BindingDirectory.getFactory(MessagePacket.class);
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
