package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.*;
import com.echomine.xmlrpc.Call;
import com.echomine.xmlrpc.Response;
import com.echomine.xmlrpc.SerializerFactory;
import org.jdom.Element;

/**
 * <p>sends and receives an XMLRPC message over Jabber's <iq> namespace.  This message will be able to submit a method call,
 * parse an incoming response, etc.  Note that the message is specifically targeted for client-side use.  It will
 * allow you to create a method call for the request.  Any incoming messages from the server is considered a response message.</p>
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0009.html">JEP-0009 Version 2.0</a></b></p>
 */
public class XMLRPCMessage extends JabberIQMessage implements JabberCode {
    public static final int XMLRPC_CALL = 1;
    public static final int XMLRPC_RESPONSE = 2;
    private int rpcType;
    private Response response;
    private Call call;
    private SerializerFactory factory;

    /**
     * constructs a default XMLRPC message. The xmlrpc type is not set. You must set the message to have
     * either a call or response before the message can be sent (otherwise,
     * there will be an exception thrown and the message will not be sent).
     * The message is also sent asynchronously.  If you want to wait for
     * a response, then you must explicitly set the Synchronization property.
     * If you set the response or call manually, the iq type and XMLRPC type will automatically be set to the proper values.
     * The default SerializerFactory is used for the factory.
     *
     * @see SerializerFactory
     */
    public XMLRPCMessage() {
        super(TYPE_SET);
        getDOM().addContent(new Element("query", XMLNS_IQ_XMLRPC));
    }

    public XMLRPCMessage(Response response) {
        this();
        setResponse(response);
    }

    public XMLRPCMessage(Call call) {
        this();
        setCall(call);
    }

    /**
     * Sets a custom serializer factory instead of using the default one.
     */
    public void setFactory(SerializerFactory factory) {
        this.factory = factory;
    }

