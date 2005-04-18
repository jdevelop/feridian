package com.echomine.xmpp;


public class XMPPConnectionContext {
    private String username;
    private String resource;
    private String host;
    private String sessionId;
    private TLSFeature tlsFeature;
    
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
     * @return Returns the resource.
     */
    public String getResource() {
        return resource;
    }
    
    /**
     * @param resource The resource to set.
     */
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    /**
     * @return Returns the username.
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * the unique session ID as sent by the remote entity
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
        if (tlsFeature == null) tlsFeature = new TLSFeature();
        return tlsFeature;
    }
    
    /**
     * @param tlsFeature The tlsFeature to set.
     */
    public void setTLSFeature(TLSFeature tlsFeature) {
        this.tlsFeature = tlsFeature;
    }
}
