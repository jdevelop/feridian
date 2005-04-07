package com.echomine.jabber;

import com.echomine.common.ParseException;
import com.echomine.jabber.msg.DelayXMessage;
import com.echomine.jabber.msg.EventXMessage;
import com.echomine.jabber.msg.PGPEncryptedXMessage;
import com.echomine.jabber.msg.RosterXMessage;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.util.List;

/**
 * <p>This is the base message for working with private IM messages, group chats, and anything that is sent
 * through the <message> tag.</p> <p>The message body may come in different formats.  For instance, it can come in as XHTML
 * for better style support.  This is an extension and is NOT considered to be an X Message.  Thus, it is retrieved through
 * this class' getHTML().  To display XHTML data in Java is rather easy.  You can simply use a JTextPane or JEditorPane,
 * set the Content MIME Type to text/html, and then just set the Text Pane's text to getHTML().</p>
 * <p>Processing of X Messages are supported as always, but it is up to the developer to implement capabilities to work with
 * the X Message types.</p> <p>Thread IDs is easy to work with.  Normally, if you initiate a chat for the first time with a
 * JID, you should set the Thread ID to a new ID (you can obtain a new ID from the generateThreadID() method, which will
 * return a GUID 32-byte hex string). However, if you are replying to a message, you should set your reply message's Thread ID
 * to the ID of the message that you're replying to. ie. reply.setThreadID(origMsg.getThreadID()).  The developer is
 * responsible for setting the Thread IDs for reply messages. However, if this message is a new message, the API will
 * automatically set the Thread ID to a new GUID for you so you don't need to worry about that part.  But that's
 * only for new messages.</p>
 */
public class JabberChatMessage extends AbstractJabberMessage implements JabberCode {
    public final static String TYPE_NORMAL = "normal";
    public final static String TYPE_CHAT = "chat";
    public final static String TYPE_GROUPCHAT = "groupchat";
    public final static String TYPE_HEADLINE = "headline";
    private static final Namespace htmlNS = Namespace.getNamespace("http://www.w3.org/1999/xhtml");
    private String body;
    private String subject;
    private String threadID;

    /**
     * Normally used for creating an outgoing message
     */
    public JabberChatMessage(String type) {
        super(type, new Element("message", XMLNS_CHAT));
        //set new thread id
        setThreadID(generateThreadID());
    }

