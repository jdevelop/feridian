package com.echomine.net;

import java.io.IOException;
import java.net.Socket;

import com.echomine.util.IOUtil;

/**
 * <p>
 * This class works similar to SocketConnector to add in handshaking capability.
 * The event processing is as follows: connection starting, handshake called,
 * connection established, handle called, connection closed.
 * </p>
 * 
 * @see SocketConnector
 */
public class HandshakeableSocketConnector extends TimeableConnection {
    private HandshakeableSocketHandler socketHandler;

    /**
     * Do-nothing constructor. Usually used for multi-threading reuse of this
     * instance since the Handler can be passed in as a parameter for connect().
     */
    public HandshakeableSocketConnector() {
    }

    public HandshakeableSocketConnector(HandshakeableSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    /**
     * Synchronous connect method using internal socket handler. The method will
     * return when handling of the connection is finished.
     */
    public void connect(ConnectionModel connectionModel) throws ConnectionFailedException {
        connect(socketHandler, connectionModel);
    }

    /**
     * Synchronous connect method. The method will return when handling of the
     * connection is finished. This method will fire connection events at the
     * appropriate time and call the handshake method at the appropriate time.
     */
    public void connect(HandshakeableSocketHandler socketHandler, ConnectionModel connectionModel) throws ConnectionFailedException {
        try {
            ConnectionEvent event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_STARTING);
            ConnectionEvent vetoEvent = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_VETOED);
            socketHandler.start();
            fireConnectionStarting(event, vetoEvent);
            Socket socket = new Socket(connectionModel.getHost(), connectionModel.getPort());
            try {
                event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_OPENED);
                socketHandler.handshake(socket);
                fireConnectionEstablished(event);
                socketHandler.handle(socket);
                event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_CLOSED);
                fireConnectionClosed(event);
            } catch (HandshakeFailedException ex) {
                event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error during handshaking: " + ex.getMessage());
                fireConnectionClosed(event);
                throw new ConnectionFailedException("Error during handshaking");
            } catch (IOException ex) {
                event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error while handling connection: " + ex.getMessage());
                fireConnectionClosed(event);
                throw new ConnectionFailedException("Error while handling connection");
            } finally {
                IOUtil.closeSocket(socket);
            }
        } catch (ConnectionVetoException ex) {
            // do nothing, connection closed event already fired
        } catch (IOException ex) {
            // error connecting the socket
            ConnectionEvent event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error..." + ex.getMessage());
            fireConnectionClosed(event);
            throw new ConnectionFailedException("Cannot Connect to remote host");
        }
    }

    /**
     * makes a connection asynchronously using internal socket handler. This
     * means that the method will be run in a separate thread and return control
     * to the caller of the method immediately.
     */
    public void aconnect(ConnectionModel connectionModel) {
        aconnect(socketHandler, connectionModel);
    }

    /**
     * makes a connection asynchronously. This means that the method will be run
     * in a separate thread and return control to the caller of the method
     * immediately. This method will fire connection events at the appropriate
     * time and call the handshake method at the appropriate time.
     */
    public void aconnect(final HandshakeableSocketHandler socketHandler, final ConnectionModel connectionModel) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    ConnectionEvent event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_STARTING);
                    ConnectionEvent vetoEvent = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_VETOED);
                    socketHandler.start();
                    fireConnectionStarting(event, vetoEvent);
                    Socket socket = new Socket(connectionModel.getHost(), connectionModel.getPort());
                    try {
                        event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_OPENED);
                        socketHandler.handshake(socket);
                        fireConnectionEstablished(event);
                        socketHandler.handle(socket);
                        event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_CLOSED);
                        fireConnectionClosed(event);
                    } catch (HandshakeFailedException ex) {
                        event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error during handshaking: " + ex.getMessage());
                        fireConnectionClosed(event);
                    } catch (IOException ex) {
                        event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error while handling connection: " + ex.getMessage());
                        fireConnectionClosed(event);
                    } finally {
                        IOUtil.closeSocket(socket);
                    }
                } catch (IOException ex) {
                    // error connecting
                    ConnectionEvent event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error connecting to host: " + ex.getMessage());
                    fireConnectionClosed(event);
                } catch (ConnectionVetoException ex) {
                    // do nothing, connection closed event already fired
                }
            }
        });
        thread.start();
    }

    /** @return the socket handler associated with the connection */
    public HandshakeableSocketHandler getSocketHandler() {
        return socketHandler;
    }

    /**
     * override parent to check and make sure the handler is a
     * HandshakeableSocketHandler.
     * 
     * @throws IllegalArgumentException if socket handler does not implement the
     *             right handler
     */
    public void setSocketHandler(HandshakeableSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }
}
