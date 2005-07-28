package com.echomine.xmpp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.echomine.xmpp.packet.StanzaErrorPacket;

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
    private HashMap extensions;
    private long timeout = 5000;
    private StanzaErrorPacket error;

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
     * Adds an extension to the current stanza. An extension is simply an
     * additional stanza tagged to the main stanza. For instance, delay time,
     * vcard, etc can be sent as extensions to a message or presence packet. It
     * allows arbitrary information to be added to the existing main stanzas.
     * Each extension is associated with its own namespace. Thus, when adding a
     * new extension, a namespace is required. As long as the extension can be
     * marshalled, it will be appended to the main packet. If a packet is added
     * that conflicts with a packet already registered with the specified
     * namespace, then the new packet will replace the old one. <br/>When
     * marshalling, the namespace is not taken into account. The namespace is
     * governed by the binding file. Thus, when sending packets with extensions,
     * null or any arbitrarily unique namespace can be used. On the other hand,
     * if this packet contains unmarshalled data, then the namespace will
     * indicate what type of packet it is.
     * 
     * @param ns the namespace
     * @param packet the extension packet
     */
    public void addExtension(String ns, IPacket packet) {
        if (extensions == null)
            extensions = new HashMap();
        extensions.put(ns, packet);
    }

    /**
     * Removes an extension from the main packet. If this extension exists, then
     * it will be removed and the packet will be returned. Otherwise, null is
     * returned.
     * 
     * @param ns the packet with the specified namespace to remove
     * @return the packet associated with the namespace, or null if namespace is
     *         not found.
     */
    public IPacket removeExtension(String ns) {
        if (extensions == null)
            return null;
        return (IPacket) extensions.remove(ns);
    }

    /**
     * Retrieves the packet associated with the namespace.
     * 
     * @param ns the namespace that the packet is associated with
     * @return the packet if found, or null if no packet found
     */
    public IPacket getExtension(String ns) {
        if (extensions == null)
            return null;
        return (IPacket) extensions.get(ns);
    }

    /**
     * Get a list of extensions. This list is not modifiable.
     * 
     * @return
     */
    public Collection getExtensions() {
        if (extensions == null)
            return Collections.EMPTY_SET;
        return Collections.unmodifiableCollection(extensions.values());
    }
}
