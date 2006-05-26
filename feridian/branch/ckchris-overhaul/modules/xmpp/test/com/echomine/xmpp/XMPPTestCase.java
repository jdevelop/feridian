package com.echomine.xmpp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.jibx.extras.DocumentComparator;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.util.ClassUtil;

/**
 * A basic test case that simply provides a set of convenient methods.
 */
public class XMPPTestCase extends TestCase {
    protected ByteArrayOutputStream os;
    protected UnmarshallingContext uctx;
    protected XMPPSessionContext sessCtx;
    protected XMPPStreamWriter writer;
    protected MockIDGenerator generator = new MockIDGenerator();

    static {
        IDGenerator.setIDGenerator(new MockIDGenerator());
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        os = new ByteArrayOutputStream(256);
        sessCtx = new XMPPSessionContext();
        uctx = new UnmarshallingContext();
        writer = createXMPPStreamWriter();
        writer.setOutput(os);
    }

    /**
     * Creates a new XMPP Stream writer for use during setup.  This method
     * is here so that subclasses can override to create customized stream writers
     * for testing (such as changing the URIs).
     */
    protected XMPPStreamWriter createXMPPStreamWriter() {
        return new XMPPStreamWriter();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        writer.close();
        uctx.reset();
    }

    /**
     * Obtains the reader for a specified resource in the classpath. The default
     * encoding is UTF-8.
     * 
     * @param res the resource path
     * @throws Exception
     */
    protected Reader getResourceAsReader(String res) throws Exception {
        return ClassUtil.getResourceAsReader(res, "UTF-8");
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
        // check that the stream sent by the stream is proper.
        String str = os.toString("UTF-8");
        InputStreamReader brdr = new InputStreamReader(new ByteArrayInputStream(os.toByteArray()), "UTF-8");
        DocumentComparator comp = new DocumentComparator(System.err);
        assertTrue("Invalid XML: " + str, comp.compare(outReader, brdr));
    }
}
