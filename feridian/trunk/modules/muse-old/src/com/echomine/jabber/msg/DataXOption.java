package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JabberCode;
import org.jdom.Element;

/**
 * Supporting class for jabber:x:data support.  This represents one option that is used for list-single
 * and list-multi fields.
 * @since 0.8a4
 * @see DataXMessage
 * @see DataXField
 */
public class DataXOption {
    private String label;
    private String value;

    /** default constructor for use to parse incoming options */
    public DataXOption() {
    }

    /** accepts a value for the option */
    public DataXOption(String value) {
        this(value, null);
    }

    /**
     * accepts a value and optionally a label.  The label can be null.
     * If the label is null, then by default, the label will be the same as the value as per the JEP specification.
     * @param value the value of the option
     * @param label optionally the label, or null if not used (which will default to value when retrieved)
     */
    public DataXOption(String value, String label) {
        this.label = label;
        this.value = value;
    }

    /**
     * retrieves the label.  If the label does not exist (ie. null), then the value will be returned instead
     * @return the label or the value string if label is null
     */
    public String getLabel() {
        if (label == null) return getValue();
        return label;
    }

    /** sets the label.  Set to null to default to using the value for the label */
    public void setLabel(String label) {
        this.label = label;
    }

    /** @return the value for the option */
    public String getValue() {
        return value;
    }

    /** sets the value */
    public void setValue(String value) {
        this.value = value;
    }

    /** encodes the data and returns the element for use to serialize the data over the wire */
    public Element encode() throws ParseException {
        if (value == null) throw new ParseException("value for the option is required and cannot be null");
        Element opElem = new Element("option", JabberCode.XMLNS_X_DATA);
        if (label != null)
            opElem.setAttribute("label", label);
        opElem.addContent(new Element("value", JabberCode.XMLNS_X_DATA).setText(value));
        return opElem;
    }

    /** parses the incoming option element */
    public void parse(Element opElem) throws ParseException {
        if (!"option".equals(opElem.getName()) && JabberCode.XMLNS_X_DATA != opElem.getNamespace())
            throw new ParseException("option must begin with an <option> element and in the proper namespace");
        value = opElem.getChildTextNormalize("value", JabberCode.XMLNS_X_DATA);
        label = opElem.getAttributeValue("label");
    }
}
