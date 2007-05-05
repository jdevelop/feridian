package com.echomine.jabber.compat.auth;

import java.util.ArrayList;

import com.echomine.net.MockSocket;
import com.echomine.xmpp.BaseStreamTestCase;
import com.echomine.xmpp.XMPPAuthCallback;
import com.echomine.xmpp.XMPPConstants;

public class NonSASLAuthenticatorTest extends BaseStreamTestCase implements XMPPConstants {
    NonSASLAuthenticator auth;

    protected void setUp() throws Exception {
        super.setUp();
        auth = new NonSASLAuthenticator();
        sessCtx.setHostName("example.com");
        MockSocket socket = new MockSocket();
        streamCtx.setSocket(socket);
        XMPPAuthCallback authCallback = new XMPPAuthCallback();
        authCallback.setUsername("romeo");
        authCallback.setPassword("somepass".toCharArray());
        authCallback.setResource("Home");
        streamCtx.setAuthCallback(authCallback);
    }

    public void testNoAuthenticateWhenSaslSupported() throws Exception {
        ArrayList<String> mechanisms = new ArrayList<String>(2);
        mechanisms.add("PLAIN");
        mechanisms.add("DIGEST-MD5");
        streamCtx.getFeatures().addFeature(NS_STREAM_SASL, "mechanisms", mechanisms);
        assertFalse(auth.canAuthenticate(sessCtx, streamCtx));
    }
    
    public void testRedoHandshakeAlwaysFalse() throws Exception {
        assertFalse(auth.redoHandshake());
    }
    
    public void testSuccessfulPlainAuth() throws Exception {
        String inRes = "com/echomine/jabber/compat/data/NonSASLPlain_in.xml";
        String outRes = "com/echomine/jabber/compat/data/NonSASLPlain_out.xml";
        runAndCompare(inRes, outRes, auth, true, true);
    }

    public void testSuccessfulDigestAuth() throws Exception {
        String inRes = "com/echomine/jabber/compat/data/NonSASLDigest_in.xml";
        String outRes = "com/echomine/jabber/compat/data/NonSASLDigest_out.xml";
        streamCtx.getAuthCallback().setPassword("Calli0pe".toCharArray());
        sessCtx.setStreamId("3EE948B0");
        runAndCompare(inRes, outRes, auth, true, true);
    }
}
