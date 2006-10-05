package com.echomine.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This connection context override the real one in order to override and 
 * disable any operations requiring the use of any network operations purely
 * for testcase isolation purpose. 
 */
public class MockConnectionContext extends ConnectionContext {
    String hostname;
    String ip;
    
    public MockConnectionContext(InetAddress host, int port, ConnectionThrottler throttler) {
        super(host, port, throttler);
    }

    public MockConnectionContext(InetAddress host, int port) {
        super(host, port);
    }

    public MockConnectionContext(int port) {
        super(port);
    }

    public MockConnectionContext(String hostname, String ip, int port, ConnectionThrottler throttler) throws UnknownHostException {
        super(port);
        this.hostname = hostname;
        this.ip = ip;
        this.throttler = throttler;
    }

    public MockConnectionContext(String hostname, String ip, int port) throws UnknownHostException {
        this(hostname, ip, port, null);
    }

    @Override
    public String getHostAddress() {
        return ip;
    }

    @Override
    public String getHostName() {
        return hostname;
    }

    @Override
    public void setHost(InetAddress host) {
        super.setHost(host);
        this.hostname = super.getHostName();
        this.ip = super.getHostAddress();
    }

}
