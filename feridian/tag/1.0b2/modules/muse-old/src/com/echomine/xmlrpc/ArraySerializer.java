package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Used to serialize and deserialize the XMLRPC array xml data.
 * Example:
 * <pre>
 * &lt;array>
 * &lt;data>
 *    &lt;value>&lt;i4>12&lt;/i4>&lt;/value>
 *    &lt;value>&lt;string>Egypt&lt;/string>&lt;/value>
 *    &lt;value>&lt;boolean>0&lt;/boolean>&lt;/value>
 *    &lt;/data>
 * &lt;/array>
 * </pre>
 */
public class ArraySerializer implements Serializer, Deserializer, SerializerFactoryAware {
    public static final String NAME = "array";
    private SerializerFactory factory;

    /** sets the serializer factory instance */
    public void setSerializerFactory(SerializerFactory factory) {
        this.factory = factory;
    }

    /**
     * Serializes the data object into an XML element.
     * The data object must be of type List or one of its subclasses.
     * @param data object that implements the List interface
     * @param ns optional namespace, null if none
     * @return the Element object of the serialized data, null if there were any problems
     * @throws IllegalArgumentException if data object does not implement List
     */
    public Element serialize(Object data, Namespace ns) {
        //must be valid type
        if (!(data instanceof List))
            throw new IllegalArgumentException("Object must implement List");
        List params = (List) data;
        Element root = new Element(NAME, ns);
        Element dataElem = new Element("data", ns);
        root.addContent(dataElem);
        //start adding the stuff
        Iterator iter = params.iterator();
        Object obj;
        Element value;
        Element valueData;
        while (iter.hasNext()) {
            //create the <member> tag
            value = new Element("value", ns);
            obj = iter.next();
            //get the value
            valueData = factory.serialize(obj, ns);
            //if the value is null, then no serializer available for it
            if (valueData == null) continue;
            //serialize the value
            value.addContent(valueData);
            //add to the main struct element
            dataElem.addContent(value);
        }
        return root;
    }

    /**
     * deserializes the data element into a List object.
     * @param elem the element data
     * @return the deserialized List object, containing sub objects of corresponding types
     */
    public Object deserialize(Element elem) {
        ArrayList data = new ArrayList();
        Namespace ns = elem.getNamespace();
        //get the <value> tags under <data>
        Element dataValue = elem.getChild("data", ns);
        if (dataValue == null) return null;
        Iterator iter = dataValue.getChildren("value", ns).iterator();
        Element member;
        Object value = null;
        Element valElem = null;
        List valChilds;
        while (iter.hasNext()) {
            value = null;
            valElem = null;
            member = (Element) iter.next();
            //get the value inside the <value> tag
            valChilds = member.getChildren();
            //get the first element
            if (valChilds.size() > 0)
                valElem = (Element) valChilds.get(0);
            //serialize the internal data
            if (valElem != null)
                value = factory.deserialize(valElem);
            //add the value into the array
            if (value != null)
                data.add(value);
        }
        return data;
    }
}
