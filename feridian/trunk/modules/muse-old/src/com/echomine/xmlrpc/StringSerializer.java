package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * The string serializer will work with the &lt;string> type in the XML-RPC specs.
 * An example string element looks like the following:
 * <pre>
 * &lt;string>hello world&lt;string>
 * </pre>
 */
public class StringSerializer implements Serializer, Deserializer {
    public static final String NAME = "string";

    /**
     * serializes the data into an XML element.  Note that this method will accept
     * any object class.  However, if it happens not to be of class String, then
     * the toString() will be called.
     * @param data an object to serialize, can be any object
     * @param ns optional namespace, null if none
     * @return the xml element representation of the serialized data
     */
    public Element serialize(Object data, Namespace ns) {
        Element root = new Element(NAME, ns);
        root.setText(data.toString());
        return root;
    }

    /**
     * deserializes the element data into a String object.
     * @param elem the element containing the string data
     * @return a String object
     */
    public Object deserialize(Element elem) {
        //it's the correct tag, get the data inside
        String text = elem.getText();
        return text;
    }
}
