package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.*;
import org.jdom.Element;

/**
 * Submits and parses a Out-Of-Band (OOB) IQ message.  The message will return the URL to download a file from.
 * The OOB URL does not necessarily have to be http based.  However, if it is not, then you may or may not be
 * able to handle the protocol.  That is up to you to either accept or reject the OOB request.
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0066.html">JEP-0066 Version 1.0</a></b></p>
 *
 * @since 0.8a4
 */
public class OOBIQMessage extends JabberIQMessage implements JabberCode {
    private String url;
    private String description;

    /**
     * this constructor is for messages with type.
     */
    public OOBIQMessage(String type) {
        super(type);
        //add in the query element
        getDOM().addContent(new Element("query", XMLNS_IQ_OOB));
    }

    /**
     * defaults to iq type set to initiate a request
     */
    public OOBIQMessage() {
        this(TYPE_SET);
    }

    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        super.parse(parser, msgTree);
        //parse out the iq message data
        Element query = msgTree.getChild("query", XMLNS_IQ_OOB);
        //check to make sure that there is a query tag
        if (query != null) {
            url = query.getChildText("url", XMLNS_IQ_OOB);
            description = query.getChildText("desc", XMLNS_IQ_OOB);
        }
        return this;
    }

    /**
     * @return the URL associated with the OOB
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return description associated with the OOB
     */
    public String getDescription() {
        return description;
    }

    /**
     * sets the URL for the file to be downloaded
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * sets the description attached to the url
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the message type
     */
    public int getMessageType() {
        return MSG_IQ_OOB;
    }

    public String encode() throws ParseException {
        //add all the attributes into the tree
        Element x = getDOM();
        //remove any children
        x.getChildren().clear();
        Element query = new Element("query", XMLNS_IQ_OOB);
        if (url != null)
            query.addContent(new Element("url", XMLNS_IQ_OOB).addContent(url));
        if (description != null)
            query.addContent(new Element("desc", XMLNS_IQ_OOB).addContent(description));
        x.addContent(query);
        return super.encode();
    }

    /**
     * Creates a success message to send to the remote user notifying that file has been successfully transferred
     *
     * @param to    the recipient jid to send the message to
     * @param msgId the message ID of the original request message
     */
    public static OOBIQMessage createSuccessMessage(JID to, String msgId) {
        OOBIQMessage msg = new OOBIQMessage(TYPE_RESULT);
        msg.setTo(to);
        msg.setMessageID(msgId);
        return msg;
    }

    /**
     * creates a request to notify the remote client to download a file.  This request
     * will require a reply as to whether or not the transfer was success, fail, rejected, etc.
     *
     * @param to   the recipient jid to send the message to
     * @param url  the url of the file to be transferred
     * @param desc an optional description attached with the url. Null if none.
     */
    public static OOBIQMessage createSendUrlMessage(JID to, String url, String desc) {
        OOBIQMessage msg = new OOBIQMessage(TYPE_SET);
        msg.setTo(to);
        msg.setUrl(url);
        msg.setDescription(desc);
        msg.setReplyRequired(true);
        return msg;
    }

    /**
     * creates a request to notify the remote client that the resource at the URL was not
     * found or cannot be retrieved.
     *
     * @param to    the recipient jid to send the message to
     * @param msgId the message id of the original request
     */
    public static OOBIQMessage createNotFoundErrorMessage(JID to, String msgId) {
        OOBIQMessage msg = new OOBIQMessage(TYPE_ERROR);
        msg.setTo(to);
        msg.setMessageID(msgId);
        ErrorMessage emsg = new ErrorMessage(ErrorCode.NOT_FOUND, "Not Found");
        msg.setErrorMessage(emsg);
        return msg;
    }

    /**
     * convenience method to create a OOBIQMessage that sends back a Not Acceptable Error Message to the
     * originator of the OOBIQRequest.
     *
     * @param to    the recipient JID to receive this message
     * @param msgId the message thread ID associated with the first OOB request
     * @return the OOBIQmessage instance
     */
    public static OOBIQMessage createNotAcceptableErrorMessage(JID to, String msgId) {
        OOBIQMessage msg = new OOBIQMessage(TYPE_ERROR);
        msg.setTo(to);
        msg.setMessageID(msgId);
        ErrorMessage emsg = new ErrorMessage(ErrorCode.NOT_ACCEPTABLE, "Not Acceptable");
        msg.setErrorMessage(emsg);
        return msg;
    }
}
