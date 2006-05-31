package com.echomine.net;

import java.util.EventListener;

/** Interface for listening to file transfer activities. */
public interface FileListener extends EventListener {
    /**
     * <p>The event is fired when filesize changes.  This event is useful for
     * keeping an eye on the file transfer progress.</p>
     */
    void filesizeChanged(FileEvent e);

    /**
     * <p>The event gets fired when any field of the file data gets changed.</p>
     * <p>NOTE: File size changes will/should not be fired under this event; rather, the file size changes should be fired in
     * another event method.  This is because file size changes are extremely frequent and therefore segregated out to
     * decrease the number of events fired.</p>
     */
    void fileInfoChanged(FileEvent e);

    /** file transfer is finished */
    void fileTransferFinished(FileEvent e);

    /** File transfer is starting (ie. transfer is has not yet started) */
    void fileTransferStarting(FileEvent e) throws TransferVetoException;
}
