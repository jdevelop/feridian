package com.echomine.jabber;

import junit.framework.TestCase;
import org.jdom.Element;

/**
 * Test case to test ErrorMessage methods.
 */
public class ErrorMessageTest extends TestCase {
    /**
     * Tests only a few code translation. It does not do acceptance testing of all the
     * required codes specified in the JEP.
     */
    public void testTranslateCodeToCondition() {
        ErrorMessage msg = new ErrorMessage(400, "Bad Request");
        assertEquals(ErrorCode.C_BAD_REQUEST, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_MODIFY, msg.getType());
        msg = new ErrorMessage(401, "Not Authorized");
        assertEquals(ErrorCode.C_NOT_AUTHORIZED, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_AUTH, msg.getType());
        msg = new ErrorMessage(503, "Service Unavailable");
        assertEquals(ErrorCode.C_SERVICE_UNAVAILABLE, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_CANCEL, msg.getType());
        msg = new ErrorMessage(502, "Remote Server Error");
        assertEquals(ErrorCode.C_SERVICE_UNAVAILABLE, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_WAIT, msg.getType());
        msg = new ErrorMessage(510, "Disconnected");
        assertEquals(ErrorCode.C_SERVICE_UNAVAILABLE, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_CANCEL, msg.getType());
    }

    public void testTranslateConditionToCode() {
        ErrorMessage msg = new ErrorMessage(ErrorCode.C_BAD_REQUEST, ErrorMessage.TYPE_MODIFY);
        assertEquals(400, msg.getCode());
        msg = new ErrorMessage(ErrorCode.C_SERVICE_UNAVAILABLE, ErrorMessage.TYPE_CANCEL);
        assertEquals(503, msg.getCode());
        msg = new ErrorMessage(ErrorCode.C_RECIPIENT_UNAVAILABLE, ErrorMessage.TYPE_WAIT);
        assertEquals(404, msg.getCode());
    }

    /**
     * Tests that the parsing is working properly
     */
    public void testParseWithCode() throws Exception {
        String xml = "<error code='401'>Not Authorized</error>";
        Element dom = JabberUtil.parseXmlStringToDOM(xml);
        ErrorMessage msg = ErrorMessage.createErrorMessage(dom);
        assertEquals(401, msg.getCode());
        assertEquals(ErrorCode.C_NOT_AUTHORIZED, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_AUTH, msg.getType());
        assertEquals("Not Authorized", msg.getMessage());
    }

    /**
     * Tests that parsing with condition only specified is working properly
     */
    public void testParseWithStreamsConditionNoText() throws Exception {
        String xml = "<error type='auth'><forbidden xmlns='urn:ietf:params:xml:ns:xmpp-streams'/></error>";
        Element dom = JabberUtil.parseXmlStringToDOM(xml);
        ErrorMessage msg = ErrorMessage.createErrorMessage(dom);
        assertEquals(403, msg.getCode());
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_AUTH, msg.getType());
    }

    public void testParseWithStreamsConditionAndText() throws Exception {
        String xml = "<error type='auth'><forbidden xmlns='urn:ietf:params:xml:ns:xmpp-streams'/><text xmlns='urn:ietf:params:xml:ns:xmpp-streams'>Forbidden</text></error>";
        Element dom = JabberUtil.parseXmlStringToDOM(xml);
        ErrorMessage msg = ErrorMessage.createErrorMessage(dom);
        assertEquals(403, msg.getCode());
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_AUTH, msg.getType());
        assertEquals("Forbidden", msg.getMessage());
    }

    /**
     * Tests that parsing with condition specified only is working properly
     */
    public void testParseWithStanzaConditionNoText() throws Exception {
        String xml = "<error type='auth'><forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/></error>";
        Element dom = JabberUtil.parseXmlStringToDOM(xml);
        ErrorMessage msg = ErrorMessage.createErrorMessage(dom);
        assertEquals(403, msg.getCode());
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_AUTH, msg.getType());
    }

    public void testParseWithStanzasConditionAndText() throws Exception {
        String xml = "<error type='auth'><forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/><text xmlns='urn:ietf:params:xml:ns:xmpp-streams'>Forbidden</text></error>";
        Element dom = JabberUtil.parseXmlStringToDOM(xml);
        ErrorMessage msg = ErrorMessage.createErrorMessage(dom);
        assertEquals(403, msg.getCode());
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_AUTH, msg.getType());
        assertEquals("Forbidden", msg.getMessage());
    }

    public void testParseWithCodeAndCondition() throws Exception {
        String xml = "<error code='402' type='auth'><forbidden xmlns='urn:ietf:params:xml:ns:xmpp-streams'/><text xmlns='urn:ietf:params:xml:ns:xmpp-streams'>Forbidden</text>Unauthorized</error>";
        Element dom = JabberUtil.parseXmlStringToDOM(xml);
        ErrorMessage msg = ErrorMessage.createErrorMessage(dom);
        assertEquals(402, msg.getCode());
        assertEquals(ErrorCode.C_FORBIDDEN, msg.getCondition());
        assertEquals(ErrorMessage.TYPE_AUTH, msg.getType());
        assertEquals("Forbidden", msg.getMessage());
    }
}
