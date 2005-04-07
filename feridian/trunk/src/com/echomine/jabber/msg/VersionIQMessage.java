package com.echomine.jabber.msg;

import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberIQMessage;
import org.jdom.Element;

/**
 * Submits and parses a Client Version message.  The message will return the software (and its version) of the recipient that
 * you sent the message to. It will give you information such as the software client being used, the version, and the OS the
 * client is running on. This message seems to only work with the server and not when you send it to a user (somehow not
 * supported).  Thus, current implementation will not allow you to create a message that contains your own
 * time information to send to the server.  When such feature is supported, the message will implement it.
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0092.html">JEP-0092 Version 1.0</a></b></p>
 */
public class VersionIQMessage extends JabberIQMessage implements JabberCode {
    private String name;
    private String version;
    private String os;

    /**
     * this constructor is for messages with type.
     */
    public VersionIQMessage(String type) {
        super(type);
        //add in the query element
        getDOM().addContent(new Element("query", XMLNS_IQ_VERSION));
    }

    /**
     * defaults to iq type get
     */
    public VersionIQMessage() {
        this(TYPE_GET);
    }

    public String getName() {
        if (name != null) return name;
        Element query = getDOM().getChild("query", XMLNS_IQ_VERSION);
        if (query != null)
            name = query.getChildText("name", XMLNS_IQ_VERSION);
        return name;
    }

    public String getVersion() {
        if (version != null) return version;
        Element query = getDOM().getChild("query", XMLNS_IQ_VERSION);
        if (query != null)
            version = query.getChildText("version", XMLNS_IQ_VERSION);
        return version;
    }

    /**
     * retrieve the operating system the client is running on.
     */
    public String getOS() {
        if (os != null) return os;
        Element query = getDOM().getChild("query", XMLNS_IQ_VERSION);
        if (query != null)
            os = query.getChildText("os", XMLNS_IQ_VERSION);
        return os;
    }

    public int getMessageType() {
        return MSG_IQ_VERSION;
    }
}
