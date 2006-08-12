package com.echomine.jabber;

import com.echomine.common.SendMessageFailedException;
import com.echomine.jabber.msg.LastIQMessage;
import com.echomine.jabber.msg.OOBIQMessage;
import com.echomine.jabber.msg.PrivateXmlIQMessage;
import org.jdom.Element;

/**
 * the clients service contains the methods that you will use 90% of the time.  For the rest, you will
 * need to manually create the individual methods (should be easy) and work with the message object directly.
 * The service relates to working with client-related information, such as obtaining a client's last
 * signed on time.  The functionality is similar to the User Service, except that it deals with the other
 * clients rather than with your own information.
 */
public class JabberClientService {
    private JabberSession session;

    public JabberClientService(JabberSession session) {
        this.session = session;
    }

    /**
     * this method works two ways. First, if the user is online, then you will
     * get the idle time of the user.  If the user is offline, then you will
     * obtain the time since the last time the user has signed on.  If you want
     * to retrieve the last signed on message for the user, you will need
     * to create your own message and retrieve the information directly yourself.
     * This method submits the message synchronously. <p><b>BUG:</b> Currently, Jabber Instant Messenger does not send the
     * Last Idle Time message correctly; it is not sending back the result
     * with the ID of the request message.  Thus, synchronized messaging will
     * fail.  However, if you're using unsynchronized messaging, then it should
     * be ok since you're not dependent on the ID.</p>
     * @return the time (seconds) since the last time the user signed on.
     */
    public long getClientLastOnline(JID jid) throws SendMessageFailedException {
        LastIQMessage msg = new LastIQMessage();
        msg.setTo(jid);
        msg.setSynchronized(true);
        session.sendMessage(msg);
        LastIQMessage reply = (LastIQMessage) msg.getReplyMessage();
        if (reply != null)
            return reply.getSeconds();
        return -1;
    }

    /**
     * sends an idle time reply back to the client who sent us the request.
     * This message is Asynchronous and will return immediately after sending the message.
     * @param jid the jid of the user who submitted the request
     * @param msgID the message id of the request message
     * @param seconds the idle time in seconds
     */
    public void sendIdleTimeReply(JID jid, String msgID, long seconds) throws SendMessageFailedException {
        LastIQMessage msg = new LastIQMessage(JabberIQMessage.TYPE_RESULT);
        msg.setTo(jid);
        msg.setMessageID(msgID);
        msg.setSeconds(seconds);
        msg.setReplyRequired(false);
        session.sendMessage(msg);
    }

    /**
     * Provides a convenient way to send an OOB request to the specified user.  The message will be
     * sent asynchronously and will not listen for a reply either.  The reply might come back later
     * in reply to the request (success, failure, etc).  Thus, the message id is returned so that
     * you can match it later with the reply when it comes back.
     * @param jid the user JID to send this request to
     * @param url the URL to retrieve the file
     * @param desc the description of the URL
     * @return the message ID associated with the message that is sent out
     */
    public String sendOutOfBandRequest(JID jid, String url, String desc) throws SendMessageFailedException {
        OOBIQMessage msg = new OOBIQMessage(JabberIQMessage.TYPE_SET);
        msg.setTo(jid);
        msg.setUrl(url);
        msg.setDescription(desc);
        return msg.getMessageID();
    }

    /**
     * Provides a convenient way to send a request to retrieve private data.  The message will be sent
     * synchronously and will return either the private data or null.
     * @param elemName the element name that the private data is stored as
     * @param ns the namespace that the private data is stored in
     * @return the private data as an Element, null if the private data requested does not exist
     */
    public Element getPrivateData(String elemName, String ns) throws SendMessageFailedException {
        PrivateXmlIQMessage msg = new PrivateXmlIQMessage();
        msg.setSynchronized(true);
        msg.setPrivateDataRequest(elemName, ns);
        session.sendMessage(msg);
        PrivateXmlIQMessage reply = (PrivateXmlIQMessage) msg.getReplyMessage();
        if (reply != null)
            return reply.getPrivateData(elemName, ns);
        return null;
    }
}
