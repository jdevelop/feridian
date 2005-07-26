package com.echomine.xmpp.stream;

import java.util.ArrayList;

import com.echomine.net.MockSocket;
import com.echomine.xmpp.XMPPAuthCallback;
import com.echomine.xmpp.XMPPConstants;

/**
 * This tests the TLS handshaking feature. It will not test the TLS negotiation,
 * which is assumed to be working since JSSE is used. However, it will tests the
 * XML handshaking.
 */
public class SASLHandshakeStreamTest extends BaseStreamTestCase implements XMPPConstants {
    SASLHandshakeStream stream;
    ArrayList mechanisms;

    protected void setUp() throws Exception {
        super.setUp();
        stream = new SASLHandshakeStream();
        sessCtx.setHostName("example.com");
        mechanisms = new ArrayList(2);
        mechanisms.add("PLAIN");
        mechanisms.add("DIGEST-MD5");
        streamCtx.getFeatures().addFeature(NS_STREAM_SASL, "mechanisms", mechanisms);
        MockSocket socket = new MockSocket();
        streamCtx.setSocket(socket);
        XMPPAuthCallback authCallback = new XMPPAuthCallback();
        authCallback.setUsername("romeo");
        authCallback.setPassword("somepass".toCharArray());
        authCallback.setResource("Home");
        streamCtx.setAuthCallback(authCallback);
    }

    public void testPLAINAuthentication() throws Exception {
        String inRes = "com/echomine/xmpp/data/SASLPlain_in.xml";
        String outRes = "com/echomine/xmpp/data/SASLPlain_out.xml";
        mechanisms.remove("DIGEST-MD5");
        runAndCompare(inRes, outRes, stream, true, true);
    }
}
