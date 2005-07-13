package com.echomine.xmpp.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Represents the Roster IQ packet. It is simply a class that contains a list of
 * roster items.
 */
public class IQRosterPacket extends IQPacket {
    private List rosterItems;

    public IQRosterPacket() {
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
