package com.echomine.xmpp;

/**
 * Contains useful constants used by XMPP classes.
 */
public interface XMPPConstants {
    //namespace constants
    static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
    static final String NS_JABBER_STREAM = "http://etherx.jabber.org/streams";
    static final String NS_XMPP_CLIENT = "jabber:client";
    static final String NS_XMPP_STREAMS = "urn:ietf:params:xml:ns:xmpp-streams";
    static final String NS_TLS = "urn:ietf:params:xml:ns:xmpp-tls";
    
    //indexes and URIs for working with jibx
    static final String[] STREAM_URIS = new String[] { "", NS_XML, NS_JABBER_STREAM, NS_XMPP_CLIENT, NS_XMPP_STREAMS };
    static final int IDX_JABBER_STREAM = 2;
    static final int IDX_XMPP_CLIENT = 3;
    static final int IDX_XMPP_STREAMS = 4;
}
