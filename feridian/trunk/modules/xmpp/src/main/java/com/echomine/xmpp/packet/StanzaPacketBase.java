package com.echomine.xmpp.packet;

import com.echomine.xmpp.IStanzaPacket;
import com.echomine.xmpp.JID;

/**
 * This is the base packet for the main stanzas -- message, presence, and iq.
 * All these stanzas share the following common attributes, hence the reason why
 * this base packet exists.
 */
public class StanzaPacketBase implements IStanzaPacket {
    protected static final String TYPE_ATTRIBUTE_NAME = "type";
    protected static final String ID_ATTRIBUTE_NAME = "id";
    protected static final String FROM_ATTRIBUTE_NAME = "from";
    protected static final String TO_ATTRIBUTE_NAME = "to";
    protected static final String ERROR_ELEMENT_NAME = "error";

    private JID to;
    private JID from;
    private String id;
    private String type;
    private long timeout = 5000;
    private StanzaErrorPacket error;

    public StanzaPacketBase() {
        super();
    }

    /**
     * Constructs a packet base with a default type
     * 
     * @param type the packet's type attribute
     */
    public StanzaPacketBase(String type) {
        this.type = type;
    }
    
    /**
     * @return Returns the from.
     */
    public JID getFrom() {
        return from;
    }

    /**
     * @param from The from to set.
     */
    public void setFrom(JID from) {
        this.from = from;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Returns the to.
     */
    public JID getTo() {
        return to;
    }

    /**
     * @param to The to to set.
     */
    public void setTo(JID to) {
        this.to = to;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Returns the timeout.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * @param timeout The timeout to set.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Checks whether this packet contains an error or not.
     * 
     * @return true if the packet is an error packet
     */
    public boolean isError() {
        return error != null;
    }

    /**
     * @return Returns the error.
     */
    public StanzaErrorPacket getError() {
        return error;
    }

    /**
     * Setting the error packet will automatically set the type to ERROR.
     * Changing the type afterwards is not advisable even though it is not
     * enforced. If error is set to null, then error will be reset as well as
     * the type (set to null as well).
     * 
     * @param error The error to set.
     */
    public void setError(StanzaErrorPacket error) {
        this.error = error;
        if (error != null)
            this.type = TYPE_ERROR;
        else
            this.type = null;
    }
    
    /**
     * copies the values from the specified packet to this object. this default
     * method only copies stanza attributes and any errors from the packet.
     * 
     * @param packet the object that will have data be copied to
     */
    public void copyFrom(IStanzaPacket packet) {
        type = packet.getType();
        id = packet.getId();
        to = packet.getTo();
        from = packet.getFrom();
        error = packet.getError();
    }
}
