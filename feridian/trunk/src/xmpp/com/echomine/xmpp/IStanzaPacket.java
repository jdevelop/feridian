package com.echomine.xmpp;

import com.echomine.xmpp.packet.StanzaErrorPacket;

/**
 * Represents the XML stanza packet. These packets are the message, IQ, and
 * presence stanzas. After handshaking and authentication, these packets are the
 * main way for two-way conversations. Every stanza packet type must implement
 * this interface. FIXME: add xml:lang support
 */
public interface IStanzaPacket extends IPacket {
    public static final String TYPE_ERROR = "error";

    /**
     * @return the JID this packet is destined for.
     */
    JID getTo();

    /**
     * The originator of the packet.
     * 
     * @return the jid who sent this message
     */
    JID getFrom();

    /**
     * An optional ID attribute for serialized conversation.
     * 
     * @return the associated ID or null if none exists
     */
    String getId();

    /**
     * Obtains the packet's type. This maybe be "modify", "get", etc. The type
     * is different for the three main stanza types. However, the "error" is
     * universal to all stanzas.
     * 
     * @return the type associated with this packet
     */
    String getType();

    /**
     * Retrieves the error packet if one exists
     * 
     * @return the error packet, or null if one is not found
     */
    StanzaErrorPacket getError();

    /**
     * Retrieves the timeout set for the packet. This is used to indicate how
     * long the user should wait for a reply when sending a packet
     * synchronously. The implementation should set a default reasonable timeout
     * period (ie. 5 seconds). Normally, if the server does not reply within a
     * short period of time, there's probably something wrong.
     * 
     * @return the timeout in millis
     */
    long getTimeout();

    /**
     * Copies data from the specified packet over to the this object.
     * 
     * @param packet the packet containing data to copy from
     */
    void copyFrom(IStanzaPacket packet);
}
