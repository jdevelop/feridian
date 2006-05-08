package com.echomine.jabber;

import com.echomine.common.SendMessageFailedException;
import com.echomine.common.ParseException;
import com.echomine.jabber.msg.*;

import java.util.HashMap;
import java.util.List;

/**
 * the server service contains the methods that you will use 90% of the time.  For the rest, you will
 * need to manually create the individual methods (should be easy) and work with the message object directly.
 */
public class JabberServerService {
    private JabberSession session;

    public JabberServerService(JabberSession session) {
        this.session = session;
    }

    /**
     * retrieves the server's time.  The time zone will be your time zone, not the server's.
     * Thus, the time is the server's time in your time zone. If you need to retrieve other information such as the UTF date
     * or timezone, you should submit your own message This method is provided as a convenience and is synchronous.
     * @return the server's time or null if no server time can be retrieved
     */
    public String getServerTimeInLocal() throws SendMessageFailedException {
        TimeIQMessage msg = new TimeIQMessage();
        msg.setSynchronized(true);
        msg.setTo(session.getContext().getServerNameJID());
        session.sendMessage(msg);
	if (msg.isError()) return null;
        TimeIQMessage reply = (TimeIQMessage) msg.getReplyMessage();
        if (reply != null)
            return reply.getTimeInLocal().toString();
        return null;
    }

    /**
     * retrieves the server's time in a string. The time zone indicated is the server's time zone, not yours.  Thus, the time
     * is the server's local time. If you need to retrieve other information such as the UTF date or timezone, you should
     * submit your own message This method is provided as a convenience and is synchronous.
     * @return the server's time or null if no server time can be retrieved
     */
    public String getServerTime() throws SendMessageFailedException {
        TimeIQMessage msg = new TimeIQMessage();
        msg.setSynchronized(true);
        msg.setTo(session.getContext().getServerNameJID());
        session.sendMessage(msg);
	if (msg.isError()) return null;
        TimeIQMessage reply = (TimeIQMessage) msg.getReplyMessage();
        if (reply != null)
            return reply.getDisplay().toString();
        return null;
    }

    /**
     * obtains the server version.  The version is the jabber version that the server is running on.
     * It will include the Jabber Server plus the version, ie. "jsm 1.4.1".
     * This method is provided as a convenience and is synchronous.
     */
    public String getServerVersion() throws SendMessageFailedException {
        VersionIQMessage msg = new VersionIQMessage();
        msg.setSynchronized(true);
        msg.setTo(session.getContext().getServerNameJID());
        session.sendMessage(msg);
	if (msg.isError()) return null;
        VersionIQMessage reply = (VersionIQMessage) msg.getReplyMessage();
        String version = null;
        if (reply != null)
            version = reply.getName() + " " + reply.getVersion();
        return version;
    }

    /**
     * retrieves the server uptime synchronously. The time will return -1 if there's any problems.
     * @return the server uptime
     */
    public long getServerUptime() throws SendMessageFailedException {
        LastIQMessage msg = new LastIQMessage();
        msg.setTo(session.getContext().getServerNameJID());
        msg.setSynchronized(true);
        session.sendMessage(msg);
	if (msg.isError()) return -1;
        LastIQMessage reply = (LastIQMessage) msg.getReplyMessage();
        if (reply != null)
            return reply.getSeconds();
        return -1;
    }

    /**
     * Retrieves a list of agents that are available for the server.  These
     * agents can include conferencing, gateways/transports, Jabber User Directories,
     * etc.  Whatever the server offers is what this list contains.
     * The returned list is simply a list of services provided by the server.
     * This method uses the old style of agent retrieval (by using the jabber:iq:agents namespace)
     * for backwards compatibility.  The new way (using browsing) is also supported, but you
     * will have to instantiate your own Browse message for that (or use the generic browse method).
     * This method is synchronous and will not return until a reply or timeout is received.
     * Likely the Agent namespace is still supported by remote servers, but be on the lookout for changes.
     * @return a list of Agent objects, or null if there are none.
     * @deprecated Replace by new JEP-0030 Service Discovery Protocol
     * @see ServiceInfoIQMessage
     * @see ServiceItemsIQMessage
     */
    public List getAgents() throws SendMessageFailedException {
        AgentsIQMessage msg = new AgentsIQMessage();
        msg.setSynchronized(true);
        msg.setTo(session.getContext().getServerNameJID());
        //send the message
        session.sendMessage(msg);
	if (msg.isError()) return null;
        //obtain the reply
        AgentsIQMessage reply = (AgentsIQMessage) msg.getReplyMessage();
        if (reply != null)
            return reply.getAgentList();
        return null;
    }

