package com.echomine.xmpp.helper;

import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.SendPacketFailedException;
import com.echomine.xmpp.XMPPStanzaErrorException;
import com.echomine.xmpp.packet.IQPacket;
import com.echomine.xmpp.packet.PrivacyIQPacket;
import com.echomine.xmpp.packet.PrivacyList;

/**
 * <p>
 * This helper provides methods to work with IQ Privacy as stated in the XMPP IM
 * RFC. Privacy management is actually a rather complicated subject. The
 * developer is recommended to consult the RFC on how to work with Privacy
 * management. An excerpt is provided below for reference.
 * <p>
 * If the type is "jid", then the 'value' attribute MUST contain a valid Jabber
 * ID. JIDs SHOULD be matched in the following order:
 * <ul>
 * <li>&lt;user@domain/resource> (only that resource matches)</li>
 * <li>&lt;user@domain> (any resource matches)</li>
 * <li>&lt;domain/resource> (only that resource matches)</li>
 * <li>&lt;domain> (the domain itself matches, as does any user@domain,
 * domain/resource, or address containing a subdomain)</li>
 * </ul>
 * <p>
 * If the type is "group", then the 'value' attribute SHOULD contain the name of
 * a group in the user's roster. (If a client attempts to update, create, or
 * delete a list item with a group that is not in the user's roster, the server
 * SHOULD return to the client an <item-not-found/> stanza error.)
 * <p>
 * If the type is "subscription", then the 'value' attribute MUST be one of
 * "both", "to", "from", or "none" as defined under Roster Syntax and Semantics,
 * where "none" includes entities that are totally unknown to the user and
 * therefore not in the user's roster at all.
 * <p>
 * If no 'type' attribute is included, the rule provides the "fall-through"
 * case.
 * <p>
 * The 'action' attribute MUST be included and its value MUST be either "allow"
 * or "deny".
 * <p>
 * The 'order' attribute MUST be included and its value MUST be a non-negative
 * integer that is unique among all items in the list. (If a client attempts to
 * create or update a list with non-unique order values, the server MUST return
 * to the client a &lt;bad-request/> stanza error.)
 * <p>
 * The &lt;item/> element MAY contain one or more child elements that enable an
 * entity to specify more granular control over which kinds of stanzas are to be
 * blocked (i.e., rather than blocking all stanzas). The allowable child
 * elements are:
 * <p>
 * &lt;message/> -- blocks incoming message stanzas <br>
 * &lt;iq/> -- blocks incoming IQ stanzas<br>
 * &lt;presence-in/> -- blocks incoming presence notifications<br>
 * &lt;presence-out/&gt; -- blocks outgoing presence notifications
 * <p>
 * Within the 'jabber:iq:privacy' namespace, the &lt;query/> child of an IQ
 * stanza of type 'set' MUST NOT include more than one child element (i.e., the
 * stanza MUST contain only one &lt;active/> element, one &lt;default/> element,
 * or one &lt;list/> element); if a sending entity violates this rule, the
 * receiving entity MUST return a &lt;bad-request/> stanza error.
 * <p>
 * When a client adds or updates a privacy list, the &lt;list/> element SHOULD
 * contain at least one &lt;item/> child element; when a client removes a
 * privacy list, the &lt;list/> element MUST NOT contain any &lt;item/> child
 * elements.
 * <p>
 * When a client updates a privacy list, it must include all of the desired
 * items (i.e., not a 'delta').
 * <p>
 * See <a href='http://www.xmpp.org/specs/rfc3921.html#privacy'>Block
 * Communications</a>
 * 
 */
