package com.echomine.jabber;

import org.xml.sax.Attributes;

/**
 * <p>Handles incoming message and creates the proper message object to contain the incoming parse data. It is pretty much a
 * message processor. The handler works in conjunction with the Message Builder, but it doesn't always need to use the message
 * builder.</p><p>The way message handling works is pretty simple.</p><p>
 * 1. message comes in through the low-level sax content handler. <br>
 * 2. content handler looks up a list of handler that can handle incoming elements.<br>
 * 3. content obtain reference of the handler and delegates the rest of the data to the handler. <br>
 * 4. handler receives data, creating new message objects each time that it is called to start a new object. Optionally, the
 * handler can delegate the task of creating new message objects to the Message Builders (this is default behavior for the
 * module).<br> 5. message is then sent out to the message listener. <br>
 * </p> <p> Pretty much each message type will require its own message handler OR message builder. Most of the time you will
 * not need to create a message handler unless it's your desire to work directly with the SAX events (ie. you are not
 * interested in using JDOM for DOM processing).  Register a message builder and you are good to go.</p>
 * <p>The way the methods here are called is very similar to the way SAX Handlers are called.</p> <p>
 * 1. when a new message starts, startMessage() is called. (same as startDocument).<br>
 * 2. Elements are then received through startElement() and endElement(). <br>
 * 3. When the end of the message is reached, endMessage() is called.<br>
 * 4. Then getMessage() will hand it off to a message builder to instantiate a message object of the approriate type.<br>
 * </p> <p>Note that the first element received through startElement() is the beginning of the message itself.</p>
 */
abstract public class JabberMessageHandler {
    private JabberMessageParser msgParser;

    public JabberMessageHandler(JabberMessageParser msgParser) {
        this.msgParser = msgParser;
    }

    public JabberMessageParser getMessageParser() {
        return msgParser;
    }

    /**
     * This method is called when the beginning of the message is received.
     * Any sort of resetting or initializing should be done here.
     */
    public abstract void startMessage();

    /**
     * This method is called when the end of the message is reached. Any sort of resetting or destroying should be done here.
     * Normally, nothing needs to be done, but you may need to do some post processing if you want.
     */
    public abstract void endMessage();

    /** the methods that must be implemented to work with the XML content that's coming in */
    public abstract void startElement(String namespaceURI, String localName, String qName, Attributes attr);

    /** the methods that must be implemented to work with the XML content that's coming in */
    public abstract void endElement(String namespaceURI, String localName, String qName);

    /** the methods that must be implemented to work with the XML content that's coming in */
    public abstract void characters(char[] ch, int start, int length);

    public abstract JabberMessage getMessage();
}
