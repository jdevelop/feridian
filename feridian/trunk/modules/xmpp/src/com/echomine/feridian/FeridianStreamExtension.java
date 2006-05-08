package com.echomine.feridian;

/**
 * Encapsulates a feridian stream extension element.
 */
public class FeridianStreamExtension extends FeridianPacketExtension {
    private String namespace;
    private Class unmarshaller;
    private Class cls;

    /**
     * The class associated with the packet extension
     * 
     * @return Returns the cls.
     */
    public Class getStreamClass() {
        return cls;
    }

    /**
     * @return Returns the namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @return Returns the unmarshaller.
     */
    public Class getUnmarshallerClass() {
        return unmarshaller;
    }
}
