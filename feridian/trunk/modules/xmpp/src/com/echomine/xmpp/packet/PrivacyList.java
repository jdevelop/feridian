package com.echomine.xmpp.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This represent one privacy list. A list contains the list name along with
 * optional list of privacy items.
 */
public class PrivacyList {
    private String name;
    private List<PrivacyItem> privacyItems;

    public PrivacyList() {
    }

    /**
     * Constructor to set the list name
     * 
     * @param name the list name
     */
    public PrivacyList(String name) {
        this.name = name;
    }

    /**
     * @return returns the list name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the list
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * May possibly be empty, but never null.
     * 
     * @return Returns a non-null list of privacy items
     */
    public List getItems() {
        if (privacyItems == null)
            return Collections.EMPTY_LIST;
        return privacyItems;
    }

    /**
     * sets the list of privacy items to use.
     * 
     * @param privacyItems The privacyItems to set.
     */
    public void setItems(List<PrivacyItem> privacyItems) {
        this.privacyItems = privacyItems;
    }

    /**
     * Retrieves the privacy item associated with the index.
     * 
     * @param idx the index of the privacy item to retrieve
     * @return the privacy item or null if no items exist
     * @throws ArrayIndexOutOfBoundsException if index is out of list size range
     */
    public PrivacyItem getItem(int idx) {
        if (privacyItems == null)
            return null;
        return privacyItems.get(idx);
    }

    /**
     * adds a privacy item to the list
     * 
     * @param item the item to add
     */
    public void addItem(PrivacyItem item) {
        if (privacyItems == null)
            privacyItems = new ArrayList<PrivacyItem>();
        privacyItems.add(item);
    }

    /**
     * Removes the specified item off the list
     * 
     * @param item the item to remove
     */
    public void removeItem(PrivacyItem item) {
        if (privacyItems != null)
            privacyItems.remove(item);
    }

    /**
     * checks whether the item is contained in the list
     * 
     * @param item the item to check
     * @return true if item is in list, false otherwise
     */
    public boolean containsItem(PrivacyItem item) {
        if (privacyItems != null)
            return privacyItems.contains(item);
        return false;
    }
}
