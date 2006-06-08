package com.echomine.xmlrpc;

import org.jdom.Element;

/**
 * interface that will implement the deserializing of an XMLRPC element
 */
public interface Deserializer {
    /** deserializes the given element into an object and returns the object */
    Object deserialize(Element elem);
}
