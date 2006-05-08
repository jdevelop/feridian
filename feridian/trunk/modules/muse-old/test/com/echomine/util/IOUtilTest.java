package com.echomine.util;

import junit.framework.TestCase;

import java.io.IOException;

/**
 * Tests the utility methods inside the IOUtil class
 */
public class IOUtilTest extends TestCase {
    public IOUtilTest(String name) {
        super(name);
    }

    /**
     * Tests certain conditions for CRLF
     */
    public void testReadToCRLF() throws IOException {
        byte[] buf = new byte[1024];
        int bread = 0;
        StringStream sis = new StringStream("This string with CRLF should parse normally\r\n");
        bread = IOUtil.readToCRLF(sis, buf, 0, 1024);
        assertTrue(sis.equals(buf, 0, bread));
        sis = new StringStream("This string with LFLF should parse normally\n\n");
        bread = IOUtil.readToCRLF(sis, buf, 0, 1024);
        assertTrue(sis.equals(buf, 0, bread));
        try {
            sis.setString("This string with simulated 0x00 should throw\0 an exception\n\n");
            bread = IOUtil.readToCRLF(sis, buf, 0, 1024);
            fail("The string should have caused an exception");
        } catch (IOException ex) {
        }
    }

    public void testReadToLF() throws IOException {
        byte[] buf = new byte[1024];
        int bread = 0;
        StringStream sis = new StringStream("This string with LF should parse normally\n");
        bread = IOUtil.readToLF(sis, buf, 0, 1024);
        assertTrue(sis.equals(buf, 0, bread));
        try {
            sis.setString("This string with simulated 0x00 should throw\0 an exception\n");
            bread = IOUtil.readToLF(sis, buf, 0, 1024);
            fail("The string should have caused an exception");
        } catch (IOException ex) {
        }
    }
}
