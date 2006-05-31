package com.echomine.xmlrpc;

import junit.framework.TestCase;

import java.util.*;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Tests the serializer factory to make sure it is doing the proper things
 */
public class SerializerFactoryTest extends TestCase {
    private SerializerFactory factory;

    protected void setUp() throws Exception {
        factory = new SerializerFactory();
    }

    /**
     * makes sure that all the serializers are registered to properly
     * serialize data to an XMLRPC xml document.
     */
    public void testRegisteredSerializers() {
        assertTrue(factory.isSerializerRegisteredFor(Integer.class));
        assertTrue(factory.isSerializerRegisteredFor(Double.class));
        assertTrue(factory.isSerializerRegisteredFor(Boolean.class));
        assertTrue(factory.isSerializerRegisteredFor(Date.class));
        assertTrue(factory.isSerializerRegisteredFor(String.class));
        assertTrue(factory.isSerializerRegisteredFor(byte[].class));
        assertTrue(factory.isSerializerRegisteredFor(List.class));
        assertTrue(factory.isSerializerRegisteredFor(Map.class));
        assertTrue(factory.isSerializerRegisteredFor(ArrayList.class));
        assertTrue(factory.isSerializerRegisteredFor(HashMap.class));
    }

    /**
     * makes sure that all the deserializers are registered to properly
     * deserialize data from an XMLRPC xml document
     */
    public void testRegisteredDeserializers() {
        assertTrue(factory.isDeserializerRegisteredFor("int"));
        assertTrue(factory.isDeserializerRegisteredFor("i4"));
        assertTrue(factory.isDeserializerRegisteredFor("double"));
        assertTrue(factory.isDeserializerRegisteredFor("boolean"));
        assertTrue(factory.isDeserializerRegisteredFor("dateTime.iso8601"));
        assertTrue(factory.isDeserializerRegisteredFor("string"));
        assertTrue(factory.isDeserializerRegisteredFor("base64"));
        assertTrue(factory.isDeserializerRegisteredFor("array"));
        assertTrue(factory.isDeserializerRegisteredFor("struct"));
    }

    /**
     * tests that classes implementing the SerializerFactoryAware interface is obtaining
     * the factory properly.
     */
    public void testSerializerFactoryAwareSerializers() {
        TestSerializer serializer = new TestSerializer();
        factory.addSerializer(Object.class, serializer);
        assertTrue(serializer.serializerSet);
        serializer = new TestSerializer();
        factory.addDeserializer("object", serializer);
        assertTrue(serializer.serializerSet);
    }

    class TestSerializer implements Serializer, Deserializer, SerializerFactoryAware {
        boolean serializerSet = false;

        public Element serialize(Object data, Namespace ns) {
            return null;
        }

        public Object deserialize(Element elem) {
            return null;
        }

        public void setSerializerFactory(SerializerFactory factory) {
            serializerSet = true;
        }
    }
}
