package com.echomine.xmpp.impl;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPLoggableReader;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.net.ConnectionContext;
import com.echomine.net.HandshakeFailedException;
import com.echomine.net.HandshakeableSocketHandler;
import com.echomine.util.IOUtil;
import com.echomine.xmpp.IDGenerator;
import com.echomine.xmpp.IStanzaPacket;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.SendPacketFailedException;
import com.echomine.xmpp.StanzaPacketBase;
import com.echomine.xmpp.XMPPAuthCallback;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.XMPPStreamFactory;
import com.echomine.xmpp.packet.IQPacket;
import com.echomine.xmpp.packet.MessagePacket;
import com.echomine.xmpp.packet.PresencePacket;
import com.echomine.xmpp.packet.XMLTextPacket;

/**
 * The handler for working with the xmpp client connection. The handler will
 * actually delegate the work to Streams that handle all the incoming and
 * outgoing parsing. In addition, the handler will do automatic TLS negotation
 * if the remote entity supports it. It is on by default.
 */
public class XMPPConnectionHandler implements HandshakeableSocketHandler, XMPPConstants {
    private static final Log log = LogFactory.getLog(XMPPConnectionHandler.class);
    private static final String PRESENCE_ELEMENT_NAME = "presence";
    private static final String IQ_ELEMENT_NAME = "iq";
    private static final String MESSAGE_ELEMENT_NAME = "message";

    protected XMPPSessionContext sessCtx;
    protected boolean shutdown;
    protected boolean connected;
    protected XMPPStreamContext streamCtx;
    private IXMPPStream handshakeStream;
    private IXMPPStream tlsStream;
    private PacketQueue queue;
    private PacketListenerManager listenerManager;
    private boolean paused;
    private Socket mainSocket;

    /**
     * The constructor for the handler. It accepts a connection context to use
     * the data stored or to store any connection-related data.
     * 
     * @param listenerManager the listener manager to use
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
     * @param listenerManager the listener manager to use
     */
    public XMPPConnectionHandler(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) {
        this.sessCtx = sessCtx;
        this.streamCtx = streamCtx;
        this.queue = new PacketQueue(this);
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
        try {
            this.mainSocket = socket;
            socket.setKeepAlive(true);
            streamCtx.getWriter().setOutput(socket.getOutputStream());
            XMPPLoggableReader reader = new XMPPLoggableReader(socket.getInputStream(), "UTF-8");
            streamCtx.getUnmarshallingContext().setDocument(reader);
            streamCtx.setSocket(socket);
            streamCtx.setReader(reader);
            if (log.isDebugEnabled())
                log.debug("Starting Handshake with " + connCtx.getHostName());
            sessCtx.setHostName(connCtx.getHostName());
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
            connected = true;
        } catch (IOException ex) {
            throw new HandshakeFailedException("Socket error during handshake", ex);
        } catch (JiBXException ex) {
            throw new HandshakeFailedException("Jibx Exception occurred during handshake", ex);
        } catch (XMPPException ex) {
            throw new HandshakeFailedException("XMPP Exception occurred during handshake", ex);
        }
    }

