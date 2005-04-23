package com.echomine.xmpp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.jibx.extras.DocumentComparator;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.stream.XMPPConnectionContext;

/**
 * Base class for stream test cases to extend from that provides many common
 * functionality.
 */
public class XMPPTestCase extends TestCase implements XMPPConstants {
    protected UnmarshallingContext uctx;
    protected XMPPClientContext clientCtx;
    protected XMPPConnectionContext connCtx;
    protected XMPPStreamWriter writer;
    protected ByteArrayOutputStream os;

    public XMPPTestCase() {
        super();
    }

    protected void setUp() throws Exception {
        super.setUp();
        clientCtx = new XMPPClientContext();
        connCtx = new XMPPConnectionContext();
        uctx = new UnmarshallingContext();
        writer = new XMPPStreamWriter(STREAM_URIS);
        os = new ByteArrayOutputStream(256);
        writer.setOutput(os);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        writer.close();
        uctx.reset();
    }

    /**
     * This method will take an incoming resource and run it through a stream
     * handler. Afterwards, it will compare the output with the out resource.
     * 
     * @param inRes the incoming resource to read inputs
     * @param outRes the resource to compare with output
     * @param stream the stream handler to run the resources through
     * @param addHeaders whether to enclose this resource with stream header for
     *            comparing
     * @param stripHeaders whether to strip incoming resource's stream header
     * @throws Exception
     */
    protected void runAndCompare(String inRes, String outRes, IXMPPStream stream, boolean addHeaders, boolean stripHeaders) throws Exception {
        InputStreamReader inReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(inRes), "UTF-8");
        InputStreamReader outReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(outRes));
        runAndCompare(inReader, outReader, stream, addHeaders, stripHeaders);
    }

    /**
     * This will take an incoming reader (from any source) and run it through a
     * stream handler. Afterwards, it will compare the output with the indicated
     * out resource.
     * 
     * @param inReader a reader that contains incoming XML to read.
     * @param outRes the resource to compare the output with
     * @param stream the stream handler to run the reader through
     * @param addHeaders whether to enclose this resource with stream header for
     *            comparing
     * @param stripHeaders whether to strip incoming resource's stream header
     * @throws Exception
     */
    protected void runAndCompare(Reader inReader, String outRes, IXMPPStream stream, boolean addHeaders, boolean stripHeaders) throws Exception {
        InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(outRes));
        runAndCompare(inReader, reader, stream, addHeaders, stripHeaders);
    }

    /**
     * This will take an incoming reader (from any source) and run it through a
     * stream handler. Afterwards, it will compare the output with the indicated
     * out reader.
     * 
     * @param inReader reader that acontains incoming XML to read
     * @param outReader the reader to compare the output with
     * @param stream the stream handler to run the inReader through
     * @param addHeaders whether to enclose this resource with stream header for
     *            comparing
     * @param stripHeaders whether to strip incoming resource's stream header
     * @throws Exception
     */
    protected void runAndCompare(Reader inReader, Reader outReader, IXMPPStream stream, boolean addHeaders, boolean stripHeaders) throws Exception {
        uctx.setDocument(inReader);
        Exception thrownEx = null;
        if (addHeaders)
            startOutgoingStreamHeader();
        if (stripHeaders)
            stripIncomingStreamHeader();
        //if processing is fine, no exception will be thrown
        try {
            stream.process(clientCtx, connCtx, uctx, writer);
        } catch (XMPPException ex) {
            thrownEx = ex;
        }
        if (addHeaders)
            endOutgoingStreamHeader();
        writer.flush();
        //check that the stream sent by the stream is proper.
        compare(outReader);
        if (thrownEx != null)
            throw thrownEx;
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

    /**
     * This will take an incoming resource to give the stream to unmarshall but
     * will only run through the stream handler. It will not do any comparison
     * afterwards, and is up to the subclasses to test any assertions.
     * 
     * @param inRes the resource to read incoming XML
     * @param stream the stream handler to run the resource through
     * @throws Exception
     */
    protected void run(String inRes, IXMPPStream stream) throws Exception {
        uctx.setDocument(getClass().getClassLoader().getResourceAsStream(inRes), "UTF-8");
        stream.process(clientCtx, connCtx, uctx, writer);
    }

    /**
     * This will take an incoming reader to give the stream to unmarshall but
     * will only run through the stream handler. It will not do any comparison
     * afterwards, and is up to the subclasses to test any assertions.
     * 
     * @param inRes the reader containing incoming XML
     * @param stream the stream handler to run the resource through
     * @throws Exception
     */
    protected void run(Reader rdr, IXMPPStream stream) throws Exception {
        uctx.setDocument(rdr);
        stream.process(clientCtx, connCtx, uctx, writer);
    }
    
    /**
     * Prepends the stream element header. Some tests will work with subelements
     * and require a root element in order to be a valid XML. This will prepend
     * the header for those test cases that needs it
     * 
     * @throws Exception
     */
    protected void startOutgoingStreamHeader() throws Exception {
        writer.startTagNamespaces(IDX_JABBER_STREAM, "stream", new int[] { 2, 3 }, new String[] { "stream", "" });
        writer.addAttribute(IDX_XMPP_CLIENT, "version", "1.0");
        writer.addAttribute(IDX_XMPP_CLIENT, "to", clientCtx.getHost());
        writer.closeStartTag();
        writer.flush();
    }

    /**
     * This will close the element header, effectively closing the XML document.
     * This method works diretly with the enclosing OutputStream that gets
     * checked with the out resource. Whatever goes on with the connection
     * context socket stream is a separate matter. This is made to specifically
     * work properly in case streams were changed in the middle of handshake and
     * the writer and unmarshalling context got reset.
     * 
     * @throws Exception
     */
    protected void endOutgoingStreamHeader() throws Exception {
        os.write("</stream:stream>".getBytes());
    }

    /**
     * This will take the parser past the stream starting element if there is
     * one.
     * 
     * @throws Exception
     */
    protected void stripIncomingStreamHeader() throws Exception {
        if (uctx.isAt(NS_JABBER_STREAM, "stream"))
            uctx.parsePastStartTag(NS_JABBER_STREAM, "stream");
    }
}
