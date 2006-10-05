package com.echomine.xmpp;

import com.echomine.feridian.FeridianConfiguration;
import com.echomine.util.ClassUtil;

/**
 * Holds a singleton instance of the id generator. It will give you unique id
 * sequences. The unique ID sequence has a header prepended before the sequence
 * number. This is to make the ID attribute conform to the XML Specification.
 * The XML standard states that the unique ID attribute in an XML document must
 * begin with an alphabet(a-z), underscore(_), or colon(:). The ID generator is
 * an abstract factory class. It will obtain the real implementation from the
 * configuration and instantiate.
 */
public abstract class IDGenerator {
    private static IDGenerator generator;

    /**
     * obatins the real generator implementation
     */
    private static IDGenerator getGenerator() throws XMPPException {
        try {
            return (IDGenerator) ClassUtil.newInstance(FeridianConfiguration.getConfig().getIdGeneratorClass(), IDGenerator.class);
        } catch (Exception ex) {
            throw new XMPPException(ex);
        }
    }

    /**
     * This is the implementation to retrieve the next ID; This ID must be
     * guaranteed to be unique in a given session.
     */
    protected abstract String generateID();

    /**
     * if this is the first time the method is called, it will initialize the
     * generator.
     */
    public static String nextID() throws XMPPException {
        if (generator == null)
            generator = getGenerator();
        return generator.generateID();
    }

    /**
     * This method allows the user to set a custom ID generator. It can only be
     * set once and must be called before any nextID() calls are made.
     * 
     * @param gen the generator implementation
     */
    public static void setIDGenerator(IDGenerator gen) {
        generator = gen;
    }
}
