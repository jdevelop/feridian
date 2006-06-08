package com.echomine.feridian;

/**
 * Represents a packet extension. This is used for configuration purposes.
 */
public class FeridianPacketExtension {
    private String namespace;
    private Class cls;

    /**
     * The class associated with the packet extension
     * 
     * @return Returns the cls.
     */
    public Class getPacketClass() {
        return cls;
    }

    /**
     * @return Returns the namespace.
     */
    public String getNamespace() {
        return namespace;
    }
}
