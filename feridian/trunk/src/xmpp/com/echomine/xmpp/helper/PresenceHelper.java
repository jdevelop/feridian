package com.echomine.xmpp.helper;

import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.SendPacketFailedException;
import com.echomine.xmpp.packet.PresencePacket;

/**
 * This helper provides convenience methods to work with presence states and
 * subscriptions. A majority of presence tasks can be done through this helper.
 * Advanced uses SHOULD directly instantiate the presence packet.
 * 
 * @see com.echomine.xmpp.packet.PresencePacket
 */
public class PresenceHelper {
    /**
     * sets status to available with optional show state and status line. The
     * default priority is 0. This method is asynchronous. If you need to tag
     * additional extension information to the presence packet, you will need to
     * create your own presence packet.
     * 
     * @param conn the connection to send this packet through
     * @param show optional parameter to set the show state (available states
     *            are listed in PresencePacket), null if not setting a state
     * @param status optional status to set, or null if not setting a status
     *            text
     */
    public static final void setAvailable(IXMPPConnection conn, String show, String status) throws SendPacketFailedException {
        PresencePacket packet = new PresencePacket();
        if (show != null)
            packet.setShow(show);
        if (status != null)
            packet.setStatus(status);
        else
            packet.setStatus("Online");
        conn.sendPacket(packet, false);
    }

    /**
     * Sets the status to unavailable. Other users will no longer see you
     * online. If you require sending additional extension data in the presence
     * packet, you will need to create your own presence packet.
     * 
     * @param conn the conection to send the packet through
     */
    public static final void setUnavailable(IXMPPConnection conn) throws SendPacketFailedException {
        PresencePacket packet = new PresencePacket();
        packet.setType(PresencePacket.TYPE_UNAVAILABLE);
        conn.sendPacket(packet, false);
    }

    /**
     * sends a request to subscribe to a JID's presence. JID is an unique
     * identifier for a user, resource, etc. If the subscription request is
     * being sent to an instant messaging contact, the JID supplied in the 'to'
     * attribute SHOULD be of the form <contact@example.org> rather than
     * <contact@example.org/resource>, since the desired result is normally for
     * the user to receive presence from all of the contact's resources, not
     * merely the particular resource specified in the 'to' attribute.
     * 
     * @param conn the conection to send the packet through
     * @param jid the jid of the contact to request subscription
     */
    public static final void subscribe(IXMPPConnection conn, JID jid) throws SendPacketFailedException {
        PresencePacket packet = new PresencePacket();
        packet.setType(PresencePacket.TYPE_SUBSCRIBE);
        packet.setTo(jid);
        conn.sendPacket(packet, false);
    }

    /**
     * request to unsubscribe from the jid so as not to receive further presence
     * notifications from that jid. This is only one way. If the other contact
     * is subscribed to your presence, he will still get your presence
     * information.
     * 
     * @param conn the conection to send the packet through
     * @param jid the jid of the contact to request subscription
     */
    public static final void unsubscribe(IXMPPConnection conn, JID jid) throws SendPacketFailedException {
        PresencePacket packet = new PresencePacket();
        packet.setType(PresencePacket.TYPE_UNSUBSCRIBE);
        packet.setTo(jid);
        conn.sendPacket(packet, false);
    }

    /**
     * sends a "subscribed" (not subscribe) message to the jid, essentially
     * giving permission to the JID to know about your subscription. This is
     * used to confirm and accept a subscription request.
     * 
     * @param conn the conection to send the packet through
     * @param jid the jid of the contact to request subscription
     */
    public static final void sendSubscribed(IXMPPConnection conn, JID jid) throws SendPacketFailedException {
        PresencePacket packet = new PresencePacket();
        packet.setType(PresencePacket.TYPE_SUBSCRIBED);
        packet.setTo(jid);
        conn.sendPacket(packet, false);
    }

    /**
     * sends a "unsubscribed" (not unsubscribe) message to the jid, essentially
     * cancelling any permission given previous to be subscribed to you. This
     * effectively makes it so the JID does not receive any of your presence
     * info. This can be sent to reject a subscription request OR to cancel a
     * previously accepted subscription (essentially blocking the user).
     * 
     * @param conn the conection to send the packet through
     * @param jid the jid of the contact to request subscription
     */
    public static final void sendUnsubscribed(IXMPPConnection conn, JID jid) throws SendPacketFailedException {
        PresencePacket packet = new PresencePacket();
        packet.setType(PresencePacket.TYPE_UNSUBSCRIBED);
        packet.setTo(jid);
        conn.sendPacket(packet, false);
    }
}
