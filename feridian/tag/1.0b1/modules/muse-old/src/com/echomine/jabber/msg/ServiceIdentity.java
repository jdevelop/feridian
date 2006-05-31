package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JabberCode;
import org.jdom.Element;

/**
 * Supporting class to work with Service Discovery protocol.  This objects contains data for one identity entry
 * @see ServiceInfoIQMessage
 * @since 0.8a4
 */
public class ServiceIdentity {
    String category;
    String name;
    String type;

    /**
     * constructor to work with the required fields
     * @param category the category name
     * @param name the name/description of the category
     */
    public ServiceIdentity(String category, String name) {
        this(category, name, null);
    }

    /**
     * constructor to work with the data
     * @param category the category name
     * @param name the name/description of the category
     * @param type optional type, can be null
     */
    public ServiceIdentity(String category, String name, String type) {
        if (category == null || name == null)
            throw new IllegalArgumentException("category and name both cannot be null");
        this.category = category;
        this.name = name;
        this.type = type;
    }

    /** parses out the data for the identity out of the element */
    public ServiceIdentity(Element identElem) throws ParseException {
        parse(identElem);
    }

    /** @return the category name of the identity */
    public String getCategory() {
        return category;
    }

    /** sets the category name of the identity.  Cannot be null. */
    public void setCategory(String category) {
        if (category == null) throw new IllegalArgumentException("category cannot be null");
        this.category = category;
    }

    /** @return the name/description of the identity */
    public String getName() {
        return name;
    }

    /** sets the name/description of the identity.  Cannot be null. */
    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        this.name = name;
    }

    /** @return the identity type, or null if no type exists */
    public String getType() {
        return type;
    }

    /** sets the identity type. can be null. */
    public void setType(String type) {
        this.type = type;
    }

    /** parses the element for the required data */
    public void parse(Element identElem) throws ParseException {
        if (!"identity".equals(identElem.getName()) && JabberCode.XMLNS_IQ_DISCO_INFO != identElem.getNamespace())
            throw new ParseException("The incoming element is not a recognizable service identity XML element");
        category = identElem.getAttributeValue("category");
        if (category == null) throw new ParseException("category must exist");
        name = identElem.getAttributeValue("name");
        if (name == null) throw new ParseException("name must exist");
        type = identElem.getAttributeValue("type");
    }

    /** encodes the data into an identity element and returns it for inclusion into another dom tree */
    public Element encode() {
        Element identElem = new Element("identity", JabberCode.XMLNS_IQ_DISCO_INFO);
        identElem.setAttribute("category", category);
        identElem.setAttribute("name", name);
        if (type != null)
            identElem.setAttribute("type", type);
        return identElem;
    }
}
