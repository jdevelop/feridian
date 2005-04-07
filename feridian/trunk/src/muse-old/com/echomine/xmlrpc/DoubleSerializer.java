package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Serializer for the double value.  This will deal with the XMLRPC's &lt;double> element.
 * Example:
 * <pre>
 * &lt;double>-12.214&lt;double>
 * </pre>
 */
public class DoubleSerializer implements Serializer, Deserializer {
    public static final String NAME = "double";

    /**
     * Serializes the object into its XML representation
     * The object can be any Number-based instance.  It will be
     * transformed to a double value before writing out the string
     * representation.
     * NOTE: The serialization may not serialize the exact value that you specified, but it will be close.
     * For instance, if you have -12.222, the serialized value may be -12.22200000100023.  There is nothing
     * that can be done in regards to the current situation.
     * @param data a Number instance
     * @param ns optional namespace, null if no namespace
     * @return the element data
     * @throws IllegalArgumentException when the data is not an accepted format
     */
    public Element serialize(Object data, Namespace ns) {
        if (!(data instanceof Number))
            throw new IllegalArgumentException("Object must be of type Number or its subclass");
        Element root = new Element(NAME, ns);
        root.setText(String.valueOf(((Number) data).doubleValue()));
        return root;
    }

    /**
     * Deserializes the xml data into a Double object.
     * NOTE: The deserialized double value may not be the exact value that the data specified.
     * However, you can round it off to your nearest precision if you'd like afterwards.
     * @param elem the element containing the data
     * @return the Double instance of the deserialized object
     * @throws NumberFormatException if the data cannot be parsed properly
     */
    public Object deserialize(Element elem) {
        //it's the correct tag, get the data inside
        String text = elem.getTextTrim();
        if (text == null) return null;
        Double value = new Double(text);
        return value;
    }
}
