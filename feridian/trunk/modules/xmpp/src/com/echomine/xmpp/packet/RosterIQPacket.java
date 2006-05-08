package com.echomine.xmpp.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the Roster IQ packet. It is simply a class that contains a list of
 * roster items.
 * <p>
 * The state of the presence subscription in relation to a roster item is
 * captured in the 'subscription' attribute of the <item/> element. Allowable
 * values for this attribute are:
 * 
 * <ul>
 * <li>"none" -- the user does not have a subscription to the contact's
 * presence information, and the contact does not have a subscription to the
 * user's presence information</li>
 * <li>"to" -- the user has a subscription to the contact's presence
 * information, but the contact does not have a subscription to the user's
 * presence information</li>
 * <li>"from" -- the contact has a subscription to the user's presence
 * information, but the user does not have a subscription to the contact's
 * presence information</li>
 * <li>"both" -- both the user and the contact have subscriptions to each
 * other's presence information</li>
 * </ul>
 * </p>
 * <p>
 * Each &lt;item/> element MAY contain a 'name' attribute, which sets the
 * "nickname" to be associated with the JID, as determined by the user (not the
 * contact). The value of the 'name' attribute is opaque.
 * 
 * Each &lt;item/> element MAY contain one or more &lt;group/> child elements,
 * for use in collecting roster items into various categories. The XML character
 * data of the &lt;group/> element is opaque.
 * </p>
 * <p>
 * Upon connecting to the server and becoming an active resource, a client
 * SHOULD request the roster before sending initial presence (however, because
 * receiving the roster may not be desirable for all resources, e.g., a
 * connection with limited bandwidth, the client's request for the roster is
 * OPTIONAL).
 * </p>
 */
public class RosterIQPacket extends IQPacket {
    private List rosterItems;

    public RosterIQPacket() {
        super();
    }

    /**
     * returns the list of roster items.
     * 
     * @return Returns a non-null list of rosterItems.
     */
    public List getRosterItems() {
        if (rosterItems == null)
            return Collections.EMPTY_LIST;
        return rosterItems;
    }

    /**
     * sets the list of roster items
     * 
     * @param rosterItems The rosterItems to set.
     */
    public void setRosterItems(List rosterItems) {
        this.rosterItems = rosterItems;
    }

    /**
     * Adds a roster item to the current roster list
     * 
     * @param item the item to add
     */
    public void addItem(RosterItem item) {
        if (item == null)
            return;
        if (rosterItems == null)
            rosterItems = new ArrayList();
        rosterItems.add(item);
    }
}
