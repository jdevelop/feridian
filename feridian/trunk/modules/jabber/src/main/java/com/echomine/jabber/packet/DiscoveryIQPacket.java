package com.echomine.jabber.packet;

import com.echomine.xmpp.packet.IQPacket;

public abstract class DiscoveryIQPacket extends IQPacket {

    protected String xmlns = null;

    /**
     * @param xmlns - namespace for given type of discovery packet
     */
    public DiscoveryIQPacket(String xmlns) {
        this.xmlns = xmlns;
    }

    /**
     * @return the xmlns
     */
    public String getXmlns() {
        return xmlns;
    }

    /**
     * @param xmlns the xmlns to set
     */
    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

}