public class PrivacyHelper {
    /**
     * Retrieves a list of privacy lists. this will include the active and
     * default list as well. The lists will only contain list names.. There will
     * be no list items included. That requires a separate query.
     * 
     * @param conn the connection that the packet will be sent through
     * @param wait true to wait for a reply
     * @return the reply packet containing the lists, or null if wait is false
     * @throws SendPacketFailedException if packet cannot be sent or if timeout
     *             occurred while waiting for reply
     * @throws XMPPStanzaErrorException if wait is true and reply packet is an
     *             error packet, then this exception will be thrown to indicate
     *             an error.
     */
    public static final PrivacyIQPacket getLists(IXMPPConnection conn, boolean wait) throws SendPacketFailedException, XMPPStanzaErrorException {
        PrivacyIQPacket req = new PrivacyIQPacket();
        req.setType(IQPacket.TYPE_GET);
        PrivacyIQPacket reply = (PrivacyIQPacket) conn.sendPacket(req, wait);
        if (reply != null && reply.isError())
            throw new XMPPStanzaErrorException(reply.getError());
        return reply;
    }

    /**
     * Retrieves the specified privacy list. If the privacy list cannot be
     * found, an error would be thrown by the server (item-not-found).
     * 
     * @param conn the connection that the packet will be sent through
     * @param listName the name of the list to retrieve
     * @param wait true to wait for a reply
     * @return the privacy list, or null if wait is false
     * @throws SendPacketFailedException if packet cannot be sent or if timeout
     *             occurred while waiting for reply
     * @throws XMPPStanzaErrorException if wait is true and reply packet is an
     *             error packet, then this exception will be thrown to indicate
     *             an error.
     */
    public static final PrivacyList getList(IXMPPConnection conn, String listName, boolean wait) throws SendPacketFailedException, XMPPStanzaErrorException {
        PrivacyIQPacket req = new PrivacyIQPacket();
        req.setType(IQPacket.TYPE_GET);
        PrivacyList list = new PrivacyList(listName);
        req.addPrivacyList(list);
        PrivacyIQPacket reply = (PrivacyIQPacket) conn.sendPacket(req, wait);
        if (reply != null && reply.isError())
            throw new XMPPStanzaErrorException(reply.getError());
        if (reply != null)
            return reply.getPrivacyList(0);
        return null;
    }

    /**
     * sets the active privacy list. The active list is the list used for this
     * session only. If the named list does not exist, the server will send an
     * item-not-found error. this error will be thrown here if wait is true and
     * a reply is received. In order to remove the current active list, set the
     * list name to an empty string (""). A null will not work. If the method
     * returns without any exceptions, then you can assume that the request was
     * successful (if wait is true).
     * 
     * @param conn the connection that the packet will be sent through
     * @param listName the name of the list to use as active list, or empty to
     *            remove active list
     * @param wait true to wait for a reply
     * @throws SendPacketFailedException if packet cannot be sent or if timeout
     *             occurred while waiting for reply
     * @throws XMPPStanzaErrorException if wait is true and reply packet is an
     *             error packet, then this exception will be thrown to indicate
     *             an error.
     */
    public static final void setActiveList(IXMPPConnection conn, String listName, boolean wait) throws SendPacketFailedException, XMPPStanzaErrorException {
        PrivacyIQPacket req = new PrivacyIQPacket();
        req.setType(IQPacket.TYPE_SET);
        req.setActiveName(listName);
        PrivacyIQPacket reply = (PrivacyIQPacket) conn.sendPacket(req, wait);
        if (reply != null && reply.isError())
            throw new XMPPStanzaErrorException(reply.getError());
    }

    /**
     * sets the default privacy list. The default list is the list used when an
     * active list is not set. If the named list does not exist, the server will
     * send an item-not-found error. this error will be thrown here if wait is
     * true and a reply is received. In order to remove the current default
     * list, set the list name to an empty string (""). A null will not work. If
     * the method returns without any exceptions, then you can assume that the
     * request was successful (if wait is true).
     * 
     * @param conn the connection that the packet will be sent through
     * @param listName the name of the list to use as active list, or empty to
     *            remove active list
     * @param wait true to wait for a reply
     * @throws SendPacketFailedException if packet cannot be sent or if timeout
     *             occurred while waiting for reply
     * @throws XMPPStanzaErrorException if wait is true and reply packet is an
     *             error packet, then this exception will be thrown to indicate
     *             an error.
     */
    public static final void setDefaultList(IXMPPConnection conn, String listName, boolean wait) throws SendPacketFailedException, XMPPStanzaErrorException {
        PrivacyIQPacket req = new PrivacyIQPacket();
        req.setType(IQPacket.TYPE_SET);
        req.setDefaultName(listName);
        PrivacyIQPacket reply = (PrivacyIQPacket) conn.sendPacket(req, wait);
        if (reply != null && reply.isError())
            throw new XMPPStanzaErrorException(reply.getError());
    }

