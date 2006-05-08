package com.echomine.net;

/**
 * A mock connection listener that will hold flags to indicate which methods has
 * been called.
 */
public class MockConnectionListener implements ConnectionListener {
    boolean startingCalled;
    boolean establishedCalled;
    boolean closedCalled;
    ConnectionEvent closeEvent;

    public void connectionStarting(ConnectionEvent e) throws ConnectionVetoException {
        startingCalled = true;
    }

    public void connectionEstablished(ConnectionEvent e) {
        establishedCalled = true;
    }

    public void connectionClosed(ConnectionEvent e) {
        synchronized(this) {
            closedCalled = true;
            closeEvent = e;
            notifyAll();
        }
    }

    /**
     * @return the closed event
     */
    public ConnectionEvent getCloseEvent() {
        return closeEvent;
    }
    
    /**
     * @return Returns the closedCalled.
     */
    public boolean isClosedCalled() {
        return closedCalled;
    }

    /**
     * @return Returns the establishedCalled.
     */
    public boolean isEstablishedCalled() {
        return establishedCalled;
    }

    /**
     * @return Returns the startingCalled.
     */
    public boolean isStartingCalled() {
        return startingCalled;
    }

    public void waitForConnectionClose() throws InterruptedException {
        synchronized(this) {
            if (!closedCalled)
                wait();
        }
    }
}
