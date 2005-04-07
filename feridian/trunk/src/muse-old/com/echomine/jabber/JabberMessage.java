package com.echomine.jabber;

import com.echomine.common.ParseException;

import java.util.HashMap;

/**
 * <p>The base class for all Jabber messages.  Normally, you wouldn't have to extend from this class.  The easiest way is to
 * extend from the DOM-based Jabber message.  However, if you would like to do your own parsing and encoding, you may do so.</p>
 * <p><b>NOTE:</b> Each message should technically be used once only because certain states are set once the message is
 * sent/received. Thus, if you try to reuse the message object, you might get unexpected behavior.</p>
 * <p>Note that jabber messages are usually dual-identity based.  One identity is for outgoing and the other is for incoming.
 * What this means is that the data being sent out is different from incoming parsed data. Normally this is not an issue since
 * encode() will know exactly what information to send out.</p> <p>When writing your own custom messages, be sure to know what
 * kind of content is being sent and what is expected from the server.</p>
 * <p>Jabber Messages have default support for storing X Namespace.
 * However, nothing is done with it.  You will have to manually add the X Namespaces if you need to.  They are only available
 * to you when you know they exist and you can process them.</p> <p>Jabber Messages can also be synchronized or not synchronized.
 * By synchronized, it means that when an actor (ie. client application) calls send() to send a message, it will not return
 * until a message reply is received.  Be very careful with this as message replies are tied to the original message through
 * the unique "id" attribute.  Thus, if there are no replies for the original message and you set it to synchronized, it might
 * hang the entire application.  Currently, the default behavior will do some sanity checking to make sure that this message
 * can be sent synchronously or not.  If it can't, it'll just work with the message asynchronously, even if synchronization is
 * stated.  Also, make sure that you don't try to send a synchronized message when you're inside a message receiving method
 * (ie. Message Listeners).  Since you're inside the incoming message reader thread when processing
 * a message, sending a synchronized message will just hang the reader thread and not allow it to process more incoming
 * messages, thus causing a hang.  If you need to send a synchronized message inside a message processing method, you need to
 * call the function sendLater() method of JabberConnection.</p> <p>Jabber Messages also contain a very special method for
 * handling message replies. This replyReceived() method actually has the ability to alter the behavior of the underlying
 * message processor.  It is called <b>Automatic Message Transformation</b>.  First, this method does its work to react to the
 * message reply.  Afterwards, it needs to return a Jabber Message instance.  The default behavior is to return the message
 * that was passed in (ie. the reply message) since it's the original message received from the server.  However, you have the
 * ability to create a totally new or altered message and return it because the message processor send the message that you
 * return from this method.  For instance, based on the reply message, you decide to send a totally new message that is
 * supposed to reflect what this reply message tells you.  A concrete example can be that when a login error message returns
 * (a reply message to your login request), you might return an End Session Message that effectively tells the high level
 * application to end the session, and therefore the
 * connection.  Not a very useful example, but it demonstrates the point. </p>
 */
abstract public class JabberMessage {
    private JabberMessage replymsg;
    protected String messageID;
    private boolean replyRequired = false;
    private boolean sync = false;
    //default 30 seconds
    private HashMap xMsgs;
    private long timeout = 30000;
    private boolean sendXMsgs = true;

    public JabberMessage() {
        //obtain a unique message id
        messageID = MessageID.nextID();
    }

