package com.echomine.jabber;

import com.echomine.jabber.msg.EventXMessage;
import com.echomine.jabber.msg.ExpireXMessage;
import com.echomine.jabber.msg.LastIQMessage;
import com.echomine.jabber.parser.JabberJAXPParser;
import junit.framework.TestCase;
import org.xml.sax.InputSource;

import java.io.StringReader;

/**
 * This class tests to make sure that the content handler is doing its job.
 */
public class JabberContentHandlerTest extends TestCase {
    private JabberMessageReceiver receiver;
    private JabberSession session;
    private MessageRequestQueue queue = new MessageRequestQueue();
    private Jabber jabber = new Jabber();
    private JabberContext context;
    private JabberContentHandler contentHandler;
    private JabberErrorHandler errorHandler;
    private JabberJAXPParser parser = new JabberJAXPParser();

    public JabberContentHandlerTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        //setup dummy objects
        if (context == null)
            context = new JabberContext("test", "test", "jabber.org");
        if (session == null)
            session = jabber.createSession(context);
        receiver = new DefaultMessageReceiver(session);
        if (contentHandler == null)
            contentHandler = new JabberContentHandler(session, receiver, queue, new JDOMXMessageHandler(new DefaultMessageParser()));
        if (errorHandler == null)
            errorHandler = new JabberErrorHandler();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        queue.clear();
    }

    /**
     * tests that the content handler will parse the stream:stream init
     * message properly
     */
    public void testStreamStream() {
        String streamXML = "<stream:stream xmlns:stream='http://etherx.jabber.org/streams' from='jabber.org' id='1234567890'></stream:stream>";
        SuccessMessageListener ml = new SuccessMessageListener() {
            public void messageReceived(JabberMessageEvent event) {
                if (event.getMessageType() == JabberCode.MSG_INIT) {
                    MsgSessionInit msg = (MsgSessionInit) event.getMessage();
                    if ("1234567890".equals(msg.getSessionID()) && "jabber.org".equals(msg.getServerName()))
                        success = true;
                }
            }
        };
        receiver.addMessageListener(ml);
        //now start the parsing
        parser.parse(false, true, contentHandler, errorHandler, new InputSource(new StringReader(streamXML)));
        receiver.removeMessageListener(ml);
        //parsing done check success
        assertTrue(ml.success);
    }

    /**
     * Tests that the parsing of normal messages without any X Messages is correct
     */
    public void testMessageWithoutX() {
        String streamXML = "<stream:stream xmlns:stream='http://etherx.jabber.org/streams' from='jabber.org' id='1234567890'>" +
                "<iq xmlns='jabber:client' from='jabber.org' to='ckchris@jabber.org/Home' type='result' id='id_10001'>" +
                "<query xmlns='jabber:iq:last' seconds='409186'/></iq></stream:stream>";
        SuccessMessageListener ml = new SuccessMessageListener() {
            public void messageReceived(JabberMessageEvent event) {
                if (event.getMessageType() == JabberCode.MSG_IQ_LAST) {
                    LastIQMessage msg = (LastIQMessage) event.getMessage();
                    if (msg.getSeconds() == 409186)
                        success = true;
                }
            }
        };
        receiver.addMessageListener(ml);
        //now start the parsing
        parser.parse(false, true, contentHandler, errorHandler, new InputSource(new StringReader(streamXML)));
        receiver.removeMessageListener(ml);
        //parsing done check success
        assertTrue(ml.success);
    }

    /**
     * Tests message parsing that includes ONE X Message only.
     */
    public void testMessageWithOneXMessage() {
        String streamXML = "<stream:stream xmlns:stream='http://etherx.jabber.org/streams' from='jabber.org' id='1234567890'>" +
                "<message xmlns='jabber:client' to='test@test.org' id='msg811'><subject>subject</subject>" +
                "<body>This is the body</body>" +
                "<x xmlns='jabber:x:expire' seconds='1800' stored='912830221'/></message></stream:stream>";
        SuccessMessageListener ml = new SuccessMessageListener() {
            public void messageReceived(JabberMessageEvent event) {
                if (event.getMessageType() == JabberCode.MSG_CHAT) {
                    JabberChatMessage msg = (JabberChatMessage) event.getMessage();
                    if ("subject".equals(msg.getSubject()) && "This is the body".equals(msg.getBody())) {
                        ExpireXMessage xmsg = (ExpireXMessage) msg.getXMessage("jabber:x:expire");
                        if (xmsg != null && xmsg.getStoredTimeInMillis() == 912830221L && xmsg.getExpireTimeout() == 1800)
                            success = true;
                    }
                }
            }
        };
        receiver.addMessageListener(ml);
        //now start the parsing
        parser.parse(false, true, contentHandler, errorHandler, new InputSource(new StringReader(streamXML)));
        receiver.removeMessageListener(ml);
        //parsing done check success
        assertTrue(ml.success);
    }

    /**
     * Tests message parsing that includes more than one X Message to see how parsing of multiple X messages
     * work.
     */
    public void testMessageWithMultipleXMessages() {
        String streamXML = "<stream:stream xmlns:stream='http://etherx.jabber.org/streams' from='jabber.org' id='1234567890'>" +
                "<message xmlns='jabber:client' to='test@test.org' id='msg811'><subject>subject</subject>" +
                "<body>This is the body</body>" +
                "<x xmlns='jabber:x:expire' seconds='1800' stored='912830221'/>" +
                "<x xmlns='jabber:x:event'><delivered/><composing/></x></message></stream:stream>";
        SuccessMessageListener ml = new SuccessMessageListener() {
            public void messageReceived(JabberMessageEvent event) {
                if (event.getMessageType() == JabberCode.MSG_CHAT) {
                    JabberChatMessage msg = (JabberChatMessage) event.getMessage();
                    if ("subject".equals(msg.getSubject()) && "This is the body".equals(msg.getBody())) {
                        ExpireXMessage xmsg = (ExpireXMessage) msg.getXMessage("jabber:x:expire");
                        EventXMessage emsg = msg.getEventMessage();
                        if (xmsg != null && xmsg.getStoredTimeInMillis() == 912830221L && xmsg.getExpireTimeout() == 1800)
                            if (emsg != null && emsg.isComposing() && emsg.isDelivered() && !emsg.isDisplayed() && !emsg.isOffline())
                                success = true;
                    }
                }
            }
        };
        receiver.addMessageListener(ml);
        //now start the parsing
        parser.parse(false, true, contentHandler, errorHandler, new InputSource(new StringReader(streamXML)));
        receiver.removeMessageListener(ml);
        //parsing done check success
        assertTrue(ml.success);
    }

    abstract class SuccessMessageListener implements JabberMessageListener {
        boolean success = false;
    }
}
