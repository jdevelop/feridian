package com.echomine.xmpp;

/**
 * Contains useful constants used by XMPP classes.
 */
public interface XMPPConstants {
    //namespace constants
    static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
    static final String NS_JABBER_STREAM = "http://etherx.jabber.org/streams";
    static final String NS_XMPP_CLIENT = "jabber:client";
    static final String NS_STREAMS_ERROR = "urn:ietf:params:xml:ns:xmpp-streams";
    static final String NS_STANZA_ERROR = "urn:ietf:params:xml:ns:xmpp-stanzas";
    static final String NS_STREAM_TLS = "urn:ietf:params:xml:ns:xmpp-tls";
    static final String NS_STREAM_BINDING = "urn:ietf:params:xml:ns:xmpp-bind";
    static final String NS_STREAM_SESSION = "urn:ietf:params:xml:ns:xmpp-session";
    
    //indexes and URIs for working with jibx
    static final String[] STREAM_URIS = new String[] { "", NS_XML, NS_JABBER_STREAM, NS_XMPP_CLIENT };
    static final int IDX_JABBER_STREAM = 2;
    static final int IDX_XMPP_CLIENT = 3;
}