    /**
     * the method will wake and interrupt all the waiting threads due to some sort of shutdown request or something
     */
    public void interrupt() {
        if (sync) {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    /**
     * sets the timeout for this message when waiting for reply.  The default is 30 seconds.
     *
     * @param ms timeout in milliseconds. 0 to disable timeout and wait forever.
     */
    public void setTimeout(long ms) {
        this.timeout = ms;
    }

    public long getTimeout() {
        return timeout;
    }

    /**
     * check if this message requires waiting for a reply.
     *
     * @return true if message requires waiting for a reply, false otherwise.
     */
    public boolean isReplyRequired() {
        return replyRequired;
    }

    /**
     * @return true if message is synchronized
     */
    public boolean isSynchronized() {
        return sync;
    }

    /**
     * the unique id associated with each message.  This is automatically set internally.
     * You should not do anything to this id.  Note that not all messages require an id,
     * but an id is still generated for it.  It's up to you to use the id (or not use it)
     * when you implement your encode() method.
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * retrieves the reply message associated with this message, if any.
     *
     * @return the reply message or null if no reply exists
     */
    public JabberMessage getReplyMessage() {
        return replymsg;
    }

    /**
     * sets the message id.  This is not used by outsiders to set the message id since
     * ID's are automatically generated. Basically, all outgoing message have automatic IDs generated, but incoming message IDs are
     * sent by the remote server. However, sometimes you may need to manipulate
     * the id yourself, though it's normally not the case.
     */
    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    /**
     * sets the message to require waiting for a reply or not. Requiring a reply does not
     * necessarily mean it has to be synchronized.  It just means that this message is
     * expecting a reply and will have certain methods called to work with replies.
     *
     * @param replyRequired true if message needs to wait for a reply, false otherwise.
     */
    public void setReplyRequired(boolean replyRequired) {
        this.replyRequired = replyRequired;
        if (!replyRequired)
            sync = false;
    }

    /**
     * sets the message to be sent synchronously. This means the sender of this message will wait
     * until a message reply is received before getting back control of flow.
     * Setting a message to be synchronous ALSO means setting the message reply.  Thus, a synchronous message
     * is a message that requires a reply, but a message that requires a reply does not necessarily have to be synchronized.
     */
    public void setSynchronized(boolean sync) {
        this.sync = sync;
        if (sync)
            replyRequired = true;
    }

    /**
     * <p>This method is called when a reply for a sent message is received.  This method pretty much does nothing
     * except set the internal reply message to reference to the new reply message.</p>
     * <p>You can override this method to do whatever you like with the reply message.
     * Note that message listeners will receive the reply message (to those listeners, it's just another message),
     * and has nothing to do with handling the reply message.  This is where any message reply handling should be done. </p>
     * <p>The default Automatic Message Transformation handling is to return the reply message, NOT the original message
     * because the reply message is the real new message that is being sent by the server.</p>
     * <b>If you are writing your own custom message and your message supports synchronization,
     * be sure to submit a notifyAll() inside your replyReceived() method so that no threads will hang</b>.
     */
    public JabberMessage replyReceived(JabberMessage replymsg) {
        this.replymsg = replymsg;
        //notify any waiters for synchronized message
        if (sync) {
            synchronized (this) {
                notifyAll();
            }
        }
        return replymsg;
    }

    /**
     * Retrieves the name/value pairs of the X Messages.  Note that the returned instance is not a copy.
     * Thus, if you make any changes to the Hashtable, it will reflect in the message.
     *
     * @return a list of all the messages, null if no messages exists
     */
    public HashMap getXMessages() {
        return xMsgs;
    }

    /**
     * retrieves an "x" message with the specified namespace string (ie. "jabber:x:oob")
     *
     * @return the jabber message if one exists, null otherwise
     */
    public JabberMessage getXMessage(String ns) {
        if (xMsgs == null) return null;
        return (JabberMessage) xMsgs.get(ns);
    }

    /**
     * adds an "x" message to the chat message.  If a msg already exists for a specific namespace, it will get
     * replaced by the new message.
     */
    public void setXMessage(String ns, JabberMessage msg) {
        if (xMsgs == null)
            xMsgs = new HashMap();
        xMsgs.put(ns, msg);
    }

    /**
     * this sets the X Messages for this Jabber message to the one passed in by the caller.
     * The new X Message hash map will replace the old one.  The name/value pairs in xmsgs
     * should be the Namespace String as the key and a JabberMessage as the value.
     * For example, the name/key would be "jabber:x:oob" and the value would be a JabberMessage instance.
     */
    public void setXMessages(HashMap xmsgs) {
        this.xMsgs = xmsgs;
    }

    /**
     * enable or disabled sending of the X Messages
     *
     * @param sendXMsgs true to send X Messages, false otherwise
     */
    public void setSendXMessages(boolean sendXMsgs) {
        this.sendXMsgs = sendXMsgs;
    }

    /**
     * returns whether to send X Messages or not
     */
    public boolean isSendXMessages() {
        return sendXMsgs;
    }

    /**
     * This method will indicate what type of message it is.  Currently, the unique way to identify a message
     * is through an arbitrarily assigned int that's listed in JabberCode.  By comparing the message type, you can then filter
     * out the exact type of message you're looking for.
     *
     * @return a message code that is unique to the message
     * @see com.echomine.jabber.JabberCode
     */
    public abstract int getMessageType();

    /**
     * encodes the data into an XML string that is ready to be sent out to the network.
     * This method is only used for outgoing messages.
     *
     * @throws com.echomine.common.ParseException
     *          if something went wrong during encoding
     */
    public abstract String encode() throws ParseException;
}
