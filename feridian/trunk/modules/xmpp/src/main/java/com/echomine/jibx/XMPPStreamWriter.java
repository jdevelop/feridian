package com.echomine.jibx;

import java.io.IOException;
import java.util.Locale;

import org.jibx.runtime.impl.UTF8StreamWriter;

import com.echomine.util.LocaleUtil;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPLogger;

/**
 * This customized stream writer will automatically register the prefixes for
 * the stream namespaces. Thus, Index 3 is always the jabber stream namespace
 * (ie. http://etherx.jabber.org/streams). Index 4 is always the stanza
 * namespace (either jabber:client or jabber:server). Furthermore, this stream
 * includes additional methods to work with streaming xml.
 */
public class XMPPStreamWriter extends UTF8StreamWriter {
    private static final int IDX_JABBER_STREAM = 3;

    private int STANZA_IDX = 4;

    private static final String JABBER_STREAM_PREFIX = "stream";

    private static final String[] STREAM_URIS = new String[] { "",
            "http://www.w3.org/XML/1998/namespace",
            "http://www.w3.org/2001/XMLSchema-instance",
            XMPPConstants.NS_JABBER_STREAM };

    private boolean streamCloseable = true;

    /**
     * This constructor will setup a default set of URIs specifically for XMPP
     */
    public XMPPStreamWriter() {
        this(STREAM_URIS);
        defineNamespace(IDX_JABBER_STREAM, JABBER_STREAM_PREFIX);
    }

    /**
     * @param uris ordered array of URIs for namespaces used in document (must
     *        be constant; the value in position 0 must always be the empty
     *        string "", and the value in position 1 must always be the XML
     *        namespace "http://www.w3.org/XML/1998/namespace"). Position 3 must
     *        always be the Jabber Streams namespace
     *        "http://etherx.jabber.org/streams"
     */
    public XMPPStreamWriter(String[] uris) {
        super(uris);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.impl.StreamWriterBase#reset()
     */
    @Override
    public void reset() {
        super.reset();
        streamCloseable = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IXMLWriter#flush()
     */
    @Override
    public void flush() throws IOException {
        if (XMPPLogger.canLogOutgoing())
            if (m_fillOffset != 0)
                XMPPLogger.logOutgoing(new String(m_buffer, 0, m_fillOffset));
        super.flush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.impl.StreamWriterBase#close()
     */
    @Override
    public void close() throws IOException {
        if (streamCloseable) super.close();
    }

    /**
     * Outputs the raw data. This method turns the parent's method to public
     * instead of protected.
     */
    public void writeMarkup(String text) throws IOException {
        super.writeMarkup(text);
    }

    /**
     * This will write out the initial handshake stream start tag that begins
     * the document. This will set to the most current version of the stream.
     * This method will also close the start tag but keep it open for adding
     * content. The namespace can either be jabber:client or jabber:server
     * 
     * @param stanzaNs the namespace of the stanza stream
     * @param hostName the hostname of the remote client/server
     * @param locale optional locale for the stream, null if not used
     * @throws IOException when error occurs while writing out the tag
     */
    public void startHandshakeStream(String stanzaNs, String hostName,
            Locale locale) throws IOException {
        if (stanzaNs == null || hostName == null)
            throw new IllegalArgumentException(
                    "Namespace and host name cannot be null");
        // pushes the stanza namespace in use
        String[] uris = new String[] { stanzaNs };
        pushExtensionNamespaces(uris);
        startTagNamespaces(IDX_JABBER_STREAM, "stream", new int[] {
                IDX_JABBER_STREAM, STANZA_IDX }, new String[] {
                JABBER_STREAM_PREFIX, "" });
        addAttribute(0, "version", "1.0");
        addAttribute(0, "to", hostName);
        if (locale != null)
            addAttribute(XMPPConstants.IDX_XML, "lang", LocaleUtil
                    .format(locale));
        closeStartTag();
        flagContent();
        flush();
    }

    /**
     * Ends the XMPP stream by sending out the document ending tag.
     * 
     * @throws IOException
     */
    public void endStream() throws IOException {
        endTag(IDX_JABBER_STREAM, "stream");
        flush();
    }

    /**
     * Writes out the tag that uses the stream namespace (ie. stream:features)
     * and leave the tag open for adding attributes. Caller must explicitly
     * close the start tag to continue adding content.
     * 
     * @param name the local element name
     * @throws IOException
     */
    public void startStreamTagOpen(String name) throws IOException {
        startTagNamespaces(IDX_JABBER_STREAM, name,
                new int[] { IDX_JABBER_STREAM },
                new String[] { JABBER_STREAM_PREFIX });
    }

    /**
     * Ends the XMPP tag that uses the stream namespace.
     * 
     * @param name the local element name using the stream namespace
     * @throws IOException
     */
    public void endStreamTag(String name) throws IOException {
        endTag(IDX_JABBER_STREAM, name);
    }

    /**
     * write out the initial stanza start tag and leave it open for writing
     * additional attributes. The start tag must be explicitly closed to
     * continue writing content.
     * 
     * @param name the local element name for the IQ tag (normally "iq" by xmpp
     *        spec standard).
     * @throws IOException when error occurs while writing out the tag
     */
    public void startStanzaTagOpen(String name) throws IOException {
        startTagNamespaces(STANZA_IDX, name, new int[] { STANZA_IDX },
                new String[] { "" });
    }

    /**
     * Closes the stanza tag. This should be called at the end to end the main
     * IQ stanza.
     * 
     * @param name the local element name for the IQ tag (normally "iq" by xmpp
     *        spec standard).
     * @throws IOException when error occurs while writing out the tag
     */
    public void endStanzaTag(String name) throws IOException {
        endTag(STANZA_IDX, name);
    }

    /**
     * sets the stream to be closeable.  If a stream is not closeable,
     * then this writer will ignore any close() events.  This is
     * useful as a workaround when using JiBX's own marshalling methods
     * that will automatically close any output streams after marshalling.
     * <p><b>NOTE: once a call to set stream back to closeable, remember
     * to flush if required.  flush() is not explicitly called after
     * marshalling.</b></p>
     */
    public void setStreamCloseable(boolean streamCloseable) {
        this.streamCloseable = streamCloseable;
    }
}
