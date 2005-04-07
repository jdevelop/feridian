package com.echomine.jabber;

import com.echomine.common.SendMessageFailedException;
import com.echomine.jabber.msg.RosterIQMessage;
import com.echomine.jabber.msg.RosterItem;

import java.util.List;

/**
 * <p>Contains all the methods to work with rosters.  Roster Management is really simple.</p>
 * <p>Few things to note about roster management.  If you're adding a user to the roster, you will not be notified of the
 * user's presence.  You need to explicitly submit a presence subscription message after you add a user to the roster.
 * However, if you're removing an user from the roster, the server will automatically unsubscribe you from that user's
 * presence automatically.  Be sure to pay attention to this distinction or you will have some bugs in your application.</p>
 * <p>The recommended way of subscribing to an user's presence is to follow the simple procedure: (1) add the user to the
 * roster, including the groups the user will be in, (2) send a presence subscription message, and (3) wait for replies from
 * the server and update your application accordingly.  Do not update your application after you submit these messages.  The
 * server will send you roster/presence update information automatically, which you can then use to
 * update your application.</p>
 */
public class JabberRosterService {
    private JabberSession session;

    public JabberRosterService(JabberSession session) {
        this.session = session;
    }

    /**
     * requests the server to send a list of the roster, the reply will be
     * sent to roster listeners, so be sure to listen for the events.
     *
     * @param wait true if the caller wants to wait until there is a reply to the message
     * @return a list of RosterItem objects or null if wait is false.
     */
    public List requestRosterList(boolean wait) throws SendMessageFailedException {
        RosterIQMessage msg = new RosterIQMessage(JabberIQMessage.TYPE_GET);
        msg.setSynchronized(wait);
        session.sendMessage(msg);
        RosterIQMessage replymsg = (RosterIQMessage) msg.getReplyMessage();
        if (replymsg != null) return replymsg.getRosterItems();
        return null;
    }

    /**
     * this is a convenience method to add a JID to the roster.  It probably will satisfy
     * 80% of the uses.  If you need to set more features, then you should create your own
     * message and use the other addToRoster() method (ie. multiple groups rather than just one).
     *
     * @param wait true if the caller wants to wait until there is a reply to the message
     */
    public void addToRoster(JID jid, String name, String group, boolean wait) throws SendMessageFailedException {
        //simply create a message
        RosterItem item = new RosterItem(jid, name);
        item.addGroup(group);
        addToRoster(item, wait);
    }

    /**
     * adds a roster item to the list stored on the server.
     *
     * @param wait true if the caller wants to wait until there is a reply to the message
     */
    public void addToRoster(RosterItem item, boolean wait) throws SendMessageFailedException {
        RosterIQMessage msg = new RosterIQMessage(JabberIQMessage.TYPE_SET);
        //add the roster item to the server
        msg.setSynchronized(wait);
        msg.addRosterItem(item);
        session.sendMessage(msg);
    }

    /**
     * a convenience method to remove a JID from the roster.
     *
     * @param wait true if the caller wants to wait until there is a reply to the message
     */
    public void removeFromRoster(JID jid, boolean wait) throws SendMessageFailedException {
        RosterItem item = new RosterItem(jid, null);
        item.setRemove(true);
        removeFromRoster(item, wait);
    }

    /**
     * removes a specific item from the roster
     *
     * @param wait true if the caller wants to wait until there is a reply to the message
     */
    public void removeFromRoster(RosterItem item, boolean wait) throws SendMessageFailedException {
        RosterIQMessage msg = new RosterIQMessage(JabberIQMessage.TYPE_SET);
        //set the remove in case it didn't get set
        msg.setSynchronized(wait);
        item.setRemove(true);
        msg.addRosterItem(item);
        session.sendMessage(msg);
    }
}
