package com.echomine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.jibx.extras.DocumentComparator;

import junit.framework.TestCase;

/**
 * A basic test case that simply provides a set of convenient methods.
 */
public class BasicTestCase extends TestCase {
    protected ByteArrayOutputStream os;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        os = new ByteArrayOutputStream(256);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Obtains the reader for a specified resource in the classpath.
     * 
     * @param res the resource path
     * @throws Exception
     */
    protected Reader getResourceAsReader(String res) throws Exception {
        return new InputStreamReader(getClass().getClassLoader().getResourceAsStream(res));
    }

    /**
     * Only compares the output with the given resource stream. It is assumed
     * that the run() or something equivalent has already been done, and there
     * is data to compare in the output.
     * 
     * @param outRes the resource to compare with
     * @throws Exception
     */
    protected void compare(String outRes) throws Exception {
        InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(outRes));
        compare(reader);
    }

    /**
     * Only compares the output with the given out reader. It is assumed that
     * the run() or something equivalent has already been done, and there is
     * data to compare in the output.
     * 
     * @param outReader the resource to compare output with
     * @throws Exception
     */
    protected void compare(Reader outReader) throws Exception {
        //check that the stream sent by the stream is proper.
        String str = os.toString("UTF-8");
        InputStreamReader brdr = new InputStreamReader(new ByteArrayInputStream(os.toByteArray()), "UTF-8");
        DocumentComparator comp = new DocumentComparator(System.err);
        assertTrue("Invalid XML: " + str, comp.compare(outReader, brdr));
    }
}
