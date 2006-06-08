package com.echomine.net;

import java.io.IOException;
import java.net.Socket;

/**
 * <p>
 * This acceptor works similar to SocketAcceptor except for the addition of
 * adding handshaking capability.
 * </p>
 * 
 * @see SocketAcceptor
 */
public class HandshakeableSocketAcceptor extends SocketAcceptor {
    public HandshakeableSocketAcceptor() {
        super();
    }

    public HandshakeableSocketAcceptor(ConnectionContext context) throws IOException {
        this(context, 20);
    }

    public HandshakeableSocketAcceptor(ConnectionContext context, int backlog) throws IOException {
        super(context, backlog);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.net.SocketAcceptor#establishingConnection()
     */
    protected void establishingConnection(Socket socket, SocketHandler socketHandler, ConnectionContext connectionCtx) throws IOException, ConnectionException {
        if (socketHandler instanceof HandshakeableSocketHandler) {
            socketHandler.handle(socket, connectionCtx);
            ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_OPENED);
            fireConnectionEstablished(e);
            return;
        }
        super.establishingConnection(socket, socketHandler, connectionCtx);
    }
}
