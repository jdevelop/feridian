package com.echomine.jabber.msg;

import junit.framework.TestCase;
import org.jdom.Element;

/** tests the DataXOption class */
public class DataXOptionTest extends TestCase {
    /** tests that when the label is null, it defaults to being the same as the value */
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

    /** tests that the label setting is proper (especially when the label is set to null) */
    public void testLabelSetting() throws Exception {
        DataXOption option = new DataXOption("value1");
        option.setLabel("blah");
        assertEquals("blah", option.getLabel());
        option.setLabel(null);
        assertEquals("value1", option.getLabel());
    }

    /** tests the encoding of the data into XML string and back */
    public void testEncodeAndParsingWithLabelValue() throws Exception {
        DataXOption option = new DataXOption("value1", "label1");
        Element elem = option.encode();
        option = new DataXOption();
        option.parse(elem);
        assertEquals("value1", option.getValue());
        assertEquals("label1", option.getLabel());
    }

    /** tests that the encoding and parsing is proper when only value is specified */
    public void testEncodeAndParsingWithValueOnly() throws Exception {
        DataXOption option = new DataXOption("value1");
        Element elem = option.encode();
        option = new DataXOption();
        option.parse(elem);
        assertEquals("value1", option.getValue());
        assertEquals("value1", option.getLabel());
    }
}
