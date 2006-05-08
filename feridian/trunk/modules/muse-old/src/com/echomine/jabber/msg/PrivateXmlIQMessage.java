package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberIQMessage;
import com.echomine.jabber.JabberUtil;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;

import java.io.IOException;

/**
 * This message object helps the developer to work with storing and retrieving Private XML data that is stored
 * with the user through the jabber:iq:private IQ namespace.  Normally, this could be client preferences or
 * some sort of data that you want stored on the server so you can retrieve later.
 * In order to store private xml messages, the private xml data that you submit MUST be in its own namespace.
 * Preferably, this namespace does not conflict with any of the namespaces registered with the Jabber Registry.
 * To retrieve the proper XML private data, you must set the namespace and element name that you will be retrieving.
 * You will obtain an error if you do not specify a namespace for the element you want to retrieve.
 * You cannot set xml data for other user except for yourself (ie. you can only send this message to the server).
 * Only one element can be queried per IQ message.  However, you may store multiple elements with different namespaces
 * independently on the server without any problems.
 * <br><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0049.html">JEP-0049 Version 1.0</a></b></br>
 * @since 0.8a4
 */
public class PrivateXmlIQMessage extends JabberIQMessage {
    /** this constructor is for messages with type. */
    public PrivateXmlIQMessage(String type) {
        super(type);
        //add in the query element
        getDOM().addContent(new Element("query", JabberCode.XMLNS_IQ_PRIVATE));
    }

    /** defaults to iq type get to retrieve private xml data */
    public PrivateXmlIQMessage() {
        this(TYPE_GET);
    }

    /**
     * This method gives you the convenience of retrieving the private data you requested in a simpler
     * fashion.  You specify the element name and the namespace that you used in the request (ie. prefs).
     * @param elemName the element name for the root element of your private data
     * @param ns the namespace that you used to store the private data
     * @return The element tree that contains the private data your requested, null if none exists
     */
    public Element getPrivateData(String elemName, String ns) {
        Element query = getDOM().getChild("query", JabberCode.XMLNS_IQ_PRIVATE);
        if (query != null)
            return query.getChild(elemName, Namespace.getNamespace(ns));
        return null;
    }

    /**
     * sets the private data based on the DOM structure that is passed in.
     * @param pvtdata the private data with its own element name and namespace, cannot be null.
     */
    public void setPrivateData(Element pvtdata) {
        if (pvtdata == null) throw new IllegalArgumentException("Private data element cannot be null");
        Element query = getDOM().getChild("query", JabberCode.XMLNS_IQ_PRIVATE);
        if (query != null) {
            //clear the query's children first
            query.getChildren().clear();
            query.addContent(pvtdata);
        }
    }

    /**
     * sets the private data based on the XML String passed in.  The XML string will be turned into an Element
     * and then added into the main DOM tree.
     * @param pvtdata the private data contained in an XML string.
     */
    public void setPrivateData(String pvtdata) throws ParseException {
        if (pvtdata == null) throw new IllegalArgumentException("Private data cannot be null");
        try {
            Element elem = JabberUtil.parseXmlStringToDOM(pvtdata);
            //detach from root document, otherwise exception will be thrown
            elem.detach();
            setPrivateData(elem);
        } catch (JDOMException ex) {
            throw new ParseException("JDOMException thrown: " + ex.getMessage());
        } catch (IOException ex) {
            throw new ParseException("IOException thrown: " + ex.getMessage());
        }
    }

    /**
     * sets the private data element name and namespace that will be retrieved from the server.  Essentially
     * it creates a private data element automatically for you without any children. it also set the message
     * type to a TYPE_GET.
     * @param elemName the element name to be retrieved
     * @param ns the namespace where the private data is stored
     */
    public void setPrivateDataRequest(String elemName, String ns) {
        Element elem = new Element(elemName, ns);
        setPrivateData(elem);
    }

    /** @return the message type */
    public int getMessageType() {
        return JabberCode.MSG_IQ_PRIVATE;
    }
}
