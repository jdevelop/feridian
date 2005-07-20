package com.echomine.xmpp;

/**
 * Contains useful constants used by XMPP classes.
 */
public interface XMPPConstants {
    // custom API namespaces
    static final String NS_STREAM_HANDSHAKE = "urn:echomine:feridian:xmpp-handshake";

    // namespace constants
    static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
    static final String NS_JABBER_STREAM = "http://etherx.jabber.org/streams";
    static final String NS_XMPP_CLIENT = "jabber:client";
    static final String NS_STREAMS_ERROR = "urn:ietf:params:xml:ns:xmpp-streams";
    static final String NS_STANZA_ERROR = "urn:ietf:params:xml:ns:xmpp-stanzas";
    static final String NS_STREAM_TLS = "urn:ietf:params:xml:ns:xmpp-tls";
    static final String NS_STREAM_BINDING = "urn:ietf:params:xml:ns:xmpp-bind";
    static final String NS_STREAM_SESSION = "urn:ietf:params:xml:ns:xmpp-session";
    static final String NS_STREAM_SASL = "urn:ietf:params:xml:ns:xmpp-sasl";
    static final String NS_STREAM_IQ_AUTH = "http://jabber.org/features/iq-auth";
    static final String NS_STREAM_IQ_REGISTER = "http://jabber.org/features/iq-register";
}
