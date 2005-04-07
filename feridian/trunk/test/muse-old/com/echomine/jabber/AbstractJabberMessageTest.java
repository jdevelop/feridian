package com.echomine.jabber;

import junit.framework.TestCase;
import org.jdom.Element;

/** tests the named class */
public class AbstractJabberMessageTest extends TestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * This method tests a bug where when the type is not set (ie. null),
     * null pointer exception will be thrown when checking to see what
     * the type is.
     */
    public void testNPENotThrownWhenTypeIsNull() {
        TestJabberMessage msg = new TestJabberMessage();
        assertFalse(msg.isError());
        assertNull(msg.getErrorMessage());
    }

    class TestJabberMessage extends AbstractJabberMessage {
        public TestJabberMessage(String type, Element root) {
            super(type, root);
        }

        protected TestJabberMessage() {
        }
    }
}
