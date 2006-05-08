package com.echomine.jabber;

import com.echomine.net.ConnectionEvent;
import com.echomine.net.ConnectionListener;
import com.echomine.net.ConnectionVetoException;

/**
 * The class will run in the background as a thread that periodically "pings"
 * the socket to make sure that the connection is not stale.  If the connection
 * is stale, then the session will be disconnected.  This solves the problem
 * where you have no idea when you were disconnected from the server and your
 * client still think that it is.
 */
public class KeepAlive extends Thread {
    private static final NullMessage NULLMSG = new NullMessage();
    private JabberSession session;
    private int timeout;
    private boolean running;

    /**
     * Creates a keepalive thread that does NOT run as a daemon thread.
     *
     * @param session the session that you want to do keep alive pings
     * @param timeout time interval between pings.  Must be > 0
     */
    public KeepAlive(JabberSession session, int timeout) {
        this(session, timeout, false);
    }

    /**
     * @param session the session that you want to do keep alive pings
     * @param timeout time interval between pings.  Must be > 0
     * @param isDaemon daemon thread?
     */
    public KeepAlive(JabberSession session, int timeout, boolean isDaemon) {
        if (session == null || timeout <= 0)
            throw new IllegalArgumentException("session cannot be null and timeout must be > 0");
        this.session = session;
        this.timeout = timeout;
        running = true;
        setName("Jabber KeepAlive Thread");
        setDaemon(isDaemon);
        session.getConnection().addConnectionListener(new KeepAliveConnectionListener());
        start();
    }

    public synchronized void shutdown() {
        running = false;
        notify();
    }

    public void run() {
        while (running) {
            while (session.getConnection().isConnected()) {
                synchronized (this) {
                    try {
                        wait(timeout);
                    } catch (InterruptedException e) {
                        continue;
                    }
                    try {
                        session.sendMessage(NULLMSG);
                    } catch (Exception e) {
                        session.disconnect();
                    }
                }
            }
            synchronized (this) {
                if (running && !session.getConnection().isConnected()) {
                    try {
                        wait(0);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    class KeepAliveConnectionListener implements ConnectionListener {
        public void connectionClosed(ConnectionEvent e) {
        }

        public void connectionEstablished(ConnectionEvent e) {
            synchronized (KeepAlive.this) {
                notify();
            }
        }

        public void connectionStarting(ConnectionEvent e) throws ConnectionVetoException {
        }
    }
}
