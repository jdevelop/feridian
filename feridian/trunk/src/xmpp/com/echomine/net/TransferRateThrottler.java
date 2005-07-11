package com.echomine.net;

/**
 * The throttler interface gives the file transfer handlers a way to throttle
 * the transfer rates. How the transfer rates are throttled is up to the
 * implementor. This interfaces merely helps to separate the throttling
 * algorithm from the file transfer procedure.
 */
public interface TransferRateThrottler {
    /**
     * Throttles the transfer rate based on the information provided by the file
     * model. Most likely you'll be using the calculated transfer rate and do
     * some sort of sleep to slow down the transfer rate.
     */
    void throttle(FileModel filemodel);
}
