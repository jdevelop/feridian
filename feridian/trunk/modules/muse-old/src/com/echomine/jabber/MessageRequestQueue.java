package com.echomine.jabber;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * this class stores messages that are to be sent out.  If the message is waiting for a reply message, it will put it into the
 * outstanding queue.  Everything is synchronized for multithreading safety. The queue must be "start()ed" first
 * and shutdown() afterwards.
 */
public class MessageRequestQueue {
    private LinkedList msgQueue;
    private HashMap msgReplyTable;
    private boolean shutdown;

    public MessageRequestQueue() {
        msgQueue = new LinkedList();
        msgReplyTable = new HashMap(30);
    }

    /** adds a message to send to the end of the queue */
    public void addMessage(JabberMessage msg) {
        if (!shutdown) {
            //check if message is waiting for a reply
            if (msg.isReplyRequired()) {
                synchronized(msgReplyTable) {
                    msgReplyTable.put(msg.getMessageID(), msg);
                }
            }
            synchronized(msgQueue) {
                msgQueue.addLast(msg);
                //notify threads that's waiting for messages
                msgQueue.notify();
            }
        }
    }

    /**
     * this will go into a wait state, waiting for any incoming messages.
     * Once a message comes in and is retrieved, it will check to see if the message requires a reply.
     * @return the jabber message or null if no message
     */
    public JabberMessage waitForMessage() {
        JabberMessage msg = null;
        try {
            if (!shutdown) {
                synchronized(msgQueue) {
                    //wait until there is a new request
                    //or until we get interrupted
                    if ((msgQueue.size() == 0) && !shutdown)
                        msgQueue.wait();
                    if ((msgQueue.size() > 0) && !shutdown)
                        msg = (JabberMessage)msgQueue.removeFirst();
                }
            }
        } catch (InterruptedException ex) {
        }
        return msg;
    }

    /** wake up all the waiting threads possibly because someone is doing some shutdown work */
    public void shutdown() {
        shutdown = true;
        synchronized(msgQueue) {
            msgQueue.notifyAll();
        }
        //iterate through all the msgs waiting for a reply and interrupt them
        synchronized(msgReplyTable) {
            Iterator iter = msgReplyTable.values().iterator();
            JabberMessage msg;
            while (iter.hasNext()) {
                msg = (JabberMessage)iter.next();
                msg.interrupt();
            }
        }
    }

    /**
     * checks if the there is an outstanding message waiting for a reply. If so, it will retrieve
     * the original message AND remove it from the outstanding message queue.
     * @param id the unique ID of the message
     * @return the original message, null if no message corresponds to the specified ID
     */
    public JabberMessage getMessageForReply(String id) {
        JabberMessage msg = null;
        synchronized(msgReplyTable) {
            msg = (JabberMessage)msgReplyTable.remove(id);
        }
        return msg;
    }

    /** clear all the messages in the queues */
    public void clear() {
        synchronized(msgQueue) {
            msgQueue.clear();
        }
        synchronized(msgReplyTable) {
            msgReplyTable.clear();
        }
    }

    /** resets all states back to the default */
    public void start() {
        shutdown = false;
    }
}
