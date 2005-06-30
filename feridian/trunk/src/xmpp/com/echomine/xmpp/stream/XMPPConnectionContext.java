package com.echomine.xmpp.stream;

import java.net.Socket;

/**
 * The context stores all session-specific data exchanged during communication
 * and is used by the connetion and stream handlers to hold session-specific
 * data. It contains information set by the local entity (us) and stores data
 * sent by the remote entity. In case of TLS and/or SASL handshakes, the context
 * will get reset during such communication due to XMPP specification or
 * throwing away all previous creditials after each successful TLS/SASL
 * handshake.
 */
public class XMPPConnectionContext {
    private String host;
    private String sessionId;
    private Socket socket;
    private boolean tlsSupported;
    private boolean tlsRequired;
    private boolean resourceBindingRequired;
    private boolean sessionRequired;

    public XMPPConnectionContext() {
    }

    /**
     * Resets the session data. This will reset all data acquired.
     */
    public void reset() {
        host = null;
        sessionId = null;
        tlsSupported = false;
        tlsRequired = false;
    }

    /**
     * @return Returns the tlsRequired.
     */
    public boolean isTLSRequired() {
        return tlsRequired;
    }

    /**
     * @param tlsRequired The tlsRequired to set.
     */
    public void setTLSRequired(boolean tlsRequired) {
        this.tlsRequired = tlsRequired;
    }

    /**
     * @return Returns the tlsSupported.
     */
    public boolean isTLSSupported() {
        return tlsSupported;
    }

    /**
     * @param tlsSupported The tlsSupported to set.
     */
    public void setTLSSupported(boolean tlsSupported) {
        this.tlsSupported = tlsSupported;
    }

    /**
     * @return Returns the hostname.
     */
    public String getHost() {
        return host;
    }

    /**
     * @param hostname The hostname to set.
     */
    public void setHost(String hostname) {
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
     * @param socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * @return get the socket associated with the connection
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @return Returns the resourceBindingRequired.
     */
    public boolean isResourceBindingRequired() {
        return resourceBindingRequired;
    }

    /**
     * @param resourceBindingRequired The resourceBindingRequired to set.
     */
    public void setResourceBindingRequired(boolean resourceBindingRequired) {
        this.resourceBindingRequired = resourceBindingRequired;
    }

    /**
     * @return Returns the sessionRequired.
     */
    public boolean isSessionRequired() {
        return sessionRequired;
    }

    /**
     * @param sessionRequired The sessionRequired to set.
     */
    public void setSessionRequired(boolean sessionRequired) {
        this.sessionRequired = sessionRequired;
    }
}