    /*
     * The main handler method. It simply begins the incoming packet processing
     * mode. The code is also written so that the packet processing mode can be
     * paused and resumed. This is useful when login or other streaming
     * capabilities need to take over the stream data processing. <br/>The way
     * data is handled in this API is that there is only one thread processing
     * incoming messages, which is the thread that runs this handle method.
     * Whoever is sending the packet (ie. the packet router) SHOULD run it in a
     * separate thread. This way, for instance, the router will run its queue
     * off its own thread and allow asynchronicity. Incoming messages already
     * run in the current connection thread, so incoming message processing will
     * not be affected.
     * 
     * @see com.echomine.net.SocketHandler#handle(alt.java.net.Socket,
     *      com.echomine.net.ConnectionContext)
     */
    public void handle(Socket socket, ConnectionContext connCtx) throws IOException {
        UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
        // start reading incoming data packet through stream
        try {
            while (!shutdown) {
                if (paused)
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                            // intentionally left empty
                        }
                    }
                if (shutdown)
                    break;
                streamCtx.getReader().startLogging();
                // go into wait state if no data is incoming.
                uctx.next();
                IStanzaPacket packet = null;
                if (!paused) {
                    // parse incoming data
                    if (uctx.currentEvent() == UnmarshallingContext.END_DOCUMENT) {
                        break;
                    } else if (uctx.isEnd()) {
                        continue;
                    } else if (uctx.isAt(NS_XMPP_CLIENT, PRESENCE_ELEMENT_NAME)) {
                        packet = (IStanzaPacket) JiBXUtil.unmarshallObject(uctx, PresencePacket.class);
                    } else if (uctx.isAt(NS_XMPP_CLIENT, MESSAGE_ELEMENT_NAME)) {
                        packet = (IStanzaPacket) JiBXUtil.unmarshallObject(uctx, MessagePacket.class);
                    } else if (uctx.isAt(NS_XMPP_CLIENT, IQ_ELEMENT_NAME)) {
                        packet = (IStanzaPacket) JiBXUtil.unmarshallObject(uctx, IQPacket.class);
                    } else {
                        uctx.skipElement();
                        streamCtx.getReader().flushIgnoredDataToLog();
                    }
                    // match packets with those in queue in case any packets are
                    // waiting for replies
                    if (packet != null) {
                        queue.packetReceived(packet);
                        if (listenerManager != null)
                            listenerManager.firePacketReceived(packet);
                        streamCtx.getReader().flushLog();
                    }
                }
            }
        } catch (JiBXException ex) {
            // intentionally left empty
            if (log.isWarnEnabled())
                log.warn("Error during handling..", ex);
        } finally {
            // error reading incoming data (maybe connection closed)
            shutdown();
            endStream();
            IOUtil.closeSocket(streamCtx.getSocket());
        }
    }

    /**
     * This will queue a packet for later delivery. This should be the method of
     * choice when sending ALL packets. The sendPacket() is used by the queue.
     * If the packet extends from StanzaPacketBase, it will also set the ID of
     * the packet if one doesn't already exist.
     * 
     * @param packet the packet to send
     * @param wait whether to wait for a reply
     * @return the reply packet, or null if the wait is false
     * @throws SendPacketFailedException if waiting time expired before reply is
     *             received, or if IO Exception occurred, possibly due to
     *             shutdown
     */
    public IStanzaPacket queuePacket(IStanzaPacket packet, boolean wait) throws SendPacketFailedException {
        // set default ID if one isn't set
        if (packet.getId() == null && packet instanceof StanzaPacketBase)
            try {
                ((StanzaPacketBase) packet).setId(IDGenerator.nextID());
            } catch (XMPPException ex) {
                if (log.isWarnEnabled())
                    log.warn("Unable to generate packet ID.  Will not auto-set ID. You should check into cause", ex);
            }
        return queue.queuePacket(packet, wait);
    }

    /**
     * Sends a packet to the remote network, synchronously. This method is used
     * internally by the queue and should not be used by outside users. However,
     * it IS safe to use this method to immediately send a packet without going
     * through the queue. The method is synchronized to prevent overlapping
     * writes. This method also supports XMLTextPacket, and will output direct
     * xml text to the stream without going through any marshalling.
     * 
     * @param packet the packet to send
     * @see com.echomine.xmpp.packet.XMLTextPacket
     * @throws SendPacketFailedException if packet cannot be sent (connection
     *             closed, IO error, etc)
     */
    public synchronized void sendPacket(IStanzaPacket packet) throws SendPacketFailedException {
        try {
            // IQ Packets are marshalled differently
            if (packet instanceof IQPacket)
                JiBXUtil.marshallIQPacket(streamCtx.getWriter(), (IQPacket) packet);
            else if (packet instanceof XMLTextPacket)
                streamCtx.getWriter().writeMarkup(((XMLTextPacket) packet).getText());
            else
                JiBXUtil.marshallObject(streamCtx.getWriter(), packet);
            streamCtx.getWriter().flush();
        } catch (JiBXException ex) {
            throw new SendPacketFailedException(ex);
        } catch (IOException ex) {
            throw new SendPacketFailedException(ex);
        }
    }

    /**
     * This method will authenticate the session stream with the provided
     * information. Before authentication, there are only a few tasks that the
     * server can provide -- authenticate, stream negotation, and possibly
     * Jabber In-Band registration. The handler must be in a state to work with
     * these streams. Once the stream is authenticated, full stanza processing
     * can begin (asynchronous packet processing). What this means is that it is
     * still safe to not be processing random incoming packets since it is
     * assumed that the server will not send such packets before the session is
     * authenticated.
     * 
     * @param username the username
     * @param password the password
     * @param resource optional resource to bind to
     * @throws XMPPException
     */
    public void authenticateSession(String username, char[] password, String resource) throws XMPPException {
        pause();
        XMPPAuthCallback callback = new XMPPAuthCallback();
        callback.setUsername(username);
        callback.setPassword(password);
        callback.setResource(resource);
        streamCtx.setAuthCallback(callback);
        // see if SASL feature is supported
        IXMPPStream stream = XMPPStreamFactory.getFactory().createStream(XMPPConstants.NS_STREAM_SASL);
        if (streamCtx.getFeatures().isSaslSupported() && stream != null) {
            stream.process(sessCtx, streamCtx);
            handshakeStream.process(sessCtx, streamCtx);
            // must do resource binding if supported
            if (streamCtx.getFeatures().isBindingSupported()) {
                stream = XMPPStreamFactory.getFactory().createStream(XMPPConstants.NS_STREAM_BINDING);
                stream.process(sessCtx, streamCtx);
            }
            // must do session if supported
            if (streamCtx.getFeatures().isSessionSupported()) {
                stream = XMPPStreamFactory.getFactory().createStream(XMPPConstants.NS_STREAM_SESSION);
                stream.process(sessCtx, streamCtx);
            }
            resume();
            return;
        }
        resume();
        throw new XMPPException("No proper authentication method found.");
    }

    /*
     * Resets the data before a connection begins for reusing the handler
     * 
     * @see com.echomine.net.SocketHandler#start()
     */
    public void start() {
        shutdown = false;
        connected = false;
        paused = false;
        streamCtx.reset();
        sessCtx.reset();
        queue.start();
    }

    /*
     * shutdown the connection
     * 
     * @see com.echomine.net.SocketHandler#shutdown()
     */
    public void shutdown() {
        shutdown = true;
        // must physically shutdown input stream in order to release
        // the unmarshalling context's parser wait status
        try {
            if (mainSocket != null)
                IOUtil.closeStream(mainSocket.getInputStream());
        } catch (IOException ex) {
            // intentionally left empty
        }
        // to release
        resume();
        queue.stop();
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
     * Retrieves the context used by the handler containing any session specific
     * information.
     * 
     * @return the session context.
     */
    public XMPPSessionContext getSessionContext() {
        return sessCtx;
    }

    /**
     * Retrieves the stream context associated with the handler containing
     * current stream-level details
     */
    public XMPPStreamContext getStreamContext() {
        return streamCtx;
    }

    /**
     * Retrieves the packet listener manager associated with this handler.
     * 
     * @return the listener manager
     */
    public PacketListenerManager getPacketListenerManager() {
        return listenerManager;
    }

    /**
     * sets the packet listener manager
     * 
     * @param lmanager the listener manager
     */
    public void setPacketListenerManager(PacketListenerManager lmanager) {
        this.listenerManager = lmanager;
    }

    /**
     * pauses all processing of incoming packets. This is normally used to
     * indicate that some stream wishes to take over the stream processing for
     * serialized processing.
     */
    protected synchronized void pause() {
        paused = true;
    }

    /**
     * This is called to resume processing of incoming packets after a pause.
     */
    protected synchronized void resume() {
        if (!paused)
            return;
        paused = false;
        synchronized (this) {
            notify();
        }
    }

    /**
     * Ends the stream due to either receiving an error from remote entity or
     * any error encountered here. The method will not flush or close the
     * underlying stream.
     */
    protected void endStream() {
        try {
            streamCtx.getWriter().endTag(XMPPStreamWriter.IDX_JABBER_STREAM, "stream");
            streamCtx.getWriter().flush();
        } catch (IOException ex) {
            // intentionally left empty
        }
    }
}
