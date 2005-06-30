package com.echomine.muse;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.echomine.BasicTestCase;
import com.echomine.xmpp.IQResourceBindPacket;

/**
 * Tests the muse configuration.
 */
public class MuseConfigurationTest extends BasicTestCase {
    public void testMuseConfig() throws Exception {
        InputStream is = getClass().getResourceAsStream("/com/echomine/muse/muse-test-config.xml");
        Reader rdr = new InputStreamReader(is);
        MuseConfiguration config = MuseConfiguration.getConfig(rdr);
        assertEquals(IQResourceBindPacket.class, config.getClassForURI("urn:ietf:params:xml:ns:xmpp-bind"));
    }
}
