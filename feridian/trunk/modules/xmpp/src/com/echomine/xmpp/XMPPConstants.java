package com.echomine.xmpp;

/**
 * Contains useful constants used by XMPP classes.
 */
public interface XMPPConstants {
    static final int IDX_XML = 1;

    // custom API namespaces for our handshaking stream
    // this arbitrary URI is simply for our internal purpose
    // to hook our handshake stream into the system
    static final String NS_STREAM_HANDSHAKE = "urn:echomine:feridian:xmpp-handshake";

    // namespace constants for stream-level elements
    static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
    static final String NS_JABBER_STREAM = "http://etherx.jabber.org/streams";
    static final String NS_XMPP_CLIENT = "jabber:client";
    static final String NS_XMPP_SERVER = "jabber:server";
    static final String NS_STREAMS_ERROR = "urn:ietf:params:xml:ns:xmpp-streams";
    static final String NS_STANZA_ERROR = "urn:ietf:params:xml:ns:xmpp-stanzas";
    static final String NS_STREAM_TLS = "urn:ietf:params:xml:ns:xmpp-tls";
    static final String NS_STREAM_BINDING = "urn:ietf:params:xml:ns:xmpp-bind";
    static final String NS_STREAM_SESSION = "urn:ietf:params:xml:ns:xmpp-session";
    static final String NS_STREAM_SASL = "urn:ietf:params:xml:ns:xmpp-sasl";
}
