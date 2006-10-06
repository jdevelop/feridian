package com.echomine.jabber;

import com.echomine.common.SendMessageFailedException;
import com.echomine.net.*;
import com.echomine.util.Semaphore;

/**
 * This class represents a connection to a Jabber Server.  Multiple Connections can be created to connect to multiple servers.
 * This class never should deal with any high-level filtering or anything.
 * It is essentially a pass through and receive for the protocol.  Anything
 * higher up should be handled by the net service or the service handlers.  Authentication
 * should be implemented at the high-level as the the connection does not keep any state
 * other than being connected or disconnected, and queuing of messages to send and receive.
 * This class also supports SSL-based connections if specified.
 */
public class JabberConnection extends TimeableConnection implements JabberMessageListener {
    private boolean connected;
    private SocketConnector connector;
    private JabberProtocol handler;
    private ConnectionModel cmodel;
    private Semaphore sem;
    private JabberMessageReceiver receiver;
    private JabberContext context;
    private MessageRequestQueue queue = new MessageRequestQueue();

    public JabberConnection(JabberSession session, JabberMessageReceiver receiver) {
        this.context = session.getContext();
        JabberContentHandler contentHandler = new JabberContentHandler(session, receiver, queue, new JDOMXMessageHandler(session.getMessageParser()));
        handler = new JabberProtocol(contentHandler, queue);
        connector = new SocketConnector(handler);
        this.receiver = receiver;
        sem = new Semaphore(0);
        //add myself to listen for connection events
        connector.addConnectionListener(new JabberConnectionListener());
        addMessageListener(this);
    }

    /** Disconnects from the remote server */
    public void disconnect() {
        //reset connection state
        handler.shutdown();
    }

    /**
     * This method is synchronous and will not return until connection is established or fails.
     * @throws ConnectionFailedException if connection is not established within the timeout period
     */
    public synchronized void connect(ConnectionModel cmodel) throws ConnectionFailedException {
        if (connected) return;
        this.cmodel = cmodel;
        //reset the queue
        queue.start();
        //set secure connection, if context wants it
        this.cmodel.setSSL(context.isSSL());
        connector.aconnect(cmodel);
        //now wait at most 5 secs for a response
        //before returning
        try {
            sem.acquire();
            if (!connected)
                throw new ConnectionFailedException("Connection Failed");
        } catch (InterruptedException ex) {
            //shutdown the connection first
            disconnect();
            throw new ConnectionFailedException("Timeout during connection");
        }
    }

    /** Sends a message to the server */
    public void send(JabberMessage msg) throws SendMessageFailedException {
        if (!connected)
            throw new SendMessageFailedException("Send Failure: not connected to server");
        try {
            synchronized (msg) {
                //send the message
                handler.send(msg);
                //if session ID already exists, don't bother sending again
                if (context.getSessionID() != null && msg.getMessageType() == JabberCode.MSG_INIT)
                    return;
                //if this message is synchronized, then wait until a reply is returned
                if (msg.isSynchronized())
                    msg.wait(msg.getTimeout());
            }
        } catch (InterruptedException ex) {
            //the message was somehow interrupted and did not get sent successfully
            throw new SendMessageFailedException("Send Failure: interrupted while waiting for reply");
        }
    }

    /**
     * this works the same as send() with the exception that the message will be wrapped in a separate thread before sending
     * out the message. This method is most likely used when you need to send a synchronized message but doing so will cause
     * your application to hang due to deadlock. The parameter is a thread.  This thread will likely contain code to
     * send a synchronized message. This method is similar to the way that Swing uses its invokeLater()
     * method to do repainting.
     * @param sendThread the thread that will get run to send a message
     */
    public void sendLater(Runnable sendThread) {
        Thread thread = new Thread(sendThread, "Send Message Thread");
        thread.start();
    }

    public void addConnectionListener(ConnectionListener l) {
        listenerList.add(ConnectionListener.class, l);
    }

    public void removeConnectionListener(ConnectionListener l) {
        listenerList.remove(ConnectionListener.class, l);
    }

    public ConnectionModel getConnectionModel() {
        return cmodel;
    }

    public boolean isConnected() {
        return connected;
    }

    public void addMessageListener(JabberMessageListener l) {
        receiver.addMessageListener(l);
    }

    public void removeMessageListener(JabberMessageListener l) {
        receiver.removeMessageListener(l);
    }

    /**
     * listens for a session init message in case it comes in before
     * us sending a <stream> first.  This is a workaround patch
     * for servers that send <stream> stanzas first before the client
     * does (not conforming to XMPP standards).
     */
    public void messageReceived(JabberMessageEvent event) {
        if (event.getMessageType() == JabberCode.MSG_INIT) {
            //check if session string is set
            MsgSessionInit msg = (MsgSessionInit) event.getMessage();
            if (context.getSessionID() == null)
                context.setSessionID(msg.getSessionID());
        }
    }

    class JabberConnectionListener implements ConnectionListener {
        public void connectionStarting(ConnectionEvent e) throws ConnectionVetoException {
            //does not use the parent's fire connection event method
            //as the connection event will be fired manually
            fireConnectionStartingWithoutVeto(e);
        }

        public void connectionEstablished(ConnectionEvent e) {
            connected = true;
            //send the initial message
            Runnable run = new Runnable() {
                public void run() {
                    //send an session init msg before releasing the semaphore
                    MsgSessionInit init = new MsgSessionInit(context.getServerName());
                    try {
                        //send message synchronous
                        //we are only connected when a reply message is received
                        send(init);
                        if (init.getReplyMessage() instanceof MsgSessionInit)
                            context.setSessionID(((MsgSessionInit) init.getReplyMessage()).getSessionID());
                        sem.release();
                    } catch (SendMessageFailedException ex) {
                    }
                }
            };
            sendLater(run);
            fireConnectionEstablished(e);
        }

        public void connectionClosed(ConnectionEvent e) {
            //connection was never established
            if (!connected) sem.release();
            connected = false;
            context.setSessionID(null);
            fireConnectionClosed(e);
        }
    }
}
