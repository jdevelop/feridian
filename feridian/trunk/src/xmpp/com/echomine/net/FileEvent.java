package com.echomine.net;

import java.util.EventObject;

/**
 * Contains event information regarding a file activity. There are convenience
 * methods added for easier retrieval of certain basic information such as
 * filename and filesize. The other detailed information will need to be
 * extracted from the source/model itself.
 */
public class FileEvent extends EventObject {
    private static final long serialVersionUID = 3203072545539847643L;
    public final static int FILESIZE_CHANGED = 1;
    public final static int FILEINFO_CHANGED = 2;
    public final static int TRANSFER_STARTING = 3;
    public final static int TRANSFER_FINISHED = 4;
    public final static int TRANSFER_ERRORED = 5;
    public final static int TRANSFER_CANCELLED = 6;
    public final static int TRANSFER_VETOED = 7;
    public final static int TRANSFER_QUEUED = 8;
    private int status;
    private String errormsg;
    private FileModel fmodel;

    public FileEvent(FileHandler handler, FileModel fmodel, int status) {
        this(handler, fmodel, status, null);
    }

    public FileEvent(FileHandler handler, FileModel fmodel, int status, String errormsg) {
        super(handler);
        this.fmodel = fmodel;
        this.status = status;
        this.errormsg = errormsg;
    }

    /**
     * @return the file handler
     */
    public FileHandler getFileHandler() {
        return (FileHandler) getSource();
    }

    /**
     * @return the file model
     */
    public FileModel getFileModel() {
        return fmodel;
    }

    /**
     * @return the filename stored in the file model
     */
    public String getFilename() {
        return fmodel.getFilename();
    }

    /**
     * @return the filesize stored in the file model
     */
    public long getFilesize() {
        return fmodel.getFilesize();
    }

    /**
     * @return the save location stored in the file model
     */
    public String getSaveLocation() {
        return fmodel.getSaveLocation();
    }

    /**
     * obtains the status of the file event
     * 
     * @return the file event status, as provided in this class
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return the error message, null if there is none
     */
    public String getErrorMessage() {
        return errormsg;
    }
}
