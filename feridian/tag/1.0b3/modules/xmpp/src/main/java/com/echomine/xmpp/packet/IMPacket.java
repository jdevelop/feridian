package com.echomine.xmpp.packet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import com.echomine.xmpp.IPacket;

/**
 * The IM (Instant Messaging) packet acts as the base packet for the message and
 * presence, and possibly any other packets that requires the additional
 * functions included in this class. This will include support for extensions
 * and locales.
 * <br/>This packet is purposely designated abstract simply to indicate that
 * users who wish to work with this class MUST subclass or use a subclass of this class.
 */
public abstract class IMPacket extends StanzaPacketBase {
    private HashMap<String,IPacket> extensions;
    private Locale locale;

    public IMPacket() {
        super();
    }

    /**
     * This obtains the default message-level locale. This locale should
     * indicate what the default locale is for all its children. Thus, for a
     * body or subject that contains no xml:lang, this locale should be used.
     * 
     * @return Returns the locale.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the default message-level locale. This overrides the stream-level
     * locale and can be overridden by its children.
     * 
     * @param locale The locale to set.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
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
            extensions = new HashMap<String, IPacket>();
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
        return extensions.remove(ns);
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
        return extensions.get(ns);
    }

    /**
     * Get a list of extensions. This list is not modifiable.
     * 
     * @return a list of extensions
     */
    public Collection<IPacket> getExtensions() {
        if (extensions == null)
            extensions = new HashMap<String, IPacket>();
        return Collections.unmodifiableCollection(extensions.values());
    }
}
