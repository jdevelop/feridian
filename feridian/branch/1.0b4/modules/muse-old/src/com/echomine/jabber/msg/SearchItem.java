package com.echomine.jabber.msg;

import com.echomine.jabber.JID;

/**
 * this represents a search item result. It includes the first, last, nick, and email of the user in the
 * search result.
 * @see SearchIQMessage
 * @since 0.8a4
 */
public class SearchItem {
    JID jid;
    String first;
    String last;
    String nick;
    String email;

    public SearchItem() {
    }

    /** easy way to set all the fields at once */
    public SearchItem(JID jid, String first, String last, String nick, String email) {
        this.jid = jid;
        this.first = first;
        this.last = last;
        this.nick = nick;
        this.email = email;
    }

    /** @return the jid of the user */
    public JID getJID() {
        return jid;
    }

    /** sets the jid */
    public void setJID(JID jid) {
        this.jid = jid;
    }

    /** @return the first name, or null if none exists */
    public String getFirst() {
        return first;
    }

    /** sets the first name */
    public void setFirst(String first) {
        this.first = first;
    }

    /** @return the last name, or null if none exists */
    public String getLast() {
        return last;
    }

    /** sets the last name */
    public void setLast(String last) {
        this.last = last;
    }

    /** @return the nick of the user entry, or null if none exists */
    public String getNick() {
        return nick;
    }

    /** sets the nick */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /** @return the email fo the user, or null if none exists */
    public String getEmail() {
        return email;
    }

    /** sets the email */
    public void setEmail(String email) {
        this.email = email;
    }
}
