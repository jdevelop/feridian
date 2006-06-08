package com.echomine.jabber;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.xml.sax.Attributes;

import java.util.HashMap;

/**
 * <p>This handler adds support for handling any jabber extensions. If your message does not support X Namespaces,
 * then there is no need to subclass from this handler as it'll just cause extra message processing overhead.</p>
 * <p>First, the handler looks at X elements (ie. elements with the name "x") only.  Second, once the entire message
 * is parsed, the handler will create a list of X Messages that contains the data.  It does this by consulting the Message
 * Builder List and for each X Namespace that is recognized, it will create a JabberMessage instance for that X Message and
 * store the JabberMessage in the list for retrieval.  If it doesn't find a message builder, it will create a JabberJDOMMessage
 * as default message type.</p> <p>If you want to work with X Messages, you can subclass from this
 * handler. Then, the procedure works like this:</p> <p> 1. In your startMessage(), you call super.startMessage() to
 * initialize the X Message Handler elements as well.<br> 2. In your startElement(), you call super.startElement() to let the
 * X Message Handler work its magic.  Then you proceed to process your the elements the way you want to.<br> 3. In your
 * endElement(), you call super.endElement().<br> 4. In your endMessage(), you call super.endMessage().<br> 5. In your
 * getMessage(), you can then retrieve all the X Messages from the X Message Handler by calling getXMessages(), which will
 * return a List.<br> 6. You then add it to the JabberMessage you created or use it however you like. </p>
 */
public class JDOMXMessageHandler extends JDOMMessageHandler {
    private HashMap xMsgs;
    private Element rootElem;
    private Element curElem;
    private boolean process;

    public JDOMXMessageHandler(JabberMessageParser msgParser) {
        super(msgParser);
    }

    /**
     * @return a list of JabberMessage instances of the X Namespace
     */
    public HashMap getXMessages() {
        return xMsgs;
    }

    public void startMessage() {
        super.startMessage();
        curElem = rootElem = null;
        process = false;
        xMsgs = new HashMap();
    }

    /**
     * This method will save the X Messages into the message object
     */
    public void endMessage() {
        super.endMessage();
        //get the message and add the x messages into it if there are xmessages
        if (!xMsgs.isEmpty()) getMessage().setXMessages(xMsgs);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attr) {
        //if this is not an X element and not in the middle of processing one, then
        //let parent handle it.
        if (!process && !localName.equals("x")) {
            super.startElement(namespaceURI, localName, qName, attr);
            return;
        }
        if (localName.equals("x"))
            process = true;
        Element elem = new Element(localName, namespaceURI);
        //add the attributes
        String attrPrefix, attrQName;
        Attribute att;
        for (int i = 0; i < attr.getLength(); i++) {
            attrQName = attr.getQName(i);
            if (attrQName.lastIndexOf(":") != -1)
                attrPrefix = attrQName.substring(0, attrQName.lastIndexOf(":"));
            else
                attrPrefix = "";
            att = new Attribute(attr.getLocalName(i), attr.getValue(i), Namespace.getNamespace(attrPrefix, attr.getURI(i)));
            elem.setAttribute(att);
        }
        //inside the <x> tag, process the tag
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
        //check if end of the X element message is reached
        if (process && localName.equals("x")) {
            JabberMessage msg;
            try {
                //check if there is a builder for the element
                msg = getMessageParser().createMessage(rootElem.getName(), rootElem.getNamespace(), rootElem);
            } catch (MessageNotSupportedException ex) {
                msg = new JabberJDOMMessage(rootElem);
            }
            xMsgs.put(rootElem.getNamespace().getURI(), msg);
            //reset the elements for processing next <x> tag
            rootElem = curElem = null;
            //end processing of <x> tag
            process = false;
        } else if (process) {
            curElem = (Element) curElem.getParent();
        } else {
            super.endElement(namespaceURI, localName, qName);
        }
    }

    public void characters(char[] ch, int start, int length) {
        super.characters(ch, start, length);
        if (process)
            curElem.addContent(new String(ch, start, length));
    }
}
