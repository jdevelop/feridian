package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

/**
 * <p>This message encapsulates all browsing-related messages.  The new Browsing framework for Jabber
 * allows you to retrieve any information regarding a service/conference/application/etc.  It gives
 * your a way to retrieve information about a specific JID.  For instance, if the jid is a user,
 * browsing the user will return the services/functions supported by the client (ie. xhtml, pgp/encryption,
 * oob file transfers, etc).  Browsing to a server will return a list of agents/services that the
 * server supports (ie. icq, conferencing, msn, irc, etc).  Thus, the browsing framework become extremely generic.</p>
 * <p>Inside a browse result message, you will receive a list of services that the JID supports.  You can further
 * browse down to those internal list to obtain information about those services.  The browsing framework is
 * created in such a way as to be hierarchical (sort like a tree list in a GUI, ie. XML parent/child style).
 * A service can be browsed until no further services are contained.</p>
 * <p>Notes on Implementation: Under most circumstances, the JID contained in the Browse message should
 * be exactly the same as the To field in the IQ message.  There is an exception. If the result message is from a chat
 * conference room, then any messages received from the chat is under the conference room's JID for the IQ From Field.  The
 * JID inside the message will then be the user's JID.</p> <p>There are a lot of complications with using the Browse message,
 * it is suggested that you read the Jabber Browsing draft available at http://www.jabber.org for more information.  Otherwise, you
 * can simply use the provided convenience methods offered by the Jabber Service classes.</p>
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0011.html">JEP-0011 Version 1.0</a></b></p>
 * @see ServiceInfoIQMessage
 * @see ServiceItemsIQMessage
 */
public class BrowseIQMessage extends JabberIQMessage {
    private static Log log = LogFactory.getLog(BrowseIQMessage.class);
    private JIDType jidtype;

    /**
     * this constructor is for messages with type.
     * @throws ParseException if the jid category/subtype is not in the proper format
     */
    public BrowseIQMessage(String type, String jtype) throws ParseException {
        super(type);
        jidtype = new JIDType(jtype);
        //add in the query element
        getDOM().addContent(new Element(jidtype.getCategory(), JabberCode.XMLNS_IQ_BROWSE));
    }

    /** defaults to iq type get and jidtype of "service/jabber" */
    public BrowseIQMessage() {
        super(TYPE_GET);
        try {
            jidtype = new JIDType("service/jabber");
            getDOM().addContent(new Element(jidtype.getCategory(), JabberCode.XMLNS_IQ_BROWSE));
        } catch (ParseException ex) {
            //this should never occur, so if it does, warn
            if (log.isWarnEnabled())
                log.warn("BrowseIQMessage constructor's default jidtype should be appropriate but is somehow throwing an exception");
        }
    }

    public JIDType getJIDType() {
        return jidtype;
    }

    public void setJIDType(JIDType jidtype) {
        this.jidtype = jidtype;
    }

    public int getMessageType() {
        return JabberCode.MSG_IQ_BROWSE;
    }

    /** parses the incoming message for the data */
    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        //let the parent class parse out the normal core attributes
        super.parse(parser, msgTree);
        //parse out the attributes for this type first
        //the browse element is one level under the iq element
        //and SHOULD be the only element inside the <iq>
        Element browse = (Element) msgTree.getChildren().get(0);
        if (browse == null)
            throw new ParseException("No browse message exists");
        //now retrieve the category, which is basically the element name
        String category = browse.getName();
        String subtype = browse.getAttributeValue("type");
        jidtype = new JIDType(category, subtype);
        //let the jid parse itself
        jidtype.parse(browse);
        return this;
    }

    /** overrides the encode so that we can transform all the data in this class into XML before sending it off */
    public String encode() throws ParseException {
        //tell the JID to encode the data into a DOM
        if (jidtype != null) {
            //remove all children first
            getDOM().getChildren().clear();
            getDOM().addContent(jidtype.getDOM());
        }
        return super.encode();
    }
}
