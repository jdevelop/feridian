package com.echomine.jabber;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.xml.sax.Attributes;

/**
 * <p>The JDOM MessageHandler is the default handler for all incoming messages. It simply takes all incoming messages and put
 * them into a JDOM tree structure that can later be queried and manipulated. JDOM is provided as part of the distribution.</p>
 * <p>Normally, this will be the base class for all other message handlers.  "Subhandlers" normally work directly with the DOM
 * tree and don't have to worry about SAX events.  If you're not interested in using JDOM as the base handler,
 * you can create your own MessageHandler and handle the messages your own way.</p>
 */
public class JDOMMessageHandler extends JabberMessageHandler {
    private Element rootElem;
    private Element curElem;
    private JabberMessage msg;

    public JDOMMessageHandler(JabberMessageParser msgParser) {
        super(msgParser);
    }

    /**
     * This method is called when the beginning of the message is received.
     * Any sort of resetting or initializing should be done here.
     */
    public void startMessage() {
        rootElem = curElem = null;
        msg = null;
    }

    /**
     * This method is called when the end of the message is reached. Any sort of resetting or destroying should be done here.
     * Normally, nothing needs to be done, but you may need to do some post processing if you want.
     */
    public void endMessage() {
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attr) {
        String prefix = "";
        if (qName.lastIndexOf(":") != -1)
            prefix = qName.substring(0, qName.lastIndexOf(":"));
        String attrPrefix, attrQName;
        Attribute att;
        //if root element is null, then this first element will be the root element
        Element elem = new Element(localName, prefix, namespaceURI);
        for (int i = 0; i < attr.getLength(); i++) {
            attrQName = attr.getQName(i);
            if (attrQName.lastIndexOf(":") != -1)
                attrPrefix = attrQName.substring(0, attrQName.lastIndexOf(":"));
            else
                attrPrefix = "";
            att = new Attribute(attr.getLocalName(i), attr.getValue(i), Namespace.getNamespace(attrPrefix, attr.getURI(i)));
            elem.setAttribute(att);
        }
        if (rootElem == null) {
            rootElem = elem;
            curElem = rootElem;
        } else {
            curElem.addContent(elem);
            //set to the next element
            curElem = elem;
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) {
        //end element reached.. just go up one parent
        curElem = (Element) curElem.getParent();
    }

    public void characters(char[] ch, int start, int length) {
        //add it to the current element's data
        curElem.addContent(new String(ch, start, length));
    }

    /**
     * obtains the DOM tree that is associated with this message handler. It is recommended
     * that you call this method AFTER endMessage() has been called.
     */
    public Element getDOM() {
        return rootElem;
    }

    /**
     * the method actually has the parser parse the message.
     * If parser is not found for a message, it will create a default JabberJDOMMessage.
     */
    public JabberMessage getMessage() {
        if (msg != null) return msg;
        try {
            //check if there is a parser for the element
            msg = getMessageParser().createMessage(rootElem.getName(), rootElem.getNamespace(), getDOM());
        } catch (MessageNotSupportedException ex) {
        }
        if (msg == null) {
            msg = new JabberJDOMMessage(rootElem);
        }
        return msg;
    }
}
