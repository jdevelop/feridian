package com.echomine.xmpp;

import com.echomine.net.ConnectionException;
import com.echomine.net.ConnectionFailedException;
import com.echomine.net.ConnectionListener;
import com.echomine.net.ConnectionVetoException;
import com.echomine.net.HandshakeFailedException;

/**
 * This is the main connection to an XMPP-compliant entity. It allows one to
 * connect, send packets, and receive packets. The connection does not provide
 * services to login, register, etc. Those are delegated to Services
 */
public interface IXMPPConnection {
    static final int DEFAULT_XMPP_PORT = 5222;

    /**
     * Logs the user into the system. The implementator will do all the required
     * XMPP authentication procedure. This means that if SASL is
     * available/supported, then it will be used. Afterwards, if resource
     * binding and session feature is supported, then it will be used. If none
     * of these features are available or supported, then the implementator may
     * possibly resort to older Jabber-compatible authentication if iq-auth
     * feature is available. No matter which method is used, this method
     * encapsulates the login procedure. <br/>The method will execute
     * SYNCHRONOUSLY, meaning that the method will not return until either an
     * exception occurs, login success, or login failure.
     * 
     * @param username the username
     * @param password password
     * @param resource optional resource name to bind to. Null to request
     *            dynamic resource binding if available
     * @throws SendPacketFailedException if packet cannot be sent (subclass of
     *             XMPPException)
     * @throws XMPPException if any exception occurs
     */
    void login(String username, char[] password, String resource) throws XMPPException;

    /**
     * Sends a packet to the remote entity asynchronously. Implementators must
     * queue packets until connection is established, then send those packets
     * out. If that is not possible, then exception must be thrown to indicate
     * that the packet cannot be sent. <br/>This method also can send a packet
     * and wait for a reply. This allows the user to perform synchronous
     * actions. The method waits for a reply for the packet sent. If no reply is
     * receivied within a specified timeout period, an exception is thrown. The
     * timeout is specified in the packet, per packet. NOTE: If the reply packet
     * is an error packet, then exception will be thrown instead of returning
     * the reply packet. This should allow a better flow control for users who
     * are using this method to perform a series of sequentially-based packet
     * interaction. The exception will contain the error packet data.
     * 
     * @param packet the packet to send
     * @param wait true if to wait for a reply packet, false to return
     *            immediately
     * @return the reply packet
     * @throws SendPacketFailedException if message cannot be sent, timeout
     *             occurred, or reply is an error packet.
     */
    IStanzaPacket sendPacket(IStanzaPacket packet, boolean wait) throws SendPacketFailedException;

    /**
     * Checks whether the we are connected.
     * 
     * @return true if connected, false otherwise
     */
    boolean isConnected();

    /**
     * <p>
     * connect to remote connection. This method will do any initial
     * handshaking, such as sending initial XMPP handshake and receiving the
     * server's initial handshake. This may include TLS or stream compression
     * negotiation if the remote entity supports it. Thus, if TLS is supported,
     * the handshake implementation might negotiate for TLS before returning
     * control. It will not do any authentication of any kind. The method
     * supports waiting for connection to establish before returning. This
     * allows synchronous coding. Asynchronous connection will not wait for
     * connection to establish and will return immediately. Thus, if you use
     * asynchronous connect, then do not rely that the connection is established
     * rigth after control is returned to your main code. All connection events
     * are subsequently fired to connection listeners. Note that this method
     * will only block until connection is established; it does not block until
     * the connection is disconnected.
     * </p>
     * <p>
     * If wait is true, then the return value will always be null since the
     * session context will not be available (connection might not have been
     * established yet).
     * </p>
     * 
     * @param host the host name to connect to
     * @param port the port number to connect to
     * @param wait true to wait for connection status before returning, false to
     *            return immediately
     * @return the session context IF using a waiting connect, otherwise null.
     * @throws ConnectionVetoException If connection was vetoed by connection
     *             listeners
     * @throws ConnectionFailedException if connection fails
     * @throws HandshakeFailedException if handshake failed
     */
    XMPPSessionContext connect(String host, int port, boolean wait) throws ConnectionException, ConnectionVetoException;

    /**
     * Disconnect from the remote entity. Before disconnecting, it is
     * recommended that all pending packets be processed (outgoing packets are
     * sent out and incoming packets are fired as events).
     */
    void disconnect();

    /**
     * adds a listener to listen for connection starting/establishing/closing
     * events. When connection closes, the listener will NOT be removed. Thus,
     * the listener can be used for subsequent connections. However, if you do
     * not need it anymore, you must manually remove the listener or memory
     * leaks might occur.
     * 
     * @param listener the listener to add
     */
    void addConnectionListener(ConnectionListener listener);

    /**
     * Removes the connection listener
     * 
     * @param listener the listener to remove
     */
    void removeConnectionListener(ConnectionListener listener);

    /**
     * adds a listener to listen for incoming packets. When connection closes,
     * the listener will NOT be removed. Thus, the listener can be used for
     * subsequent connections. However, if you do not need it anymore, you must
     * manually remove the listener or memory leaks may occur.
     * 
     * @param listener the lsitener to add
     */
    void addPacketListener(IPacketListener listener);

    /**
     * removes the packet listener
     * 
     * @param listener the listener to remove
     */
    void removePacketListener(IPacketListener listener);
}
