package com.echomine.xmlrpc;

import junit.framework.TestCase;
import org.jdom.Element;

/**
 * tests the boolean serializer
 */
public class BooleanSerializerTest extends TestCase {
    private BooleanSerializer serializer = new BooleanSerializer();

    /**
     * Tests the serialization of the boolean data
     */
    public void testBooleanSerialization() {
        Element elem = serializer.serialize(Boolean.TRUE, null);
        assertEquals("boolean", elem.getName());
        assertEquals("1", elem.getText());
    }

    /** tests the deserialization of the string data */
    public void testBooleanDerialization() {
        Element elem = new Element("boolean").setText("0");
        Boolean data = (Boolean) serializer.deserialize(elem);
        assertEquals(false, data.booleanValue());
    }
}
