package com.echomine.xmlrpc;

import junit.framework.TestCase;
import org.jdom.Element;

/**
 * tests the double serializer
 */
public class DoubleSerializerTest extends TestCase {
    private DoubleSerializer serializer = new DoubleSerializer();

    /**
     * Tests the serialization of the integer data
     */
    public void testDoubleSerialization() {
        Double data = new Double(-12.248f);
        Element elem = serializer.serialize(data, null);
        assertEquals("double", elem.getName());
        assertEquals(-12.248f, Double.parseDouble(elem.getText()), 0.0001);
    }

    /** tests the deserialization of the string data */
    public void testDoubleDerialization() {
        Element elem = new Element("double").setText("-12.248");
        Double data = (Double) serializer.deserialize(elem);
        assertEquals(-12.248f, data.doubleValue(), 0.0001);
    }
}
