package com.echomine.jabber;

import java.util.EventListener;

/** Interface that classes must implement in order to receive any incoming jabber messages. */
public interface JabberMessageListener extends EventListener {
    void messageReceived(JabberMessageEvent event);
}
