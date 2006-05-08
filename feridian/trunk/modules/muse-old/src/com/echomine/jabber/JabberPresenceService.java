package com.echomine.jabber;

import com.echomine.common.SendMessageFailedException;

/**
 * <p>Deals with all presence broadcast and receiving.  The service offers easy ways to set your presence
 * and also obtain presence.</p> <p>NOTE: The presence works in conjunction with the Roster.  Here is usually what happens.
 * (1) When you subscribe to a presence, the user will appear in your roster (with no group and no nickname) automatically.
 * (2) When you unsubscribe from a presence, the user will remain in the roster and must be explicitly removed.  However, you
 * will not receive any presence notification even though the person is in your roster.</p>
 * <p>The recommended way in adding and removing subscriptions is through the Roster first.  Take a look at RosterService to
 * see what the recommended way in adding and removing users.</p>
 */
public class JabberPresenceService implements PresenceCode {
    private JabberSession session;

    public JabberPresenceService(JabberSession session) {
        this.session = session;
    }

    /**
     * sets status to available with optional show state and status line. The default priority is 0.
     *
     * @param showState optional parameter to set the show state (chat, away, extended away, etc), null if not setting a state
     * @param status    the status to set, or null if not setting a status text
     * @param wait      true if the caller wants to wait until there is a reply to the message
     */
    public void setToAvailable(String showState, String status, boolean wait) throws SendMessageFailedException {
        JabberPresenceMessage msg = new JabberPresenceMessage(TYPE_AVAILABLE);
        if (showState != null)
            msg.setShowState(showState);
        if (status != null)
            msg.setStatus(status);
        else
            msg.setStatus("Online");
        msg.setSynchronized(wait);
        session.sendMessage(msg);
    }

    /**
     * Sets the status to unavailable.  Other users will no longer see you online
     */
    public void setToUnavailable() throws SendMessageFailedException {
        session.sendMessage(new JabberPresenceMessage(TYPE_UNAVAILABLE));
    }

    /**
     * request to subscribe to a JID's presence.  JID is an unique identifier for a user, resource, etc.
     */
    public void subscribe(JID jid) throws SendMessageFailedException {
        JabberPresenceMessage msg = new JabberPresenceMessage(TYPE_SUBSCRIBE);
        //set the jid to subscribe to
        msg.setTo(jid);
        session.sendMessage(msg);
    }

    /**
     * unsubscribe from the jid so as not to receive further presence notifications from that jid.
     */
    public void unsubscribe(JID jid) throws SendMessageFailedException {
        JabberPresenceMessage msg = new JabberPresenceMessage(TYPE_UNSUBSCRIBE);
        //set the jid
        msg.setTo(jid);
        session.sendMessage(msg);
    }

    /**
     * sends a "subscribed" (not subscribe) message to the jid, essentially
     * giving permission to the JID to know about your subscription.
     */
    public void sendSubscribed(JID jid) throws SendMessageFailedException {
        JabberPresenceMessage msg = new JabberPresenceMessage(TYPE_SUBSCRIBED);
        msg.setTo(jid);
        session.sendMessage(msg);
    }

    /**
     * sends a "unsubscribed" (not unsubscribe) message to the jid, essentially
     * cancelling any permission given previous to be subscribed to you.  This
     * effectively makes it so the JID does not receive any of your presence info
     */
    public void sendUnsubscribed(JID jid) throws SendMessageFailedException {
        JabberPresenceMessage msg = new JabberPresenceMessage(TYPE_UNSUBSCRIBED);
        msg.setTo(jid);
        session.sendMessage(msg);
    }

    /**
     * when you receive a subscribe message, you may pass that subscribe message to this
     * method to accept the subscription.  The method simply creates a "subscribed" message
     * and fill in the information from the original message, and then send it out.
     *
     * @param msg the subscribe message sent by the remote
     */
    public void acceptSubscribe(JabberPresenceMessage msg) throws SendMessageFailedException {
        JabberPresenceMessage reply = new JabberPresenceMessage(TYPE_SUBSCRIBED);
        reply.setTo(msg.getFrom());
        session.sendMessage(reply);
    }

    /**
     * Takes a subscription request message and uses it to return a reply that declines the subscription request.
     *
     * @param msg the subscribe message sent by the remote
     */
    public void denySubscribe(JabberPresenceMessage msg) throws SendMessageFailedException {
        JabberPresenceMessage reply = new JabberPresenceMessage(TYPE_UNSUBSCRIBED);
        reply.setTo(msg.getFrom());
        session.sendMessage(reply);
    }
}
