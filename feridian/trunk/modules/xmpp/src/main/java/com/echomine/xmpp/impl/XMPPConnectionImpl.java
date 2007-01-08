package com.echomine.xmpp.impl;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.echomine.feridian.FeridianConfiguration;
import com.echomine.net.ConnectionException;
import com.echomine.net.ConnectionListener;
import com.echomine.net.ConnectionVetoException;
import com.echomine.net.HandshakeableSocketConnector;
import com.echomine.net.XMPPConnectionContext;
import com.echomine.xmpp.IPacketListener;
import com.echomine.xmpp.IStanzaPacket;
import com.echomine.xmpp.IXMPPAuthenticator;
import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.SendPacketFailedException;
import com.echomine.xmpp.XMPPAuthCallback;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.XMPPStreamFactory;

/**
 * The primary default implementation for the API. It allows the user to
 * connect, login, and listen for incoming packets. This class is not normally
 * instantiated directly for use. Rather, use the XMPPConnectionFactory to
 * obtain XMPPConnections.
 */
public class XMPPConnectionImpl implements IXMPPConnection {
    private static final Log log = LogFactory.getLog(XMPPConnectionImpl.class);

    private HandshakeableSocketConnector conn;

    private XMPPConnectionHandler handler;

    private PacketListenerManager listenerManager;

    /**
     * The default constructor that most classes should use. It will use all
     * default objects.
     */
    public XMPPConnectionImpl() {
        this(new XMPPConnectionHandler());
    }

    /**
     * uses the specified alternate objects. This is usually used when an
     * alternate handler class is preferred (ie. user overrides the default
     * implementation and then specified a factory that instantiates the
     * alternative handler).
     * 
     * @param handler the handler to use.
     */
    public XMPPConnectionImpl(XMPPConnectionHandler handler) {
        this(new HandshakeableSocketConnector(), handler);
    }

    /**
     * uses the specified alternate objects. This is really a method used to
     * perform unit testing.
     * 
     * @param conn the connector to use
     * @param handler the handler to use.
     */
    public XMPPConnectionImpl(HandshakeableSocketConnector conn,
            XMPPConnectionHandler handler) {
        this.conn = conn;
        this.handler = handler;
        this.listenerManager = new PacketListenerManager(this);
        handler.setPacketListenerManager(listenerManager);
    }

    /**
     * Checks whether the we are connected.
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return handler.isConnected();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#connect(java.lang.String, int)
     */
    public XMPPSessionContext connect(String host, int port, boolean wait)
            throws ConnectionException, ConnectionVetoException {
        return connect(host, port, host, wait);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#connect(java.lang.String, int,
     *      java.lang.String, boolean)
     */
    public XMPPSessionContext connect(String host, int port, String domain, boolean wait)
            throws ConnectionException, ConnectionVetoException {
        try {
            XMPPConnectionContext context = new XMPPConnectionContext(host, port);
            context.setDomain(domain);
            if (wait) {
                conn.connectWithSynchStart(handler, context, "Feridian - "
                        + host);
                return handler.getSessionContext();
            } else {
                conn.aconnect(handler, context, "Feridian - " + host);
                return null;
            }
        } catch (IOException ex) {
            throw new ConnectionException(ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#disconnect()
     */
    public void disconnect() {
        handler.shutdown();
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
     * authenticated. Also, this login method actually searches through a list
     * of registered authenticators and use the first one that indicates its
     * ability to authenticate the stream.
     * 
     * @param username the username
     * @param password the password
     * @param resource optional resource to bind to
     * @throws XMPPErrorStanzaException if login process sent error reply (ie.
     *         selected resource not available, unable to create session)
     * @throws SendPacketFailedException if packet cannot be sent
     */
    public void login(String username, char[] password, String resource)
            throws XMPPException {
        XMPPAuthCallback callback = new XMPPAuthCallback();
        callback.setUsername(username);
        callback.setPassword(password);
        callback.setResource(resource);
        XMPPStreamContext streamCtx = handler.getStreamContext();
        streamCtx.setAuthCallback(callback);
        Iterator<IXMPPAuthenticator> iter = FeridianConfiguration.getConfig().getAuthenticators().iterator();
        IXMPPAuthenticator auth = null;
        while (iter.hasNext()) {
            IXMPPAuthenticator tauth = (IXMPPAuthenticator) iter.next();
            if (tauth.canAuthenticate(handler.getSessionContext(), handler.getStreamContext())) {
                auth = tauth;
                break;
            }
        }
        if (auth == null)
            throw new XMPPException("No proper authenticator method found.");
        // now authenticate
        if (log.isDebugEnabled())
            log.debug("Authenticating using the following authenticator: "
                    + auth.getClass().getName());
        handler.processStream(auth, auth.redoHandshake());
        // now check if binding and session features are supported
        // if so, binding and session negotiation must be done
        IXMPPStream stream;
        if (streamCtx.getFeatures().isBindingSupported()) {
            stream = XMPPStreamFactory.getFactory().createStream(XMPPConstants.NS_STREAM_BINDING);
            handler.processStream(stream, false);
        }
        if (streamCtx.getFeatures().isSessionSupported()) {
            stream = XMPPStreamFactory.getFactory().createStream(XMPPConstants.NS_STREAM_SESSION);
            handler.processStream(stream, false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#sendPacket(com.echomine.xmpp.IStanzaPacket)
     */
    public IStanzaPacket sendPacket(IStanzaPacket packet, boolean wait)
            throws SendPacketFailedException {
        return handler.queuePacket(packet, wait);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#addConnectionListener(com.echomine.net.ConnectionListener)
     */
    public void addConnectionListener(ConnectionListener listener) {
        conn.addConnectionListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#removeConnectionListener(com.echomine.net.ConnectionListener)
     */
    public void removeConnectionListener(ConnectionListener listener) {
        conn.removeConnectionListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#addPacketListener(com.echomine.xmpp.IPacketListener)
     */
    public void addPacketListener(IPacketListener listener) {
        listenerManager.addPacketListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#removePacketListener(com.echomine.xmpp.IPacketListener)
     */
    public void removePacketListener(IPacketListener listener) {
        listenerManager.removePacketListener(listener);
    }

}
