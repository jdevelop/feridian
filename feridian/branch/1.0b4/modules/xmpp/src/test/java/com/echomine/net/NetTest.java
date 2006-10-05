package com.echomine.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

public class NetTest extends TestCase {
    public NetTest(String name) {
        super(name);
    }

    /**
     * Tests the connection model to see whether it is inputting and outputting
     * correctly
     */
    public void testConnectionModel() {
        try {
            ConnectionContext amodel = new ConnectionContext("127.0.0.1", 7000);
            ConnectionContext bmodel = new ConnectionContext("127.0.0.1", 7000);
            ConnectionContext cmodel = new ConnectionContext(7000);
            ConnectionContext dmodel = new ConnectionContext(7000);
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            ConnectionContext emodel = new ConnectionContext(addr, 7000);
            // check whether equals() works correctly
            assertEquals(amodel, bmodel);
            assertEquals(cmodel, dmodel);
            assertEquals(amodel, emodel);
        } catch (UnknownHostException ex) {
            fail("Unknown Host Exception: " + ex.getMessage());
        }
    }
}
