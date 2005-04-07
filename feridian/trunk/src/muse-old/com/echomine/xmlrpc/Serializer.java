package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;

public interface Serializer {
    /** @return the Element object of the serialized data, null if there were any problems */
    Element serialize(Object data, Namespace ns);

}
