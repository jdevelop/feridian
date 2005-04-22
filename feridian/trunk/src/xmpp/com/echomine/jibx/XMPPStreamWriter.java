package com.echomine.jibx;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.impl.UTF8StreamWriter;

/**
 * This customized stream writer will automatically register the prefixes
 * for the stream namespaces.  Thus, Index 2 is always the jabber stream
 * namespace (ie. http://etherx.jabber.org/streams).  Index 3 is always
 * the jabber:client namespace (also the default).
 * Furthermore, this stream includes additional methods to work with 
 * streaming xml.
 */
public class XMPPStreamWriter extends UTF8StreamWriter {
    private static final Log log = LogFactory.getLog(XMPPStreamWriter.class);
    private static final String STREAM_PREFIX = "stream";
    private static final String CLIENT_PREFIX = "";
    
    /**
     * @param uris ordered array of URIs for namespaces used in document (must
     * be constant; the value in position 0 must always be the empty string "",
     * and the value in position 1 must always be the XML namespace
     * "http://www.w3.org/XML/1998/namespace").  Position 2 must always be the
     * XML namespace "http://etherx.jabber.org/streams", and position 3
     * must always be the XML namespace jabber:client.
     */
    public XMPPStreamWriter(String[] uris) {
        super(uris);
        defineNamespace(2, STREAM_PREFIX);
        defineNamespace(3, CLIENT_PREFIX);
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IXMLWriter#flush()
     */
    public void flush() throws IOException {
        if (log.isDebugEnabled())
            log.debug(new String(m_buffer, 0, m_fillOffset));
        super.flush();
    }
}
