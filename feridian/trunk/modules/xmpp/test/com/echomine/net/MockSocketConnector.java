package com.echomine.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * A mock socket connector for testing purposes. It does not really connect to
 * any specified hosts, but will keep track of expected behavior.
 */
public class MockSocketConnector extends HandshakeableSocketConnector {
    private boolean failConnection;
    private MockSocket socket;

    public MockSocketConnector() {
        super();
    }

    public MockSocketConnector(HandshakeableSocketHandler socketHandler) {
        super(socketHandler);
    }

    /**
     * This sets to simulate a failed connection.
     * 
     * @param fail true to simulate a failed connection.
     */
    public void setConnectionFailure(boolean fail) {
        this.failConnection = fail;
    }

    public void aconnect(HandshakeableSocketHandler socketHandler, ConnectionContext connCtx, String threadName) {
        if (!failConnection) {
            super.aconnect(socketHandler, connCtx, threadName);
            return;
        }
        simulateFailedConnection(connCtx);
    }

    public void connect(HandshakeableSocketHandler socketHandler, ConnectionContext connCtx) throws ConnectionFailedException {
        if (!failConnection) {
            super.connect(socketHandler, connCtx);
            return;
        }
        simulateFailedConnection(connCtx);
        throw new ConnectionFailedException("Simulated Connection Failure");
    }

    public void connectWithSynchStart(HandshakeableSocketHandler socketHandler, ConnectionContext connCtx, String threadName) throws ConnectionException, ConnectionVetoException, IOException {
        if (!failConnection) {
            super.connectWithSynchStart(socketHandler, connCtx, threadName);
            return;
        }
        simulateFailedConnection(connCtx);
        throw new ConnectionFailedException("Simulated Connection Failure");
    }

    protected Socket createSocket(InetAddress address, int port) throws IOException {
        if (socket == null)
            socket = new MockSocket(address, port);
        return socket;
    }

    /**
     * Retrieves the simulated socket object
     */
    public MockSocket getSocket(String host, int port) throws IOException {
        return (MockSocket) createSocket(InetAddress.getByName(host), port);
    }

    /**
     * Simulates a failed connection by simply firing events
     * 
     */
    private void simulateFailedConnection(ConnectionContext connCtx) {
        ConnectionEvent event = new ConnectionEvent(connCtx, ConnectionEvent.CONNECTION_STARTING);
        ConnectionEvent vetoEvent = new ConnectionEvent(connCtx, ConnectionEvent.CONNECTION_VETOED);
        try {
            fireConnectionStarting(event, vetoEvent);
        } catch (ConnectionVetoException ex) {
        } finally {
            // error connecting
            event = new ConnectionEvent(connCtx, ConnectionEvent.CONNECTION_ERRORED, "Simulated Failed Connection");
            fireConnectionClosed(event);
        }
    }

}
