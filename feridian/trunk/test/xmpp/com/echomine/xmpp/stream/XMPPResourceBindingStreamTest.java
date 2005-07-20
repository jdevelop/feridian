package com.echomine.xmpp.stream;

import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;

/**
 * Tests the resource binding stream
 */
public class XMPPResourceBindingStreamTest extends BaseStreamTestCase {
    XMPPResourceBindingStream stream;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        stream = new XMPPResourceBindingStream();
        sessCtx.setHostName("example.com");
        sessCtx.setUsername("romeo");
        streamCtx.getFeatures().addFeature(XMPPConstants.NS_STREAM_BINDING, "bind", null);
    }

    public void testNoBindingIfFeatureNotSet() throws Exception {
        streamCtx.getFeatures().removeFeature(XMPPConstants.NS_STREAM_BINDING);
        stream.process(sessCtx, streamCtx);
        writer.flush();
        assertEquals("", os.toString());
    }

    public void testDynamicResourceBinding() throws Exception {
        String inRes = "com/echomine/xmpp/data/ResourceBinding_in.xml";
        String outRes = "com/echomine/xmpp/data/ResourceBinding_out.xml";
        runAndCompare(inRes, outRes, stream, false, false);
        assertEquals("someresource", sessCtx.getResource());
    }

    public void testStaticResourceBinding() throws Exception {
        sessCtx.setResource("someresource");
        String inRes = "com/echomine/xmpp/data/ResourceBinding_in.xml";
        String outRes = "com/echomine/xmpp/data/ResourceBinding_out2.xml";
        runAndCompare(inRes, outRes, stream, false, false);
    }

    public void testResourceBindingErrorResult() throws Exception {
        sessCtx.setResource("someresource");
        String inRes = "com/echomine/xmpp/data/ResourceBindingWithError_in.xml";
        String outRes = "com/echomine/xmpp/data/ResourceBinding_out2.xml";
        try {
            runAndCompare(inRes, outRes, stream, false, false);
            fail("An error should be thrown");
        } catch (XMPPException ex) {
            assertEquals(ErrorCode.C_BAD_REQUEST, ex.getErrorCondition());
        }
    }
}
