package com.echomine.jabber.msg;

import com.echomine.jabber.JID;
import com.echomine.jabber.JabberUtil;
import junit.framework.TestCase;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

public class DataXFieldTest extends TestCase {

    /** tests the parsing and encoding for options */
    public void testEncodeAndParsingForOptions() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_LIST_SINGLE);
        ArrayList options = new ArrayList();
        options.add(new DataXOption("option1", "label1"));
        options.add(new DataXOption("option2"));
        options.add(new DataXOption("option3", "label3"));
        field.setOptions(options);
        Element elem = field.encode();
        field = new DataXField();
        field.parse(elem);
        assertEquals(DataXField.TYPE_LIST_SINGLE, field.getFieldType());
        List list = field.getOptions();
        assertEquals(3, list.size());
        assertEquals("label1", ((DataXOption) list.get(0)).getLabel());
        assertEquals("option1", ((DataXOption) list.get(0)).getValue());
        assertEquals("option2", ((DataXOption) list.get(1)).getValue());
        assertEquals("option3", ((DataXOption) list.get(2)).getValue());
        assertEquals("label3", ((DataXOption) list.get(2)).getLabel());
    }

    /** tests parsing and encoding of values */
    public void testEncodeAndParsingForValues() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_JID_MULTI);
        field.addJIDValue(new JID("123@abc.com"));
        field.addJIDValue(new JID("345@abc.com"));
        Element elem = field.encode();
        field = new DataXField();
        field.parse(elem);
        assertEquals(DataXField.TYPE_JID_MULTI, field.getFieldType());
        List list = field.getJIDValues();
        assertEquals(2, list.size());
        assertEquals("123@abc.com", field.getJIDValues().get(0).toString());
        assertEquals("345@abc.com", field.getJIDValues().get(1).toString());
    }

    /** tests parsing and encoding of optional data to make sure they get processed properly */
    public void testEncodeAndParsingForOptionalData() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_TEXT_SINGLE);
        field.setRequired(true);
        field.setDescription("description");
        field.setLabel("label");
        field.setVariableName("var");
        Element elem = field.encode();
        field = new DataXField();
        field.parse(elem);
        assertEquals(DataXField.TYPE_TEXT_SINGLE, field.getFieldType());
        assertTrue(field.isRequired());
        assertEquals("description", field.getDescription());
        assertEquals("label", field.getLabel());
        assertEquals("var", field.getVariableName());
    }

    /**
     * tests that if a incoming message does not contain a field type (ie. null or doesn't exist),
     * then the field type should default to text-single.  Same is true if the field type is not
     * a supported field type.
     */
    public void testParsingForMissingFieldType() throws Exception {
        String xmlStr = "<field xmlns='jabber:x:data'/>";
        Element elem = JabberUtil.parseXmlStringToDOM(xmlStr);
        DataXField field = new DataXField(DataXField.TYPE_LIST_SINGLE);
        field.parse(elem);
        assertEquals(DataXField.TYPE_TEXT_SINGLE, field.getFieldType());
    }

    /** test the list-single and list-multi field */
    public void testListSingleAndMultiField() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_LIST_SINGLE);
        ArrayList options = new ArrayList();
        options.add(new DataXOption("option1", "label1"));
        options.add(new DataXOption("option2"));
        options.add(new DataXOption("option3", "label3"));
        field.setOptions(options);
        List list = field.getOptions();
        assertEquals(3, list.size());
        assertEquals("label1", ((DataXOption) list.get(0)).getLabel());
        assertEquals("option1", ((DataXOption) list.get(0)).getValue());
        assertEquals("option2", ((DataXOption) list.get(1)).getValue());
        assertEquals("option3", ((DataXOption) list.get(2)).getValue());
        assertEquals("label3", ((DataXOption) list.get(2)).getLabel());
    }

    /** tests the jid-multi field */
    public void testJIDMultiField() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_JID_MULTI);
        field.addJIDValue(new JID("123@abc.com"));
        field.addJIDValue(new JID("345@abc.com"));
        assertEquals(2, field.getJIDValues().size());
        assertEquals("123@abc.com", field.getJIDValues().get(0).toString());
        assertEquals("345@abc.com", field.getJIDValues().get(1).toString());
    }

    /** tests the jid-single field */
    public void testJIDSingleField() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_JID_SINGLE);
        field.setJIDValue(new JID("123@abc.com"));
        assertEquals(1, field.getStringValues().size());
        assertEquals("123@abc.com", field.getJIDValue().toString());
    }

    /** tests the text-private field */
    public void testTextPrivateField() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_TEXT_PRIVATE);
        field.setTextValue("private1");
        assertEquals(1, field.getStringValues().size());
        assertEquals("private1", field.getStringValue());
    }

    /** tests the hidden field */
    public void testHiddenField() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_HIDDEN);
        field.setTextValue("fixed1");
        assertEquals(1, field.getStringValues().size());
        assertEquals("fixed1", field.getStringValue());
    }

    /** tests the fixed field */
    public void testFixedField() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_FIXED);
        field.addTextValue("fixed1");
        field.addTextValue("fixed2");
        field.addTextValue("fixed3");
        List list = field.getStringValues();
        assertEquals(3, list.size());
        assertEquals("fixed1", list.get(0));
        assertEquals("fixed2", list.get(1));
        assertEquals("fixed3", list.get(2));
    }

    /** tests text-multi field */
    public void testTextMultiField() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_TEXT_MULTI);
        field.addTextValue("test1");
        field.addTextValue("test2");
        field.addTextValue("test3");
        List list = field.getStringValues();
        assertEquals(3, list.size());
        assertEquals("test1", list.get(0));
        assertEquals("test2", list.get(1));
        assertEquals("test3", list.get(2));
    }

    /** Tests text-single field */
    public void testTextSingleField() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_TEXT_SINGLE);
        field.setTextValue("test1");
        assertEquals(1, field.getStringValues().size());
        assertEquals("test1", field.getStringValue());
    }

    /** tests setting and retrieving of boolean field is proper */
    public void testBooleanField() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_BOOLEAN);
        field.setBooleanValue(true);
        assertTrue(field.getBooleanValue());
        assertEquals(1, field.getStringValues().size());
        //setting additional values should be fine
        field.setBooleanValue(false);
        assertFalse(field.getBooleanValue());
        assertEquals(1, field.getStringValues().size());
    }

    /** tests that the field type is defaulting to the proper field types */
    public void testFieldType() throws Exception {
        DataXField field = new DataXField(DataXField.TYPE_LIST_SINGLE);
        assertEquals(DataXField.TYPE_LIST_SINGLE, field.getFieldType());
        field.setFieldType(null);
        assertEquals(DataXField.TYPE_TEXT_SINGLE, field.getFieldType());
        field.setFieldType("blahblah");
        assertEquals(DataXField.TYPE_TEXT_SINGLE, field.getFieldType());
    }

    /**
     * makes sure that the description will always be non-null, no matter what string is being set.
     * This is for convenience since most of the time the description retrieved should be for
     * display purpose anyways.
     */
    public void testEmptyDescriptionIsNotNull() throws Exception {
        DataXField field = new DataXField();
        field.setDescription(null);
        assertNotNull(field.getDescription());
        field.setDescription("");
        assertNotNull(field.getDescription());
        field.setDescription("blah blah");
        assertNotNull(field.getDescription());
    }
}
