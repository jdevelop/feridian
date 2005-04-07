package com.echomine.xmlrpc;

import com.echomine.jabber.JabberUtil;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the array serializer/deserializer.
 */
public class ArraySerializerTest extends BaseSerializerTestCase {
    private ArraySerializer serializer;

    protected void setUp() throws Exception {
        serializer = new ArraySerializer();
        serializer.setSerializerFactory(getSerializerFactory());
    }

    /**
     * Tests the serialization of the list object.
     */
    public void testArraySerialization() throws Exception {
        ArrayList list = new ArrayList();
        //should come out to be <array><data><value><i4>64</i4></value><value><string>hello</string></value></data></array>
        list.add(new Integer(64));
        list.add("hello");
        Element elem = serializer.serialize(list, null);
        assertEquals("array", elem.getName());
        List children = elem.getChild("data").getChildren();
        assertEquals("64", ((Element) children.get(0)).getChildText("int"));
        assertEquals("hello", ((Element) children.get(1)).getChildText("string"));
    }

    /**
     * tests the deserialization of the xml data.
     * @throws Exception
     */
    public void testArrayDeserialization() throws Exception {
        String xmlStr = "<array><data><value><i4>64</i4></value><value><string>hello</string></value></data></array>";
        //utilize the jabber util for now
        Element elem = JabberUtil.parseXmlStringToDOM(xmlStr);
        List list = (List) serializer.deserialize(elem);
        assertEquals(64, ((Integer) list.get(0)).intValue());
        assertEquals("hello", list.get(1));
    }
}
