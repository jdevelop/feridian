package com.echomine.jabber;

import javax.swing.event.EventListenerList;

/**
 * Default receiver will allow the firing of the messages to listeners.
 */
public class DefaultMessageReceiver implements JabberMessageReceiver {
    protected EventListenerList listenerList = new EventListenerList();
    private JabberSession session;

    public DefaultMessageReceiver(JabberSession session) {
        this.session = session;
    }

    /** The listener will listen to all unfiltered messages this router receives. */
    public void addMessageListener(JabberMessageListener l) {
        listenerList.add(JabberMessageListener.class, l);
    }

    public void removeMessageListener(JabberMessageListener l) {
        listenerList.remove(JabberMessageListener.class, l);
    }

    protected void fireMessageReceived(JabberMessage msg) {
        Object[] listeners = listenerList.getListenerList();
        JabberMessageEvent event = new JabberMessageEvent(session, msg);
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == JabberMessageListener.class) {
                // Lazily create the event:
                ((JabberMessageListener)listeners[i + 1]).messageReceived(event);
            }
        }
    }

    public void receive(JabberMessage msg) {
        //just send it
        fireMessageReceived(msg);
    }
}
