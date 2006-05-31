package com.echomine.xmpp.packet;

import com.echomine.xmpp.JID;
import com.echomine.xmpp.JIDFormatException;

/**
 * <p>
 * The class contains a privacy item used by the privacy IQ packet. When an
 * object of this class is instantiated, the default allow is false (ie. deny
 * rule). The order is 0. And the type is null. The type must be set or else the
 * marshalling will throw an exception.
 * </p>
 * <p>
 * Denying rules can be more detailed through the use of additional settings. If
 * iq blocking is set to true, then the rule will deny receiving any iq stanzas.
 * If message blocking is set to true, then the rule will deny receiving any
 * message stanzas. If presence in or out is set to true, then the rule will
 * deny receiving or sending presence information. These deny settings will only
 * be marshalled if action is set to deny. Otherwise, it is pointless to
 * marshall the settings. NOTE: If all these settings are set to false, then the
 * rule will BLOCK all iq, message, and presence information. The default is to
 * deny all messages that match the rule if the action is to deny.
 * </p>
 */
public class PrivacyItem {
    public static final String TYPE_GROUP = "group";
    public static final String TYPE_JID = "jid";
    public static final String TYPE_SUBSCRIPTION = "subscription";

    private String type;
    private boolean allow;
    private String value;
    private int order;
    private boolean denyIQ;
    private boolean denyIncomingPresence;
    private boolean denyOutgoingPresence;
    private boolean denyMessage;

    /**
     * sets whether to allow or deny this rule when it matches.
     * 
     * @return true if allow, false if deny.
     */
    public boolean isAllow() {
        return allow;
    }

    /**
     * sets whether to allow or deny this rule when it matches
     * 
     * @param allow true if allow, false if deny
     */
    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    /**
     * @return Returns the order.
     */
    public int getOrder() {
        return order;
    }

    /**
     * The order of the rule. In the privacy list, it must be an unique unsigned
     * integer value.
     * 
     * @param order The order to set.
     * @throws IllegalArgumentException if order value falls below 0
     */
    public void setOrder(int order) {
        if (order < 0)
            throw new IllegalArgumentException("Order value can only be a non-negative value");
        this.order = order;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retrieves the value contained. This value should conform with the
     * specified type.
     * 
     * @see RosterItem
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Retrieves the value contained. This value should conform with the type
     * specified. If the type is JID, then the value should be a valid jid. If
     * the type is subscription, then the value should be a valid subscription
     * type as listed in RosterItem. If the type is group, then the value should
     * be the name of a group that an user is in.
     * 
     * @see RosterItem
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * If the type is jid, then this convenience method serves to return the JID
     * of the parsed value. If the type is not jid and this method is called,
     * then an exception will be thrown. Thus, it is best to check the type
     * before calling this method.
     * 
     * @return the jid, or null if value is not set or type is not JID
     * @throws JIDFormatException if parsing causes error.
     */
    public JID getJid() {
        if (value == null || !TYPE_JID.equals(type))
            return null;
        return JID.parseJID(value);
    }

    /**
     * @return true if the item denies IQ, false otherwise
     */
    public boolean isDenyIQ() {
        return denyIQ;
    }

    /**
     * @param denyIQ true if item should deny IQ, false otherwise
     */
    public void setDenyIQ(boolean denyIQ) {
        this.denyIQ = denyIQ;
    }

    /**
     * @return true if item denies messages, false otherwise
     */
    public boolean isDenyMessage() {
        return denyMessage;
    }

    /**
     * @param denyMessage true if item denies messages, false otherwise
     */
    public void setDenyMessage(boolean denyMessage) {
        this.denyMessage = denyMessage;
    }

    /**
     * @return true if item denies incoming presence, false otherwise
     */
    public boolean isDenyIncomingPresence() {
        return denyIncomingPresence;
    }

    /**
     * By setting this to deny incoming presence, the user will not receive any
     * presence notifications from entities matching the rule.
     * 
     * @param denyPresenceIn true if item denies incoming presence, false
     *            otherwise
     */
    public void setDenyIncomingPresence(boolean denyPresenceIn) {
        this.denyIncomingPresence = denyPresenceIn;
    }

    /**
     * @return true if item denies outgoing presence, false otherwise
     */
    public boolean isDenyOutgoingPresence() {
        return denyOutgoingPresence;
    }

    /**
     * By setting this to deny outgoing presence, entities matching the rule
     * will not receiving presence notifications.
     * 
     * @param denyPresenceOut true if item denies outgoing presence, false
     *            otherwise
     */
    public void setDenyOutgoingPresence(boolean denyPresenceOut) {
        this.denyOutgoingPresence = denyPresenceOut;
    }
}
