package com.echomine.xmpp.stream;

import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPTestCase;

/**
 * Tests the resource binding stream
 */
public class XMPPResourceBindingStreamTest extends XMPPTestCase {
    XMPPResourceBindingStream stream;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        stream = new XMPPResourceBindingStream();
        clientCtx.setHost("example.com");
        clientCtx.setUsername("romeo");
        connCtx.setResourceBindingRequired(true);
    }

    public void testNoBindingIfFeatureNotSet() throws Exception {
        connCtx.setResourceBindingRequired(false);
        stream.process(clientCtx, connCtx, uctx, writer);
        writer.flush();
        assertEquals("", os.toString());
    }

    public void testDynamicResourceBinding() throws Exception {
        String inRes = "com/echomine/xmpp/data/ResourceBinding_in.xml";
        String outRes = "com/echomine/xmpp/data/ResourceBinding_out.xml";
        runAndCompare(inRes, outRes, stream, false, false);
        assertEquals("someresource", clientCtx.getResource());
    }

    public void testStaticResourceBinding() throws Exception {
        clientCtx.setResource("someresource");
        String inRes = "com/echomine/xmpp/data/ResourceBinding_in.xml";
        String outRes = "com/echomine/xmpp/data/ResourceBinding_out2.xml";
        runAndCompare(inRes, outRes, stream, false, false);
    }

    public void testResourceBindingErrorResult() throws Exception {
        clientCtx.setResource("someresource");
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
