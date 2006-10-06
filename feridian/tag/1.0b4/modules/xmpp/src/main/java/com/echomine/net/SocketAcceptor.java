package com.echomine.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.echomine.util.IOUtil;

/**
 * <p>
 * Default Acceptor class for TCP protocols. It contains event firing methods
 * necessary for status observation. The code contains both synchronous and
 * asynchronous acceptance of socket code. Use the accept method for synchronous
 * accepts and the aaccept method for asynchronous accepts (control returns
 * immediately to caller).
 * </p>
 * <p>
 * The Connection Ctx basically contains information for creating a new server
 * socket. If the host returned is null, then acceptor will bind to all
 * interfaces. Otherwise, the acceptor will bind only to the specified interface
 * IP.
 * </p>
 * <p>
 * A side note regarding acceptor handlers. Usually acceptor handlers are
 * multi-threaded safe and contains no per-connection instance variables. The
 * handlers can usually create another object that keeps per-connection data.
 */
public class SocketAcceptor extends TimeableConnection {
    protected ServerSocket socket;
    private ConnectionContext connectionCtx;

    public SocketAcceptor() {
    }

    public SocketAcceptor(ConnectionContext context) throws IOException {
        this(context, 20);
    }

    public SocketAcceptor(ConnectionContext context, int backlog) throws IOException {
        this.connectionCtx = context;
        this.open(backlog);
    }

    public void open(int backlog) throws IOException {
        if (connectionCtx.getHost() == null)
            socket = new ServerSocket(connectionCtx.getPort(), backlog);
        else
            socket = new ServerSocket(connectionCtx.getPort(), backlog, connectionCtx.getHost());
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
    public void accept(SocketHandler socketHandler) {
        Socket s = null;
        try {
            s = socket.accept();
            try {
                startingConnection(socketHandler, connectionCtx);
                establishingConnection(s, socketHandler, connectionCtx);
                handleConnection(s, socketHandler, connectionCtx);
            } catch (IOException ex) {
                // handle threw the exception, fire connection closed
                ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_ERRORED, "Error during handling: " + ex.getMessage());
                fireConnectionClosed(e);
            } catch (ConnectionException ex) {
                ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_ERRORED, "Error during handling: " + ex.getMessage());
                fireConnectionClosed(e);
            } catch (ConnectionVetoException ex) {
                // do nothing as connection closed event is already fired
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
     * 
     * @param threadName optional name of thread
     */
    public void aaccept(final SocketHandler socketHandler, String threadName) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Socket s = null;
                try {
                    s = socket.accept();
                    try {
                        startingConnection(socketHandler, connectionCtx);
                        establishingConnection(s, socketHandler, connectionCtx);
                        handleConnection(s, socketHandler, connectionCtx);
                    } catch (IOException ex) {
                        // handle threw the exception, fire connection closed
                        ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_ERRORED, "Error during handling: " + ex.getMessage());
                        fireConnectionClosed(e);
                    } catch (ConnectionException ex) {
                        ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_ERRORED, "Error during handling: " + ex.getMessage());
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
        if (threadName != null)
            thread.setName(threadName);
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

    public ConnectionContext getConnectionContext() {
        return connectionCtx;
    }

    /**
     * <p>
     * use a new connection context. The current server socket will be closed
     * and then a new server socket will be created. Once a connection context
     * is passed in through the constructor, it's not usually changed. However,
     * the Acceptor can be reused in this way if the situation requires it.
     * </p>
     */
    public void setConnectionContext(ConnectionContext connectionCtx) throws IOException {
        setConnectionContext(connectionCtx, 20);
    }

    /**
     * sets the connection context plus the backlog for the port that the
     * listener should bind to and listen on.
     */
    public void setConnectionContext(ConnectionContext connectionCtx, int backlog) throws IOException {
        this.connectionCtx = connectionCtx;
        // close the current server socket
        close();
        // open with backlog of 20
        open(backlog);
    }

    /**
     * starts the connection. This does not in any way deal with any connecting
     * or establishing, which is handled in another method.
     * 
     * @param socketHandler the handler
     * @throws ConnectionVetoException
     */
    protected void startingConnection(SocketHandler socketHandler, ConnectionContext connectionCtx) throws ConnectionVetoException {
        ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_STARTING);
        ConnectionEvent vetoEvent = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_VETOED);
        socketHandler.start();
        fireConnectionStarting(e, vetoEvent);
    }

    /**
     * establishes the connection. This involves connecting to remote and
     * performing handshakes.
     */
    protected void establishingConnection(Socket socket, SocketHandler socketHandler, ConnectionContext connectionCtx) throws IOException, ConnectionException {
        ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_OPENED);
        fireConnectionEstablished(e);
    }

    /**
     * handles the main meat of the connection processing.
     * 
     * @param socketHandler the handler
     * @param s the socket
     * @throws IOException
     */
    protected void handleConnection(Socket s, SocketHandler socketHandler, ConnectionContext connectionCtx) throws IOException {
        socketHandler.handle(s, connectionCtx);
        ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_CLOSED);
        fireConnectionClosed(e);
    }
}
