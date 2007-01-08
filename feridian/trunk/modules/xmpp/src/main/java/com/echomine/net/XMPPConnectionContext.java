/**
 * 
 */
package com.echomine.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This enhanced version of the connection event simply adds additional fields
 * that the xmpp entity would use such as resource domain name, etc.
 * 
 * @author Chris Chen
 * @since 1.0b5
 */
public class XMPPConnectionContext extends ConnectionContext {
    private String domain;

    /**
     * @param port the port to connect to
     */
    public XMPPConnectionContext(int port) {
        super(port);
    }

    /**
     * @param host the host ip to connect to
     * @param port the port to connect to
     */
    public XMPPConnectionContext(InetAddress host, int port) {
        super(host, port);
    }

    /**
     * @param hostname the hostname to connect to
     * @param port the port to connect to
     * @throws UnknownHostException when the host cannot be resolved
     */
    public XMPPConnectionContext(String hostname, int port)
            throws UnknownHostException {
        super(hostname, port);
    }

    /**
     * @param host the host to connect to
     * @param port the port to connect to
     * @param throttler optional throttler to throttle the connection data rates
     */
    public XMPPConnectionContext(InetAddress host, int port,
            ConnectionThrottler throttler) {
        super(host, port, throttler);
    }

    /**
     * @param hostname the hostname to connect to
     * @param port the port to connect to
     * @param throttler optional throttler to throttle the connection data rates
     * @throws UnknownHostException
     */
    public XMPPConnectionContext(String hostname, int port,
            ConnectionThrottler throttler) throws UnknownHostException {
        super(hostname, port, throttler);
    }

    /**
     * Will retrieve the domain to be used for negotiation. If the value is
     * null, the method by default will return the hostname instead.
     * 
     * @return the non-null resource domain to be used
     */
    public String getDomain() {
        if (domain == null)
            return getHostName();
        return domain;
    }

    /**
     * By default, this is set to be the same as the hostname used. If an IP is
     * used, this will default to null and must be set.
     * 
     * @param domain the resource domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

}
