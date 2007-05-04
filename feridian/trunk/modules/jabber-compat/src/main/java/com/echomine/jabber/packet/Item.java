package com.echomine.jabber.packet;

import com.echomine.xmpp.JID;

/**
 * Defines attributes for item structure in
 * <a href="http://www.xmpp.org/extensions/xep-0030.html">JEP-0030, Service discovery</a>
 */
public class Item {

    private JID jid;

    private String node;

    private String name;

    /**
     * @return the jid
     */
    public JID getJid() {
        return jid;
    }

    /**
     * @param jid the jid to set
     */
    public void setJid(JID jid) {
        this.jid = jid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the node
     */
    public String getNode() {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(String node) {
        this.node = node;
    }

}
