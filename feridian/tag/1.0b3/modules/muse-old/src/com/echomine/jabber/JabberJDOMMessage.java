package com.echomine.jabber;

import com.echomine.common.ParseException;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import java.util.Iterator;

/**
 * <p>The default message type that essentially contains all the data in a JDOM tree.  Subclasses can actually extend from this
 * class to parse the tree and store the data in more easily access form.  They can also provide getXXX
 * methods to retrieve the data. JDOM is provided as part of the distribution.</p>
 * <p>If you do not want to create a new message class that extends from this class, you can retrieve the internal JDOM tree
 * and then manipulate it manually.  It is suggested that you create a new message class to handle and process the information
 * so you can reuse it in the future. Then you can also contribute the message class back to the project. :) </p>
 * <p>By default, JDOM Messages adds the X Namespaces. Thus, they are automatically inserted into the DOM tree and sent
 * along with the message.  If you do not want the X Messages to be sent, you can
 * use the setSendXMessage() method to disable it.</p>
 */
public class JabberJDOMMessage extends JabberMessage implements JabberMessageParsable {
    private Element msgTree;
    private XMLOutputter output;

    protected JabberJDOMMessage() {
        super();
    }

    /**
     * normally used internally or creating outgoing messages.
     */
    public JabberJDOMMessage(Element rootElem) {
        this.msgTree = rootElem;
        String id = rootElem.getAttributeValue("id");
        if (id != null)
            messageID = id;
        else
            msgTree.setAttribute("id", messageID);
    }

    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        this.msgTree = msgTree;
        //check if there is an ID, if there is, use it
        String id = msgTree.getAttributeValue("id");
        if (id != null)
            messageID = id;
        return this;
    }

    /**
     * sets the message id.  This is not used by outsiders to set the message id since
     * ID's are automatically generated. Basically, all outgoing message have automatic IDs generated, but incoming message IDs are
     * sent by the remote server. However, sometimes you may need to manipulate
     * the id yourself, though it's normally not the case.
     */
    public void setMessageID(String messageID) {
        this.messageID = messageID;
        msgTree.setAttribute("id", messageID);
    }

    /**
     * retrieves the DOM tree related to the message.. NOTE: this method is highly unstable.  Child classes
     * may not synchronize data with the DOM tree unless it's during the parsing of the incoming message
     * or the encoding of an outgoing message (basically, when parse() and encode() are run).  Otherwise,
     * within any given time between those two methods are run, the DOM may be out of sync with the current
     * data stored in the object.  It is thus advisable that you do NOT retrieve the DOM unless you know what
     * you're doing.  In fact, if you want to retrieve certain information and you require accessing the internal
     * DOM structure, subclass the message object and override the parse() and encode() to retrieve the data
     * you want.  Another alternative is to call encode() every time you need to access the DOM.  This will
     * ensure that the data inside the DOM is updated (but at the price of performance since you have to go through
     * one encode to get data sync'ed). The best way is not to touch this method at all. :)
     */
    public Element getDOM() {
        return msgTree;
    }

    /**
     * The default encoding will serialize the DOM Tree.  If there are X Messages, it will also
     * get included one level below the main "root" element.  By one level below, it means that
     * the X Messages will be put as children of the DOM Tree's main element.
     * If this is not a desired behavior, you will need to override and encode your own message.
     */
    public String encode() throws ParseException {
        if (isSendXMessages() && getXMessages() != null && !getXMessages().isEmpty()) {
            //attach x messages to the end of the DOM
            Iterator iter = getXMessages().values().iterator();
            JabberMessage msg;
            msg = (JabberMessage) iter.next();
            if (JabberJDOMMessage.class.isAssignableFrom(msg.getClass())) {
                msgTree.addContent(((JabberJDOMMessage) msg).getDOM());
            } else {
                //not assignable from JabberJDOMMessage, let's turn it into DOM first
                try {
                    Element dom = JabberUtil.parseXmlStringToDOM(msg.encode());
                    msgTree.addContent(dom);
                } catch (Exception ex) {
                    //IOException and JDOMException
                    throw new ParseException("Unable to parse a JabberMessage text into DOM element", ex);
                }
            }
        }
        XMLOutputter os = getXMLOutputter();
        return os.outputString(msgTree);
    }

    public String toString() {
        try {
            return encode();
        } catch (ParseException ex) {
            return "Parse Exception: " + ex.toString();
        }
    }

    /**
     * retrieves the XML Outputter associated with every jabber jdom message. it does lazy loading for memory conservation.
     */
    protected XMLOutputter getXMLOutputter() {
        if (output == null) {
            output = new XMLOutputter();
            output.getFormat().setEncoding("UTF8");
        }
        return output;
    }

    /**
     * the default message type is unknown.  It doesn't mean that there is no type.
     * It just means that there is no known message parser for this particular object.
     */
    public int getMessageType() {
        return JabberCode.MSG_UNKNOWN;
    }
}
