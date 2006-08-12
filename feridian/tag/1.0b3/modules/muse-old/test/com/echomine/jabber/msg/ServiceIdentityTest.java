package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberUtil;
import junit.framework.TestCase;
import org.jdom.Element;

/**
 * Tests the service identity class to make sure it conforms to the specs
 */
public class ServiceIdentityTest extends TestCase {
    /**
     * Tests that the name and the category are never null (they are required by specs).
     */
    public void testNameAndCategoryNeverNull() throws Exception {
        try {
            new ServiceIdentity(null, "name");
            fail("Category cannot be null.  Must check and throw exception");
        } catch (Throwable thr) {
        }
        try {
            new ServiceIdentity("category", null);
            fail("Name cannot be null.  Must check and throw exception");
        } catch (Throwable thr) {
        }
        try {
            new ServiceIdentity(null, "name", null);
            fail("Category cannot be null.  Must check and throw exception");
        } catch (Throwable thr) {
        }
        try {
            new ServiceIdentity("category", null, null);
            fail("Name cannot be null.  Must check and throw exception");
        } catch (Throwable thr) {
        }
        Element elem = JabberUtil.parseXmlStringToDOM("<identity xmlns='" + JabberCode.XMLNS_IQ_DISCO_INFO.getURI() + "' category='cat'/>");
        try {
            new ServiceIdentity(elem);
            fail("Name cannot be null.  Must check and throw exception");
        } catch (ParseException ex) {
        }
        elem = JabberUtil.parseXmlStringToDOM("<identity xmlns='" + JabberCode.XMLNS_IQ_DISCO_INFO.getURI() + "' name='name'/>");
        try {
            new ServiceIdentity(elem);
            fail("Category cannot be null.  Must check and throw exception");
        } catch (ParseException ex) {
        }
    }

    /**
     * JEP specifies that when setting the category, it will not allow null
     */
    public void testSetCategoryAndName() throws Exception {
        ServiceIdentity ident = new ServiceIdentity("cat", "name");
        try {
            ident.setCategory(null);
            fail("Should not be able to set category to null");
        } catch (Throwable thr) {
        }
        try {
            ident.setName(null);
            fail("Should not be able to set name to null");
        } catch (Throwable thr) {
        }
    }

    /**
     * tests parsing and encoding of the data
     */
    public void testParsingAndEncoding() throws Exception {
        ServiceIdentity ident = new ServiceIdentity("cat", "name", "type");
        Element elem = ident.encode();
        ident = new ServiceIdentity(elem);
        assertEquals("cat", ident.getCategory());
        assertEquals("name", ident.getName());
        assertEquals("type", ident.getType());
    }
}
