package com.echomine.xmpp.auth;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPLoggableReader;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.IXMPPAuthenticator;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;

/**
 * This is the base parent authenticator that all SASL-based authenticators can
 * subclass. The class offers some convenience methods as well as implementing
 * all interface methods to offer initial default behaviors, guard checks, etc.
 * All subclasses can simply implement one authentication method and only have
 * to worry about the authentication itself without having to take care of other
 * things.
 * 
 * @author ckchris
 * @since 1.0b5
 */
public abstract class BaseSaslAuthenticator implements IXMPPAuthenticator,
        XMPPConstants {
    private static Log log = LogFactory.getLog(BaseSaslAuthenticator.class);
    protected final static int SOCKETBUF = 8192;
    protected static final String FAILURE_ELEMENT_NAME = "failure";

    /**
     * SASL requires a new handshake after successful authentication.
     * 
     * @return true
     */
    public boolean redoHandshake() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    public void process(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx)
            throws XMPPException {
        if (!streamCtx.getFeatures().isSaslSupported())
            throw new XMPPException("SASL is not supported and this stream should not have been called");
        if (log.isDebugEnabled()) {
            Iterator iter = streamCtx.getFeatures().getSaslMechanisms().iterator();
            StringBuffer buf = new StringBuffer("Available Mechanism: ");
            while (iter.hasNext())
                buf.append(iter.next() + ",");
            log.debug(buf.toString());
        }
        XMPPStreamWriter writer = streamCtx.getWriter();
        UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
        String[] extns = new String[] { NS_STREAM_SASL };
        int idx = writer.getNamespaceCount();
        writer.pushExtensionNamespaces(extns);
        try {
            streamCtx.getReader().startLogging();
            processSasl(idx, uctx, writer, sessCtx, streamCtx);
            // save username and resource and reset stream callback
            String hostname = sessCtx.getHostName();
            sessCtx.reset();
            sessCtx.setHostName(hostname);
            sessCtx.setUsername(streamCtx.getAuthCallback().getUsername());
            sessCtx.setResource(streamCtx.getAuthCallback().getResource());
            streamCtx.getAuthCallback().clear();
            streamCtx.clearFeatures();
            if (log.isInfoEnabled())
                log.info("SASL authentication complete, resetting input and output streams for new handshake");
            // reset writer and unmarshalling context for handshake
            // renegotiation preparation
            XMPPLoggableReader bis = new XMPPLoggableReader(streamCtx.getSocket().getInputStream(), "UTF-8");
            BufferedOutputStream bos = new BufferedOutputStream(streamCtx.getSocket().getOutputStream(), SOCKETBUF);
            writer.flush();
            writer = new XMPPStreamWriter();
            writer.setOutput(bos);
            uctx.setDocument(bis);
            streamCtx.setWriter(writer);
            streamCtx.setReader(bis);
        } catch (IOException ex) {
            throw new XMPPException(ex);
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        } finally {
            streamCtx.getReader().stopLogging();
        }
    }

    /**
     * All subclasses will implement this method to do the main bulk of the
     * authentication. Before the method is called, the base parent class
     * already did some initial guard checks. After authentication, the base
     * parent class will also reinitialize the stream. Thus, this method should
     * only do the specific authentication ONLY.
     * 
     * @param idx the index of the sasl namespace element
     * @param uctx the unmarshalling context
     * @param writer the writer to write to the remote entity
     * @param sessCtx the session context
     * @param streamCtx the stream context
     * @throws IOException when IO errors occur during authentication
     * @throws JiBXException during (un)marshalling
     * @throws XMPPException when general exception occurs during authentication
     */
    protected abstract void processSasl(int idx, UnmarshallingContext uctx, XMPPStreamWriter writer, XMPPSessionContext sessCtx, XMPPStreamContext streamCtx)
            throws IOException, JiBXException, XMPPException;

    /**
     * Convenience method to parse past an element and obtaining the text
     * contained within. It expects to be at the start tag, and will parse the
     * context, then end tag. This method is here to bypass a bug in jibx where
     * jibx's parseElementText() will hang while reading an xml stream. This is
     * because the jibx method attempts to read past the end tag, but in a
     * streaming xml, there is no more data, and thus the parser will hang,
     * waiting for more incoming data.
     * 
     * @param uctx the unmarshalling context
     * @param elementName the element name to parse text
     * @return the text, or null if none cannot be found.
     * @throws JiBXException if any exception occurs
     */
    protected String parseElementText(UnmarshallingContext uctx, String elementName, XMPPStreamContext streamCtx)
            throws JiBXException {
        uctx.parsePastStartTag(NS_STREAM_SASL, elementName);
        String text = uctx.parseContentText();
        uctx.toEnd();
        streamCtx.getReader().flushLog();
        return text;
    }

    /**
     * will parse and throw exception if failure during negotiation is caught
     * 
     * @param uctx
     * @throws JiBXException
     * @throws XMPPException
     */
    protected void parseAndThrowFailure(UnmarshallingContext uctx, XMPPStreamContext streamCtx)
            throws JiBXException, XMPPException {
        if (uctx.isAt(NS_STREAM_SASL, FAILURE_ELEMENT_NAME)) {
            uctx.parsePastStartTag(NS_STREAM_SASL, FAILURE_ELEMENT_NAME);
            uctx.toTag();
            String errorType = uctx.getName();
            uctx.parsePastElement(NS_STREAM_SASL, errorType);
            uctx.toEnd();
            streamCtx.getReader().stopLogging();
            if (log.isInfoEnabled())
                log.info("SASL stream negotiation failed with error "
                        + errorType);
            throw new XMPPException("SASL Failed with error: " + errorType);
        }
    }
}
