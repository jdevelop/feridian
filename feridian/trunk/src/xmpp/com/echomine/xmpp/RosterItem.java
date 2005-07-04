package com.echomine.xmpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is the main class containing information for each roster item.
 */
public class RosterItem {
    public static final String SUBSCRIBE_BOTH = "both";
    public static final String SUBSCRIBE_FROM = "from";
    public static final String SUBSCRIBE_TO = "to";
    public static final String SUBSCRIBE_NONE = "none";
    public static final String SUBSCRIBE_REMOVE = "remove";
    private static final String ASK_PENDING = "subscribe";
    private String ask;
    private String subscription;
    private JID jid;
    private String name;
    private List groups;

    /**
     * @return Returns the ask.
     */
    public boolean isPending() {
        if (ASK_PENDING.equals(ask))
            return true;
        return false;
    }

    /**
     * retrieve the list of groups that the item belongs to. If the user is not
     * in any group, then an empty list is returned.
     * 
     * @return Returns non-null list of groups
     */
    public List getGroups() {
        if (groups == null)
            return Collections.EMPTY_LIST;
        return groups;
    }

    /**
     * sets a list of groups. Set to null to indicate no groups.
     * 
     * @param groups The groups to set.
     */
    public void setGroups(List groups) {
        this.groups = groups;
    }

    /**
     * @return Returns the jid.
     */
    public JID getJid() {
        return jid;
    }

    /**
     * @param jid The jid to set.
     */
    public void setJid(JID jid) {
        this.jid = jid;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the current subscription state of the roster contact. The list
     * of available states are constants listed in this class.
     * 
     * @return Returns the subscription.
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * sets the subscription state
     * 
     * @param subscription The subscription to set.
     */
    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    /**
     * Adds this roster item to a group
     * 
     * @param groupName the name of the group
     */
    public void addGroup(String groupName) {
        if (groups == null)
            groups = new ArrayList();
        groups.add(groupName);
    }

    /**
     * Removes the roster contact from the group
     * 
     * @param groupName the group name
     */
    public void removeGroup(String groupName) {
        if (groups != null)
            groups.remove(groupName);
    }

    /**
     * is the item/user in the specified group?
     * 
     * @param groupName the group name
     * @return true item is in group, false otherwise
     */
    public boolean isInGroup(String groupName) {
        return groups.contains(groupName);
    }
}
