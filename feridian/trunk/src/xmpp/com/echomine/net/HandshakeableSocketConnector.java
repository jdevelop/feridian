package com.echomine.net;

import java.io.IOException;
import java.net.Socket;

/**
 * <p>
 * This class works similar to SocketConnector to add in handshaking capability.
 * The event processing is as follows: connection starting, handshake called,
 * connection established, handle called, connection closed.
 * </p>
 * 
 * @see SocketConnector
 */
public class HandshakeableSocketConnector extends SocketConnector {
    /**
     * Do-nothing constructor. Usually used for multi-threading reuse of this
     * instance since the Handler can be passed in as a parameter for connect().
     */
    public HandshakeableSocketConnector() {
        super();
    }

    public HandshakeableSocketConnector(HandshakeableSocketHandler socketHandler) {
        super(socketHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.net.SocketConnector#establishingConnection(com.echomine.net.SocketHandler,
     *      com.echomine.net.ConnectionContext)
     */
    protected Socket establishingConnection(SocketHandler socketHandler, ConnectionContext connectionCtx) throws IOException, ConnectionException {
        if (socketHandler instanceof HandshakeableSocketHandler) {
            Socket socket = null;
            if (connectionCtx.isSSL()) {
                socket = createSSLSocket(connectionCtx);
            } else {
                socket = createSocket(connectionCtx.getHost(), connectionCtx.getPort());
            }
            ConnectionEvent event = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_OPENED);
            ((HandshakeableSocketHandler) socketHandler).handshake(socket, connectionCtx);
            fireConnectionEstablished(event);
            return socket;
        }
        return super.establishingConnection(socketHandler, connectionCtx);
    }
}
