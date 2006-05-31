package com.echomine.jabber.msg;

import com.echomine.jabber.DefaultMessageParser;
import com.echomine.jabber.JID;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberUtil;
import junit.framework.TestCase;
import org.jdom.Element;

import java.util.List;

public class DataXMessageTest extends TestCase {
    DefaultMessageParser parser = new DefaultMessageParser();

    public void testParsingAndEncodingForItemFields() throws Exception {
        DataXMessage msg = new DataXMessage(DataXMessage.TYPE_RESULT);
        DataXField field = new DataXField(DataXField.TYPE_JID_SINGLE);
        field.setVariableName("jid");
        field.setJIDValue(new JID("reatmon@jabber.org"));
        msg.addItemField(field);
        field = new DataXField();
        field.setVariableName("email");
        field.setTextValue("reatmon@jabber.org");
        msg.addItemField(field);
        field = new DataXField();
        field.setVariableName("first");
        field.setTextValue("Ryan");
        msg.addItemField(field);
        String encode = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(encode);
        msg = new DataXMessage();
        msg.parse(null, elem);
        List list = msg.getItemFields();
        int size = list.size();
        assertEquals(3, size);
        assertEquals("jid", ((DataXField) list.get(0)).getVariableName());
        assertEquals("email", ((DataXField) list.get(1)).getVariableName());
        assertEquals("first", ((DataXField) list.get(2)).getVariableName());
    }

    /** tests that the parsing and encoding of the reported element is done properly */
    public void testParsingAndEncodingForReportedFields() throws Exception {
        DataXMessage msg = new DataXMessage(DataXMessage.TYPE_RESULT);
        msg.setTitle("Search Results");
        msg.addReportedField(new DataXField(DataXField.TYPE_JID_SINGLE, "jid", "JID"));
        msg.addReportedField(new DataXField(DataXField.TYPE_TEXT_SINGLE, "first", "First"));
        msg.addReportedField(new DataXField(DataXField.TYPE_TEXT_SINGLE, "last", "Last"));
        msg.addReportedField(new DataXField(DataXField.TYPE_TEXT_SINGLE, "nick", "Nick"));
        msg.addReportedField(new DataXField(DataXField.TYPE_TEXT_SINGLE, "email", "Email"));
        String encode = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(encode);
        msg = new DataXMessage();
        msg.parse(null, elem);
        assertEquals("Search Results", msg.getTitle());
        List list = msg.getReportedFields();
        int size = list.size();
        assertEquals(5, size);
        assertEquals("jid", ((DataXField) list.get(0)).getVariableName());
        assertEquals("first", ((DataXField) list.get(1)).getVariableName());
        assertEquals("last", ((DataXField) list.get(2)).getVariableName());
        assertEquals("nick", ((DataXField) list.get(3)).getVariableName());
        assertEquals("email", ((DataXField) list.get(4)).getVariableName());
    }

    /**
     * Tests that the parsing and encoding of the fields are done properly.
     */
    public void testGetFieldsParsingAndEncoding() throws Exception {
        DataXMessage msg = new DataXMessage(DataXMessage.TYPE_FORM);
        DataXField field = new DataXField();
        field.setVariableName("var1");
        field.setLabel("label1");
        field.addValue("value1");
        msg.addField(field);
        field = new DataXField(DataXField.TYPE_BOOLEAN);
        field.setVariableName("var2");
        field.setBooleanValue(true);
        msg.addField(field);
        String encode = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(encode);
        msg = new DataXMessage();
        msg.parse(parser, elem);
        assertEquals(DataXMessage.TYPE_FORM, msg.getFormType());
        List fields = msg.getFields();
        assertNotNull(fields);
        assertEquals(2, fields.size());
        field = (DataXField) fields.get(0);
        assertNotNull(field);
        assertEquals("var1", field.getVariableName());
        assertEquals("label1", field.getLabel());
        assertEquals("value1", field.getStringValues().get(0));
        field = (DataXField) fields.get(1);
        assertNotNull(field);
        assertEquals("var2", field.getVariableName());
        assertTrue(field.getBooleanValue());
    }

    /**
     * Tests the retrieval of the fields with no fields in there.
     * It will check that the return value is never null.
     */
    public void testGetFieldsWithNoFields() throws Exception {
        DataXMessage msg = new DataXMessage();
        List fields = msg.getFields();
        assertNotNull(fields);
        assertEquals(0, fields.size());
    }

    /** tests the retrieving/encoding/parsing of instructions tag */
    public void testInstructions() throws Exception {
        DataXMessage msg = new DataXMessage();
        msg.setInstructions("simple instructions");
        String encode = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(encode);
        msg = new DataXMessage();
        msg.parse(parser, elem);
        assertEquals("simple instructions", msg.getInstructions());
    }

    /** tests retrieving/encoding/parsing of title tag */
    public void testTitle() throws Exception {
        DataXMessage msg = new DataXMessage();
        msg.setTitle("simple title");
        String encode = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(encode);
        msg = new DataXMessage();
        msg.parse(parser, elem);
        assertEquals("simple title", msg.getTitle());
    }

    /**
     * Tests that the XML for the create cancel is encoded and parsed properly
     */
    public void testCreateCancelForm() throws Exception {
        DataXMessage msg = new DataXMessage(DataXMessage.TYPE_CANCEL);
        String encodeData = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(encodeData);
        msg = new DataXMessage();
        msg.parse(parser, elem);
        assertEquals(DataXMessage.TYPE_CANCEL, msg.getFormType());
    }

    /**
     * Tests that the default constructor uses a default form type is submit
     */
    public void testDefaultFormType() throws Exception {
        DataXMessage msg = new DataXMessage();
        assertEquals(DataXMessage.TYPE_SUBMIT, msg.getFormType());
    }

    /**
     * this tests that the parser has the message registered to parse the namespace
     */
    public void testParserSupportsMessage() throws Exception {
        assertTrue(parser.supportsParsingFor("x", JabberCode.XMLNS_X_DATA));
    }

    /**
     * Test to make sure that the message type is set properly
     */
    public void testMessageType() throws Exception {
        DataXMessage msg = new DataXMessage();
        assertEquals(JabberCode.MSG_X_DATA, msg.getMessageType());
    }
}
