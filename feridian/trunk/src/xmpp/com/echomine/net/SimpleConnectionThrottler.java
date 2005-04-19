package com.echomine.net;

/**
 * <p>This throttler simply throttles the rate by checking to see if the rate is above the rate limit, and if it
 * is, sleep occurs so that the rate is decreased (since rate is calculated as data transferred over a period of time).  The
 * throttling algorithm is to simply sleep and wait for the next checkup.  Sleep interval will actually double each time a
 * checkup occurs and the rate is above the limit.  Sleep interval will get reset once rate goes below the limit.  The problem
 * with this throttler is that if the remote client is sending at an extremely high speed, you will get a massively long
 * interval which may not be what you want.</p> <p>Also make sure that you don't use this for global throttling.  This class
 * is not multi-thread safe and must not be used as a such (ie. controlling throttling limited to a global transfer rate
 * averaged across all the uploads).</p>
 */
public class SimpleConnectionThrottler implements ConnectionThrottler {
    private final static long MAX_SLEEP_INTERVAL = 10000;

    /** The max limit that the rate cannot go above */
    private int bps;
    private long sleepInterval;

    /** Constructor that accepts the rate limit in KBytes/sec. */
    public SimpleConnectionThrottler(int bps_in) {
        bps = bps_in;
        sleepInterval = 100;
    }

    public void throttle(ConnectionModel cmodel) {
        if (cmodel.getTransferBPS() > bps) {
            try {
                Thread.sleep(sleepInterval);
                if (sleepInterval > MAX_SLEEP_INTERVAL)
                    sleepInterval = MAX_SLEEP_INTERVAL;
                else
                    sleepInterval *= 2;
            } catch (InterruptedException ex) {
            }
        } else {
            sleepInterval = 100;
        }
    }

    public int getBPS() {
        return bps;
    }

    public void setBPS(int bps_in) {
        bps = bps_in;
    }
}
