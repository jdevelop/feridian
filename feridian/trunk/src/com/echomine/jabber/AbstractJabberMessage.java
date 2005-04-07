package com.echomine.jabber;

import com.echomine.common.ParseException;
import org.jdom.Element;

/**
 * This message adds some base functionality for those that uses it.  It adds parsing of error messages,
 * parsing of to, from, and type.  The default for the message is not to look for reply and not sent synchronously.
 */
abstract public class AbstractJabberMessage extends JabberJDOMMessage {
    private String type;
    private JID to;
    private JID from;
    private ErrorMessage errorMessage;
    public final static String TYPE_ERROR = "error";

    /**
     * this constructor is for creating outgoing messages.  It is here to be used by
     * subclasses.  The constructor simply creates a default element tree with the
     * root element as the top top level tag, and then sets the message to use that tree.
     */
    public AbstractJabberMessage(String type, Element root) {
        super(root);
        setType(type);
    }

    protected AbstractJabberMessage() {
        super();
    }

    /**
     * parses the message. The parser is really not used
     */
    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        super.parse(parser, msgTree);
        this.type = msgTree.getAttributeValue("type");
        String val = msgTree.getAttributeValue("to");
        if (val != null)
            this.to = new JID(val);
        val = msgTree.getAttributeValue("from");
        if (val != null)
            this.from = new JID(val);
        return this;
    }

    /**
     * @return the type of the message (ie. error, get, etc)
     */
    public String getType() {
        return type;
    }

    /**
     * sets the type for the message.  If the type is null, type will be not be
     */
    public void setType(String type) {
        this.type = type;
        if (type != null)
            getDOM().setAttribute("type", type);
        else
            getDOM().removeAttribute("type", getDOM().getNamespace());
    }

    /**
     * @return the JID of the recipient
     */
    public JID getTo() {
        return to;
    }

    /**
     * sets the recipient of the message
     */
    public void setTo(JID to) {
        this.to = to;
        if (to != null)
            getDOM().setAttribute("to", to.toString());
        else
            getDOM().removeAttribute("to", getDOM().getNamespace());
    }

    /**
     * @return the from attribute of the message
     */
    public JID getFrom() {
        return from;
    }

    /**
     * sets the originator of the message.  Normally, this is used to set the incoming message.  Outgoing messages
     * will normally not use this because the server will automatically append this field for you.
     *
     * @param from the originator of the message
     */
    public void setFrom(JID from) {
        this.from = from;
        if (from != null)
            getDOM().setAttribute("from", from.toString());
        else
            getDOM().removeAttribute("from", getDOM().getNamespace());
    }

    /**
     * check to see if this is an error message.  There is a special way this method works (for pure
     * convenience sake).  It will first check to see if the message itself is an error message.  If it
     * is, it will return itself as the error.  If it is not an error, then it will check to see if its
     * reply message is an error message (if a reply exists).  If it is, it will use its reply message as the error message.
     *
     * @return true if either the message itself or its reply message is an error message
     */
    public boolean isError() {
        if (TYPE_ERROR.equals(type))
            return true;
        //check if reply is an error message (if it exists)
        AbstractJabberMessage reply = (AbstractJabberMessage) getReplyMessage();
        if ((reply != null) && (reply.isError())) return true;
        return false;
    }

    /**
     * retrieves the error message if this message is an error type. The error message is dynamically processed.
     * If the message itself is an error, it will return itself as the error message.  Otherwise,
     * it will check if its reply message (if a reply exists) is an error message, and if so, use the
     * reply message as the error message. This is purely for convenience sake.
     *
     * @return the error message either from itself or from its reply message, null if no error message
     */
    public ErrorMessage getErrorMessage() {
        if (errorMessage != null) return errorMessage;
        AbstractJabberMessage reply = (AbstractJabberMessage) getReplyMessage();
        //check to make sure it's an error message
        if ((!TYPE_ERROR.equals(type)) && (reply == null)) return null;
        //it's an error, retrieve the error element
        if (TYPE_ERROR.equals(type)) {
            //this message is the error message, not the reply
            Element errorElem = getDOM().getChild("error", getDOM().getNamespace());
            //in case there is no error tag
            if (errorElem == null) return null;
            String code = errorElem.getAttributeValue("code");
            String errmsg = errorElem.getText();
            int errcode = 0;
            if (code != null)
                errcode = Integer.parseInt(code);
            errorMessage = new ErrorMessage(errcode, errmsg);
        } else {
            //check if the reply message is an error
            if ((reply != null) && (!reply.isError())) return null;
            //reply is an error
            errorMessage = reply.getErrorMessage();
        }
        return errorMessage;
    }

    /**
     * sets the error message.  The message type will automatically be changed to TYPE_ERROR.
     * You can still include any other data along with the error message, but the error message
     * will be returned and the type is now TYPE_ERROR.
     *
     * @param msg the error message associated with this message
     */
    public void setErrorMessage(ErrorMessage msg) {
        if (msg == null) throw new IllegalArgumentException("Error message cannot be null");
        this.errorMessage = msg;
        setType(TYPE_ERROR);
        getDOM().removeChild("error", getDOM().getNamespace());
        Element errorElem = new Element("error", getDOM().getNamespace());
        errorElem.setAttribute("code", "" + msg.getCode());
        errorElem.addContent(msg.getMessage());
        getDOM().addContent(errorElem);
    }
}
