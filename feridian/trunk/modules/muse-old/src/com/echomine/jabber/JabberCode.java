package com.echomine.jabber;

import org.jdom.Namespace;

/**
 * The jabber code contains all the static codes used by the Jabber API.  These includes the XML Namespcaes,
 * the default parser classes, and the message type codes.  Normally you do not concern yourself with the
 * XMLNS or the parser codes; you will most likely use the MSG_XXX codes the most to identify the message
 * type for the incoming messages. Please do NOT use the integer numbers associated with each message type.  Rather, use the
 * canonical names associated with each number.  The numbers are assigned arbitrarily for internal message processing
 * use and is subject to change without notice.
 */
public interface JabberCode {
    //namespaces for the main stream as well as error stanzas
    public static final Namespace XMLNS_STREAM = Namespace.getNamespace("http://etherx.jabber.org/streams");
    public static final Namespace XMLNS_ERROR_STREAM = Namespace.getNamespace("urn:ietf:params:xml:ns:xmpp-streams");
    public static final Namespace XMLNS_ERROR_STANZA = Namespace.getNamespace("urn:ietf:params:xml:ns:xmpp-stanzas");

    //the namespaces used by each message
    public static final Namespace XMLNS_IQ = Namespace.getNamespace("jabber:client");
    public static final Namespace XMLNS_PRESENCE = Namespace.getNamespace("jabber:client");
    public static final Namespace XMLNS_CHAT = Namespace.getNamespace("jabber:client");
    public static final Namespace XMLNS_IQ_REGISTER = Namespace.getNamespace("jabber:iq:register");
    public static final Namespace XMLNS_IQ_ROSTER = Namespace.getNamespace("jabber:iq:roster");
    public static final Namespace XMLNS_IQ_AUTH = Namespace.getNamespace("jabber:iq:auth");
    public static final Namespace XMLNS_IQ_XMLRPC = Namespace.getNamespace("jabber:iq:rpc");
    public static final Namespace XMLNS_IQ_TIME = Namespace.getNamespace("jabber:iq:time");
    public static final Namespace XMLNS_IQ_VERSION = Namespace.getNamespace("jabber:iq:version");
    public static final Namespace XMLNS_IQ_LAST = Namespace.getNamespace("jabber:iq:last");
    public static final Namespace XMLNS_IQ_BROWSE = Namespace.getNamespace("jabber:iq:browse");
    public static final Namespace XMLNS_IQ_GATEWAY = Namespace.getNamespace("jabber:iq:gateway");
    public static final Namespace XMLNS_IQ_AGENTS = Namespace.getNamespace("jabber:iq:agents");
    public static final Namespace XMLNS_IQ_VCARD = Namespace.getNamespace("vcard-temp");
    public static final Namespace XMLNS_IQ_PRIVATE = Namespace.getNamespace("jabber:iq:private");
    public static final Namespace XMLNS_IQ_SEARCH = Namespace.getNamespace("jabber:iq:search");
    public static final Namespace XMLNS_IQ_OOB = Namespace.getNamespace("jabber:iq:oob");
    public static final Namespace XMLNS_IQ_VACATION = Namespace.getNamespace("http://www.jabber.org/protocol/vacation");
    public static final Namespace XMLNS_IQ_DISCO_INFO = Namespace.getNamespace("http://jabber.org/protocol/disco#info");
    public static final Namespace XMLNS_IQ_DISCO_ITEMS = Namespace.getNamespace("http://jabber.org/protocol/disco#items");
    public static final Namespace XMLNS_X_OOB = Namespace.getNamespace("jabber:x:oob");
    public static final Namespace XMLNS_X_DELAY = Namespace.getNamespace("jabber:x:delay");
    public static final Namespace XMLNS_X_ROSTER = Namespace.getNamespace("jabber:x:roster");
    public static final Namespace XMLNS_X_EVENT = Namespace.getNamespace("jabber:x:event");
    public static final Namespace XMLNS_X_EXPIRE = Namespace.getNamespace("jabber:x:expire");
    public static final Namespace XMLNS_X_PGP_SIGNED = Namespace.getNamespace("jabber:x:signed");
    public static final Namespace XMLNS_X_PGP_ENCRYPTED = Namespace.getNamespace("jabber:x:encrypted");
    public static final Namespace XMLNS_X_DATA = Namespace.getNamespace("jabber:x:data");

