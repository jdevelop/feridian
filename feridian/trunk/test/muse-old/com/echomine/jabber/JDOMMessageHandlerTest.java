package com.echomine.jabber;

import com.echomine.jabber.parser.JabberJAXPParser;
import junit.framework.TestCase;
import org.xml.sax.InputSource;

import java.io.StringReader;

/**
 * Test cases for checking that the message handler handles XML parsing properly.
 */
public class JDOMMessageHandlerTest extends TestCase {
    private MessageRequestQueue queue;
    private TestMessageReceiver receiver;
    private DefaultMessageParser msgParser;
    private JabberSession session;
    private JDOMMessageHandler handler;
    private JabberContentHandler chandler;
    private JabberErrorHandler errorHandler;

    protected void setUp() throws Exception {
        queue = new MessageRequestQueue();
        receiver = new TestMessageReceiver();
        msgParser = new DefaultMessageParser();
        msgParser.setParser("test", TestMessage.XMLNS, TestMessage.class.getName());
        Jabber jabber = new Jabber();
        JabberContext ctx = new JabberContext("username", "password", "servername");
        session = jabber.createSession(ctx);
        handler = new JDOMMessageHandler(msgParser);
        chandler = new JabberContentHandler(session, receiver, queue, handler);
        errorHandler = new JabberErrorHandler();
    }

    /**
     * tests that session init messages are parsed and immedialy fired using the content
     * handler and the JDOMX Message handler
     */
    public void testParseAndFireMsgSessionInit() throws Exception {
        receiver.reset();
        StringReader reader = new StringReader("<stream:stream from='jabber.org' id='774455332' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>");
        JabberJAXPParser parser = new JabberJAXPParser();
        parser.parse(false, true, chandler, errorHandler, new InputSource(reader));
        assertTrue(receiver.msgReceived);
        assertTrue(receiver.msg instanceof MsgSessionInit);
        MsgSessionInit msg = (MsgSessionInit) receiver.msg;
        assertEquals("774455332", msg.getSessionID());
        assertEquals("jabber.org", msg.getServerName());
    }

    /**
     * this tests makes sure that when the ending &lt;/stream:stream> is reached, an event is NOT fired for that.
     * The ending even should simply be ignored, skipped, or cause the entire parsing to end promptly.
     *
     * @throws Exception
     */
    public void testMsgSessionInitEndElementDoesNotFireEvent() throws Exception {
        receiver.reset();
        StringReader reader = new StringReader("<stream:stream from='jabber.org' id='774455332' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'></stream:stream>");
        JabberJAXPParser parser = new JabberJAXPParser();
        parser.parse(false, true, chandler, errorHandler, new InputSource(reader));
        assertEquals(1, receiver.count);
    }

    class TestMessageReceiver implements JabberMessageReceiver {
        boolean msgReceived = false;
        int count;
        JabberMessage msg;

        public void addMessageListener(JabberMessageListener l) {
        }

        public void removeMessageListener(JabberMessageListener l) {
        }

        public void reset() {
            count = 0;
            msgReceived = false;
            msg = null;
        }

        public void receive(JabberMessage msg) {
            msgReceived = true;
            this.msg = msg;
            count++;
        }
    }
}
