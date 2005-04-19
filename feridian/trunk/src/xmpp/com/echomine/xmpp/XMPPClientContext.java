package com.echomine.xmpp;

/**
 * The client context hold information set by the local entity that is used to
 * communicate with the remote entity. Note that this context is different from
 * XMPPConnectionContext, which holds session-specific data during negotiation.
 */
public class XMPPClientContext {
    private String username;
    private String host;

    /**
     * @return Returns the host to connect to.
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host The host to connect to.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return Returns the username for authentication.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The username used to authenticate.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
