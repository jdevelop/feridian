package com.echomine.xmpp.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;

import com.echomine.jibx.JiBXOutputStreamWrapper;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.net.ConnectionContext;
import com.echomine.net.HandshakeFailedException;
import com.echomine.xmpp.IConnectionHandler;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.XMPPStreamFactory;

/**
 * The handler for working with the xmpp client connection. The handler will
 * actually delegate the work to Streams that handle all the incoming and
 * outgoing parsing. In addition, the handler will do automatic TLS negotation
 * if the remote entity supports it. It is on by default.
 */
public class XMPPConnectionHandler implements IConnectionHandler {
    private static final Log log = LogFactory.getLog(XMPPConnectionHandler.class);

    protected XMPPSessionContext sessCtx;
    protected boolean shutdown;
    protected boolean connected;
    protected XMPPStreamContext streamCtx;
    private IXMPPStream handshakeStream;
    private IXMPPStream tlsStream;

    /**
     * The constructor for the handler. It accepts a connection context to use
     * the data stored or to store any connection-related data.
     * 
     * @throws IllegalArgumentException if handshake stream is not defined or
     *             cannot be found
     */
    public XMPPConnectionHandler() {
        this(new XMPPSessionContext(), new XMPPStreamContext());
    }

    /**
     * A method allowing the customization of the session and stream context.
     * This is normally used for unit testing purposes. Normal users will use
     * the default no-argument constructor.
     * 
     * @param sessCtx the custom session context
     * @param streamCtx the custom stream context
     */
    public XMPPConnectionHandler(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) {
        this.sessCtx = sessCtx;
        this.streamCtx = streamCtx;
        try {
            handshakeStream = XMPPStreamFactory.getFactory().createStream(XMPPConstants.NS_STREAM_HANDSHAKE);
            if (handshakeStream == null)
                throw new IllegalArgumentException("Unable to find handshake stream. Must be declared in config");
            tlsStream = XMPPStreamFactory.getFactory().createStream(XMPPConstants.NS_STREAM_TLS);
        } catch (XMPPException ex) {
            throw new IllegalArgumentException("Error while retrieving stream: " + ex.getMessage());
        }
    }

    /*
     * This handshake will negotiate the initial handshaking of the XMPP stream.
     * Furthermore, if TLS is supported, it will automatically negotiate TLS
     * before returning. If TLS requires a callback to verify any unknown
     * certificates, it will be done here as well. Handshaking does NOT include
     * authentication, SASL, or any other kind of packet processing.
     * 
     * @see com.echomine.net.HandshakeableSocketHandler#handshake(java.net.Socket,
     *      com.echomine.net.ConnectionContext)
     */
    public void handshake(Socket socket, ConnectionContext connCtx) throws HandshakeFailedException {
        sessCtx.setHostName(connCtx.getHostName());
        try {
            socket.setKeepAlive(true);
            streamCtx.getWriter().setOutput(new JiBXOutputStreamWrapper(socket.getOutputStream()));
            streamCtx.getUnmarshallingContext().setDocument(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            streamCtx.setSocket(socket);
            if (log.isDebugEnabled())
                log.debug("Starting Handshake with " + sessCtx.getHostName());
            handshakeStream.process(sessCtx, streamCtx);
            // tls stream negotiation if a stream supports it
            if (tlsStream != null && streamCtx.getFeatures().isTLSSupported()) {
                if (log.isDebugEnabled())
                    log.debug("Found TLS Feature support and stream processor.  Trying to negotiate TLS...");
                tlsStream.process(sessCtx, streamCtx);
                if (log.isDebugEnabled())
                    log.debug("TLS negotiation successful! Redoing handshaking in TLS mode...");
                handshakeStream.process(sessCtx, streamCtx);
            }
            if (log.isDebugEnabled())
                log.debug("Handshake completed... Ready for XMPP Stanza processing...");
        } catch (IOException ex) {
            throw new HandshakeFailedException("Socket error during handshake", ex);
        } catch (JiBXException ex) {
            throw new HandshakeFailedException("Jibx Exception occurred during handshake", ex);
        } catch (XMPPException ex) {
            throw new HandshakeFailedException("XMPP Exception occurred during handshake", ex);
        }
    }

    /*
     * The main handler method.
     * 
     * @see com.echomine.net.SocketHandler#handle(alt.java.net.Socket,
     *      com.echomine.net.ConnectionContext)
     */
    public void handle(Socket socket, ConnectionContext connCtx) throws IOException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IConnectionHandler#authenticateSession(java.lang.String,
     *      char[], java.lang.String)
     */
    public void authenticateSession(String username, char[] password, String resource) throws XMPPException {
        throw new XMPPException("NOT IMPLEMENTED");
    }

    /*
     * Resets the data before a connection begins for reusing the handler
     * 
     * @see com.echomine.net.SocketHandler#start()
     */
    public void start() {
        shutdown = false;
        connected = false;
        streamCtx.reset();
        sessCtx.reset();
    }

    /*
     * shutdown the connection
     * 
     * @see com.echomine.net.SocketHandler#shutdown()
     */
    public void shutdown() {
        shutdown = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.net.SocketHandler#isConnected()
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Ends the stream due to either receiving an error from remote entity or
     * any error encountered here. The method will not flush or close the
     * underlying stream.
     */
    protected void endStream() throws IOException {
        streamCtx.getWriter().endTag(XMPPStreamWriter.IDX_JABBER_STREAM, "stream");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IConnectionHandler#getSessionContext()
     */
    public XMPPSessionContext getSessionContext() {
        return sessCtx;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IConnectionHandler#getStreamContext()
     */
    public XMPPStreamContext getStreamContext() {
        return streamCtx;
    }

}
