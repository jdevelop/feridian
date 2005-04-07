package com.echomine.xmlrpc;

import junit.framework.TestCase;
import org.jdom.Element;

/**
 * tests the string serializer
 */
public class StringSerializerTest extends TestCase {
    private StringSerializer serializer = new StringSerializer();

    /**
     * Tests the serialization of the string data
     */
    public void testStringSerialization() {
        String data = "hello world";
        Element elem = serializer.serialize(data, null);
        assertEquals("string", elem.getName());
        assertEquals("hello world", elem.getText());
    }

    /** tests the deserialization of the string data */
    public void testStringDerialization() {
        Element elem = new Element("string").setText("hello world");
        String text = (String) serializer.deserialize(elem);
        assertEquals("hello world", text);
    }
}
