package com.echomine.jabber;

import com.echomine.common.SendMessageFailedException;
import com.echomine.net.ConnectionFailedException;
import com.echomine.net.ConnectionModel;
import java.net.UnknownHostException;

/**
 * The main entry into Jabber itself. It will contain all the necessary services that is supported by the module.  This is
 * basically an aggregator class that simplifies working with the module.
 */
public class JabberSession {
    private JabberContext context;
    private JabberMessageReceiver receiver;
    private JabberConnection connection;
    private JabberMessageParser msgParser;
    private JabberUserService userService;
    private JabberPresenceService presenceService;
    private JabberRosterService rosterService;
    private JabberChatService chatService;
    private JabberServerService serverService;
    private JabberClientService clientService;

    public JabberSession(JabberContext context, JabberMessageParser parser) {
        this.context = context;
        this.receiver = new DefaultMessageReceiver(this);
        this.msgParser = parser;
    }

    /**
     * connect to the specified server.  The method will not return until a connection is established
     * or fails.  Thus, this method is synchronous.
     */
    public void connect(String hostname, int port) throws ConnectionFailedException, UnknownHostException {
        connect(new ConnectionModel(hostname, port));
    }

    /**
     * connect using an existing connection model. This is a synchronous method where control won't
     * be return to the called until either connection is established or failed.
     */
    public void connect(ConnectionModel cmodel) throws ConnectionFailedException {
        if (connection == null)
            connection = new JabberConnection(this, receiver);
        connection.connect(cmodel);
    }

    /** disconnect from the jabber server.  This will shutdown all running connections and services. */
    public void disconnect() {
        if (connection != null)
            connection.disconnect();
    }

    /** wrapper method to send messages. */
    public void sendMessage(JabberMessage msg) throws SendMessageFailedException {
        if (connection == null) throw new SendMessageFailedException("Sending a message when connection not established");
        connection.send(msg);
    }

    /**
     * This sends a message later rather than synchronously inside the current
     * thread that called this method.  This is what you would use if you need
     * to send a synchronous message while within a JabberMessageListener.messageReceived()
     * method.  If you do not use this method to send those messages, a
     * thread deadlock will occur.
     */
    public void sendMessageLater(Runnable runnable) {
        if (connection == null) throw new IllegalArgumentException("Connection is not initialized and established.");
        connection.sendLater(runnable);
    }

    /** @return the jabber context associated with this context */
    public JabberContext getContext() {
        return context;
    }

    /** @return the connection object associated with this session */
    public JabberConnection getConnection() {
        if (connection == null)
            connection = new JabberConnection(this, receiver);
        return connection;
    }

    /** @return the message parser associated with this session */
    public JabberMessageParser getMessageParser() {
        return msgParser;
    }

    /** @return the roster service associated with this session */
    public JabberRosterService getRosterService() {
        if (rosterService == null)
            rosterService = new JabberRosterService(this);
        return rosterService;
    }

    public JabberChatService getChatService() {
        if (chatService == null)
            chatService = new JabberChatService(this);
        return chatService;
    }

    /** @return the presence service associated with this session */
    public JabberPresenceService getPresenceService() {
        if (presenceService == null)
            presenceService = new JabberPresenceService(this);
        return presenceService;
    }

    /** @return the user service associated with this session */
    public JabberUserService getUserService() {
        if (userService == null)
            userService = new JabberUserService(this);
        return userService;
    }

    /** @return the high level server service */
    public JabberServerService getServerService() {
        if (serverService == null)
            serverService = new JabberServerService(this);
        return serverService;
    }

    /** @return the client service */
    public JabberClientService getClientService() {
        if (clientService == null)
            clientService = new JabberClientService(this);
        return clientService;
    }

    /** convenience method equivalent to calling getConnection().addMessageListner() */
    public void addMessageListener(JabberMessageListener l) {
        connection.addMessageListener(l);
    }

    /** convenience method equivalent to calling getConnection().removeMessageListner() */
    public void removeMessageListener(JabberMessageListener l) {
        connection.removeMessageListener(l);
    }
}
