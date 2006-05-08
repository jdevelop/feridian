package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Serializer for the base64 binary data
 * Example:
 * <pre>
 * &lt;base64>eW91IGNhbid0IHJlYWQgdGhpcyE=&lt;/base64>
 * </pre>
 */
public class Base64Serializer implements Serializer, Deserializer {
    public static final String NAME = "base64";
    private static Log log = LogFactory.getLog(Base64Serializer.class);

    /**
     * Serializes the data into a base64 element.
     * The object accepted must be a byte[] array.
     * @param data a byte[] array object
     * @param ns optional namespace, null if none
     * @return the element representing the data
     */
    public Element serialize(Object data, Namespace ns) {
        if (!(data instanceof byte[]))
            throw new IllegalArgumentException("Object passed in must be a byte[] array");
        Element root = new Element(NAME, ns);
        BASE64Encoder base64 = new BASE64Encoder();
        root.setText(base64.encode((byte[]) data));
        return root;
    }

    /**
     * Deserializes the data into a byte[] array object.
     * If an error occurs while deserializing the base64 data, exception WILL NOT be thrown.
     * However, null will be returned instead.
     * @param elem the data
     * @return a byte[] array element containing the byte data, or null if the data cannot be decoded
     */
    public Object deserialize(Element elem) {
        //it's the correct tag, get the data inside
        String text = elem.getText();
        if (text == null) return null;
        try {
            BASE64Decoder base64 = new BASE64Decoder();
            return base64.decodeBuffer(text);
        } catch (Exception ex) {
            if (log.isWarnEnabled())
                log.warn("Base64 data cannot be decoded properly", ex);
        }
        return null;
    }
}
