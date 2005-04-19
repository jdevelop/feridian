package com.echomine.xmpp.jibx;

import java.io.IOException;

import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLWriter;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.stream.TLSFeature;

/**
 * The jibx mapper (unmarshaller/marshaller) for working with the starttls
 * element.
 */

public class StartTLSFeatureMapper implements IUnmarshaller, IMarshaller, IAliasable, XMPPConstants {
    private static final String STARTTLS_ELEMENT_NAME = "starttls";
    private static final String REQUIRED_ELEMENT_NAME = "required";

    private String uri;
    private String name;

    /**
     * Default mapper
     */
    public StartTLSFeatureMapper() {
        uri = NS_TLS;
        name = STARTTLS_ELEMENT_NAME;
    }

    public StartTLSFeatureMapper(String uri, int index, String name) {
        this.uri = uri;
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IMarshaller#isExtension(int)
     */
    public boolean isExtension(int index) {
        return false;
    }

    /**
     * marshalls the data into an xml string
     */
    public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {
        // make sure the parameters are as expected
        if (!(obj instanceof TLSFeature)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else {
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext) ictx;
            TLSFeature packet = (TLSFeature) obj;
            //if tls not supported, no need to send data
            if (!packet.tlsSupported)
                return;
            //obtain the writer
            IXMLWriter writer = ctx.getXmlWriter();
            int tlsIdx = IDX_XMPP_STREAMS + 1;
            ctx.startTagNamespaces(tlsIdx, name, new int[] { tlsIdx }, new String[] { "" });
            //if tls is not required, close tag
            if (!packet.tlsRequired) {
                ctx.closeStartEmpty();
                return;
            }
            ctx.closeStartContent();
            //write out the <required/> element
            ctx.startTagAttributes(tlsIdx, "required").closeStartEmpty();
            //close tag
            ctx.endTag(tlsIdx, name);
            try {
                writer.flush();
            } catch (IOException ex) {
                throw new JiBXException("Error flushing stream", ex);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IUnmarshaller#isPresent(org.jibx.runtime.IUnmarshallingContext)
     */
    public boolean isPresent(IUnmarshallingContext ictx) throws JiBXException {
        return ictx.isAt(uri, name);
    }

    /**
     * Unmarshalls the error packet. The reason for this is that the error
     * packet uses different condition elements and may also condition
     * application-specific conditions. Due to the highly non-conforming nature
     * of the error message, a custom mapper is required.
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException {
        //make sure we're at the right start tag
        UnmarshallingContext ctx = (UnmarshallingContext) ictx;
        if (!ctx.isAt(uri, name)) {
            ctx.throwStartTagNameError(uri, name);
        }
        com.echomine.xmpp.stream.TLSFeature packet = (com.echomine.xmpp.stream.TLSFeature) obj;
        if (packet == null)
            packet = new com.echomine.xmpp.stream.TLSFeature();
        //parse past the starttls element
        ctx.parsePastStartTag(uri, name);
        packet.tlsSupported = true;
        //find optional required element text
        int eventType = ctx.toTag();
        if (eventType == UnmarshallingContext.START_TAG && REQUIRED_ELEMENT_NAME.equals(ctx.getName())) {
            packet.tlsRequired = true;
            ctx.parsePastEndTag(uri, REQUIRED_ELEMENT_NAME);
        }
        ctx.parsePastEndTag(uri, name);
        return packet;
    }

}
