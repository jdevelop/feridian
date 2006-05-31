package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberJDOMMessage;
import com.echomine.jabber.JabberMessage;
import com.echomine.jabber.JabberMessageParser;
import org.jdom.Element;

/**
 * Submits and parses a Out-Of-Band (OOB) IQ message.  The message will return the URL to download a file from.
 * The OOB URL does not necessarily have to be http based.  However, if it is not, then you may or may not be
 * able to handle the protocol.  That is up to you to either accept or reject the OOB request.
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0066.html">JEP-0066 Version 1.0</a></b></p>
 *
 * @since 0.8a4
 */
public class OOBXMessage extends JabberJDOMMessage implements JabberCode {
    private String url;
    private String description;

    public OOBXMessage() {
        super(new Element("x", JabberCode.XMLNS_X_OOB));
    }

    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        super.parse(parser, msgTree);
        url = msgTree.getChildText("url", XMLNS_X_OOB);
        description = msgTree.getChildText("desc", XMLNS_X_OOB);
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

    public void setUrl(String url) {
        this.url = url;
        getDOM().removeChild("url", XMLNS_X_OOB);
        if (url != null)
            getDOM().addContent(new Element("url", XMLNS_X_OOB).addContent(url));
    }

    public void setDescription(String description) {
        this.description = description;
        getDOM().removeChild("desc", XMLNS_X_OOB);
        if (description != null)
            getDOM().addContent(new Element("desc", XMLNS_X_OOB).addContent(description));
    }

    public int getMessageType() {
        return MSG_X_OOB;
    }
}
