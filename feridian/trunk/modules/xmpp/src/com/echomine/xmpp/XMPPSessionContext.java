package com.echomine.xmpp;

import java.util.Locale;

/**
 * The context stores all session-specific data exchanged during communication
 * and is used by the connection and stream handlers to hold session-specific
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
    private String streamId;
    private Locale locale;

    /**
     * Resets the session data. This will reset all data acquired.
     */
    public void reset() {
        host = null;
        streamId = null;
        version = null;
        username = null;
        resource = null;
        locale = null;
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
     * @return Returns the streamId.
     */
    public String getStreamId() {
        return streamId;
    }

    /**
     * @param streamId The session id to set.
     */
    public void setStreamId(String streamId) {
        this.streamId = streamId;
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

    /**
     * Sets the default stream-level locale. This is the locale that will be
     * recognized as the default for the entire session if no children overrides
     * it.
     * 
     * @param locale the locale, null for no locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * retrieves the current default session locale. This is the default locale
     * for the entire session unless the children overrides it. If no locale is
     * set by you, the server may issue its own default locale during handshake,
     * in which case the locale will be set to the locale offered by the server.
     * 
     * @return the locale, or null if none
     */
    public Locale getLocale() {
        return locale;
    }
}