    /**
     * parses the message. The parser will only retrieve information to find out
     * what type of XMLRPC message this is (call/response).
     */
    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        //let the parent parse first
        super.parse(parser, msgTree);
        //get the child element to see if it's methodCall or methodName
        Element query = msgTree.getChild("query", XMLNS_IQ_XMLRPC);
        //if query is null, then it means the message hasn't been fully initialized
        if (query != null) {
            String method = ((Element) query.getChildren().get(0)).getName();
            if (method.equals("methodCall"))
                rpcType = XMLRPC_CALL;
            else if (method.equals("methodResponse"))
                rpcType = XMLRPC_RESPONSE;
            else
                throw new ParseException("XMLRPC Message type is not recognized");
        }
        return this;
    }

    /**
     * the method is overridden to provide some sanity checks before sending
     * the data out.  If the message does not have a call or response set, an exception is thrown.
     */
    public String encode() throws ParseException {
        switch (rpcType) {
            case XMLRPC_CALL:
                if (getCall() == null)
                    throw new ParseException("XMLRPC Message: No call set for XMLRPC Call Type!");
                break;
            case XMLRPC_RESPONSE:
                if (getResponse() == null)
                    throw new ParseException("XMLRPC Message: No response set for XMLRPC Response Type!");
                break;
            default:
                throw new ParseException("XMLRPC Message: No XMLRPC Type Set!");
        }
        //sanity check went fine, let's encode it
        return super.encode();
    }

    /**
     * checks to see if the message is an error message.  It will first check to make sure
     * that the server didn't give us an error.  If it did, then it's a server error.  Then,
     * it will check to see if this is a response message and also check to see if the reply message is an error.
     */
    public boolean isError() {
        //check if message (this or reply) contains server error
        if (super.isError()) return true;
        //now check what type we are
        if (isResponse()) {
            Response resp = getResponse();
            if (resp != null && resp.isFault())
                return true;
            else
                return false;
        } else {
            XMLRPCMessage reply = (XMLRPCMessage) getReplyMessage();
            if (reply == null) return false;
            return reply.isError();
        }
    }

    /**
     * convenience methods to check if the messag is a response
     */
    public boolean isResponse() {
        return (rpcType == XMLRPC_RESPONSE) ? true : false;
    }

    /**
     * convenience methods to check if the messag is a call/request
     */
    public boolean isCall() {
        return (rpcType == XMLRPC_CALL) ? true : false;
    }

    /**
     * retrieves the response object if there is one for this message.
     * it utilizes the currently set serializer factory to parse the response.
     *
     * @return the response object, null if none exists
     */
    public Response getResponse() {
        //use cached response if possible
        if (response != null) return response;
        //parse out the data
        Element elem = getDOM().getChild("query", XMLNS_IQ_XMLRPC).getChild("methodResponse",
                XMLNS_IQ_XMLRPC);
        if (elem == null) return null;
        rpcType = XMLRPC_RESPONSE;
        if (factory == null) factory = new SerializerFactory();
        response = new Response(factory);
        response.parse(elem);
        return response;
    }

    /**
     * retrieves the call object if there is one for this message.
     * it utilizes the currently set serializer factory to parse the call.
     *
     * @return the call object, null if none exists
     */
    public Call getCall() {
        //use cached response if possible
        if (call != null) return call;
        //parse out the data
        Element elem = getDOM().getChild("query", XMLNS_IQ_XMLRPC).getChild("methodCall",
                XMLNS_IQ_XMLRPC);
        if (elem == null) return null;
        rpcType = XMLRPC_CALL;
        if (factory == null) factory = new SerializerFactory();
        call = new Call(factory);
        call.parse(elem);
        return call;
    }

    /**
     * sets the response for this message to be sent.  By calling this message, the
     * message type will automatically be set as a Response type,  the IQ type will be set to "result", all other data
     * will be reset, and the DOM will be newly constructed. You should only
     * use this when you are creating a new message to submit to the server. Don't call this method for an incoming message!
     */
    public void setResponse(Response response) {
        setType(TYPE_RESULT);
        rpcType = XMLRPC_RESPONSE;
        call = null;
        this.response = response;
        Element elemTree = getDOM();
        Element query = elemTree.getChild("query", XMLNS_IQ_XMLRPC);
        //remove all children
        query.getChildren().clear();
        //set the call's namespace to our own to compensate for Jabber's protocol
        response.setNamespace(XMLNS_IQ_XMLRPC);
        //add the call stuff into the tree
        query.addContent(response.getDOM());
    }

    /**
     * sets the call request for this message to be sent.  By calling this message, the
     * message type will automatically be set as a Call type, the IQ type will be
     * set to "set", all other data will be reset, and the DOM will be newly constructed. You should only
     * use this when you are creating a new message to submit to the server. Don't call this method for an incoming message!
     */
    public void setCall(Call call) {
        setType(TYPE_SET);
        rpcType = XMLRPC_CALL;
        response = null;
        this.call = call;
        Element elemTree = getDOM();
        Element query = elemTree.getChild("query", XMLNS_IQ_XMLRPC);
        //remove all children
        query.getChildren().clear();
        //set the call's namespace to our own to compensate for Jabber's protocol
        call.setNamespace(XMLNS_IQ_XMLRPC);
        //add the call stuff into the tree
        query.addContent(call.getDOM());
    }

    /**
     * get the type for this XMLRPC message, whether it is a request or response type.
     */
    public int getXMLRPCType() {
        return rpcType;
    }

    /**
     * this will retrieve the error if this message is an error message.  It this
     * message is not an error message, it will check its reply message to see if it's
     * an error message.  If the reply message is an error message, it will return the error from its reply
     */
    public ErrorMessage getErrorMessage() {
        //check if it's a server error message
        ErrorMessage msg = super.getErrorMessage();
        if (msg != null) return msg;
        //not a server error message, check if it's a response error
        //now check what type we are
        if (isResponse()) {
            Response resp = getResponse();
            if (resp != null && resp.isFault()) {
                msg = new ErrorMessage(response.getFaultCode(), response.getFaultString());
            } else {
                msg = null;
            }
        } else {
            XMLRPCMessage reply = (XMLRPCMessage) getReplyMessage();
            if (reply == null) msg = null;
            msg = reply.getErrorMessage();
        }
        return msg;
    }

    /**
     * @return the message type code of this message
     */
    public int getMessageType() {
        return MSG_IQ_XMLRPC;
    }
}
