package com.echomine.xmpp.packet;

import com.echomine.xmpp.IQPacket;
import com.echomine.xmpp.JID;

/**
 * This packet maps to the resource binding IQ stanza.
 */
public class IQResourceBindPacket extends IQPacket {
    private String resourceName;
    private JID jid;

    public IQResourceBindPacket() {
        super();
    }

    /**
     * @return Returns the resourceName.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @param resourceName The resourceName to set.
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * @return Returns the jid.
     */
    public JID getJid() {
        return jid;
    }
}
