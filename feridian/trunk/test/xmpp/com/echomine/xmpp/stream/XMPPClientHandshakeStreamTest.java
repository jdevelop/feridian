package com.echomine.xmpp.stream;

import java.io.StringReader;
import java.util.Locale;

import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPStanzaErrorException;

/**
 * Tests the handshaking stream to make sure it's processing properly
 */
public class XMPPClientHandshakeStreamTest extends BaseStreamTestCase {
    XMPPClientHandshakeStream stream;

    protected void setUp() throws Exception {
        super.setUp();
        sessCtx.setUsername("romeo");
        sessCtx.setHostName("example.com");
        stream = new XMPPClientHandshakeStream();
    }

    public void testHandshakeWithNoError() throws Exception {
        StringReader rdr = new StringReader("<stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' id='c2s_123' from='example.com' version='1.0'></stream:stream>");
        String outRes = "com/echomine/xmpp/data/XMPPEmptyStream.xml";
        run(rdr, stream);
        endOutgoingStreamHeader();
        compare(outRes);
        assertEquals("c2s_123", sessCtx.getStreamId());
        assertEquals("example.com", sessCtx.getHostName());
    }

    public void testInternationalHandshake() throws Exception {
        StringReader rdr = new StringReader("<stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' id='c2s_123' from='example.com' xml:lang='en-us' version='1.0'></stream:stream>");
        String outRes = "com/echomine/xmpp/data/XMPPEmptyStream.xml";
        run(rdr, stream);
        endOutgoingStreamHeader();
        compare(outRes);
        assertEquals("c2s_123", sessCtx.getStreamId());
        assertEquals("example.com", sessCtx.getHostName());
        assertEquals(Locale.US, sessCtx.getLocale());
    }

    public void testHandshakeWithError() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPErrorStream.xml";
        try {
            // the processing should throw error indicating stream failure
            // due to an error
            run(inRes, stream);
            fail("The stream processing with error should throw an exception");
        } catch (XMPPException ex) {
            assertTrue(ex instanceof XMPPStanzaErrorException);
            // assert that the error is properly parsed
            assertNotNull(((XMPPStanzaErrorException) ex).getErrorPacket());
            assertEquals(ErrorCode.S_UNSUPPORTED_VERSION, ((XMPPStanzaErrorException) ex).getErrorCondition());
        }
    }

    public void testHandshakeWithFeatures() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPClientHandshakeStream_in1.xml";
        String outRes = "com/echomine/xmpp/data/XMPPEmptyStream.xml";
        run(inRes, stream);
        endOutgoingStreamHeader();
        compare(outRes);
        assertTrue(streamCtx.getFeatures().isTLSSupported());
        assertTrue(streamCtx.getFeatures().isTLSRequired());
    }
}
