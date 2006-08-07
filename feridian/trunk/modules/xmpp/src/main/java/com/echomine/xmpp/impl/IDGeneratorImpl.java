package com.echomine.xmpp.impl;

import com.echomine.xmpp.IDGenerator;

/**
 * Holds a singleton instance of the id generator. It will give you unique id
 * sequences. The unique ID sequence has a header prepended before the sequence
 * number. This is to make the ID attribute conform to the XML Specification.
 * The XML standard states that the unique ID attribute in an XML document must
 * begin with an alphabet(a-z), underscore(_), or colon(:).
 */
public class IDGeneratorImpl extends IDGenerator {
    private static final String ID_HEADER = "frdn_";
    private int id;

    protected String generateID() {
        return String.valueOf(ID_HEADER + increment());
    }

    public int increment() {
        // this is an atomic operation, no need for synchronization
        return id++;
    }
}
