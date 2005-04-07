package com.echomine.xmlrpc;

import junit.framework.TestCase;
import org.jdom.Element;

/**
 * tests the integer serializer
 */
public class IntSerializerTest extends TestCase {
    private IntSerializer i4ser = new IntSerializer("i4");
    private IntSerializer intser = new IntSerializer("int");

    /**
     * Tests the serialization of the integer data
     */
    public void testIntSerialization() {
        Integer data = new Integer(64);
        Element elem = i4ser.serialize(data, null);
        assertEquals("i4", elem.getName());
        assertEquals("64", elem.getText());
        elem = intser.serialize(data, null);
        assertEquals("int", elem.getName());
        assertEquals("64", elem.getText());
    }

    /** tests the deserialization of the string data */
    public void testIntDerialization() {
        Element elem = new Element("int").setText("128");
        Integer data = (Integer) intser.deserialize(elem);
        assertEquals(128, data.intValue());
        elem = new Element("i4").setText("128");
        data = (Integer) i4ser.deserialize(elem);
        assertEquals(128, data.intValue());
    }

    /** tests the getName() method to make sure it returns the proper string */
    public void testGetName() {
        assertEquals("i4", i4ser.getName());
        assertEquals("int", intser.getName());
    }
}
