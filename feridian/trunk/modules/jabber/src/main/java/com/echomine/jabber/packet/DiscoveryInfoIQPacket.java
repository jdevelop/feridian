package com.echomine.jabber.packet;

import java.util.List;

/**
 * <a href="http://www.xmpp.org/extensions/xep-0030.html">JEP-0030, Service discovery</a>
 * Defines structures for namespace http://jabber.org/protocol/disco#info
 */
public class DiscoveryInfoIQPacket extends DiscoveryIQPacket {

    public static final String NAMESPACE = "http://jabber.org/protocol/disco#info";

    private List<Feature> features;

    private List<Identity> identities;

    public DiscoveryInfoIQPacket() {
        super(NAMESPACE);
    }

    /**
     * @return the features
     */
    public List<Feature> getFeatures() {
        return features;
    }

    /**
     * @param features the features to set
     */
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    /**
     * @return the identities
     */
    public List<Identity> getIdentities() {
        return identities;
    }

    /**
     * @param identities the identities to set
     */
    public void setIdentities(List<Identity> identities) {
        this.identities = identities;
    }

}
