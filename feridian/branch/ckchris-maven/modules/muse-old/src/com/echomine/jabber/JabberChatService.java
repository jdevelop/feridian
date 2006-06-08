package com.echomine.jabber;

import com.echomine.common.SendMessageFailedException;

/**
 * <p>This service deals with all aspects of chatting (private one-on-one, group chats, headlines, etc).  You can use this
 * service to send private messages, to send group chat messages, or to reply to messages.  A special note: most of the old
 * group chatting functionality are replaced with the newer conference-style chatting.  This service supports only the old
 * style for backwards compatibility.  In fact, currently only the commercial Jabber server supports the new conferencing
 * protocol (and it uses a special draft conference namespace).  Another service will be created to handle conferencing-style
 * chat rooms.</p> <p>There are currently several different types of chatting available.  The "chat" type is
 * your regular one-on-one chatting.  The "groupchat" is the chatroom-style chatting.  The "headline" is more of a
 * notification.  The "normal" is used when no type is specified.  Usually you would open a new window for each specific JID
 * you're chatting with.  You should just treat "normal" types the same as "chat" types by default.</p> <p>Note that the group
 * chat capability is the predecessor of the new Conferencing feature,
 * and are technically NOT the same thing.</p><p><b>Private Chatting
 * Notes</b></p><p>Private chatting support both plain text and XHTML text. It is up to you to parse the HTML text if you
 * support it.  It is also up to you to create the XHTML message. The Chat Service currently gives you rudimentary methods to
 * send messages with XHTML support.  If you require any advanced function, you should instantiate your own
 * JabberChatMessage, set the proper fields, and send the message yourself.</p>
 */
public class JabberChatService implements PresenceCode {
    private JabberSession session;

    public JabberChatService(JabberSession session) {
        this.session = session;
    }

    /**
     * sends a private message to a specific JID that's plain text.  If you want to use HTML, you will
     * need to instantiate your own Chat Message and set the HTML body yourself.  Subject is not normally
     * needed since most other IMs don't use subjects.
     *
     * @param toJID the JID to send the message to
     * @param body  the plain text body of the message
     * @param wait  true if the caller wants to wait until there is a reply to the message
     */
    public void sendPrivateMessage(JID toJID, String body, boolean wait) throws SendMessageFailedException {
        JabberChatMessage msg = new JabberChatMessage(JabberChatMessage.TYPE_CHAT);
        msg.setTo(toJID);
        msg.setBody(body);
        msg.setSynchronized(wait);
        session.sendMessage(msg);
    }

    /**
     * replies to a message.  It is fairly simple in that it simply takes the From and ThreadID fields
     * from the original message and uses them to set the To and ThreadID of the reply message
     *
     * @param toJID    the JID to send the message back to
     * @param threadID the thread id of the originating message
     * @param body     the body text
     * @param wait     true if the caller wants to wait until there is a reply to the message
     */
    public void replyToPrivateMessage(JID toJID, String threadID, String body, boolean wait) throws SendMessageFailedException {
        JabberChatMessage msg = new JabberChatMessage(JabberChatMessage.TYPE_CHAT);
        msg.setTo(toJID);
        msg.setBody(body);
        msg.setThreadID(threadID);
        msg.setSynchronized(wait);
        session.sendMessage(msg);
    }

    /**
     * joins a chat room with a specified arbitrary nickname.  Currently,
     * joining chat rooms uses the old style of joining, not the new
     * conferencing style as it's not fully supported by Jabber Servers yet.
     * Any result from joining the room will be fired off as message events (which you should listen to).
     *
     * @param roomJID the jid of the room, in the form of room@server
     * @param nick    the nickname to be used in the room
     * @param wait    true if the caller wants to wait until there is a reply to the message
     */
    public void joinChatRoom(JID roomJID, String nick, boolean wait) throws SendMessageFailedException {
        JabberPresenceMessage msg = new JabberPresenceMessage();
        JID roomnickJID = new JID(roomJID.getNode(), roomJID.getHost(), nick);
        msg.setTo(roomnickJID);
        msg.setSynchronized(wait);
        session.sendMessage(msg);
    }

    /**
     * leaves a chat room that you are currently in.  When you leave, you will received an
     * unavailable presence that is sent from the room JID.
     *
     * @param roomJID the JID of the room to leave
     * @param wait    true if the caller wants to wait until there is a reply to the message
     */
    public void leaveChatRoom(JID roomJID, boolean wait) throws SendMessageFailedException {
        JabberPresenceMessage msg = new JabberPresenceMessage(TYPE_UNAVAILABLE);
        msg.setTo(roomJID);
        msg.setSynchronized(wait);
        session.sendMessage(msg);
    }

    /**
     * a simple method to send a text message to the chat room.  If you want to send XHTML
     * text, you will have to instantiate your own Chat Message.
     *
     * @param roomJID the JID of the chat room
     * @param body    the text to send to the chat room
     * @param wait    true if the caller wants to wait until there is a reply to the message
     */
    public void sendChatMessage(JID roomJID, String body, boolean wait) throws SendMessageFailedException {
        JabberChatMessage msg = new JabberChatMessage(JabberChatMessage.TYPE_GROUPCHAT);
        msg.setTo(roomJID);
        msg.setBody(body);
        msg.setSynchronized(wait);
        session.sendMessage(msg);
    }

    /**
     * sets status to available with optional show state and status line for a specific chat room.
     * Use this to set your status as either away, extended away, etc.  To set back to available,
     * simply set show state and status to null and you will be set back to available.  The priority
     * is set to default 0.
     *
     * @param roomJID   the JID of the room to send the availability to
     * @param showState optional parameter to set the show state (chat, away, extended away, etc), null if not setting a state
     * @param status    the status to set, or null if not setting a status text
     * @param wait      true if the caller wants to wait until there is a reply to the message
     */
    public void setChatAvailable(JID roomJID, String showState, String status, boolean wait) throws SendMessageFailedException {
        JabberPresenceMessage msg = new JabberPresenceMessage(TYPE_AVAILABLE);
        msg.setTo(roomJID);
        msg.setSynchronized(wait);
        if (showState != null)
            msg.setShowState(showState);
        if (status != null)
            msg.setStatus(status);
        else
            msg.setStatus("Online");
        session.sendMessage(msg);
    }
}
