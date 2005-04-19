package com.echomine.xmpp.stream;

import java.io.StringReader;

import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPTestCase;

/**
 * Tests the handshaking stream to make sure it's processing properly
 */
public class XMPPClientHandshakeStreamTest extends XMPPTestCase {
    XMPPClientHandshakeStream stream;

    protected void setUp() throws Exception {
        super.setUp();
        clientCtx.setUsername("romeo");
        clientCtx.setHost("example.com");
        stream = new XMPPClientHandshakeStream();
    }

    public void testHandshakeWithNoError() throws Exception {
        StringReader rdr = new StringReader("<stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' id='c2s_123' from='example.com' version='1.0'></stream:stream>");
        String outRes = "com/echomine/xmpp/data/XMPPEmptyStream.xml";
        runAndCompare(rdr, outRes, stream, false, false);
        assertEquals("c2s_123", connCtx.getSessionId());
        assertEquals("example.com", connCtx.getHost());
    }

    public void testHandshakeWithError() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPErrorStream.xml";
        try {
            //the processing should throw error indicating stream failure
            //due to an error
            run(inRes, stream);
            fail("The stream processing with error should throw an exception");
        } catch (XMPPException ex) {
            //assert that the error is properly parsed
            assertNotNull(ex.getErrorPacket());
            assertEquals(ErrorCode.S_UNSUPPORTED_VERSION, ex.getErrorCondition());
        }
    }

    public void testHandshakeWithFeatures() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPClientHandshakeStream_in1.xml";
        String outRes = "com/echomine/xmpp/data/XMPPEmptyStream.xml";
        runAndCompare(inRes, outRes, stream, false, false);
        assertTrue(connCtx.getTLSFeature().tlsSupported);
        assertTrue(connCtx.getTLSFeature().tlsRequired);
    }
}
