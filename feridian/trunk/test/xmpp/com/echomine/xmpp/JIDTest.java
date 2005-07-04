package com.echomine.xmpp;

import com.echomine.XMPPTestCase;

/**
 * Tests the JID class and checks to see if the parsing of the JID is correct
 * TODO: tests that the JIDs really do conform to JEP-0029. (ie. Unicode and
 * specific character allowance checks).
 */
public class JIDTest extends XMPPTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /** test the equals() method */
    public void testJIDEquals() throws Exception {
        JID jid1 = JID.parseJID("abc@def.com");
        JID jid2 = JID.parseJID("def@ghi.com");
        assertFalse(jid1.equals(jid2));
        JID jid3 = JID.parseJID("abc@def.com");
        assertTrue(jid1.equals(jid3));
        assertTrue(jid1.equals(jid1));
    }

    public void testJIDNameParsing() throws Exception {
        assertEquals("test@blah/temp blah", JID.parseJID("test@blah/temp blah").toString());
        assertEquals("test@blah", JID.parseJID("test@blah").toString());
        assertEquals("test", JID.parseJID("test").toString());
        JID jid = JID.parseJID("test");
        assertEquals("test", jid.getHost());
        try {
            JID.parseJID("test@blah/");
            fail("JID 'test@blah/' should not have passed");
        } catch (ParseException ex) {
        }
    }
}
