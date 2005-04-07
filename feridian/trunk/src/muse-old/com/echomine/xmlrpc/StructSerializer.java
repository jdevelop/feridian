package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * serializer for parsing a struct element into a hashmap or any Map-based class into an JDOM element.
 * The Element should start with the &lt;struct> tag.
 * The struct XML example structure is as follows:
 * <pre>
 * &lt;struct>
 *    &lt;member>
 *       &lt;name>lowerBound&lt;/name>
 *       &lt;value>&lt;i4>18&lt;/i4>&lt;/value>
 *    &lt;/member>
 *    &lt;member>
 *       &lt;name>upperBound&lt;/name>
 *       &lt;value>&lt;i4>139&lt;/i4>&lt;/value>
 *    &lt;/member>
 * &lt;/struct>
 * </pre>
 */
public class StructSerializer implements Serializer, Deserializer, SerializerFactoryAware {
    public static final String NAME = "struct";
    private SerializerFactory factory;

    /** sets the serializer factory instance */
    public void setSerializerFactory(SerializerFactory factory) {
        this.factory = factory;
    }

    /**
     * Serializes a Map-based object into a struct.  This includes Hashmaps, sorted maps, tree maps, etc.
     * @param data any object that implements the Map interface
     * @param ns optional namespace, null if there is none
     * @return the Element object of the serialized data, null if there were any problems
     * @throws IllegalArgumentException when the data is not an accepted format
     */
    public Element serialize(Object data, Namespace ns) {
        //must be hashmap type
        if (!(data instanceof Map))
            throw new IllegalArgumentException("object must be of type Map or its subclass");
        Map params = (Map) data;
        Element root = new Element(NAME, ns);
        Element member;
        //start adding the stuff
        Iterator iter = params.keySet().iterator();
        String name;
        Element value;
        Element memberName;
        Element memberValue;
        while (iter.hasNext()) {
            //create the <member> tag
            member = new Element("member", ns);
            name = (String) iter.next();
            //get the value
            value = factory.serialize(params.get(name), ns);
            //if the value is null, then no serializer available for it
            if (value == null) continue;
            memberName = new Element("name", ns);
            memberName.addContent(name);
            //serialize the value
            memberValue = new Element("value", ns);
            memberValue.addContent(value);
            //add the name/value pair to the member
            member.addContent(memberName);
            member.addContent(memberValue);
            //add to the main struct element
            root.addContent(member);
        }
        return root;
    }

    /**
     * Deserializes the element into a HashMap instance
     * @param elem the element containing the struct xml
     * @return a HashMap instance
     */
    public Object deserialize(Element elem) {
        HashMap data = new HashMap();
        Namespace ns = elem.getNamespace();
        //get the <member> tags
        Iterator iter = elem.getChildren("member", ns).iterator();
        Element member;
        String name;
        Object value = null;
        Element valElem = null;
        List valChilds;
        while (iter.hasNext()) {
            value = null;
            valElem = null;
            member = (Element) iter.next();
            //get the name
            name = member.getChildText("name", ns);
            //get the value
            valChilds = member.getChild("value", ns).getChildren();
            //get the first element
            if (valChilds.size() > 0)
                valElem = (Element) valChilds.get(0);
            //serialize the internal data
            if (valElem != null)
                value = factory.deserialize(valElem);
            //add the name/value pair into the hashtable
            if (value != null)
                data.put(name, value);
        }
        return data;
    }
}