    /**
     * defaults the message type to a normal type
     */
    public JabberChatMessage() {
        this(TYPE_NORMAL);
    }

    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        //let the parent class parse out the normal core attributes
        super.parse(parser, msgTree);
        if (getType() == null)
            setType(TYPE_NORMAL);
        threadID = msgTree.getChildText("thread", XMLNS_CHAT);
        return this;
    }

    /**
     * retrieves the message body
     */
    public String getBody() {
        if (body != null) return body;
        body = getDOM().getChildText("body", XMLNS_CHAT);
        return body;
    }

    /**
     * sets the outgoing message body
     */
    public void setBody(String body) {
        this.body = body;
        //removes the child if there is one
        getDOM().removeChild("body", XMLNS_CHAT);
        if (body != null) {
            Element temp = new Element("body", XMLNS_CHAT);
            temp.setText(body);
            getDOM().addContent(temp);
        }
    }

    /**
     * retrieves the body that is encoded with XHTML.  If there isn't a XHTML body, this method
     * will return null.  Normally the body is the same as the getBody() but with additional
     * formatting tags (using XHTML Basic).
     */
    public String getHTMLBody() {
        //<html> should be one level below <message>
        XMLOutputter os = getXMLOutputter();
        String htmlStr = null;
        //html has to be xhtml standards compliant
        Element html = getDOM().getChild("html", htmlNS);
        if (html != null)
            htmlStr = os.outputString(html);
        return htmlStr;
    }

    /**
     * this sets the HTML text. You must make sure that the text is XHTML Basic compliant.
     * Otherwise, the server will report an error. Set to null to remove the HTML from being sent.
     * The html tag should be the following format: <pre><html xmlns="http://www.w3.org/1999/xhtml"/></pre>
     */
    public void setHTMLBody(String html) throws ParseException {
        //remove the html element if one exists
        getDOM().removeChild("html", htmlNS);
        if (html == null) return;
        try {
            Element elem = JabberUtil.parseXmlStringToDOM(html);
            //now insert the element into the DOM
            getDOM().addContent(elem);
        } catch (JDOMException ex) {
            throw new ParseException("JDOMException thrown: " + ex.getMessage());
        } catch (IOException ex) {
            throw new ParseException("IOException thrown: " + ex.getMessage());
        }
    }

    /**
     * retrieves the message subject
     */
    public String getSubject() {
        if (subject != null) return subject;
        subject = getDOM().getChildText("subject", XMLNS_CHAT);
        return subject;
    }

    /**
     * sets the message subject.  Normally this can be null since not all clients may use or display
     * the subject. The message will be added straight into the dom tree.  Set to null to remove an existing subject.
     */
    public void setSubject(String subject) {
        this.subject = subject;
        getDOM().removeChild("subject", XMLNS_CHAT);
        if (subject != null) {
            Element temp = new Element("subject", XMLNS_CHAT);
            temp.setText(subject);
            getDOM().addContent(temp);
        }
    }

    /**
     * retrieve the thread id associated with this message.  It's used to associate different
     * messages so you know which message is going with which conversation.  Since the Thread ID
     * is already parsed in the parse() method, we have no need to retrieve the data again
     * from the DOM.
     */
    public String getThreadID() {
        return threadID;
    }

    /**
     * sets the thread id.  Set to null to remove the thread ID from getting sent with the message
     */
    public void setThreadID(String threadID) {
        this.threadID = threadID;
        getDOM().removeChild("thread", XMLNS_CHAT);
        if (threadID != null) {
            Element temp = new Element("thread", XMLNS_CHAT);
            temp.setText(threadID);
            getDOM().addContent(temp);
        }
    }

    /**
     * checks to see if this message contains a jabber:x:roster message. If it does,
     * then this message is marked as a message that is sending a list of roster
     * contacts over to someone (either someone sending contacts to you or you sending contacts to someone else).
     *
     * @return true if this message contains roster contacts, false otherwise.
     */
    public boolean isRosterMessage() {
        JabberMessage msg = getXMessage(XMLNS_X_ROSTER.getURI());
        if (msg == null) return false;
        return true;
    }

    /**
     * retrieves a list of RosterItem's contained in this message if there are any. If not, then the method returns null.
     *
     * @return List of RosterItem objects, null if there is no list
     */
    public List getRosterList() {
        RosterXMessage msg = (RosterXMessage) getXMessage(XMLNS_X_ROSTER.getURI());
        if (msg == null) return null;
        return msg.getRosterItems();
    }

    /**
     * retrieves the event message if there is one associated with it
     *
     * @return the event message, null if none exists
     */
    public EventXMessage getEventMessage() {
        return (EventXMessage) getXMessage(XMLNS_X_EVENT.getURI());
    }

    /**
     * convenience method to retrieve the Delay X Message (you can get the message by calling getXMessage() as well)
     *
     * @return the delay message, null if none exists
     */
    public DelayXMessage getDelayMessage() {
        return (DelayXMessage) getXMessage(XMLNS_X_DELAY.getURI());
    }

    /**
     * a convenience method to retrieve the PGP encrypted data if there is one attached to this message
     *
     * @return the PGP Encrypted X Message or null if there isn't one attached to this message
     */
    public PGPEncryptedXMessage getPGPMessage() {
        return (PGPEncryptedXMessage) getXMessage(XMLNS_X_PGP_ENCRYPTED.getURI());
    }

    /**
     * the method will generate a unique ID for use.  Currently this implementation
     * will use the MessageID generator and leech some id's from there instead.
     */
    public static String generateThreadID() {
        return MessageID.nextID();
    }

    public int getMessageType() {
        return MSG_CHAT;
    }

    /*    public String toString() {
    return "From: " + getFrom() + ", To: " + getTo() + ", Type: " + getType() +
    ", Subject: " + getSubject() + ", Thread: " + getThreadID() + ", Body: " + getBody() +
    ", HTML Body: " + getHTMLBody();
    }
    */
}
