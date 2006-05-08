package com.echomine.net;

import javax.swing.event.EventListenerList;

/**
 * Base class for all network connection related services. Specifies necessary
 * methods to be implemented by subclasses plus add the foundation for
 * dispatching connection events.
 */
public abstract class Connection {
    protected EventListenerList listenerList = new EventListenerList();

    /** adds a subscriber to listen for connection events */
    public void addConnectionListener(ConnectionListener l) {
        listenerList.add(ConnectionListener.class, l);
    }

    /** remove from listening to connection events */
    public void removeConnectionListener(ConnectionListener l) {
        listenerList.remove(ConnectionListener.class, l);
    }

    /**
     * convenience method to fire off connection starting events. If a
     * connection is vetoed, it will automatically broadcast connection closed
     * events to the listeners that already processed the starting event. It
     * will also throw the exception when a connectio is vetoed.
     */
    protected void fireConnectionStarting(ConnectionEvent event, ConnectionEvent vetoEvent) throws ConnectionVetoException {
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        int i = listeners.length - 2;
        try {
            for (; i >= 0; i -= 2) {
                if (listeners[i] == ConnectionListener.class) {
                    ((ConnectionListener) listeners[i + 1]).connectionStarting(event);
                }
            }
        } catch (ConnectionVetoException ex) {
            // connection vetoed, so we need to notify listeners that processed
            // events before vetoe occurred to let them know that connection
            // has been vetoed and closed
            for (int j = listeners.length - 2; j >= i; j -= 2) {
                if (listeners[i] == ConnectionListener.class) {
                    ((ConnectionListener) listeners[i + 1]).connectionClosed(vetoEvent);
                }
            }
            throw ex;
        }
    }

    /**
     * this method is specially designed to fire off the event without capturing
     * the veto exception. The reason for using this is when the subclasses
     * wraps the connection listening event caused by another connection event.
     * This passing of the connection events should be captured at only one
     * level, the base level where connection event was first fired. The
     * difference between this method and the fireConnectionStarting() method is
     * that the latter will automatically call connection closed events while
     * this method does not.
     */
    protected void fireConnectionStartingWithoutVeto(ConnectionEvent event) throws ConnectionVetoException {
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ConnectionListener.class) {
                ((ConnectionListener) listeners[i + 1]).connectionStarting(event);
            }
        }
    }

    protected void fireConnectionEstablished(ConnectionEvent event) {
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ConnectionListener.class) {
                ((ConnectionListener) listeners[i + 1]).connectionEstablished(event);
            }
        }
    }

    protected void fireConnectionClosed(ConnectionEvent event) {
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ConnectionListener.class) {
                ((ConnectionListener) listeners[i + 1]).connectionClosed(event);
            }
        }
    }
}
