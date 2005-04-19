package com.echomine.net;

/**
 * The throttler interface gives the connection handlers a way to throttle the
 * data transfer bandwidth.  How the bandwidth is throttled is up to the
 * implementor.  This interfaces merely helps to separate the throttling
 * algorithm from the file transfer procedure.
 */
public interface ConnectionThrottler {
    /**
     * Throttles the data transfer rate based on the information provided by
     * the connection model.  Most likely you'll be using the calculated
     * transfer rate and do some sort of sleep to slow down the transfer rate.
     */
    void throttle(ConnectionModel cmodel);
}
