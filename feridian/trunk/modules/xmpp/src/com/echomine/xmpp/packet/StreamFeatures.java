package com.echomine.xmpp.packet;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.echomine.xmpp.IPacket;
import com.echomine.xmpp.XMPPConstants;

/**
 * This represents the stream:features packet used during handshake negotiation.
 * It contains a list of features supported by the server.
 */
public class StreamFeatures implements IPacket {
    private static Log log = LogFactory.getLog(StreamFeatures.class);
    private boolean tlsRequired;
    private LinkedHashMap features = new LinkedHashMap();

    /**
     * Clears all the data for reuse of this object
     */
    public void clear() {
        tlsRequired = false;
        features.clear();
    }

    /**
     * checks if TLS is required.
     * 
     * @return true if TLS is required, false otherwise
     */
    public boolean isTLSRequired() {
        return tlsRequired;
    }

    /**
     * set TLS required to true or false. Setting TLS to required (true) will
     * also add the TLS feature (ie. TLS supported will be true). Setting TLS to
     * false (not required) will not do anything; TLS may or may not be
     * supported. Thus, setting TLS required to false should be followed by the
     * removal of the TLS support if that is the desired behavior.
     * 
     * @param tlsRequired true if TLS is required.
     */
    public void setTLSRequired(boolean tlsRequired) {
        // setting element name and value to null is intentional
        // since we know that TLS is (un)marshalled separately.
        // yes, it's a cheating technique that should NOT be copied
        if (tlsRequired)
            addFeature(XMPPConstants.NS_STREAM_TLS, null, null);
        this.tlsRequired = tlsRequired;
    }

    /**
     * Indicates whether TLS is supported This is a convenience method for
     * isFeatureSupported()
     * 
     * @return Returns the tlsSupported.
     */
    public boolean isTLSSupported() {
        return isFeatureSupported(XMPPConstants.NS_STREAM_TLS);
    }

    /**
     * Whether the stream supports resource binding. This is a convenience
     * method for isFeatureSupported()
     * 
     * @return Returns the bindingRequired.
     */
    public boolean isBindingSupported() {
        return isFeatureSupported(XMPPConstants.NS_STREAM_BINDING);
    }

    /**
     * Whether the stream supports session establishment This is a convenience
     * method for isFeatureSupported()
     * 
     * @return Returns the sessionRequired.
     */
    public boolean isSessionSupported() {
        return isFeatureSupported(XMPPConstants.NS_STREAM_SESSION);
    }

    /**
     * Convenience method to check if SASL is supported
     * 
     * @return true if SASL is supported
     */
    public boolean isSaslSupported() {
        return isFeatureSupported(XMPPConstants.NS_STREAM_SASL);
    }

    /**
     * Adds a supported feature. It is assumed that if you set the value here,
     * then a binding file must be specified for that value. If a value is set,
     * then elementName is optional and can be null since it will not be used.
     * If the value is not set, then the element name must be set.
     * 
     * @param namespace the namespace for the supported feature
     * @param elementName the element name associated with the feature
     * @param value optional value to be associated with the feature, null if
     *            feature is a one-liner
     */
    public void addFeature(String namespace, String elementName, Object value) {
        if (namespace == null)
            throw new IllegalArgumentException("namespace for feature cannot be null");
        features.put(namespace, new StreamFeature(elementName, value));
        if (log.isDebugEnabled())
            log.debug("Added feature namespace: " + namespace);
    }

    /**
     * Removes the feature tied to the specified namespace
     * 
     * @param namespace the feature namespace
     */
    public void removeFeature(String namespace) {
        features.remove(namespace);
    }

    /**
     * checks if the feature is supported.
     * 
     * @param namespace the feature namespace to check
     * @return true if feature is supported
     */
    public boolean isFeatureSupported(String namespace) {
        return features.containsKey(namespace);
    }

    /**
     * Retrieves the feature object associated with the namespace. If the
     * namespace feature cannot be found, then null will be returned.
     * 
     * @param namespace the feature with the specified namespace
     * @return the feature, or null if none is found
     */
    public StreamFeature getFeature(String namespace) {
        return (StreamFeature) features.get(namespace);
    }

    /**
     * Retrieves a non-modifiable list of features
     */
    public Map getFeatures() {
        return Collections.unmodifiableMap(features);
    }

    /**
     * Checks if the specified SASL mechanism is supported
     * 
     * @param mechanism the mechanism to check
     * @return true if mechanism is supported, false otherwise.
     */
    public boolean isSaslMechanismSupported(String mechanism) {
        StreamFeature feature = (StreamFeature) getFeature(XMPPConstants.NS_STREAM_SASL);
        if (feature == null)
            return false;
        return ((List) feature.getValue()).contains(mechanism);
    }

    /**
     * Returns a unmodifiable list of supported mechanisms strings.
     * 
     * @return a non-null list of sasl mechanism strings, but possibly empty
     */
    public List getSaslMechanisms() {
        StreamFeature feature = (StreamFeature) getFeature(XMPPConstants.NS_STREAM_SASL);
        if (feature == null)
            return Collections.EMPTY_LIST;
        return Collections.unmodifiableList((List) feature.getValue());
    }
}
