package com.echomine.xmpp.stream.sasl;

import junit.framework.TestCase;

public class DigestMD5SaslContextTest extends TestCase {
    DigestMD5SaslContext ctx;
    
    protected void setUp() throws Exception {
        ctx = new DigestMD5SaslContext();
    }

    public void testUnwrap() throws Exception {
        String challenge = "cmVhbG09InNvbWVyZWFsbSIsbm9uY2U9Ik9BNk1HOXRFUUdtMmhoIixxb3A9ImF1dGgiLGNoYXJzZXQ9dXRmLTgsYWxnb3JpdGhtPW1kNS1zZXNzCg==";
        ctx.unwrap(challenge);
        assertEquals("somerealm", ctx.getRealm());
        assertEquals("OA6MG9tEQGm2hh", ctx.getNonce());
        assertEquals("auth", ctx.getQop());
        assertEquals("utf-8", ctx.getCharset());
        assertEquals("md5-sess", ctx.getAlgorithm());
    }
}
