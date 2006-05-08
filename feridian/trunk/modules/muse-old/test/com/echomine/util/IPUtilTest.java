package com.echomine.util;

import junit.framework.TestCase;
import com.echomine.util.IPUtil;

public class IPUtilTest extends TestCase {
    public IPUtilTest(String name) {
        super(name);
    }

    /**
     * Tests the private IP conformance
     */
    public void testPrivateIP() throws Exception {
        boolean isprivate = IPUtil.isHostIPPrivate("127.0.0.1");
        assertTrue("127.0.0.1 should be private", isprivate == true);
        isprivate = IPUtil.isHostIPPrivate("10.1.3.5");
        assertTrue("10.0.0.0/8 should be private", isprivate == true);
        isprivate = IPUtil.isHostIPPrivate("192.168.1.1");
        assertTrue("192.168.0.0/16 should be private", isprivate == true);
        isprivate = IPUtil.isHostIPPrivate("0.0.0.0");
        assertTrue("0.0.0.0/32 should be private", isprivate == true);
    }

    /**
     * Tests the serialization of the IP
     */
    public void testSerializationDeserialization() throws Exception {
        byte[] buf = new byte[20];
        int offset = 0;
        String ip = "127.0.0.1";
        offset = IPUtil.serializeIP(ip, buf, 0);
        StringBuffer outbuf = new StringBuffer();
        IPUtil.deserializeIP(buf, 0, outbuf);
        String newip = outbuf.toString();
        assertEquals(ip, newip);
    }
}
