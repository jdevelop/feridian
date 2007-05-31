package com.echomine.jabber.compat.packet;

import com.echomine.xmpp.JID;
import com.echomine.xmpp.packet.IQPacket;

/**
 * Packet implementation for gateway interaction
 * <a href="http://www.xmpp.org/extensions/xep-0100.html#addressing-iqgateway">XEP-0100</a>
 */
public class GatewayIQPacket extends IQPacket {

    /**
     * Description
     */
    private String desc;

    /**
     * Promt message
     */
    private String prompt;

    /**
     * Jabber ID for user, escaped and returned by transport
     */
    private JID jid;

    public GatewayIQPacket() {
        desc = prompt = null;
        jid = null;
    }

    public GatewayIQPacket(String desc, String prompt, JID jid) {
        this.desc = desc;
        this.prompt = prompt;
        this.jid = jid;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

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
     * @return the promt
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * @param promt the promt to set
     */
    public void setPrompt(String promt) {
        this.prompt = promt;
    }

}
