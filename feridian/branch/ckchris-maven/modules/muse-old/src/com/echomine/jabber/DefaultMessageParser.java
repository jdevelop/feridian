package com.echomine.jabber;

import com.echomine.common.ParseException;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.HashMap;

/**
 * <p>Contains a list of message parsers.  Essentially, for each type of message, there should be a registered parser for it
 * that will be able to process the incoming data.</p> <p>When a handler receives a message,
 * it will lookup a message class name that can be instantiated to parse the incoming message.
 * Thus, for each custom message that you create, you should register that message's class name.
 * This includes classes that are contained inside the IQ Message.  In fact, the way that the
 * IQMessageParser works is it calls this class again to retrieve a second parser for the message
 * contained inside the IQ Message.</p>
 */
public class DefaultMessageParser implements JabberMessageParser, JabberCode {
    private HashMap msgParsers = new HashMap();
    private HashMap msgClasses = new HashMap();

    public DefaultMessageParser() {
        //create the default set of message parsers
        try {
            setParser("presence", XMLNS_PRESENCE, PARSER_PRESENCE);
            setParser("message", XMLNS_CHAT, PARSER_CHAT);
            setParser("iq", XMLNS_IQ, PARSER_IQ);
            setParser("query", XMLNS_IQ_AUTH, PARSER_IQ_AUTH);
            setParser("query", XMLNS_IQ_ROSTER, PARSER_IQ_ROSTER);
            setParser("query", XMLNS_IQ_REGISTER, PARSER_IQ_REGISTER);
            setParser("query", XMLNS_IQ_XMLRPC, PARSER_IQ_XMLRPC);
            setParser("query", XMLNS_IQ_TIME, PARSER_IQ_TIME);
            setParser("query", XMLNS_IQ_VERSION, PARSER_IQ_VERSION);
            setParser("query", XMLNS_IQ_LAST, PARSER_IQ_LAST);
            setParser("query", XMLNS_IQ_GATEWAY, PARSER_IQ_GATEWAY);
            setParser("query", XMLNS_IQ_AGENTS, PARSER_IQ_AGENTS);
            setParser("query", XMLNS_IQ_PRIVATE, PARSER_IQ_PRIVATE);
            setParser("query", XMLNS_IQ_SEARCH, PARSER_IQ_SEARCH);
            setParser("query", XMLNS_IQ_OOB, PARSER_IQ_OOB);
            setParser("query", XMLNS_IQ_VACATION, PARSER_IQ_VACATION);
            setParser("query", XMLNS_IQ_DISCO_INFO, PARSER_IQ_DISCO_INFO);
            setParser("query", XMLNS_IQ_DISCO_ITEMS, PARSER_IQ_DISCO_ITEMS);
            setParser("vCard", XMLNS_IQ_VCARD, PARSER_IQ_VCARD);
            //register every possible jid type
            //adding workaround.. currently service is under "jabber:client"
            setParser("service", XMLNS_IQ, PARSER_IQ_BROWSE);
            setParser("service", XMLNS_IQ_BROWSE, PARSER_IQ_BROWSE);
            setParser("conference", XMLNS_IQ_BROWSE, PARSER_IQ_BROWSE);
            setParser("user", XMLNS_IQ_BROWSE, PARSER_IQ_BROWSE);
            setParser("application", XMLNS_IQ_BROWSE, PARSER_IQ_BROWSE);
            setParser("headline", XMLNS_IQ_BROWSE, PARSER_IQ_BROWSE);
            setParser("render", XMLNS_IQ_BROWSE, PARSER_IQ_BROWSE);
            setParser("keyword", XMLNS_IQ_BROWSE, PARSER_IQ_BROWSE);
            setParser("x", XMLNS_X_DELAY, PARSER_X_DELAY);
            setParser("x", XMLNS_X_ROSTER, PARSER_X_ROSTER);
            setParser("x", XMLNS_X_EVENT, PARSER_X_EVENT);
            setParser("x", XMLNS_X_EXPIRE, PARSER_X_EXPIRE);
            setParser("x", XMLNS_X_PGP_ENCRYPTED, PARSER_X_PGP_ENCRYPTED);
            setParser("x", XMLNS_X_PGP_SIGNED, PARSER_X_PGP_SIGNED);
            setParser("x", XMLNS_X_DATA, PARSER_X_DATA);
            setParser("x", XMLNS_X_OOB, PARSER_X_OOB);
        } catch (ParseException ex) {
            //there should absolutely be no parsing exceptions
            //since we know these parsers exists
            ex.printStackTrace();
        }
    }

    /**
     * checks whether a parser is registered for the specified qname and namespace
     */
    public boolean supportsParsingFor(String qName, Namespace ns) {
        return msgParsers.containsKey(ns.getURI() + ":" + qName);
    }

    /**
     * removes the message parser associated with a specific namespace tag
     */
    public void removeParser(String qName, Namespace ns) {
        Object clsName = msgParsers.remove(ns.getURI() + ":" + qName);
        msgClasses.remove(clsName);
    }

    /**
     * sets a message parser to handle a specific namespace. If a parser already exists for the specific namespace, the new
     * parser will replace the old one.  This way, if you decide to override the default parsers, you can
     * do so in an easy manner.
     *
     * @param qName    the fully qualified tag name
     * @param ns       the Namespace for the tag element
     * @param msgClass the message class that will be instantiated
     * @throws ParseException thrown when class is not found or class is not a message parser
     */
    public void setParser(String qName, Namespace ns, String msgClass) throws ParseException {
        try {
            Class cls = Class.forName(msgClass);
            if (!JabberMessageParsable.class.isAssignableFrom(cls))
                throw new ParseException("Cannot be assigned!");
            //everything is fine, store the class and the parser
            msgClasses.put(msgClass, cls);
            msgParsers.put(ns.getURI() + ":" + qName, msgClass);
        } catch (ClassNotFoundException ex) {
            throw new ParseException("Parser class not found");
        }
    }

    /**
     * instantiate a message object by looking at the DOM tree.  It does this through reflection.
     *
     * @throws MessageNotSupportedException if no class is able to parse this message
     */
    public JabberMessage createMessage(String qName, Namespace ns, Element msgTree) throws MessageNotSupportedException {
        //find a parser
        String msgClassName = (String) msgParsers.get(ns.getURI() + ":" + qName);
        if (msgClassName == null)
            throw new MessageNotSupportedException("Parser does not exist for the message");
        JabberMessageParsable parser;
        JabberMessage msg = null;
        Class msgClass = (Class) msgClasses.get(msgClassName);
        if (msgClass != null) {
            try {
                parser = (JabberMessageParsable) msgClass.newInstance();
                msg = parser.parse(this, msgTree);
            } catch (InstantiationException ex) {
                throw new MessageNotSupportedException("Error while instantiating message");
            } catch (IllegalAccessException ex) {
                throw new MessageNotSupportedException("Illegal access to message");
            } catch (ParseException ex) {
                throw new MessageNotSupportedException("Message cannot be properly parsed");
            }
        }
        if (msg == null)
            throw new MessageNotSupportedException("Parser does not exist for the message");
        return msg;
    }
}
