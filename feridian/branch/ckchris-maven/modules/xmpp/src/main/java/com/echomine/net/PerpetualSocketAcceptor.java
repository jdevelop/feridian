package com.echomine.net;

import java.io.IOException;
import java.net.Socket;

import com.echomine.util.IOUtil;

/**
 * <p>
 * Accepts connections indefinitely. Once a connection is accepted, it is
 * immediately handled in a spawned thread. The acceptor will go back to
 * receiving more connections.
 * </p>
 * <p>
 * The acceptor offers both a synchronous and asynchronous version of accept.
 * Running perpetually, this means that (1) synchronous accept will accept and
 * handle one connection at a time and (2) asynchronous accept will accept, hand
 * it to a handler in a different thread, then go back to accept more
 * connections (ie. a daemon). Depending on which suits your situation, use the
 * appropriate method.
 * </p>
 * <p>
 * Perpetual Acceptor also support firiing off of connection events.
 * </p>
 * <p>
 * NOTE: Using this class requires that the handler is multi-threading safe if
 * asynchronous accept is used (doesn't apply to synchronous accept). This means
 * for async accepts, the handler cannot contain any per-connection data.
 * Otherwise, you'll get into data corruption because of multi-threading issues.
 * </p>
 */
public class PerpetualSocketAcceptor extends SocketAcceptor {
    private boolean shutdown;

    public PerpetualSocketAcceptor() {
        super();
    }

    public PerpetualSocketAcceptor(ConnectionContext context) throws IOException {
        super(context);
    }

    public PerpetualSocketAcceptor(ConnectionContext context, int backlog) throws IOException {
        super(context, backlog);
    }

    /**
     * <p>
     * Accepts a connection and hands it over to the handler for processing one
     * at a time.
     * </p>
     * <p>
     * To stop the daemon from accepting more connections, just call the close
     * method.
     * </p>
     * <p>
     * The method will return immediately and run the daemon in a background
     * thread.
     * </p>
     * 
     * @param socketHandler the socket handler to use
     * @param threadName optional thread name for the socket acceptor
     */
    public void accept(final SocketHandler socketHandler, String threadName) {
        shutdown = false;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Socket s = null;
                    while (!shutdown) {
                        s = socket.accept();
                        ConnectionContext connectionCtx = new ConnectionContext(s.getInetAddress(), s.getPort());
                        try {
                            startingConnection(socketHandler, connectionCtx);
                            establishingConnection(s, socketHandler, connectionCtx);
                            handleConnection(s, socketHandler, connectionCtx);
                        } catch (IOException ex) {
                            // handle threw exception, fire closed event
                            ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_ERRORED, "Error while handling connection: " + ex.getMessage());
                            fireConnectionClosed(e);
                        } catch (ConnectionException ex) {
                            ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_ERRORED, "Error during handling: " + ex.getMessage());
                            fireConnectionClosed(e);
                        } catch (ConnectionVetoException ex) {
                            // do nothing because connection closed is already
                            // called
                        } finally {
                            IOUtil.closeSocket(s);
                        }
                    }
                } catch (IOException ex) {
                }
            }
        });
        if (threadName != null)
            thread.setName(threadName);
        thread.start();
    }

    /**
     * <p>
     * Accepts a connection,hands it over to the handler for processing, and
     * immediately goes back to accepting more connections. This differs from
     * the regular accept method in that it does not process connections one at
     * a time.
     * </p>
     * <p>
     * The async accept is a little more intricate than the sync accept since it
     * can't just use SocketAcceptor.aaccept().
     * </p>
     * <p>
     * The method returns immediately and runs the daemon in a background
     * thread.
     * </p>
     * 
     * @param threadName optional name of thread
     */
    public void aaccept(final SocketHandler socketHandler, String threadName) {
        shutdown = false;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Socket s;
                    while (!shutdown) {
                        s = socket.accept();
                        ConnectionContext context = new ConnectionContext(s.getInetAddress(), s.getPort());
                        AcceptorThread athread = new AcceptorThread(socketHandler, s, context);
                        athread.start();
                    }
                } catch (Exception ex) {
                }
            }
        });
        if (threadName != null)
            thread.setName(threadName);
        thread.start();
    }

    /**
     * </p>
     * Closes the server socket, unbind from the port, and shuts down the
     * perpetual loops. The close does NOT close all the currently accepted (and
     * processing) handlers. It only close the server socket so that additional
     * connections are refused. It is up to you to keep track of the handlers
     * and also close them if you want all connections to be shutdown.
     * </p>
     */
    public void close() {
        shutdown = true;
        // unbind the server socket
        super.close();
    }

    class AcceptorThread extends Thread {
        Socket s;
        ConnectionContext connectionCtx;
        SocketHandler handler;

        public AcceptorThread(SocketHandler handler, Socket s, ConnectionContext context) {
            this.s = s;
            this.connectionCtx = context;
            this.handler = handler;
        }

        public void run() {
            try {
                startingConnection(handler, connectionCtx);
                establishingConnection(s, handler, connectionCtx);
                handleConnection(s, handler, connectionCtx);
            } catch (IOException ex) {
                // handle threw exception, fire closed event
                ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_ERRORED, "Error while handling connection: " + ex.getMessage());
                fireConnectionClosed(e);
            } catch (ConnectionException ex) {
                ConnectionEvent e = new ConnectionEvent(connectionCtx, ConnectionEvent.CONNECTION_ERRORED, "Error during handling: " + ex.getMessage());
                fireConnectionClosed(e);
            } catch (ConnectionVetoException ex) {
                // do nothing as connection closed event is already fired
            } finally {
                IOUtil.closeSocket(s);
            }
        }
    }
}
