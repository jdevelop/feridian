package com.echomine.net;

/**
 * Interface added to support file transfer handling
 */
public interface FileHandler extends SocketHandler {
    /**
     * @return the file model associated with this file transfer
     */
    FileModel getModel();

    /**
     * @return the transfer rate throttler
     */
    TransferRateThrottler getTransferRateThrottler();

    /**
     * Subscribe to listen for file events
     */
    void addFileListener(FileListener l);

    /**
     * remove from listening to file events
     */
    void removeFileListener(FileListener l);
}
