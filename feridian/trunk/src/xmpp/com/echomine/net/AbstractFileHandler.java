package com.echomine.net;

/**
 * <p>
 * adds capability to use a FileModel. Most of the time you should extend from
 * this class unless it's absolutely necessary that you implement the interface
 * for some purpose.
 * </p>
 * <p>
 * A note on firing events: There are a few events that can be fired. Usually,
 * the file info changed events are fired before the transfer even begins. The
 * file transfer starting and finished events should be fired right before and
 * after a file transfer. Any transfer status in between should be fired with
 * filesize changed events. The events are left up to you to decide when to
 * fire.
 * </p>
 * <p>
 * Here's a suggested of using the event firing. When receiving file info, fire
 * fireFileInfoChanged. Right before file transfer started, fire
 * fireFileTransferStarting. If a listener vetoes the transfer, then a
 * fileTransferFinished(cancel status) will be fired automatically. During
 * transfer, fireFileInfoChanged and fireFilesizeChanged can be used. When
 * transfer is finished, fire fileTransferFinished(finished status). If socket
 * is prematurely closed or errors occurred while transferring or handshaking,
 * then fire fileTransferFinished(error status).
 * </p>
 */
public abstract class AbstractFileHandler extends BaseFileHandler {
    protected FileModel model;

    /**
     * by default with no throttling
     * 
     * @param model the file model associated with the file transfer
     */
    public AbstractFileHandler(FileModel model) {
        this.model = model;
    }

    /**
     * @return the file model associated with this file transfer
     */
    public FileModel getModel() {
        return model;
    }

    /**
     * sets the file model for this file transfer. It is recommended that you do
     * not set the file model after the file transfer has begun
     */
    public void setModel(FileModel model) {
        this.model = model;
    }

    /**
     * @return the transfer rate throttler
     */
    public TransferRateThrottler getTransferRateThrottler() {
        return model.getThrottler();
    }

    /**
     * sets the transfer rate throttler. You can safely change the throttler in
     * the middle of the file transfer to utilize a new throttler.
     */
    public void setTransferRateThrottler(TransferRateThrottler throttler) {
        model.setThrottler(throttler);
    }
}
