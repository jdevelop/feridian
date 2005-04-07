package com.echomine.jabber;

/**
 * Holds a singleton instance of the id generator.  It will give you unique id sequences.
 * The unique ID sequence has a header prepended before the sequence number.  This is to make
 * the ID attribute conform to the XML Specification.  The XML standard states that the unique
 * ID attribute in an XML document must begin with an alphabet(a-z), underscore(_), or colon(:).
 */
public class MessageID {
    private static final String ID_HEADER = "id_";
    private static MessageID generator;
    private int id;

    private MessageID(int initial) {
        this.id = initial;
    }

    public int increment() {
        //this is an atomic operation, no need for synchronization
        return id++;
    }

    /**
     * retrieves the next ID.  If this is the first time this method is called,
     * the initial value will be set to 10001.  If you want to start off at a
     * specific number, use the nextID(int) static method instead.
     */
    public static String nextID() {
        return nextID(10001);
    }

    /**
     * if this is the first time the method is called, it will initialize the generator
     * to the initial value provided.  Subsequent calls to this method will ignore the initial request.
     */
    public static String nextID(int initial) {
        if (generator == null)
            generator = new MessageID(initial);
        return String.valueOf(ID_HEADER + generator.increment());
    }
}