    /**
     * browses to the JID that you specify. The JID is basically also
     * the entity to send this message to (JID and To are equal). If you
     * need to have them be different, then create your own Browse Message
     * and send it manually.  The JID Type is basically the "category/subtype" that you want to query for.
     * This method's synchronicity is changeable and will not return until a reply or error is received.
     * Note that if the message is sent synchronously, the return value will always be null.
     * @param jid the JID of the service/user/resource to browse to
     * @param type the JID Type in the format of "category/subtype"
     * @param wait whether to wait for reply or just simply send and return
     * @return JIDType if there are any JID Types for the browsed resource, null if wait is false.
     * @throws JabberMessageException if any error occurred while waiting for reply (ie. timeouts)
     * @throws ParseException if the type passed in is not in the proper format
     */
    public JIDType browse(JID jid, String type, boolean wait) throws JabberMessageException, SendMessageFailedException, ParseException {
        if (type == null || jid == null)
            throw new IllegalArgumentException("Neither the type nor the JID can be null, check to make sure that they're both not null.");
        BrowseIQMessage msg = new BrowseIQMessage(JabberIQMessage.TYPE_GET, type);
        JIDType jidtype = msg.getJIDType();
        msg.setTo(jid);
        jidtype.setJID(jid);
        msg.setSynchronized(wait);
        session.sendMessage(msg);
        if (wait) {
            if (msg.isError())
                throw new JabberMessageException(msg.getErrorMessage());
            BrowseIQMessage reply = (BrowseIQMessage) msg.getReplyMessage();
            if (reply != null)
                return reply.getJIDType();
        }
        return null;
    }

    /**
     * Convenience method to retrieve the search fields for a particular service.  Normally used with the JUD
     * (Jabber User Directory).  The method is synchronous and will return a hashmap of fields you can use.
     * @param jid the jid to search for
     * @return a hashmap of field you can search on
     */
    public HashMap getSearchFields(JID jid) throws SendMessageFailedException, JabberMessageException {
        SearchIQMessage msg = new SearchIQMessage(JabberIQMessage.TYPE_GET);
        msg.setTo(jid);
        msg.setSynchronized(true);
        session.sendMessage(msg);
        if (msg.isError())
            throw new JabberMessageException(msg.getErrorMessage());
        SearchIQMessage reply = (SearchIQMessage) msg.getReplyMessage();
        if (reply != null)
            return reply.getFields();
        else
            return new HashMap();
    }

    /**
     * convenience method to submit a search to the server.  If the search is submitted asynchronously,
     * then the return value is null.  Otherwise, the return value will be a List of SearchItem objects.
     * @param jid the jid to submit this search to, normally the JUD.
     * @param searchFields hashmap of fields that is filled with the search criteria
     * @param wait whether to send the message synchronously or asynchronously
     * @return the list of SearchItem objects, or null if wait is false
     * @throws SendMessageFailedException
     */
    public List search(JID jid, HashMap searchFields, boolean wait) throws SendMessageFailedException, JabberMessageException {
        SearchIQMessage msg = new SearchIQMessage(JabberIQMessage.TYPE_SET);
        msg.setTo(jid);
        msg.addFields(searchFields);
        msg.setSynchronized(wait);
        session.sendMessage(msg);
        if (wait) {
            if (msg.isError())
                throw new JabberMessageException(msg.getErrorMessage());
            SearchIQMessage reply = (SearchIQMessage) msg.getReplyMessage();
            if (reply != null)
                return reply.getResultItems();
        }
        return null;
    }

    /**
     * Convenience method to use the service discovery protocol to find service items available for use.
     * Currently, not very many servers support this feature so be careful if you decide to call this method
     * synchronously because likely the call will wait until timeout occurs, in which case, your client may
     * stall.  It is recommended that you call this method asynchronously until you know for sure that the
     * remote server supports this feature.
     * @param jid the JID to send the discovery request to
     * @param node an optional node name for discovering further items, or null if none is required
     * @param wait whether to wait for a reply or not
     * @return a list of ServiceItem objects
     * @throws SendMessageFailedException
     * @throws JabberMessageException
     */
    public List discoverItems(JID jid, String node, boolean wait) throws SendMessageFailedException, JabberMessageException {
        ServiceItemsIQMessage msg = new ServiceItemsIQMessage(JabberIQMessage.TYPE_GET);
        msg.setTo(jid);
        msg.setNode(node);
        msg.setSynchronized(wait);
        session.sendMessage(msg);
        if (wait) {
            if (msg.isError())
                throw new JabberMessageException(msg.getErrorMessage());
            ServiceItemsIQMessage reply = (ServiceItemsIQMessage) msg.getReplyMessage();
            if (reply != null)
                return reply.getItems();
        }
        return null;
    }
}