    /**
     * This method will update a current list with the list provided by you. If
     * the list does not exist, then the list will be created. The provided
     * privacy list must have the 'name' field set to the list name you would
     * like to update or create. The list can contain one or more privacy items,
     * which specify your desired changes to the list by including ALL elements
     * in the list (not just the changes or "deltas"). To know how XMPP
     * processes privacy rules/items, please refer to the XMPP IM RFC for more
     * details.
     * <p>
     * After a list is updated, the server SHOULD broadcast to other entities
     * who have requested a the name of the changed list (packet with type 'set'
     * containing a privacy list with the name of the changed list). The other
     * entities, according to XMPP, MUST send an IQ result packet back
     * acknowledging receipt of the "privacy list push".
     * <p>
     * If the method returns without any exception (and wait is true), then you
     * can assume that the request was processed successfully.
     * <p>
     * See <a href='http://www.xmpp.org/specs/rfc3921.html#privacy'>Block
     * Communications</a>
     * 
     * @param conn the connection that the packet will be sent through
     * @param list the list containing the name of the list and the items (all
     *            items, not just changes)
     * @param wait true to wait for a reply
     * @throws SendPacketFailedException if packet cannot be sent or if timeout
     *             occurred while waiting for reply
     * @throws XMPPStanzaErrorException if wait is true and reply packet is an
     *             error packet, then this exception will be thrown to indicate
     *             an error.
     */
    public static final void updateList(IXMPPConnection conn, PrivacyList list, boolean wait) throws SendPacketFailedException, XMPPStanzaErrorException {
        PrivacyIQPacket req = new PrivacyIQPacket();
        req.setType(IQPacket.TYPE_SET);
        req.addPrivacyList(list);
        PrivacyIQPacket reply = (PrivacyIQPacket) conn.sendPacket(req, wait);
        if (reply != null && reply.isError())
            throw new XMPPStanzaErrorException(reply.getError());
    }

    /**
     * Removes the specified list. If a user attempts to remove a list that is
     * currently being applied to at least one resource other than the sending
     * resource, the server MUST return a &lt;conflict/> stanza error to the
     * user; i.e., the user MUST first set another list to active or default
     * before attempting to remove it. If the user attempts to remove a list but
     * a list by that name does not exist, the server MUST return an
     * &lt;item-not-found/> stanza error to the user. If the user attempts to
     * remove more than one list in the same request, the server MUST return a
     * &lt;bad-request/> stanza error to the user.
     * 
     * @param conn the connection that the packet will be sent through
     * @param listName the name of the list to retrieve
     * @param wait true to wait for a reply
     * @throws SendPacketFailedException if packet cannot be sent or if timeout
     *             occurred while waiting for reply
     * @throws XMPPStanzaErrorException if wait is true and reply packet is an
     *             error packet, then this exception will be thrown to indicate
     *             an error.
     */
    public static final void removeList(IXMPPConnection conn, String listName, boolean wait) throws SendPacketFailedException, XMPPStanzaErrorException {
        PrivacyIQPacket req = new PrivacyIQPacket();
        req.setType(IQPacket.TYPE_SET);
        PrivacyList list = new PrivacyList(listName);
        req.addPrivacyList(list);
        PrivacyIQPacket reply = (PrivacyIQPacket) conn.sendPacket(req, wait);
        if (reply != null && reply.isError())
            throw new XMPPStanzaErrorException(reply.getError());
    }
}
