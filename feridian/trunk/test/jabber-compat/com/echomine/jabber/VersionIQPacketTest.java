package com.echomine.jabber;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.jibx.extras.DocumentComparator;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

import junit.framework.TestCase;

/**
 * This tests the version IQ message to see if it gets marshalls
 * and unmarshalls properly.
 */
public class VersionIQPacketTest extends TestCase {
    public void testUnmarshall() throws Exception {
        String xml = "<query xmlns='jabber:iq:version'><name>Exodus</name><version>1.0</version><os>Windows XP</os></query>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(VersionIQPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        VersionIQPacket msg = (VersionIQPacket) ctx.unmarshalDocument(reader);
        assertEquals("Exodus", msg.getName());
        assertEquals("1.0", msg.getVersion());
        assertEquals("Windows XP", msg.getOS());
    }
    
    public void testUnmarshallWithNoOS() throws Exception {
        String xml = "<query xmlns='jabber:iq:version'><name>Exodus</name><version>1.0</version></query>";
        StringReader reader = new StringReader(xml);
        IBindingFactory bfact = BindingDirectory.getFactory(VersionIQPacket.class);
        IUnmarshallingContext ctx = bfact.createUnmarshallingContext();
        VersionIQPacket msg = (VersionIQPacket) ctx.unmarshalDocument(reader);
        assertEquals("Exodus", msg.getName());
        assertEquals("1.0", msg.getVersion());
        assertNull(msg.getOS());
    }

    public void testMarshall() throws Exception {
        String xml = "<query xmlns='jabber:iq:version'><name>Exodus</name><version>1.0</version><os>Windows XP</os></query>";
        	StringReader reader = new StringReader(xml);
        	VersionIQPacket msg = new VersionIQPacket();
        	msg.setName("Exodus");
        	msg.setVersion("1.0");
        	msg.setOS("Windows XP");
        	IBindingFactory bfact = BindingDirectory.getFactory(VersionIQPacket.class);
        	IMarshallingContext ctx = bfact.createMarshallingContext();
        	ByteArrayOutputStream bos = new ByteArrayOutputStream();
        	ctx.setOutput(bos, "UTF-8");
        	ctx.marshalDocument(msg);
        	InputStreamReader brdr = new InputStreamReader
            (new ByteArrayInputStream(bos.toByteArray()), "UTF-8");
        	DocumentComparator comp = new DocumentComparator(System.err);
        	assertTrue(comp.compare(reader, brdr));
    }
}
