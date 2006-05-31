package com.echomine.jabber.packet;

import java.io.StringReader;

import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.XMPPTestCase;

/** tests the DataXOption class */
public class DataXOptionTest extends XMPPTestCase {
    private static final String[] URIS = new String[] { "", "http://www.w3.org/XML/1998/namespace", "http://www.w3.org/2001/XMLSchema-instance", "jabber:x:data" };

    protected XMPPStreamWriter createXMPPStreamWriter() {
        return new XMPPStreamWriter(URIS);
    }

    /**
     * tests that when the label is null, it defaults to being the same as the
     * value
     */
    public void testDefaultLabelWhenNull() throws Exception {
        DataXOption option = new DataXOption("value1");
        assertEquals("value1", option.getLabel());
    }

    /** tests that the constructors are setting values properly */
    public void testConstructors() throws Exception {
        DataXOption option = new DataXOption("value1");
        assertEquals("value1", option.getValue());
        assertEquals("value1", option.getLabel());
        option = new DataXOption("value1", "label1");
        assertEquals("value1", option.getValue());
        assertEquals("label1", option.getLabel());
    }

    /**
     * tests that the label setting is proper (especially when the label is set
     * to null)
     */
    public void testLabelSetting() throws Exception {
        DataXOption option = new DataXOption("value1");
        option.setLabel("blah");
        assertEquals("blah", option.getLabel());
        option.setLabel(null);
        assertEquals("value1", option.getLabel());
    }

    /** tests the marshalling/unmarshalling of the data into XML string and back */
    public void testLabelValue() throws Exception {
        String xml = "<option xmlns='jabber:x:data' label='label1'><value>value1</value></option>";
        StringReader reader = new StringReader(xml);
        DataXOption option = new DataXOption("value1", "label1");
        JiBXUtil.marshallObject(writer, option);
        compare(reader);
        reader.reset();
        option = (DataXOption) JiBXUtil.unmarshallObject(reader, DataXOption.class);
        assertEquals("value1", option.getValue());
        assertEquals("label1", option.getLabel());
    }

    /** tests that marshalling/unmarshalling is proper when only value is specified */
    public void testValueOnly() throws Exception {
        String xml = "<option xmlns='jabber:x:data'><value>value1</value></option>";
        StringReader reader = new StringReader(xml);
        DataXOption option = new DataXOption("value1");
        JiBXUtil.marshallObject(writer, option);
        compare(reader);
        reader.reset();
        option = (DataXOption) JiBXUtil.unmarshallObject(reader, DataXOption.class);
        assertEquals("value1", option.getValue());
        assertEquals("value1", option.getLabel());
    }
}
