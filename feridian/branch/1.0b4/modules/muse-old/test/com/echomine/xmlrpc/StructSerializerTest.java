package com.echomine.xmlrpc;

import com.echomine.jabber.JabberUtil;
import org.jdom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Tests the structs serializer/deserializer.
 */
public class StructSerializerTest extends BaseSerializerTestCase {
    StructSerializer serializer;

    protected void setUp() throws Exception {
        serializer = new StructSerializer();
        serializer.setSerializerFactory(getSerializerFactory());
    }

    /**
     * Tests the serialization of the map object.
     */
    public void testStructSerialization() throws Exception {
        TreeMap map = new TreeMap();
        //should come out to be <struct><member><name>name1</name><value><string>value1</string></value></member>
        //<member><name>name2</name><value><string>value2</string></value></member></struct>
        map.put("name1", "value1");
        map.put("name2", "value2");
        Element elem = serializer.serialize(map, null);
        assertEquals("struct", elem.getName());
        List children = elem.getChildren();
        assertEquals("member", ((Element) children.get(0)).getName());
        assertEquals("member", ((Element) children.get(1)).getName());
        assertEquals("name1", ((Element) children.get(0)).getChildText("name"));
        assertEquals("name2", ((Element) children.get(1)).getChildText("name"));
        assertEquals("value1", ((Element) children.get(0)).getChild("value").getChildText("string"));
        assertEquals("value2", ((Element) children.get(1)).getChild("value").getChildText("string"));
    }

    /**
     * tests the deserialization of the xml data.
     * @throws Exception
     */
    public void testStructDeserialization() throws Exception {
        String xmlStr = "<struct><member><name>name1</name><value><string>value1</string></value></member>" +
                "<member><name>name2</name><value><string>value2</string></value></member></struct>";
        //utilize the jabber util for now
        Element elem = JabberUtil.parseXmlStringToDOM(xmlStr);
        HashMap map = (HashMap) serializer.deserialize(elem);
        assertEquals("value1", map.get("name1"));
        assertEquals("value2", map.get("name2"));
    }
}
