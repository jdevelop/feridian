package com.echomine.xmpp;

/**
 * A Namespace Identifier is used to store the "local name" and namespace URI
 * together. This allows packets to send customized elements using only the NSI.
 * For instance, the error packet may contain a custom application error
 * condition. This condition is simply an element with a custom defined
 * namespace.
 */
public class NSI {
    private String name;
    private String uri;

    /**
     * Creates an NSI with the specified name and URI
     * 
     * @param name the element name
     * @param uri the namespace URI
     */
    public NSI(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    /**
     * @return Returns the element name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the namespace uri.
     */
    public String getNamespaceURI() {
        return uri;
    }
}
