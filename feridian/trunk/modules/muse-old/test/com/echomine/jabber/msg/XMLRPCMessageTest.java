package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.DefaultMessageParser;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberUtil;
import com.echomine.xmlrpc.Call;
import com.echomine.xmlrpc.Response;
import com.echomine.xmlrpc.SerializerFactory;
import junit.framework.TestCase;
import org.jdom.Element;

/**
 * Tests the XML RPC message to see if the call is outputting the proper XML
 */
public class XMLRPCMessageTest extends TestCase {
    DefaultMessageParser parser = new DefaultMessageParser();
    SerializerFactory factory = new SerializerFactory();

    public void testXMLRPCMessage() throws Exception {
        Response response = new Response(4, "Too Many Parameters.", factory);
        // Create a Call object.
        Call call = new Call("getHelloWorldString", factory);
        call.addParameter(new Integer(1));
        XMLRPCMessage rpcMsg = new XMLRPCMessage(call);
        assertEquals("<query xmlns=\"jabber:iq:rpc\">" +
                "<methodCall><methodName>getHelloWorldString</methodName><params><param><value><int>1</int></value>" +
                "</param></params></methodCall></query></iq>", rpcMsg.toString().substring(rpcMsg.toString().indexOf("<query")));
        rpcMsg = new XMLRPCMessage(response);
        assertEquals("<query xmlns=\"jabber:iq:rpc\">" +
                "<methodResponse><fault><value><struct><member><name>faultString</name><value>" +
                "<string>Too Many Parameters.</string></value></member><member><name>faultCode</name><value>" +
                "<int>4</int></value></member></struct></value></fault></methodResponse></query></iq>", rpcMsg.toString().substring(rpcMsg.toString().indexOf("<query")));
    }

    /**
     * this tests that the parser has the message registered to parse the namespace
     */
    public void testParserSupportsMessage() throws Exception {
        assertTrue(parser.supportsParsingFor("query", JabberCode.XMLNS_IQ_XMLRPC));
    }

    /**
     * tests message type compliance to make sure it is returning the proper type
     */
    public void testMessageType() {
        XMLRPCMessage msg = new XMLRPCMessage();
        assertEquals(JabberCode.MSG_IQ_XMLRPC, msg.getMessageType());
    }

    /**
     * This test cases showcases a bug that happens when an incoming message is parsed
     * and then toString is called.  The specific order of sequence that causes the problem
     * is as follows:
     * 1) parse incoming message
     * 2) call toString() to show message
     * 3) toString() consequently calls encode()
     * 4) encode throws ParseException because call or response is always null during sanity check.
     * <p/>
     * The reason for this bug is because the parsing of the call/response object is done when
     * the requested object is retrieved.  However, if the call/response did not get requested
     * before someone decides to call toString() to output data, then things will not go as
     * planned.
     */
    public void testEncodeParseExceptionAfterParse() throws Exception {
        Call call = new Call("getHelloWorldString", factory);
        XMLRPCMessage rpcMsg = new XMLRPCMessage(call);
        String xml = rpcMsg.toString();
        Element root = JabberUtil.parseXmlStringToDOM(xml);
        rpcMsg = new XMLRPCMessage();
        //simulate bug workflow
        rpcMsg.parse(parser, root);
        try {
            rpcMsg.encode();
        } catch (ParseException ex) {
            fail("XMLRPCMessage should NOT throw ParseException when encode() is called right after parse()");
        }
    }
}
