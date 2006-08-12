package com.echomine.xmlrpc;

/**
 * An inversion of control interface that allows any serializer registered with the serializer factory to
 * have an instance of the factory.  This is used for those serializers where recursive serialization is required.
 * For instance, an array will contain more data that requires serialization/deserialization.  Thus, it uses
 * the factory to do further work.
 */
public interface SerializerFactoryAware {
    /**
     * give the implementor a given instance of the factory
     */
    void setSerializerFactory(SerializerFactory factory);
}
