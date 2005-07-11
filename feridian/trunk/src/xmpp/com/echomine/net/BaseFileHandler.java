package com.echomine.net;

import javax.swing.event.EventListenerList;

/**
 * This class simply contains methods to fire off events that other subclasses
 * can just extend and use.
 */
public abstract class BaseFileHandler implements FileHandler {
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Add to listen for file transfer events for this specific file transfer
     */
    public void addFileListener(FileListener l) {
        listenerList.add(FileListener.class, l);
    }

    /**
     * remove from listening to file transfer events for this specific file
     * transfer
     */
    public void removeFileListener(FileListener l) {
        listenerList.remove(FileListener.class, l);
    }

    protected void fireFilesizeChanged(FileEvent event) {
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == FileListener.class) {
                // Lazily create the event:
                ((FileListener) listeners[i + 1]).filesizeChanged(event);
            }
        }
    }

    /**
     * The method can be called to fire event whenever the file info (other than
     * the file size) has changed. This will notify the listeners to change the
     * info accordingly.
     */
    protected void fireFileInfoChanged(FileEvent event) {
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == FileListener.class) {
                // Lazily create the event:
                ((FileListener) listeners[i + 1]).fileInfoChanged(event);
            }
        }
    }

    /**
     * The method can be called to fire event whenever the file transfer is
     * finished successfully.
     */
    protected void fireFileTransferFinished(FileEvent event) {
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == FileListener.class) {
                // Lazily create the event:
                ((FileListener) listeners[i + 1]).fileTransferFinished(event);
            }
        }
    }

    /**
     * Fired before the transfer begins. This gives the listener a chance to set
     * things up for a transfer or any preliminary processing. The listener also
     * has a chance to "veto" the transfer. This means that if the listener
     * finds that the transfer should not start, it can throw a veto exception
     * which essentially signals the file handler to abort this transfer. If the
     * transfer is vetoed, whoever was notified before the veto will get
     * notified again that the transfer is "finished". Thus, there is no need to
     * send another file finished event.
     */
    protected void fireFileTransferStarting(FileEvent event, FileEvent vetoEvent) throws TransferVetoException {
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        int i = listeners.length - 2;
        try {
            for (; i >= 0; i -= 2) {
                if (listeners[i] == FileListener.class) {
                    // Lazily create the event:
                    ((FileListener) listeners[i + 1]).fileTransferStarting(event);
                }
            }
        } catch (TransferVetoException ex) {
            // transfer vetoed, so we need to notify listeners before the veto
            // that the transfer aborted
            for (int j = listeners.length - 2; j >= i; j -= 2) {
                if (listeners[i] == FileListener.class) {
                    // Lazily create the event:
                    ((FileListener) listeners[i + 1]).fileTransferFinished(vetoEvent);
                }
            }
            throw ex;
        }
    }

    /**
     * Fired before the transfer begins. This gives the listener a chance to set
     * things up for a transfer or any preliminary processing. The listener also
     * has a chance to "veto" the transfer. This means that if the listener
     * finds that the transfer should not start, it can throw a veto exception
     * which essentially signals the file handler to abort this transfer. This
     * method will not catch the veto exception and notify listeners that
     * already processed the event to end the transfer. This method is normally
     * used when you need to propogate the file event and will fire a transfer
     * finished event later.
     */
    protected void fireFileTransferStartingWithoutVeto(FileEvent event) throws TransferVetoException {
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        int i = listeners.length - 2;
        for (; i >= 0; i -= 2) {
            if (listeners[i] == FileListener.class) {
                // Lazily create the event:
                ((FileListener) listeners[i + 1]).fileTransferStarting(event);
            }
        }
    }
}
