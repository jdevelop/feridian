package com.echomine.xmpp.stream;

import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPStanzaErrorException;

/**
 * Tests the session stream
 */
public class XMPPSessionStreamTest extends BaseStreamTestCase {
    XMPPSessionStream stream;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        stream = new XMPPSessionStream();
        streamCtx.getFeatures().addFeature(XMPPConstants.NS_STREAM_SESSION, "session", null);
    }

    public void testNoBindingIfFeatureNotSet() throws Exception {
        streamCtx.getFeatures().removeFeature(XMPPConstants.NS_STREAM_SESSION);
        stream.process(sessCtx, streamCtx);
        writer.flush();
        assertEquals("", os.toString());
    }

    public void testDynamicResourceBinding() throws Exception {
        String inRes = "com/echomine/xmpp/data/Session_in.xml";
        String outRes = "com/echomine/xmpp/data/Session_out.xml";
        runAndCompare(inRes, outRes, stream, false, false);
    }

    public void testResourceBindingErrorResult() throws Exception {
        String inRes = "com/echomine/xmpp/data/SessionWithError_in.xml";
        String outRes = "com/echomine/xmpp/data/Session_out.xml";
        try {
            runAndCompare(inRes, outRes, stream, false, false);
            fail("An error should be thrown");
        } catch (XMPPException ex) {
            assertTrue(ex instanceof XMPPStanzaErrorException);
            assertEquals(ErrorCode.C_FORBIDDEN, ((XMPPStanzaErrorException) ex).getErrorCondition());
        }
    }
}
