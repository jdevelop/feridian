package com.echomine.xmpp;

/**
 * This is the base packet for the main stanzas -- message, presence, and iq.
 * All these stanzas share the following common attributes, hence the reason why
 * this base packet exists.
 */
public class StanzaPacketBase implements IStanzaPacket {
    protected JID to;
    protected JID from;
    protected String id;
    protected String type;
    protected StanzaErrorPacket error;

    public StanzaPacketBase() {
        super();
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
}
