package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;

import java.util.*;

/**
 * The main factory that will check to look up what serializer/deserializer to use to
 * work with the current object.
 */
public class SerializerFactory {
    private HashMap serializers;
    private HashMap deserializers;

    /** @return the Element object of the serialized data, null if there were any problems */
    public Element serialize(Object object, Namespace ns) {
        if (serializers == null) init();
        Serializer serializer;

        if (object instanceof List) //check for List subclasses
            serializer = (Serializer) serializers.get(List.class);
        else if (object instanceof Map) //check for Map subclasses
            serializer = (Serializer) serializers.get(Map.class);
        else
            serializer = (Serializer) serializers.get(object.getClass()); //take in the name and check if there is a handler for it
        if (serializer == null) return null;
        return serializer.serialize(object, ns);
    }

    /** @return the deserialized object or null if there were no deserializer for the element type */
    public Object deserialize(Element elem) {
        if (deserializers == null) init();
        //take in the name and check if there is a handler for it
        Deserializer serializer = (Deserializer) deserializers.get(elem.getName());
        if (serializer == null) return null;
        return serializer.deserialize(elem);
    }

    /**
     * sets the timezone to serialize the date/time to.  Whatever date object you pass in, no matter what time zone
     * it is in, will have the time converted to the timezone that you set here.  Note that once you set the time
     * zone, all subsequent requests will work with the new timezone.  If that is not your intention, then you should
     * set the timezone back afterwards.
     * @param tz the timezone to use
     */
    public void setTimeZone(TimeZone tz) {
        if (serializers == null) init();
        DateSerializer serializer = (DateSerializer) serializers.get(Date.class);
        if (serializer != null)
            serializer.setTimeZone(tz);
    }

    /** checks to see if there is a serializer registered for the given class type */
    public boolean isSerializerRegisteredFor(Class classType) {
        if (serializers == null) init();
        if (List.class.isAssignableFrom(classType))
            classType = List.class;
        else if (Map.class.isAssignableFrom(classType))
            classType = Map.class;
        return serializers.containsKey(classType);
    }

    /** checks to see if there is a deserializer registered for the given name type */
    public boolean isDeserializerRegisteredFor(String name) {
        if (deserializers == null) init();
        return deserializers.containsKey(name);
    }

    /**
     * registers a serializer for a particular class type.  If class type already has a serializer, then it will
     * be replaced.
     * @param classType the Class type to serialize for
     * @param serializer the serializer that will do the work
     */
    public void addSerializer(Class classType, Serializer serializer) {
        if (serializers == null) init();
        if (serializer instanceof SerializerFactoryAware)
            ((SerializerFactoryAware) serializer).setSerializerFactory(this);
        serializers.put(classType, serializer);
    }

    /**
     * registers a deserializer for the particular name type.  If the name type already has a deserializer, then
     * it will be replaced.
     * @param name the name (ie. int, i4, base64) of the type to deserialize for
     * @param deserializer the deserializer
     */
    public void addDeserializer(String name, Deserializer deserializer) {
        if (serializers == null) init();
        if (deserializer instanceof SerializerFactoryAware)
            ((SerializerFactoryAware) deserializer).setSerializerFactory(this);
        deserializers.put(name, deserializer);
    }

    protected void init() {
        serializers = new HashMap();
        deserializers = new HashMap();
        initStruct();
        initArray();
        initInt();
        initBoolean();
        initBase64();
        initDate();
        initDouble();
        initString();
    }

    /** initializes the serializer/deserializer for the given type */
    protected void initString() {
        StringSerializer serializer;
        serializer = new StringSerializer();
        addSerializer(String.class, serializer);
        addDeserializer(StringSerializer.NAME, serializer);
    }

    /** initializes the serializer/deserializer for the given type */
    protected void initDouble() {
        DoubleSerializer serializer;
        serializer = new DoubleSerializer();
        addSerializer(Double.class, serializer);
        addDeserializer(DoubleSerializer.NAME, serializer);
    }

    /** initializes the serializer/deserializer for the given type */
    protected void initDate() {
        DateSerializer serializer;
        serializer = new DateSerializer();
        addSerializer(Date.class, serializer);
        addDeserializer(DateSerializer.NAME, serializer);
    }

    /** initializes the serializer/deserializer for the given type */
    protected void initBase64() {
        Base64Serializer serializer;
        serializer = new Base64Serializer();
        addSerializer(byte[].class, serializer);
        addDeserializer(Base64Serializer.NAME, serializer);
    }

    /** initializes the serializer/deserializer for the given type */
    protected void initBoolean() {
        BooleanSerializer serializer;
        serializer = new BooleanSerializer();
        addSerializer(Boolean.class, serializer);
        addDeserializer(BooleanSerializer.NAME, serializer);
    }

    /** initializes the serializer/deserializer for the given type */
    protected void initInt() {
        IntSerializer serializer;
        serializer = new IntSerializer("int");
        addSerializer(Integer.class, serializer);
        addDeserializer(serializer.getName(), serializer);
        //add a special int serializer here for the <i4> tag
        serializer = new IntSerializer("i4");
        addDeserializer(serializer.getName(), serializer);
    }

    /** initializes the serializer/deserializer for the given type */
    protected void initArray() {
        ArraySerializer serializer;
        serializer = new ArraySerializer();
        addSerializer(List.class, serializer);
        addDeserializer(ArraySerializer.NAME, serializer);
    }

    /** initializes the serializer/deserializer for the given type */
    protected void initStruct() {
        StructSerializer serializer;
        serializer = new StructSerializer();
        addSerializer(Map.class, serializer);
        addDeserializer(StructSerializer.NAME, serializer);
    }
}
