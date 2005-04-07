package com.echomine.jabber;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/** error handling for the sax parser. */
public class JabberErrorHandler implements ErrorHandler {
    private static Log log = LogFactory.getLog(JabberErrorHandler.class);

    public void warning(SAXParseException ex) {
        if (log.isInfoEnabled())
            log.info("SAX Warning: " + ex.getMessage());
    }

    public void error(SAXParseException ex) {
        if (log.isInfoEnabled())
            log.info("SAX Error: " + ex.getMessage());
    }

    public void fatalError(SAXParseException ex) {
        if (log.isInfoEnabled())
            log.info("SAX Fatal Error: " + ex.getMessage());
    }
}
