package com.echomine.jabber;

import org.xml.sax.InputSource;

/**
 * Every sax parser implementation that wants to work with the jabber module must implement this interface.  Once you have an
 * implementation setup, you can specify the SAX Parser you want to use by setting the System Property
 * "com.echomine.jabber.SAXParser".  If you don't specify, the default parser is to use JAXP.  The underlying XML Parser will
 * still be what you specify. You must have an empty constructor for your implementation class because the parser will be
 * dynamically instantiated through reflection.
 */
public interface JabberSAXParser {
    /**
     * this method will create the parser, set the handlers, and run the parser.  The method will be run in its own thread so
     * you don't have to worry about IO Blocking.  This InputSource is actually the incoming socket reader
     * from the Jabber connection.
     */
    void parse(boolean validating, boolean namespaceAware, JabberContentHandler contentHandler,
        JabberErrorHandler errorHandler, InputSource reader);
}
