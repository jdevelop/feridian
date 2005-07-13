package com.echomine.xmpp.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * <p>
 * The iq packet that works with privacy as specified in XMPP specs. This packet
 * allows the user to set privacy lists and create allow/deny rules.
 * </p>
 * <p>
 * There are rules to using the privacy packets.
 * <ul>
 * <li>For a iq set packet, only one (default, active, or one list) set of item
 * can be specified. You can only set one list at a time, or set default, or set
 * active. If you violate the rule, the server will return an error.</li>
 * <li>For a iq get packet, any number of lists can be returns along with
 * default and active lists if it exists.</li>
 * </ul>
 * </p>
 */
public class IQPrivacyPacket extends IQPacket {
    private PrivacyList defaultList;
    private PrivacyList activeList;
    private List privacyLists;

    /**
     * @return Returns the activeName. Null if not set
     */
    public String getActiveName() {
        return activeList == null ? null : activeList.getName();
    }

    /**
     * @param activeName The activeName to set.
     */
    public void setActiveName(String listName) {
        if (listName == null) {
            activeList = null;
            return;
        } else if (activeList == null)
            activeList = new PrivacyList();
        setListName(activeList, listName);
    }

    /**
     * @return Returns the defaultName. Null if not set.
     */
    public String getDefaultName() {
        return defaultList == null ? null : defaultList.getName();
    }

    /**
     * Sets the name of the list to use as default. Default lists applies to all
     * sessions and applies automatically if no active list is set. NOTE: to set
     * default list, supply the name of the list name to set default for. Set
     * name to "" to REMOVE the default list. Set name to null for all other
     * occassions.
     * 
     * @param listName the list name to set for default.
     */
    public void setDefaultName(String listName) {
        if (listName == null) {
            defaultList = null;
            return;
        } else if (defaultList == null)
            defaultList = new PrivacyList();
        setListName(defaultList, listName);
    }

    /**
     * Retrieves an array of PrivacyList objects. May possibly be empty.
     * 
     * @see PrivacyList
     * @return Returns a non-null array of privacy lists
     */
    public List getPrivacyLists() {
        if (privacyLists == null)
            return Collections.EMPTY_LIST;
        return privacyLists;
    }

    /**
     * Sets the list of PrivacyList objects.
     * 
     * @see PrivacyList
     * @param privacyLists an array of PrivacyList objects
     */
    public void setPrivacyLists(List privacyLists) {
        this.privacyLists = privacyLists;
    }

    /**
     * Retrieves the privacy for the specified index
     * 
     * @param idx the index number
     * @return the privacy list or null if no list exists
     * @throws ArrayIndexOutOfBoundsException if idx is not within the list size
     */
    public PrivacyList getPrivacyList(int idx) {
        if (privacyLists == null)
            return null;
        return (PrivacyList) privacyLists.get(idx);
    }

    /**
     * adds a privacy list to the existing list.
     * 
     * @param list the privacy list to add
     */
    public void addPrivacyList(PrivacyList list) {
        if (privacyLists == null)
            privacyLists = new ArrayList();
        privacyLists.add(list);
    }

    /**
     * Removes a privacy list off the existing list
     * 
     * @param list the list to remove
     */
    public void removePrivacyList(PrivacyList list) {
        if (list != null)
            privacyLists.remove(list);
    }

    /**
     * This is a private method to do the setting of the list name for a
     * particular list. It is mainly used for setting default and active names.
     * If listName is null, then list will be set to default (no marshalling).
     * If listName is a non-empty name, then the list will set the name. If
     * listName is an empty string "", then it means to remove the specified
     * list.
     * 
     * @param list the list to work with
     * @param listName the list name to set
     */
    private void setListName(PrivacyList list, String listName) {
        if ("".equals(listName))
            list.setName(null);
        else
            list.setName(listName);
    }
}
