package com.echomine.xmpp.impl;

import java.io.IOException;

import com.echomine.net.ConnectionContext;
import com.echomine.net.ConnectionException;
import com.echomine.net.ConnectionListener;
import com.echomine.net.ConnectionVetoException;
import com.echomine.net.HandshakeableSocketConnector;
import com.echomine.xmpp.ISessionHandler;
import com.echomine.xmpp.IStanzaPacket;
import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.SendPacketFailedException;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;

/**
 * The primary default implementation for the API. It allows the user to
 * connect, login, and listen for incoming packets. This class is not normally
 * instantiated directly for use. Rather, use the XMPPConnectionFactory to
 * obtain XMPPConnections.
 */
public class XMPPConnectionImpl implements IXMPPConnection {
    HandshakeableSocketConnector conn;
    ISessionHandler handler;

    /**
     * The default constructor that most classes should use. It will use a
     * default connector.
     * 
     * @param conn
     */
    public XMPPConnectionImpl(ISessionHandler handler) {
        this(new HandshakeableSocketConnector(), handler);
    }

    /**
     * uses the connector and handler. This is really a method used to perform
     * unit testing.
     * 
     * @param conn the connector to use
     * @param handler the handler to use.
     */
    public XMPPConnectionImpl(HandshakeableSocketConnector conn, ISessionHandler handler) {
        this.conn = conn;
        this.handler = handler;
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
    public XMPPSessionContext connect(String host, int port, boolean wait) throws ConnectionException, ConnectionVetoException {
        try {
            ConnectionContext context = new ConnectionContext(host, port);
            if (wait) {
                conn.connectWithSynchStart(handler, context);
                return handler.getSessionContext();
            } else {
                conn.aconnect(handler, context);
                return null;
            }
        } catch (IOException ex) {
            throw new ConnectionException(ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#disconnect(boolean)
     */
    public void disconnect(boolean wait) {
        handler.shutdown();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#login(java.lang.String, char[],
     *      java.lang.String)
     */
    public void login(String username, char[] password, String resource) throws XMPPException {
        handler.authenticateSession(username, password, resource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#sendPacket(com.echomine.xmpp.IStanzaPacket)
     */
    public void sendPacket(IStanzaPacket packet) throws SendPacketFailedException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPConnection#sendSyncPacket(com.echomine.xmpp.IStanzaPacket)
     */
    public IStanzaPacket sendSyncPacket(IStanzaPacket packet) throws SendPacketFailedException {
        return null;
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

}
