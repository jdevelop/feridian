package com.echomine.jabber.parser;

import com.echomine.jabber.JabberContentHandler;
import com.echomine.jabber.JabberErrorHandler;
import com.echomine.jabber.JabberSAXParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.sax2.Driver;

import java.io.IOException;

/**
 * This uses the XML Pull Parser as the underlying xml parser.  As XPP is a pull-based parser, it requires a SAX-based
 * driver (provided with XPP) to translate the pull parsing events into SAX events.
 */
public class JabberXPPParser implements JabberSAXParser {
    private static Log log = LogFactory.getLog(JabberXPPParser.class);

    /**
     * this method will create the parser, set the handlers, and run the parser.  The method will be run in its own thread so
     * you don't have to worry about IO Blocking.  This InputSource is actually the incoming socket reader
     * from the Jabber connection.
     * @param validating whether to validate the document or not based on the DTD
     * @param namespaceAware whether the parser should be aware of namespaces
     * @param contentHandler the content handler class for receiving the sax events
     * @param errorHandler optional handler to receive error events (null if not used)
     * @param reader the stream to read the document from
     */
    public void parse(boolean validating, boolean namespaceAware, JabberContentHandler contentHandler,
                      JabberErrorHandler errorHandler, InputSource reader) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance(System.getProperty(XmlPullParserFactory.PROPERTY_NAME), getClass());
            factory.setNamespaceAware(namespaceAware);
            factory.setValidating(validating);
            XmlPullParser xpp = factory.newPullParser();
            Driver driver = new Driver(xpp);
            // Set the ContentHandler of the XMLReader
            driver.setContentHandler(contentHandler);
            // Set an ErrorHandler before parsing
            if (errorHandler != null)
                driver.setErrorHandler(errorHandler);
            // Tell the XMLReader to parse the XML document
            driver.parse(reader);
        } catch (XmlPullParserException ex) {
            if (log.isInfoEnabled())
                log.info("Error while setting up xml pull parser -- " + ex.getMessage());
        } catch (IOException ex) {
            if (log.isInfoEnabled())
                log.info("IOException (possibly due to shutdown of connection) -- " + ex.getMessage());
        } catch (SAXException ex) {
            if (log.isInfoEnabled())
                log.info("Exception occurred while parsing XML document -- " + ex.getMessage());
        }
    }
}
