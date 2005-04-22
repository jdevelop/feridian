package com.echomine.xmpp;

/**
 * This represents the stream:features packet used during handshake negotiation.
 * It contains a list of features supported by the server.
 */
public class StreamFeaturesPacket implements IPacket {
    boolean tlsRequired;
    boolean tlsSupported;

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
}
