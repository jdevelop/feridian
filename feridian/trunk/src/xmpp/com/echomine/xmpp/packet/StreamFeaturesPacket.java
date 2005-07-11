package com.echomine.xmpp.packet;

import com.echomine.xmpp.IPacket;

/**
 * This represents the stream:features packet used during handshake negotiation.
 * It contains a list of features supported by the server.
 */
public class StreamFeaturesPacket implements IPacket {
    private boolean tlsRequired;
    private boolean tlsSupported;
    private boolean sessionRequired;
    private boolean bindingRequired;

    public StreamFeaturesPacket() {
        super();
    }

    /**
     * Indicates whether TLS is required
     * 
     * @return Returns the tlsRequired.
     */
    public boolean isTLSRequired() {
        return tlsRequired;
    }

    /**
     * Indicates whether TLS is required
     * 
     * @param tlsRequired The tlsRequired to set.
     */
    public void setTLSRequired(boolean tlsRequired) {
        this.tlsRequired = tlsRequired;
    }

    /**
     * Indicates whether TLS is supported
     * 
     * @return Returns the tlsSupported.
     */
    public boolean isTLSSupported() {
        return tlsSupported;
    }

    /**
     * Indicates whether TLS is supported
     * 
     * @param tlsSupported The tlsSupported to set.
     */
    public void setTLSSupported(boolean tlsSupported) {
        this.tlsSupported = tlsSupported;
    }

    /**
     * Whether the stream supports resource binding
     * 
     * @return Returns the bindingRequired.
     */
    public boolean isBindingRequired() {
        return bindingRequired;
    }

    /**
     * @param bindingRequired The bindingRequired to set.
     */
    public void setBindingRequired(boolean bindingSupported) {
        this.bindingRequired = bindingSupported;
    }

    /**
     * Whether the stream supports session establishment
     * 
     * @return Returns the sessionRequired.
     */
    public boolean isSessionRequired() {
        return sessionRequired;
    }

    /**
     * @param sessionRequired The sessionRequired to set.
     */
    public void setSessionRequired(boolean sessionSupported) {
        this.sessionRequired = sessionSupported;
    }
}
