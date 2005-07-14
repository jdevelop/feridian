package com.echomine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.jibx.extras.DocumentComparator;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.util.ClassUtil;
import com.echomine.xmpp.XMPPClientContext;
import com.echomine.xmpp.stream.XMPPConnectionContext;

/**
 * A basic test case that simply provides a set of convenient methods.
 */
public class XMPPTestCase extends TestCase {
    protected ByteArrayOutputStream os;
    protected UnmarshallingContext uctx;
    protected XMPPClientContext clientCtx;
    protected XMPPConnectionContext connCtx;
    protected XMPPStreamWriter writer;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        os = new ByteArrayOutputStream(256);
        clientCtx = new XMPPClientContext();
        connCtx = new XMPPConnectionContext();
        uctx = new UnmarshallingContext();
        writer = new XMPPStreamWriter();
        writer.setOutput(os);
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

    /**
     * marshalls the object to the contained output stream. By default, the
     * output encoding is set to UTF8.
     * 
     * @param packet
     * @throws JiBXException
     */
    protected void marshallObject(Object obj, Class cls) throws JiBXException {
        IBindingFactory bfact = BindingDirectory.getFactory(cls);
        IMarshallingContext ctx = bfact.createMarshallingContext();
        ctx.setOutput(os, "UTF-8");
        ctx.marshalDocument(obj);
    }

    /**
     * Unmarshalls the object.
     * 
     * @param rdr the reader containing incoming data
     * @param cls the class to contain the unmarshalled data
     * @return the object or null if none can be found.
     * @throws JiBXException
     */
    protected Object unmarshallObject(Reader rdr, Class cls) throws JiBXException {
        return JiBXUtil.unmarshallObject(rdr, cls);
    }
}
