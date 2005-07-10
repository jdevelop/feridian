package com.echomine.feridian;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.echomine.XMPPTestCase;
import com.echomine.xmpp.IQResourceBindPacket;

/**
 * Tests the feridian configuration.
 */
public class FeridianConfigurationTest extends XMPPTestCase {
    public void testFeridianConfig() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/echomine/feridian/feridian-test-config.xml");
        Reader rdr = new InputStreamReader(is);
        FeridianConfiguration config = FeridianConfiguration.getConfig(rdr);
        assertEquals(IQResourceBindPacket.class, config.getClassForURI("urn:ietf:params:xml:ns:xmpp-bind"));
    }
}
