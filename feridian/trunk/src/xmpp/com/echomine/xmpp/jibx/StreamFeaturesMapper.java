package com.echomine.xmpp.jibx;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLWriter;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.xmpp.StreamFeaturesPacket;
import com.echomine.xmpp.XMPPConstants;

/**
 * The custom mapper for the stream features element.
 */
public class StreamFeaturesMapper implements IMarshaller, IUnmarshaller, IAliasable, XMPPConstants {
    private static final Log log = LogFactory.getLog(StreamFeaturesMapper.class);
    private static final String FEATURES_ELEMENT_NAME = "features";
    private static final String STARTTLS_ELEMENT_NAME = "starttls";
    private static final String REQUIRED_ELEMENT_NAME = "required";

    private String uri;
    private String name;
    private int index;

    public StreamFeaturesMapper() {
        uri = NS_TLS;
        name = FEATURES_ELEMENT_NAME;
    }

    public StreamFeaturesMapper(String uri, int index, String name) {
        this.uri = uri;
        this.name = name;
        this.index = IDX_JABBER_STREAM;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IMarshaller#isExtension(int)
     */
    public boolean isExtension(int index) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IUnmarshaller#isPresent(org.jibx.runtime.IUnmarshallingContext)
     */
    public boolean isPresent(IUnmarshallingContext ctx) throws JiBXException {
        return ctx.isAt(uri, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IMarshaller#marshal(java.lang.Object,
     *      org.jibx.runtime.IMarshallingContext)
     */
    public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {
        // make sure the parameters are as expected
        if (!(obj instanceof StreamFeaturesPacket)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else {
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext) ictx;
            StreamFeaturesPacket packet = (StreamFeaturesPacket) obj;
            IXMLWriter writer = ctx.getXmlWriter();
            ctx.startTagNamespaces(index, name, new int[] { index }, new String[] { "stream" }).closeStartContent();
            //now check if tls should be marshalled
            marshallStartTLSFeature(ctx, packet);
            ctx.endTag(index, name);
            try {
                writer.flush();
            } catch (IOException ex) {
                throw new JiBXException("Error flushing stream", ex);
            }
        }
    }

    /**
     * marshalls the start TLS feature
     * 
     * @param ctx the marshalling context
     * @param packet the packet containing the data to marshall
     * @throws JiBXException
     */
    private void marshallStartTLSFeature(MarshallingContext ctx, StreamFeaturesPacket packet) throws JiBXException {
        if (!packet.isTLSSupported())
            return;
        String[] extns = new String[] { NS_TLS };
        int tlsIdx = ctx.getNamespaces().length;
        ctx.getXmlWriter().pushExtensionNamespaces(extns);
        ctx.startTagNamespaces(tlsIdx, STARTTLS_ELEMENT_NAME, new int[] { tlsIdx }, new String[] { "" });
        //if tls is not required, close tag
        if (!packet.isTLSRequired()) {
            ctx.closeStartEmpty();
            return;
        }
        ctx.closeStartContent();
        //write out the <required/> element
        ctx.startTagAttributes(tlsIdx, "required").closeStartEmpty();
        //close tag
        ctx.endTag(tlsIdx, STARTTLS_ELEMENT_NAME);
        ctx.getXmlWriter().popExtensionNamespaces();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IUnmarshaller#unmarshal(java.lang.Object,
     *      org.jibx.runtime.IUnmarshallingContext)
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException {
        //make sure we're at the right start tag
        UnmarshallingContext ctx = (UnmarshallingContext) ictx;
        if (!ctx.isAt(uri, name))
            ctx.throwStartTagNameError(uri, name);
        StreamFeaturesPacket packet = (StreamFeaturesPacket) obj;
        if (packet == null)
            packet = new StreamFeaturesPacket();
        //parse past the features element
        ctx.parsePastStartTag(uri, name);
        //now check if TLS is supported
        if (ctx.isAt(NS_TLS, STARTTLS_ELEMENT_NAME))
            unmarshallStartTLSFeature(ctx, packet);
        ctx.toEnd();
        return packet;
    }

    /**
     * unmarshalls the starttls feature.
     * 
     * @param ctx the unmarshalling context
     * @param packet the stream features packet
     * @throws JiBXException
     */
    private void unmarshallStartTLSFeature(UnmarshallingContext ctx, StreamFeaturesPacket packet) throws JiBXException {
        if (!ctx.isAt(NS_TLS, STARTTLS_ELEMENT_NAME))
            ctx.throwStartTagNameError(NS_TLS, STARTTLS_ELEMENT_NAME);
        ctx.parsePastStartTag(NS_TLS, STARTTLS_ELEMENT_NAME);
        packet.setTLSSupported(true);
        //find optional required element text
        int eventType = ctx.toTag();
        if (eventType == UnmarshallingContext.START_TAG && REQUIRED_ELEMENT_NAME.equals(ctx.getName())) {
            packet.setTLSRequired(true);
            ctx.parsePastEndTag(NS_TLS, REQUIRED_ELEMENT_NAME);
        }
        ctx.parsePastEndTag(NS_TLS, STARTTLS_ELEMENT_NAME);
    }

}
