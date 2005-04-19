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
    private TLSFeature tlsFeature;

    public XMPPConnectionContext() {
        tlsFeature = new TLSFeature();
    }

    /**
     * Resets the session data. This will reset all data acquired.
     */
    public void reset() {
        host = null;
        sessionId = null;
        tlsFeature.tlsRequired = false;
        tlsFeature.tlsSupported = false;
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
     * @return Returns the tlsFeature.
     */
    public TLSFeature getTLSFeature() {
        return tlsFeature;
    }

    /**
     * @param tlsFeature The tlsFeature to set.
     */
    public void setTLSFeature(TLSFeature feature) {
        tlsFeature.tlsSupported = feature.tlsSupported;
        tlsFeature.tlsRequired = feature.tlsRequired;
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
}
