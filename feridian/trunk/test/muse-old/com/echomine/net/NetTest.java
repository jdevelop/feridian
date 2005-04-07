package com.echomine.net;

import junit.framework.TestCase;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetTest extends TestCase {
    public NetTest(String name) {
        super(name);
    }

    /**
     * Tests the connection model to see whether it is inputting and outputting correctly
     */
    public void testConnectionModel() {
        try {
            ConnectionModel amodel = new ConnectionModel("127.0.0.1", 7000);
            ConnectionModel bmodel = new ConnectionModel("127.0.0.1", 7000);
            ConnectionModel cmodel = new ConnectionModel(7000);
            ConnectionModel dmodel = new ConnectionModel(7000);
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            ConnectionModel emodel = new ConnectionModel(addr, 7000);
            //check whether equals() works correctly
            assertEquals(amodel, bmodel);
            assertEquals(cmodel, dmodel);
            assertEquals(amodel, emodel);
        } catch (UnknownHostException ex) {
            fail("Unknown Host Exception: " + ex.getMessage());
        }
    }
}
