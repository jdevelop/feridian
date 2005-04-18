package com.echomine.net;

import java.util.EventListener;

/** listener that should be implemented to listen for any connection events */
public interface ConnectionListener extends EventListener {
    /**
     * This event is fired when a connection is just starting.  This may
     * take place before or after a connection is physically connected.
     * However, it is guaranteed that any handling of connection data
     * will not occur until AFTER this event is entirely processed.
     * The listener implementation can also throw an exception to indicate
     * that the connection is vetoed.  A vetoed connection will close normally
     * but will contain a vetoed status (not closed or errored status).
     */
    void connectionStarting(ConnectionEvent e) throws ConnectionVetoException;

    /**
     * This event is fired when a connection is fully established.  If a connection is not established due to some sort of
     * error (ie. unknown host name or just unable to connect), then this event will NOT be fired.
     */
    public void connectionEstablished(ConnectionEvent e);

    /**
     * <p>The event is fired when the connection is closed normally or abnormally.</p>
     * <p>The status code will let you know how the connection is closed. If the
     * connection is closed abnormally, a error message will go along with it</p>
     */
    public void connectionClosed(ConnectionEvent e);
}
