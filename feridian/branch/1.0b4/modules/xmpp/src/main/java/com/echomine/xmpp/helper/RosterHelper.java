package com.echomine.xmpp.helper;

import java.util.Collections;
import java.util.List;

import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.SendPacketFailedException;
import com.echomine.xmpp.XMPPStanzaErrorException;
import com.echomine.xmpp.packet.IQPacket;
import com.echomine.xmpp.packet.RosterIQPacket;
import com.echomine.xmpp.packet.RosterItem;

/**
 * <p>
 * This is the helper class for working with rosters. These are static
 * convenience methods for easily working with some of the most common uses.
 * Advanced uses SHOULD directly instantiate the roster packet.
 * <p>
 * Please refer to the roster packet for more details on how to work with
 * rosters
 * 
 * @see com.echomine.xmpp.packet.RosterIQPacket
 * @see com.echomine.xmpp.packet.RosterItem
 */
public class RosterHelper {

    /**
     * This will request the roster list from the server.
     * 
     * @param conn the connection that the packet will be sent through
     * @param wait true to wait for a reply
     * @return a non-null list of roster items (may possibly be empty)
     * @throws SendPacketFailedException if packet cannot be sent or if timeout
     *             occurred while waiting for reply
     * @throws XMPPStanzaErrorException if wait is true and reply packet is an
     *             error packet, then this exception will be thrown to indicate
     *             an error.
     */
    public static final List getRosterList(IXMPPConnection conn, boolean wait) throws SendPacketFailedException, XMPPStanzaErrorException {
        RosterIQPacket packet = new RosterIQPacket();
        packet.setType(IQPacket.TYPE_GET);
        RosterIQPacket reply = (RosterIQPacket) conn.sendPacket(packet, wait);
        if (reply != null && reply.isError())
            throw new XMPPStanzaErrorException(reply.getError());
        if (reply != null)
            return reply.getRosterItems();
        else
            return Collections.EMPTY_LIST;
    }

    /**
     * this is a convenience method to add a JID to the roster. It probably will
     * satisfy 80% of the uses. If you need to set more features, then you
     * should create your own roster item and use the alternate method.
     * Normally, the jid of the contact (friends, etc) is a jid without
     * resource. This method does not return any values because if there is an
     * error, exception will be thrown instead. Otherwise, you can assume that
     * the request was successful. This only applies when wait is true.
     * 
     * @param conn the connection that the packet will be sent through
     * @param jid the contact's JID
     * @param name optional nickname for the contact
     * @param group optional group name for the contact to be in
     * @param wait true to wait for confirmation, false to return immediately
     * @throws SendPacketFailedException if packet cannot be sent or if timeout
     *             occurred while waiting for reply
     * @throws XMPPStanzaErrorException if wait is true and reply packet is an
     *             error packet, then this exception will be thrown to indicate
     *             an error.
     */
    public static final void addItem(IXMPPConnection conn, JID jid, String name, String group, boolean wait) throws SendPacketFailedException, XMPPStanzaErrorException {
        RosterItem item = new RosterItem();
        item.setJid(jid);
        item.setName(name);
        item.addGroup(group);
        addItem(conn, item, wait);
    }

    /**
     * This is a convenience method to add a roster item to the roster. This
     * method does not return any values because if there is an error, exception
     * will be thrown instead. Otherwise, you can assume that the request was
     * successful. This only applies when wait is true.
     * 
     * @param conn the connection that the packet will be sent through
     * @param item the roster item to add
     * @param wait true to wait for confirmation, false to return immediately
     * @throws SendPacketFailedException if packet cannot be sent or if timeout
     *             occurred while waiting for reply
     * @throws XMPPStanzaErrorException if wait is true and reply packet is an
     *             error packet, then this exception will be thrown to indicate
     *             an error.
     */
    public static final void addItem(IXMPPConnection conn, RosterItem item, boolean wait) throws SendPacketFailedException, XMPPStanzaErrorException {
        RosterIQPacket packet = new RosterIQPacket();
        packet.setType(IQPacket.TYPE_SET);
        packet.addItem(item);
        IQPacket reply = (IQPacket) conn.sendPacket(packet, wait);
        if (reply != null && reply.isError())
            throw new XMPPStanzaErrorException(reply.getError());
    }

    /**
     * This method will remove the specified JID from the roster list. The
     * method simply creates the roster packet and sets the subscription for the
     * specified JID to remove. This is one of the only times where the
     * subscription can be specified.
     * 
     * @param conn the connection that the packet will be sent through
     * @param jid the contact/resource jid to remove
     * @param wait true to wait for confirmation, false to return immediately
     * @throws SendPacketFailedException if packet cannot be sent or if timeout
     *             occurred while waiting for reply
     * @throws XMPPStanzaErrorException if wait is true and reply packet is an
     *             error packet, then this exception will be thrown to indicate
     *             an error.
     */
    public static final void removeItem(IXMPPConnection conn, JID jid, boolean wait) throws SendPacketFailedException, XMPPStanzaErrorException {
        RosterItem item = new RosterItem();
        item.setJid(jid);
        item.setRemove(true);
        RosterIQPacket packet = new RosterIQPacket();
        packet.setType(IQPacket.TYPE_SET);
        packet.addItem(item);
        IQPacket reply = (IQPacket) conn.sendPacket(packet, wait);
        if (reply != null && reply.isError())
            throw new XMPPStanzaErrorException(reply.getError());
    }
}
