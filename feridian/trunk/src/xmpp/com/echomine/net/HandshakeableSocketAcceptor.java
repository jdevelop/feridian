package com.echomine.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.echomine.util.IOUtil;

/**
 * <p>
 * This acceptor works similar to SocketAcceptor except for the addition of
 * adding handshaking capability.
 * </p>
 * 
 * @see SocketAcceptor
 */
public class HandshakeableSocketAcceptor extends TimeableConnection {
    protected ServerSocket socket;
    private ConnectionModel connectionModel;

    public HandshakeableSocketAcceptor() {
    }

    public HandshakeableSocketAcceptor(ConnectionModel model) throws IOException {
        this(model, 20);
    }

    public HandshakeableSocketAcceptor(ConnectionModel model, int backlog) throws IOException {
        this.connectionModel = model;
        this.open(backlog);
    }

    public void open(int backlog) throws IOException {
        if (connectionModel.getHost() == null)
            socket = new ServerSocket(connectionModel.getPort(), backlog);
        else
            socket = new ServerSocket(connectionModel.getPort(), backlog, connectionModel.getHost());
    }

    /**
     * <p>
     * Accepts a connection synchronously and hands it over to the handler for
     * processing. The caller of this method will wait until data processing is
     * finished before regaining control. This is meant for occassions such as
     * HTTP requests where retrieval of data occurs once and data is returned
     * for further processing.
     * </p>
     * <p>
     * Notice that the server socket is NOT closed after handler returns. The
     * connection should be closed explicitly by the application or inside the
     * handler.
     * </p>
     */
    public void accept(HandshakeableSocketHandler socketHandler) {
        Socket s;
        try {
            s = socket.accept();
            ConnectionEvent e = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_STARTING);
            ConnectionEvent vetoEvent = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_VETOED);
            try {
                socketHandler.start();
                fireConnectionStarting(e, vetoEvent);
                e = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_OPENED);
                socketHandler.handshake(s);
                fireConnectionEstablished(e);
                socketHandler.handle(s);
                e = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_CLOSED);
                fireConnectionClosed(e);
            } catch (ConnectionVetoException ex) {
                // do nothing as connection closed event is already fired
            } catch (HandshakeFailedException ex) {
                // connection handshaking problem, fire connection closed
                e = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error during handshaking: " + ex.getMessage());
                fireConnectionClosed(e);
            } catch (IOException ex) {
                e = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error while handling connection: " + ex.getMessage());
                fireConnectionClosed(e);
            } finally {
                IOUtil.closeSocket(s);
            }
        } catch (IOException ex) {
            // doesn't fire anything since what happens here
            // is that someone closed the server socket for us
        }
    }

    /**
     * <p>
     * The asynchronous version of the accept method. The connection is passed
     * to a thread for execution. The server socket is not automatically closed
     * at the end. You must explicitly close the server socket when you're done.
     * Failure to do so will result in a port that's binded until the end of the
     * application.
     * </p>
     */
    public void aaccept(final HandshakeableSocketHandler socketHandler) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Socket s;
                try {
                    s = socket.accept();
                    ConnectionEvent e = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_STARTING);
                    ConnectionEvent vetoEvent = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_VETOED);
                    try {
                        socketHandler.start();
                        fireConnectionStarting(e, vetoEvent);
                        e = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_OPENED);
                        socketHandler.handshake(s);
                        fireConnectionEstablished(e);
                        socketHandler.handle(s);
                        e = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_CLOSED);
                        fireConnectionClosed(e);
                    } catch (HandshakeFailedException ex) {
                        // connection handshaking problem, fire connection
                        // closed
                        e = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error during handshaking: " + ex.getMessage());
                        fireConnectionClosed(e);
                    } catch (IOException ex) {
                        e = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error while handling connection: " + ex.getMessage());
                        fireConnectionClosed(e);
                    } catch (ConnectionVetoException ex) {
                        // do nothing as connection closed event is already
                        // fired
                    } finally {
                        IOUtil.closeSocket(s);
                    }
                } catch (IOException ex) {
                    // doesn't fire anything since what happens here
                    // is that someone closed the server socket for us
                }
            }
        });
        thread.start();
    }

    /**
     * </p>
     * Closes the server socket and unbind from the port. The close does NOT
     * close all the currently accepted (and processing) handlers. It only close
     * the server socket so that additional connections are refused. It is up to
     * you to keep track of the handlers and also close them if you want all
     * connections to be shutdown.
     * </p>
     */
    public void close() {
        try {
            // unbind the server socket
            if (socket != null)
                socket.close();
        } catch (IOException ex) {
        }
    }

    public ConnectionModel getConnectionModel() {
        return connectionModel;
    }

    /**
     * <p>
     * use a new connection model. The current server socket will be closed and
     * then a new server socket will be created. Once a connection model is
     * passed in through the constructor, it's not usually changed. However, the
     * Acceptor can be reused in this way if the situation requires it.
     * </p>
     */
    public void setConnectionModel(ConnectionModel connectionModel) throws IOException {
        setConnectionModel(connectionModel, 20);
    }

    /**
     * sets the connection model plus the backlog for the port that the listener
     * should bind to and listen on.
     */
    public void setConnectionModel(ConnectionModel connectionModel, int backlog) throws IOException {
        this.connectionModel = connectionModel;
        // close the current server socket
        close();
        // open with backlog of 20
        open(backlog);
    }
}
