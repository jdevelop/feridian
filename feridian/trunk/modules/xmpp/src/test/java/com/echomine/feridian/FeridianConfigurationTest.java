package com.echomine.feridian;

import junit.framework.TestCase;

import com.echomine.xmpp.packet.ResourceBindIQPacket;

/**
 * Tests the feridian configuration.
 */
public class FeridianConfigurationTest extends TestCase {
    public void testFeridianConfig() throws Exception {
        FeridianConfiguration config = FeridianConfiguration.getConfig();
        assertEquals(ResourceBindIQPacket.class, config.getClassForUri("urn:ietf:params:xml:ns:xmpp-bind"));
    }

    public void testGetClassForURINPE() throws Exception {
        FeridianConfiguration config = FeridianConfiguration.getConfig();
        try {
            config.getClassForUri("test");
        } catch (NullPointerException ex) {
            fail("getClassForURI should not throw NPE when default config is used");
        }
    }
}
