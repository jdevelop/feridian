package com.echomine.xmpp.packet;

/**
 * This class represents one stream feature. The feature will contain a list of
 * properties that are appropriate for each feature. The feature can be
 * subclassed, but is actually generic enough for use without any enhancements.
 */
public class StreamFeature {
    String elementName;
    Object value;

    /**
     * empty constructor
     */
    public StreamFeature() {
    }

    /**
     * sets the name and value during instantiation
     * 
     * @param elementName the element name
     * @param value the value to store
     */
    public StreamFeature(String elementName, Object value) {
        this.elementName = elementName;
        this.value = value;
    }

    /**
     * @return Returns the element name for the specified feature.
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * @param elementName The element name to set for the feature.
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * the true value associated with the feature. Null if the feature is a
     * one-liner.
     * 
     * @return Returns the value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * sets the value associated with the feature. Null if the feature is a
     * one-liner.
     * 
     * @param value The value to set.
     */
    public void setValue(Object value) {
        this.value = value;
    }

}
