package com.echomine.xmpp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.jibx.extras.DocumentComparator;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;

/**
 * Tests the handshaking stream to make sure it's processing properly
 */
public class XMPPClientHandshakeStreamTest extends TestCase implements XMPPConstants {
    UnmarshallingContext uctx;
    XMPPConnectionContext ctx;
    XMPPStreamWriter writer;
    XMPPClientHandshakeStream stream;

    protected void setUp() throws Exception {
        ctx = new XMPPConnectionContext();
        ctx.setUsername("romeo");
        ctx.setHost("example.com");
        uctx = new UnmarshallingContext();
        writer = new XMPPStreamWriter(STREAM_URIS);
        stream = new XMPPClientHandshakeStream();
    }

    public void testHandshakeWithNoError() throws Exception {
        StringReader rdr = new StringReader("<stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' id='c2s_123' from='example.com' version='1.0'></stream:stream>");
        ByteArrayOutputStream os = new ByteArrayOutputStream(256);
        writer.setOutput(os);
        uctx.setDocument(rdr);
        //if processing is fine, no exception will be thrown
        stream.process(ctx, uctx, writer);
        writer.close();
        //check that the stream sent by the stream is proper.
        String str = os.toString("UTF-8");
        InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("com/echomine/xmpp/data/XMPPClientHandshakeStream_out1.xml"));
        InputStreamReader brdr = new InputStreamReader(new ByteArrayInputStream(os.toByteArray()), "UTF-8");
        DocumentComparator comp = new DocumentComparator(System.err);
        assertTrue("Invalid XML: " + str, comp.compare(reader, brdr));
        assertEquals("c2s_123", ctx.getSessionId());
        assertEquals("example.com", ctx.getHost());
    }

    public void testHandshakeWithError() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(256);
        writer.setOutput(os);
        uctx.setDocument(getClass().getClassLoader().getResourceAsStream("com/echomine/xmpp/data/XMPPClientHandshakeStream_in1.xml"), "UTF-8");
        try {
            //the processing should throw error indicating stream failure
            //due to an error
            stream.process(ctx, uctx, writer);
            fail("The stream processing with error should throw an exception");
        } catch (XMPPException ex) {
            //assert that the error is properly parsed
            assertNotNull(ex.getErrorPacket());
            assertEquals(ErrorCode.S_UNSUPPORTED_VERSION, ex.getErrorCondition());
        }
        writer.close();
    }
    
    public void testHandshakeWithFeatures() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream(256);
        writer.setOutput(os);
        uctx.setDocument(getClass().getClassLoader().getResourceAsStream("com/echomine/xmpp/data/XMPPClientHandshakeStream_in2.xml"), "UTF-8");
        stream.process(ctx, uctx, writer);
        writer.close();
        String str = os.toString("UTF-8");
        InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("com/echomine/xmpp/data/XMPPClientHandshakeStream_out1.xml"));
        StringReader brdr = new StringReader(str);
        DocumentComparator comp = new DocumentComparator(System.err);
        assertTrue("Invalid XML: " + str, comp.compare(reader, brdr));
        assertTrue(ctx.getTLSFeature().tlsSupported);
        assertTrue(ctx.getTLSFeature().tlsRequired);
    }
}
