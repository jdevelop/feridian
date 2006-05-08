package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * A serializer for integer types.  It can be used to serialize both the &lt;int> and the &lti4>
 * elements.  Each one should be registered by the caller just in case either one is used.
 * Example:
 * <pre>
 * &lt;int>64&lt;/int>
 * &lt;i4>128&lt;/i4>
 * </pre>
 */
public class IntSerializer implements Serializer, Deserializer {
    private String name;

    /** accepts the name of the element to use (ie. i4 or int) */
    public IntSerializer(String name) {
        this.name = name;
    }

    /**
     * serializes the data which can be a Number.  If you pass in an object that is
     * more than what an integer can handle, the number will be rounded or truncated
     * (ie. if a large double value is used, it will be truncated/rounded).
     * Preferably this should only accept Integer.
     * @param data an object of type Number
     * @param ns optional namespace, null if there is no namespace
     * @return the element of the serialized data
     * @throws IllegalArgumentException when the data is not an accepted format
     */
    public Element serialize(Object data, Namespace ns) {
        if (!(data instanceof Number))
            throw new IllegalArgumentException("The passed in data must be of type Number or its subclass");
        Element root = new Element(getName(), ns);
        root.setText(String.valueOf(((Number)data).intValue()));
        return root;
    }

    /**
     * Deserializes the element into an Integer value.
     * @param elem the element containing the data
     * @return the parsed integer
     * @throws NumberFormatException if the contained data does not contain a parsable integer value
     */
    public Object deserialize(Element elem) {
        //it's the correct tag, get the data inside
        String text = elem.getTextTrim();
        if (text == null) return null;
        Integer value = new Integer(text);
        return value;
    }

    /** @return the name of the element used */
    public String getName() {
        return name;
    }
}
