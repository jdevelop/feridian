package com.echomine.jabber.packet;

import java.util.List;

/**
 * <a href="http://www.xmpp.org/extensions/xep-0030.html">JEP-0030, Service discovery</a>
 * Defines structures for namespace http://jabber.org/protocol/disco#items
 */

public class DiscoveryItemIQPacket extends DiscoveryIQPacket {

    public static final String NAMESPACE = "http://jabber.org/protocol/disco#items";

    private List<Item> items;

    public DiscoveryItemIQPacket() {
        super(NAMESPACE);
    }

    /**
     * @return the items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

}
