package com.echomine.net;

/**
 * <p>Contains data and information used by the Handler.  This may include information such as filename, filesize, current
 * transfer file size, current status, etc.  Default class is basically an empty class that contains no data.</p>
 * <p>Just as a note, the current filesize acts as both the current file transferred size and also as the initial resume
 * offset before transfer begins.</p>
 */
public class FileModel {
    String filename = "";
    long filesize;
    String saveLocation = "";
    long currentFilesize;
    long startTime;
    long endTime;
    long bps;
    long startFileSize;
    TransferRateThrottler throttler;

    /**
     * @param filename the name of the file to request
     */
    public FileModel(String filename) {
        this(filename, 0);
    }

    /**
     * @param filename the name of the file to request
     * @param resumeOffset the filesize from where to resume
     */
    public FileModel(String filename, long resumeOffset) {
        this(filename, "", resumeOffset, null);
    }

    /**
     * @param filename the name of the file to request
     * @param saveLocation the location (path + filename) to store the file locally
     */
    public FileModel(String filename, String saveLocation) {
        this(filename, saveLocation, 0, null);
    }

    /**
     * @param filename the name of the file to request
     * @param saveLocation the location (path + filename) to store the file locally
     * @param resumeOffset the filesize from where to resume
     */
    public FileModel(String filename, String saveLocation, long resumeOffset) {
        this(filename, saveLocation, resumeOffset, null);
    }

    /**
     * @param filename the name of the file to request
     * @param saveLocation the location (path + filename) to store the file locally
     * @param resumeOffset the filesize from where to resume
     * @param throttler the throttler, null if no throttling is done
     */
    public FileModel(String filename, String saveLocation, long resumeOffset, TransferRateThrottler throttler) {
        this.filename = filename;
        this.saveLocation = saveLocation;
        this.filesize = resumeOffset;
        this.throttler = throttler;
    }

    /**
     * @return the filename you want to retrieve
     */
    public String getFilename() {
        return filename;
    }

    /**
     * sets the filename you want to retrieve
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /** @return the registered filesize */
    public long getFilesize() {
        return filesize;
    }

    /**
     * Sets the filesize or resume offset depending on whether
     * you are sending or receiving a file
     */
    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    /**
     * Sets the CURRENT filesize or resume offset depending on whether
     * you are sending or receiving a file
     */
    public void setCurrentFilesize(long currentFilesize) {
        this.currentFilesize = currentFilesize;
        //set the start file size to be the same as the current file size
        //for more accurate bps rate calculation
        if (startFileSize == 0 && currentFilesize != 0) {
            startFileSize = currentFilesize;
        }
    }

    /**
     * Obtains the current filesize
     */
    public long getCurrentFilesize() {
        return currentFilesize;
    }

    /**
     * increments the current filesize by a specified amount
     */
    public void incrementCurrentFilesize(long increment) {
        setCurrentFilesize(currentFilesize + increment);
    }

    /** @return the save location, or empty string if no save location exists */
    public String getSaveLocation() {
        return saveLocation;
    }

    /**
     * sets the location on where to save the file.  This is normally called
     * before the real transfer is started.  However, you can still make
     * changes during the fileTransferStarting event.
     */
    public void setSaveLocation(String saveLocation) {
        this.saveLocation = saveLocation;
    }

    /**
     * Sets the start time when the transfer begins
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
        //reset end time since start time got changed
        endTime = 0;
    }

    /** @return the transfer rate in KBytes/sec */
    public float getTransferKBPS() {
        if (endTime > 0) return 0;
        float kbps = (float) (getTransferBPS() / 1024);
        return kbps;
    }

    /** @return the transfer rate in Bytes/sec. */
    public long getTransferBPS() {
        if (endTime > 0) return 0;
        if (endTime == 0) {
            //file transfer not yet complete, calculate our rate
            //rate = current filesize / delta time
            bps = (long) (((float) (currentFilesize - startFileSize)) / ((float) ((System.currentTimeMillis() - startTime) / 1000)));
        }
        return bps;
    }

    /** @return the estimated time left to finish, in the format of HH:MM:SS */
    public String getTimeLeft() {
        //if transfer ended, then just return zeros
        if (endTime > 0) return "00:00:00";
        //otherwise, get current time and calculate the time left
        //time left = (filesize - current filesize) / rate (bytes/s)
        long left;
        if (bps == 0)
            left = 99 * 3600 + 99 * 60 + 99;
        else
            left = (filesize - currentFilesize) / bps;
        if (left <= 0) return "00:00:00";
        int hour, min, sec;
        hour = (int) (left / 3600);
        left = left % 3600;
        min = (int) (left / 60);
        sec = (int) (left % 60);
        StringBuffer buffer = new StringBuffer(9);
        buffer.append(hour < 10 ? "0" : "").append(hour).append(":");
        buffer.append(min < 10 ? "0" : "").append(min).append(":");
        buffer.append(sec < 10 ? "0" : "").append(sec);
        return buffer.toString();
    }

    /** Resets all the data fields back to the initial state.  This is good when the model is to be reused. */
    public void reset() {
        filename = "";
        filesize = 0;
        saveLocation = "";
        currentFilesize = 0;
        startTime = 0;
        endTime = 0;
        bps = 0;
    }

    /** the objects are equal if filename, filesize, and savelocation are equal. */
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof FileModel)) return false;
        FileModel model = (FileModel) obj;
        if (filename.equals(model.getFilename()) && filesize == model.getFilesize() &&
            saveLocation.equals(model.getSaveLocation()))
            return true;
        return false;
    }

    /**
     * Sets the end time when the transfer is finished
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * sets the transfer rate throttler to use for throttling the transfer rate
     */
    public void setThrottler(TransferRateThrottler throttler) {
        this.throttler = throttler;
    }

    /**
     * @return the current throttler, or null if there is none
     */
    public TransferRateThrottler getThrottler() {
        return throttler;
    }
}
