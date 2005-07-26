package com.echomine.feridian;

import junit.framework.TestCase;

import com.echomine.xmpp.packet.IQResourceBindPacket;

/**
 * Tests the feridian configuration.
 */
public class FeridianConfigurationTest extends TestCase {
    public void testFeridianConfig() throws Exception {
        FeridianConfiguration config = FeridianConfiguration.getConfig();
        assertEquals(IQResourceBindPacket.class, config.getClassForIQUri("urn:ietf:params:xml:ns:xmpp-bind"));
    }

    public void testGetClassForURINPE() throws Exception {
        FeridianConfiguration config = FeridianConfiguration.getConfig();
        try {
            config.getClassForIQUri("test");
        } catch (NullPointerException ex) {
            fail("getClassForURI should not throw NPE when default config is used");
        }
    }
}
