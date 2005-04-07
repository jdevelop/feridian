package com.echomine.jabber;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The main content handler used by the protocol itself.  It essentially acts as a delegator.
 * When an element comes in, it will automatically look for the required parser that is able
 * to parse the contents of the message and then pass along all the incoming callbacks to it.
 */
public class JabberContentHandler extends DefaultHandler {
    private static Log inlogger = LogFactory.getLog("jabber/msg/incoming");
    private JabberSession session;
    private JabberMessageReceiver receiver;
    private JabberMessageHandler msgHandler;
    private MessageRequestQueue queue;
    private boolean delegated;
    private String msgLocalName;

    public JabberContentHandler(JabberSession session, JabberMessageReceiver receiver, MessageRequestQueue queue, JabberMessageHandler msgHandler) {
        super();
        this.queue = queue;
        this.receiver = receiver;
        this.session = session;
        this.msgHandler = msgHandler;
    }

    /** This method is only handled by the protocol itself and is never sent to the higher level */
    public void startDocument() {
        delegated = false;
        msgLocalName = null;
    }

    /** This method only handled by the protocol itself and is never sent to the higher level */
    public void endDocument() {
    }

    /**
     * check to see if there is a handler for this message. If so, delegate the rest of the parsing
     * to this element until the end of the element is reached. At that point, we are back to selecting and delegating.
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes attr) {
        //compensate for non-closing element stream:stream (the document root)
        //this element's starting tag contains very important information
        //and cannot wait until the end of the element is reached before firing it
        //(Obviously when the end tag is reached, connection is finished)
        //Thus, we have to fire it manually here and not delegated in any way.
        if (localName.equals("stream")) {
            //id and from must exist before sending this information out
            //otherwise, disconnect.. invalid message format
            String id = attr.getValue("id");
            String from = attr.getValue("from");
            String version = attr.getValue("version");
            if (id == null || from == null) {
                session.disconnect();
                return;
            }
            MsgSessionInit initmsg = new MsgSessionInit(from, id, version);
            //check if this message is a reply to a sent message
            JabberMessage sentMsg = queue.getMessageForReply(initmsg.getMessageID());
            if (sentMsg != null) {
                //the message is indeed a reply to another one
                //give it to the original message to handle
                sentMsg.replyReceived(initmsg);
            }
            //log the message
            if (inlogger.isDebugEnabled())
                inlogger.debug(initmsg);
            receiver.receive(initmsg);
            return;
        }
        if (!delegated) {
            //not delegated, find if there is one that can handle this message type
            delegated = true;
            msgLocalName = localName;
            msgHandler.startMessage();
        }
        //delegated, just let the msg handler handle it
        if (delegated) {
            msgHandler.startElement(namespaceURI, localName, qName, attr);
        }
    }

    /** ending the element essentially "resets" our delegation back to us */
    public void endElement(String namespaceURI, String localName, String qName) {
        //if stream:stream end element is received, don't fire it to delegator
        if (localName.equals("stream")) {
            //this indicates the end of connection already
            //so let's disconnect
            session.disconnect();
            return;
        }
        if (delegated) {
            msgHandler.endElement(namespaceURI, localName, qName);
        }
        if (localName.equals(msgLocalName)) {
            //same element, meaning that the elements are finished
            msgHandler.endMessage();
            //retrieve the message
            JabberMessage msg = msgHandler.getMessage();
            //check if this message is a reply to a sent message
            JabberMessage sentMsg = queue.getMessageForReply(msg.getMessageID());
            if (sentMsg != null) {
                //the message is indeed a reply to another one
                //give it to the original message to handle
                msg = sentMsg.replyReceived(msg);
            }
            //log the message
            if (inlogger.isDebugEnabled())
                inlogger.debug(msg);
            //send the message out to listeners
            receiver.receive(msg);
            //no more delegation needed
            delegated = false;
            msgLocalName = null;
        }
    }

    public void characters(char[] ch, int start, int length) {
        if (delegated) {
            msgHandler.characters(ch, start, length);
        }
    }
}
