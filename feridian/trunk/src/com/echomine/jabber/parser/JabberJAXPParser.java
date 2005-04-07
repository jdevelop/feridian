package com.echomine.jabber.parser;

import com.echomine.jabber.JabberContentHandler;
import com.echomine.jabber.JabberErrorHandler;
import com.echomine.jabber.JabberSAXParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * This class uses the JAXP parser to parse incoming XML data from Jabber servers.  This is good if you want parser
 * independence, but sometimes using this parser will cause problems if you have another parser already loaded. Most likely
 * you may not want to use this custom parser class if you are running under a servlet engine since most servlet engines
 * uses a xml parser already.
 */
public class JabberJAXPParser implements JabberSAXParser {
    private static Log log = LogFactory.getLog(JabberJAXPParser.class);

    /**
     * this method will create the parser, set the handlers, and run the parser.  The method will be run in its own thread so
     * you don't have to worry about IO Blocking.  This InputSource is actually the incoming socket reader
     * from the Jabber connection.
     */
    public void parse(boolean validating, boolean namespaceAware, JabberContentHandler contentHandler,
                      JabberErrorHandler errorHandler, InputSource reader) {
        //create a sax handler to handle the incoming xml data
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(validating);
        spf.setNamespaceAware(namespaceAware);
        try {
            // Create a JAXP SAXParser
            SAXParser jaxpParser = spf.newSAXParser();
            // Get the encapsulated SAX parser
            XMLReader xmlReader = jaxpParser.getXMLReader();
            // Set the ContentHandler of the XMLReader
            xmlReader.setContentHandler(contentHandler);
            // Set an ErrorHandler before parsing
            xmlReader.setErrorHandler(errorHandler);
            // Tell the XMLReader to parse the XML document
            xmlReader.parse(reader);
        } catch (ParserConfigurationException ex) {
            if (log.isInfoEnabled())
                log.info("Parser Configuration Error: " + ex.getMessage());
        } catch (SAXException ex) {
            if (log.isInfoEnabled())
                log.info("SAX Exception: " + ex.getMessage());
        } catch (IOException ex) {
            if (log.isInfoEnabled())
                log.info("IOException: " + ex.getMessage());
        }
    }
}
