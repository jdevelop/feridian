package com.echomine.xmpp;

import com.echomine.xmpp.stream.TLSFeature;


/**
 * This represents the stream:features packet used during handshake negotiation.
 * It contains a list of features supported by the server.
 */
public class StreamFeaturesPacket implements IPacket {
    TLSFeature tlsFeature;

    public StreamFeaturesPacket() {
        super();
    }

    /**
     * Obtains the tls feature object itself
     * @return the tls feature object
     */
    public TLSFeature getTLSFeature() {
        if (tlsFeature == null)
            tlsFeature = new TLSFeature();
        return tlsFeature;
    }
    
    /**
     * Indicates whether TLS is required
     * 
     * @return Returns the tlsRequired.
     */
    public boolean isTLSRequired() {
        if (tlsFeature != null)
            return tlsFeature.tlsRequired;
        return false;
    }

    /**
     * Indicates whether TLS is required
     * 
     * @param tlsRequired The tlsRequired to set.
     */
    public void setTLSRequired(boolean tlsRequired) {
        if (tlsFeature == null)
            tlsFeature = new TLSFeature();
        tlsFeature.tlsRequired = tlsRequired;
    }

    /**
     * Indicates whether TLS is supported
     * 
     * @return Returns the tlsSupported.
     */
    public boolean isTLSSupported() {
        if (tlsFeature != null)
            return tlsFeature.tlsSupported;
        return false;
    }

    /**
     * Indicates whether TLS is supported
     * 
     * @param tlsSupported The tlsSupported to set.
     */
    public void setTLSSupported(boolean tlsSupported) {
        if (tlsFeature == null)
            tlsFeature = new TLSFeature();
        tlsFeature.tlsSupported = tlsSupported;
    }
}
