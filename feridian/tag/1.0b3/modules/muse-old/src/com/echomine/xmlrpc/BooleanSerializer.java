package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Serializer for the XMLRPC boolean type.
 * Example:
 * <pre>
 * &lt;boolean>1&lt;/boolean>
 * </pre>
 */
public class BooleanSerializer implements Serializer, Deserializer {
    public static final String NAME = "boolean";

    /**
     * Serializes the data into a element data.  This method will only accept Boolean objects.
     * @param data a Boolean instance
     * @param ns optional namespace, null if none
     * @return the element data representing the data
     */
    public Element serialize(Object data, Namespace ns) {
        if (!(data instanceof Boolean))
            throw new IllegalArgumentException("Object must be of type Boolean");
        Element root = new Element(NAME, ns);
        boolean bool = ((Boolean)data).booleanValue();
        if (bool)
            root.setText("1");
        else
            root.setText("0");
        return root;
    }

    /**
     * deserializes the element data into a Boolean object.
     * If the value is "1", then the value is true.
     * All other strings or values will return a false boolean value.
     * @param elem the data
     * @return a Boolean object containing the value
     */
    public Object deserialize(Element elem) {
        //it's the correct tag, get the data inside
        String text = elem.getTextTrim();
        if (text == null) return null;
        Boolean value;
        if (text.equals("1"))
            value = new Boolean(true);
        else
            value = new Boolean(false);
        return value;
    }
}
