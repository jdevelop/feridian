package com.echomine.net;

/**
 * Adds a feature that will record the amount of time since the connection is
 * online. This time is started when established event is fired, and stops when
 * closed event is fired. This class simply utilizes the available features in
 * ConnectionContext
 */
public class TimeableConnection extends Connection {
    protected void fireConnectionEstablished(ConnectionEvent event) {
        event.getConnectionContext().setStartTime(System.currentTimeMillis());
        super.fireConnectionEstablished(event);
    }

    protected void fireConnectionClosed(ConnectionEvent event) {
        event.getConnectionContext().setEndTime(System.currentTimeMillis());
        super.fireConnectionClosed(event);
    }
}
