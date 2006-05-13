package com.echomine.jabber.packet;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.XMPPTestCase;

public class DataXPacketTest extends XMPPTestCase {
    private static final String[] URIS = new String[] { "", "http://www.w3.org/XML/1998/namespace", "jabber:x:data" };

    protected XMPPStreamWriter createXMPPStreamWriter() {
        return new XMPPStreamWriter(URIS);
    }
    
    public void testItemFields() throws Exception {
        String xml = "<x xmlns='jabber:x:data' type='result'><item><field type='jid-single' var='jid'><value>reatmon@jabber.org</value></field><field type='text-single' var='email'><value>reatmon@jabber.org</value></field><field type='text-single' var='first'><value>Ryan</value></field></item></x>";
        StringReader reader = new StringReader(xml);
        DataXPacket packet = new DataXPacket(DataXPacket.TYPE_RESULT);
        ArrayList<DataXField> itemList = new ArrayList<DataXField>();
        DataXField field = new DataXField(DataXField.TYPE_JID_SINGLE);
        field.setVariableName("jid");
        field.setJIDValue(JID.parseJID("reatmon@jabber.org"));
        itemList.add(field);
        field = new DataXField();
        field.setVariableName("email");
        field.setTextValue("reatmon@jabber.org");
        itemList.add(field);
        field = new DataXField();
        field.setVariableName("first");
        field.setTextValue("Ryan");
        itemList.add(field);
        packet.addItemFieldList(new DataXItemList(itemList));
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
        reader.reset();
        packet = (DataXPacket) JiBXUtil.unmarshallObject(reader, DataXPacket.class);
        List<DataXField> list = packet.getItemFieldList(0).getFields();
        int size = list.size();
        assertEquals(3, size);
        assertEquals("jid", list.get(0).getVariableName());
        assertEquals("email", list.get(1).getVariableName());
        assertEquals("first", list.get(2).getVariableName());
    }

    /**
     * tests that the parsing and encoding of the reported element is done
     * properly
     */
    public void testReportedFields() throws Exception {
        String xml = "<x xmlns='jabber:x:data' type='result'><title>Search Results</title><reported><field type='jid-single' var='jid' label='JID'/><field type='text-single' var='first' label='First'/><field type='text-single' var='last' label='Last'/><field type='text-single' var='nick' label='Nick'/><field type='text-single' var='email' label='Email'/></reported></x>";
        StringReader reader = new StringReader(xml);
        DataXPacket packet = new DataXPacket(DataXPacket.TYPE_RESULT);
        packet.setTitle("Search Results");
        packet.addReportedField(new DataXField(DataXField.TYPE_JID_SINGLE, "jid", "JID"));
        packet.addReportedField(new DataXField(DataXField.TYPE_TEXT_SINGLE, "first", "First"));
        packet.addReportedField(new DataXField(DataXField.TYPE_TEXT_SINGLE, "last", "Last"));
        packet.addReportedField(new DataXField(DataXField.TYPE_TEXT_SINGLE, "nick", "Nick"));
        packet.addReportedField(new DataXField(DataXField.TYPE_TEXT_SINGLE, "email", "Email"));
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
        reader.reset();
        packet = (DataXPacket) JiBXUtil.unmarshallObject(reader, DataXPacket.class);
        assertEquals("Search Results", packet.getTitle());
        List list = packet.getReportedFields();
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
    public void testGetFields() throws Exception {
        String xml = "<x xmlns='jabber:x:data' type='form'><field type='text-single' var='var1' label='label1'><value>value1</value></field><field type='boolean' var='var2'><value>1</value></field></x>";
        StringReader reader = new StringReader(xml);
        DataXPacket packet = new DataXPacket(DataXPacket.TYPE_FORM);
        DataXField field = new DataXField();
        field.setVariableName("var1");
        field.setLabel("label1");
        field.addValue("value1");
        packet.addField(field);
        field = new DataXField(DataXField.TYPE_BOOLEAN);
        field.setVariableName("var2");
        field.setBooleanValue(true);
        packet.addField(field);
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
        reader.reset();
        packet = (DataXPacket) JiBXUtil.unmarshallObject(reader, DataXPacket.class);
        assertEquals(DataXPacket.TYPE_FORM, packet.getFormType());
        List fields = packet.getFields();
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
     * Tests the retrieval of the fields with no fields in there. It will check
     * that the return value is never null.
     */
    public void testGetFieldsWithNoFields() throws Exception {
        DataXPacket msg = new DataXPacket();
        List fields = msg.getFields();
        assertNull(fields);
    }

    /** tests the retrieving/encoding/parsing of instructions tag */
    public void testInstructions() throws Exception {
        String xml = "<x xmlns='jabber:x:data' type='submit'><instructions>simple instructions</instructions></x>";
        StringReader reader = new StringReader(xml);
        DataXPacket packet = new DataXPacket();
        packet.addInstruction("simple instructions");
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
        reader.reset();
        packet = (DataXPacket) JiBXUtil.unmarshallObject(reader, DataXPacket.class);
        assertEquals("simple instructions", packet.getInstructions());
    }

    /** tests retrieving/encoding/parsing of title tag */
    public void testTitle() throws Exception {
        String xml = "<x xmlns='jabber:x:data' type='submit'><title>simple title</title></x>";
        StringReader reader = new StringReader(xml);
        DataXPacket packet = new DataXPacket();
        packet.setTitle("simple title");
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
        reader.reset();
        packet = (DataXPacket) JiBXUtil.unmarshallObject(reader, DataXPacket.class);
        assertEquals("simple title", packet.getTitle());
    }

    /**
     * Tests that the XML for the create cancel is marshalled/unmarshalled properly
     */
    public void testCreateCancelForm() throws Exception {
        String xml = "<x xmlns='jabber:x:data' type='cancel'></x>";
        StringReader reader = new StringReader(xml);
        DataXPacket packet = new DataXPacket(DataXPacket.TYPE_CANCEL);
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
        reader.reset();
        packet = (DataXPacket) JiBXUtil.unmarshallObject(reader, DataXPacket.class);
        assertEquals(DataXPacket.TYPE_CANCEL, packet.getFormType());
    }

    /**
     * Tests that the default constructor uses a default form type is submit
     */
    public void testDefaultFormType() throws Exception {
        DataXPacket msg = new DataXPacket();
        assertEquals(DataXPacket.TYPE_SUBMIT, msg.getFormType());
    }
}
