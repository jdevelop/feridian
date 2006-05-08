package com.echomine.xmlrpc;

import junit.framework.TestCase;
import org.jdom.Element;
import sun.misc.BASE64Encoder;

/**
 * tests the base64 serializer
 */
public class Base64SerializerTest extends TestCase {
    private Base64Serializer serializer = new Base64Serializer();
    private byte[] byteData;
    private String encodedData;

    protected void setUp() throws Exception {
        byteData = "hello world".getBytes();
        BASE64Encoder encoder = new BASE64Encoder();
        encodedData = encoder.encode(byteData);
    }

    /**
     * Tests the serialization of the binary data
     */
    public void testBase64Serialization() {
        Element elem = serializer.serialize(byteData, null);
        assertEquals("base64", elem.getName());
        assertEquals(encodedData, elem.getText());
    }

    /** tests the deserialization of the string data */
    public void testBase64Derialization() {
        Element elem = new Element("base64").setText(encodedData);
        byte[] data = (byte[]) serializer.deserialize(elem);
        assertEquals(new String(byteData), new String(data));
    }
}
