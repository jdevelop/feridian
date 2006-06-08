package com.echomine.jabber;

/** creates sessions for you.  This is where you can override the methods to return your own JabberSessions if you have one. */
public class Jabber {
    public JabberSession createSession(JabberContext context) {
        return createSession(context, new DefaultMessageParser());
    }

    public JabberSession createSession(JabberContext context, JabberMessageParser parser) {
        return new JabberSession(context, parser);
    }
}
