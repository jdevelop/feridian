package com.echomine.xmlrpc;

import junit.framework.TestCase;

/**
 * Base class containing common code for subclasses to use
 */
public abstract class BaseSerializerTestCase extends TestCase {
    SerializerFactory serializerFactory = new SerializerFactory();

    public SerializerFactory getSerializerFactory() {
        return serializerFactory;
    }
}
