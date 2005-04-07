package com.echomine.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Contains all the data that are needed by Connector to make a connection.  Subclasses can store additional details such as
 * protocol version (ie. HTTP 1.1).  This connection model also adds support
 * for throttling and bandwidth management.  Not all uses of this model will
 * make use of such feature, but the feature is there is a need for it.
 */
public class ConnectionModel {
    long startTime;
    long endTime;
    long bytesTransferred;
    int port;
    InetAddress host;
    ConnectionThrottler throttler;
    boolean secure = false;

    /**
     * Normally used to create a listener for incoming connections.  This is
     * used so that acceptors can bind to 0.0.0.0, and not to a specific
     * interface.
     * @param port the port to connect to/receive from
     */
    public ConnectionModel(int port) {
        this.host = null;
        this.port = port;
    }

    /**
     * @param host the InetAddress containing the hostname/IP
     * @param port the port to connect to/receive from
     */
    public ConnectionModel(InetAddress host, int port) {
        this(host, port, null);
    }

    /**
     * @param hostname the hostname/IP to create
     * @param port the port to connect to/receive from
     */
    public ConnectionModel(String hostname, int port) throws UnknownHostException {
        this(hostname, port, null);
    }

    /**
     * @param host the InetAddress containing the hostname/IP
     * @param port the port to connect to/receive from
     * @param throttler the bandwidth throttler, or null if no throttling
     */
    public ConnectionModel(InetAddress host, int port, ConnectionThrottler throttler) {
        this.host = host;
        this.port = port;
        this.throttler = throttler;
    }

    /**
     * @param hostname the hostname/IP to create
     * @param port the port to connect to/receive from
     * @param throttler the bandwidth throttler, or null if no throttling
     */
    public ConnectionModel(String hostname, int port, ConnectionThrottler throttler) throws UnknownHostException {
        this.host = InetAddress.getByName(hostname);
        this.port = port;
        this.throttler = throttler;
    }

    /** @return the port */
    public int getPort() {
        return port;
    }

    /** @return the host */
    public InetAddress getHost() {
        return host;
    }

    /** Convenience Method for obtaining the hostname from the InetAddress */
    public String getHostName() {
        if (host == null) return null;
        return host.getHostName();
    }

    /** Convenience Method for obtaining the host IP from the InetAddress */
    public String getHostAddress() {
        if (host == null) return null;
        return host.getHostAddress();
    }

    /** sets the port after the connection is created */
    public void setPort(int port) {
        this.port = port;
    }

    /** sets the host after the connection created */
    public void setHost(InetAddress host) {
        this.host = host;
    }

    /** sets this to be a secure SSL connection */
    public void setSSL(boolean secure) {
        this.secure = secure;
    }

    /** replies whether this is secure or not */
    public boolean isSSL() {
        return secure;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        //same reference always true
        if (this == obj) return true;
        //must be the same sort of instance
        if (!(obj instanceof ConnectionModel)) return false;
        ConnectionModel model = (ConnectionModel) obj;
        if ((host == null && model.getHost() == null) && (port == model.getPort()) && (secure == model.isSSL()))
            return true;
        else if (host != null && host.equals(((ConnectionModel) obj).getHost()) && port == ((ConnectionModel) obj).getPort() && (secure == model.isSSL())) return true;
        return false;
    }

    /**
     * sets the throttler for the connection bandwidth.
     * Set to null if no throttling is required.
     */
    public void setThrottler(ConnectionThrottler throttler) {
        this.throttler = throttler;
    }

    /**
     * @return the connection bandwidth throttler, or null if none exists
     */
    public ConnectionThrottler getThrottler() {
        return throttler;
    }

    /** @return a string in the format of <ip>:<port> */
    public String toString() {
        return host.getHostAddress() + ":" + port;
    }

    /**
     * increments the bytes transferred for calculation of throttling
     * as well as the bandwidth BPS
     */
    public void incrementBytesTransferred(long increment) {
        bytesTransferred += increment;
    }

    /** @return the bandwidth rate in KBytes/sec */
    public float getTransferKBPS() {
        if (endTime > 0) return 0;
        float kbps = (float) (getTransferBPS() / 1024);
        return kbps;
    }

    /** @return the bandwidth rate in Bytes/sec. */
    public long getTransferBPS() {
        if (endTime > 0) return 0;
        //file transfer not yet complete, calculate our rate
        //rate = current data transferred / delta time
        return (long) (((float) bytesTransferred) / ((float) ((System.currentTimeMillis() - startTime) / 1000)));
    }

    /**
     * @return the time online in milliseconds
     */
    public long getTimeOnlineMillis() {
        if (startTime <= 0) return 0;
        if (endTime > 0) return endTime - startTime;
        return System.currentTimeMillis() - startTime;
    }

    /** @return the estimated time online, in the format of HH:MM:SS */
    public String getTimeOnlineString() {
        long timeOnline = getTimeOnlineMillis() / 1000;
        if (timeOnline <= 0) return "00:00:00";
        int hour, min, sec;
        hour = (int) (timeOnline / 3600);
        timeOnline = timeOnline % 3600;
        min = (int) (timeOnline / 60);
        sec = (int) (timeOnline % 60);
        StringBuffer buffer = new StringBuffer(9);
        buffer.append(hour < 10 ? "0" : "").append(hour).append(":");
        buffer.append(min < 10 ? "0" : "").append(min).append(":");
        buffer.append(sec < 10 ? "0" : "").append(sec);
        return buffer.toString();
    }

    /** Resets all the data fields back to the initial state.  This is good when the model is to be reused. */
    public void reset() {
        bytesTransferred = 0;
        startTime = 0;
        endTime = 0;
    }

    /**
     * Sets the start time when the connection begins.  This will effectively
     * reset all the other stats automatically
     */
    public void setStartTime(long startTime) {
        reset();
        this.startTime = startTime;
    }

    /**
     * Sets the end time when the transfer is finished
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
