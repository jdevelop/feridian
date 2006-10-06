package com.echomine.jabber;

import junit.framework.TestCase;
import com.echomine.common.ParseException;

/**
 * Tests the JID class and checks to see if the parsing of the JID is correct
 * TODO: tests that the JIDs really do conform to JEP-0029.  (ie. Unicode and specific character allowance checks).
 */
public class JIDTest extends TestCase {
    /** test the equals() method */
    public void testJIDEquals() throws Exception {
        JID jid1 = new JID("abc@def.com");
        JID jid2 = new JID("def@ghi.com");
        assertFalse(jid1.equals(jid2));
        JID jid3 = new JID("abc@def.com");
        assertTrue(jid1.equals(jid3));
        assertTrue(jid1.equals(jid1));
    }

    public void testJIDNameParsing() throws Exception {
        assertEquals("test@blah/temp blah", new JID("test@blah/temp blah").toString());
        assertEquals("test@blah", new JID("test@blah").toString());
        assertEquals("test", new JID("test").toString());
        JID jid = new JID("test");
        assertEquals("test", jid.getHost());
        try {
            new JID("test@blah/");
            fail("JID 'test@blah/' should not have passed");
        } catch (ParseException ex) {
        }
    }
}