    //these message parser will parse all the jabber core iq messages
    public static final String PARSER_IQ = "com.echomine.jabber.JabberIQMessageParser";
    public static final String PARSER_PRESENCE = "com.echomine.jabber.JabberPresenceMessage";
    public static final String PARSER_CHAT = "com.echomine.jabber.JabberChatMessage";
    public static final String PARSER_IQ_REGISTER = "com.echomine.jabber.msg.RegisterIQMessage";
    public static final String PARSER_IQ_ROSTER = "com.echomine.jabber.msg.RosterIQMessage";
    public static final String PARSER_IQ_AUTH = "com.echomine.jabber.msg.AuthIQMessage";
    public static final String PARSER_IQ_XMLRPC = "com.echomine.jabber.msg.XMLRPCMessage";
    public static final String PARSER_IQ_TIME = "com.echomine.jabber.msg.TimeIQMessage";
    public static final String PARSER_IQ_VERSION = "com.echomine.jabber.msg.VersionIQMessage";
    public static final String PARSER_IQ_LAST = "com.echomine.jabber.msg.LastIQMessage";
    public static final String PARSER_IQ_BROWSE = "com.echomine.jabber.msg.BrowseIQMessage";
    public static final String PARSER_IQ_GATEWAY = "com.echomine.jabber.msg.GatewayIQMessage";
    public static final String PARSER_IQ_AGENTS = "com.echomine.jabber.msg.AgentsIQMessage";
    public static final String PARSER_IQ_VCARD = "com.echomine.jabber.msg.JabberVCardMessage";
    public static final String PARSER_IQ_PRIVATE = "com.echomine.jabber.msg.PrivateXmlIQMessage";
    public static final String PARSER_IQ_SEARCH = "com.echomine.jabber.msg.SearchIQMessage";
    public static final String PARSER_IQ_OOB = "com.echomine.jabber.msg.OOBIQMessage";
    public static final String PARSER_IQ_VACATION = "com.echomine.jabber.msg.VacationIQMessage";
    public static final String PARSER_IQ_DISCO_INFO = "com.echomine.jabber.msg.ServiceInfoIQMessage";
    public static final String PARSER_IQ_DISCO_ITEMS = "com.echomine.jabber.msg.ServiceItemsIQMessage";
    public static final String PARSER_X_OOB = "com.echomine.jabber.msg.OOBXMessage";
    public static final String PARSER_X_DELAY = "com.echomine.jabber.msg.DelayXMessage";
    public static final String PARSER_X_ROSTER = "com.echomine.jabber.msg.RosterXMessage";
    public static final String PARSER_X_EVENT = "com.echomine.jabber.msg.EventXMessage";
    public static final String PARSER_X_EXPIRE = "com.echomine.jabber.msg.ExpireXMessage";
    public static final String PARSER_X_PGP_SIGNED = "com.echomine.jabber.msg.PGPSignedXMessage";
    public static final String PARSER_X_PGP_ENCRYPTED = "com.echomine.jabber.msg.PGPEncryptedXMessage";
    public static final String PARSER_X_DATA = "com.echomine.jabber.msg.DataXMessage";
    //arbitrary message type codes for each message.  This is not in any way associated with
    //Jabber. Rather, it is used to uniquely identify each specific type of message for
    //easier use when listening for specific messages.
    //an unknown message is a message that is not parsed by a known message object.
    //in this case, an unknown message will stay as a generic message object (and is
    //normally of type JabberJDOMMessage).
    //DO NOT USE THE INTEGER NUMBER ASSIGNED TO THESE MESSAGE TYPES.  USE THE
    //CANONICAL NAMES INSTEAD.  THE NUMBERS ARE SUBJECT TO CHANGE WITHOUT NOTICE.
    public static final int MSG_UNKNOWN = 0;
    public static final int MSG_INIT = 1;
    public static final int MSG_PRESENCE = 2;
    public static final int MSG_CHAT = 3;
    public static final int MSG_IQ = 10;
    public static final int MSG_IQ_REGISTER = 11;
    public static final int MSG_IQ_ROSTER = 12;
    public static final int MSG_IQ_AUTH = 13;
    public static final int MSG_IQ_XMLRPC = 14;
    public static final int MSG_IQ_TIME = 15;
    public static final int MSG_IQ_VERSION = 16;
    public static final int MSG_IQ_LAST = 17;
    public static final int MSG_IQ_BROWSE = 18;
    public static final int MSG_IQ_GATEWAY = 19;
    public static final int MSG_IQ_AGENTS = 20;
    public static final int MSG_IQ_VCARD = 21;
    public static final int MSG_IQ_OOB = 22;
    public static final int MSG_IQ_PRIVATE = 23;
    public static final int MSG_IQ_SEARCH = 24;
    public static final int MSG_IQ_VACATION = 25;
    public static final int MSG_IQ_DISCO_INFO = 26;
    public static final int MSG_IQ_DISCO_ITEMS = 27;
    public static final int MSG_X_DELAY = 100;
    public static final int MSG_X_ROSTER = 101;
    public static final int MSG_X_EVENT = 102;
    public static final int MSG_X_EXPIRE = 103;
    public static final int MSG_X_PGP_SIGNED = 104;
    public static final int MSG_X_PGP_ENCRYPTED = 105;
    public static final int MSG_X_OOB = 106;
    public static final int MSG_X_DATA = 107;
}
