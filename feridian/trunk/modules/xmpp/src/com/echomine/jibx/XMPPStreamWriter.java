package com.echomine.jibx;

import java.io.IOException;

import org.jibx.runtime.impl.UTF8StreamWriter;

import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPLogger;

/**
 * This customized stream writer will automatically register the prefixes for
 * the stream namespaces. Thus, Index 2 is always the jabber stream namespace
 * (ie. http://etherx.jabber.org/streams). Index 3 is always the jabber:client
 * namespace (also the default). Furthermore, this stream includes additional
 * methods to work with streaming xml.
 */
public class XMPPStreamWriter extends UTF8StreamWriter {
    public static final int IDX_JABBER_STREAM = 2;
    public static final int IDX_XMPP_CLIENT = 3;
    private static final String STREAM_PREFIX = "stream";
    private static final String CLIENT_PREFIX = "";
    private static final String[] STREAM_URIS = new String[] { "", "http://www.w3.org/XML/1998/namespace", XMPPConstants.NS_JABBER_STREAM, XMPPConstants.NS_XMPP_CLIENT };

    /**
     * This constructor will setup a default set of URIs specifically for XMPP
     */
    public XMPPStreamWriter() {
        this(STREAM_URIS);
        defineNamespace(IDX_JABBER_STREAM, STREAM_PREFIX);
        defineNamespace(IDX_XMPP_CLIENT, CLIENT_PREFIX);
    }

    /**
     * @param uris ordered array of URIs for namespaces used in document (must
     *            be constant; the value in position 0 must always be the empty
     *            string "", and the value in position 1 must always be the XML
     *            namespace "http://www.w3.org/XML/1998/namespace"). Position 2
     *            must always be the Jabber Streams namespace
     *            "http://etherx.jabber.org/streams", and position 3 must always
     *            be the XML namespace jabber:client.
     */
    public XMPPStreamWriter(String[] uris) {
        super(uris);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IXMLWriter#flush()
     */
    public void flush() throws IOException {
        if (XMPPLogger.canLogOutgoing())
            if (m_fillOffset != 0)
                XMPPLogger.logOutgoing(new String(m_buffer, 0, m_fillOffset));
        super.flush();
    }

    /**
     * Outputs the raw data. This method turns the parent's method to public
     * instead of protected. It also adds debugging support.
     */
    public void writeMarkup(String text) throws IOException {
        super.writeMarkup(text);
    }
}
