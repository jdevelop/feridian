package com.echomine.jabber;

import java.util.EventObject;

public class JabberMessageEvent extends EventObject {
    private JabberMessage msg;

    public JabberMessageEvent(JabberSession session, JabberMessage msg) {
        super(session);
        this.msg = msg;
    }

    /** @return the message that this event was fired for */
    public JabberMessage getMessage() {
        return msg;
    }

    /** @return the jabber session associated with this message */
    public JabberSession getSession() {
        return (JabberSession)getSource();
    }

    /** @return the message's type */
    public int getMessageType() {
        return msg.getMessageType();
    }
}
