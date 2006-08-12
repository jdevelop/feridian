package com.echomine.net;

import java.io.IOException;
import java.net.Socket;

/**
 * <p>
 * Accepts connections indefinitely. Once a connection is accepted, it is
 * immediately handled in a spawned thread. The acceptor will go back to
 * receiving more connections.
 * </p>
 * <p>
 * The socket acceptor work similar to PerpetualSocketAcceptor
 * </p>
 * 
 * @see PerpetualSocketAcceptor
 */
public class HandshakeablePerpetualSocketAcceptor extends PerpetualSocketAcceptor {
    public HandshakeablePerpetualSocketAcceptor() {
        super();
    }

    public HandshakeablePerpetualSocketAcceptor(ConnectionContext context) throws IOException {
        super(context);
    }

    public HandshakeablePerpetualSocketAcceptor(ConnectionContext context, int backlog) throws IOException {
        super(context, backlog);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.net.SocketAcceptor#establishingConnection(java.net.Socket,
     *      com.echomine.net.SocketHandler, com.echomine.net.ConnectionContext)
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
