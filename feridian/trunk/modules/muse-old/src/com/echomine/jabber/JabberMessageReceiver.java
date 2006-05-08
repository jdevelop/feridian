package com.echomine.jabber;

public interface JabberMessageReceiver {
    /** The listener will listen to all unfiltered messages this connection receives. */
    void addMessageListener(JabberMessageListener l);

    void removeMessageListener(JabberMessageListener l);

    void receive(JabberMessage msg);
}
