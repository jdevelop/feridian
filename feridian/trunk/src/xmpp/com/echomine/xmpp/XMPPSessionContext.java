package com.echomine.xmpp;

/**
 * The context stores all session-specific data exchanged during communication
 * and is used by the connetion and stream handlers to hold session-specific
 * data. It contains information set by the local entity (us) and stores data
 * sent by the remote entity. In case of TLS and/or SASL handshakes, the context
 * will get reset during such communication due to XMPP specification or
 * throwing away all previous creditials after each successful TLS/SASL
 * handshake.
 */
public class XMPPSessionContext {
    private String username;
    private String resource;
    private String host;
    private String version;
    private String sessionId;

    /**
     * Resets the session data. This will reset all data acquired.
     */
    public void reset() {
        host = null;
        sessionId = null;
        version = null;
    }

    /**
     * @return Returns the hostname.
     */
    public String getHostName() {
        return host;
    }

    /**
     * @param hostname The hostname to set.
     */
    public void setHostName(String hostname) {
        this.host = hostname;
    }

    /**
     * the unique session ID as sent by the remote entity
     * 
     * @return Returns the sessionId.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId The sessionId to set.
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * The version of the string
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version number.
     * 
     * @param version the version of the stream
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.ISessionContext#getResource()
     */
    public String getResource() {
        return resource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.ISessionContext#getUsername()
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param resource The resource to set.
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
