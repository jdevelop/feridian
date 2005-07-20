package com.echomine.xmpp.packet.mapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLWriter;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.feridian.FeridianConfiguration;
import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.packet.StreamFeature;
import com.echomine.xmpp.packet.StreamFeatures;

/**
 * The custom mapper for the stream features element. This mapper supports all
 * XMPP features (ie. TLS, SASL, resource bidning, and session). In addition, it
 * supports "one-liner" features that do not contain child elements like TLS and
 * SASL. Normally this will cover 99% of features. However, if you happen to
 * come across features that contain children, then a specialize feature mapper
 * must be created. Fortunately, this mapper supports custom specialized
 * features. The developer must create a separate class and binding file for the
 * feature in question. The feature's class and namespace must be registered
 * with the Feridian configuration system (instructions on how to do this are
 * located in the manual). Afterwards, the system will pick up the new class and
 * namespace, and use it here to unmarshall the custom features.
 */
public class StreamFeaturesMapper extends AbstractPacketMapper implements XMPPConstants {
    protected static final String FEATURES_ELEMENT_NAME = "features";
    protected static final String STARTTLS_ELEMENT_NAME = "starttls";
    protected static final String REQUIRED_ELEMENT_NAME = "required";
    protected static final String BINDING_ELEMENT_NAME = "bind";
    protected static final String SESSION_ELEMENT_NAME = "session";
    protected static final String SASL_ELEMENT_NAME = "mechanisms";
    protected static final String MECHANISM_ELEMENT_NAME = "mechanism";
    protected static final String MECHANISMS_ELEMENT_NAME = "mechanisms";

    public StreamFeaturesMapper(String uri, int index, String name) {
        super(uri, XMPPStreamWriter.IDX_JABBER_STREAM, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IMarshaller#marshal(java.lang.Object,
     *      org.jibx.runtime.IMarshallingContext)
     */
    public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {
        // make sure the parameters are as expected
        if (!(obj instanceof StreamFeatures)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else {
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext) ictx;
            StreamFeatures packet = (StreamFeatures) obj;
            IXMLWriter writer = ctx.getXmlWriter();
            ctx.startTagNamespaces(index, name, new int[] { index }, new String[] { "stream" }).closeStartContent();
            marshallStartTLSFeature(ctx, packet);
            // marshall sasl
            List list = packet.getSaslMechanisms();
            int size = list.size();
            if (size > 0) {
                int saslIdx = ctx.getNamespaces().length;
                String[] extns = new String[] { NS_STREAM_SASL };
                ctx.getXmlWriter().pushExtensionNamespaces(extns);
                ctx.startTagNamespaces(saslIdx, MECHANISMS_ELEMENT_NAME, new int[] { saslIdx }, new String[] { "" }).closeStartContent();
                for (int i = 0; i < size; i++)
                    ctx.element(saslIdx, MECHANISM_ELEMENT_NAME, (String) list.get(i));
                ctx.endTag(saslIdx, MECHANISMS_ELEMENT_NAME);
                ctx.getXmlWriter().popExtensionNamespaces();
            }
            // marshall the rest
            marshallSupportedFeatures(ctx, packet);
            ctx.endTag(index, name);
            try {
                writer.flush();
            } catch (IOException ex) {
                throw new JiBXException("Error flushing stream", ex);
            }
        }
    }

    /**
     * Marshalls the session and resource binding feature
     * 
     * @param ctx the marshalling context
     * @param packet the packet containing the data to marshall
     * @throws JiBXException
     */
    private void marshallSupportedFeatures(MarshallingContext ctx, StreamFeatures packet) throws JiBXException {
        int featIdx = ctx.getNamespaces().length;
        Map features = packet.getFeatures();
        Iterator iter = features.keySet().iterator();
        String ns;
        String[] extns;
        while (iter.hasNext()) {
            ns = (String) iter.next();
            // TLS is marshalled separately, so ignore it here
            if (NS_STREAM_TLS.equals(ns) || NS_STREAM_SASL.equals(ns))
                continue;
            try {
                // if there is a value associated with the feature, then
                // it is automatically assumed that the feature requires a
                // marshaller. Otherwise, simple marshalling is used
                StreamFeature feature = packet.getFeature(ns);
                if (feature.getValue() != null) {
                    StringWriter strWriter = new StringWriter(256);
                    JiBXUtil.marshallObject(strWriter, feature.getValue());
                    ((XMPPStreamWriter) ctx.getXmlWriter()).writeMarkup(strWriter.toString());
                } else {
                    extns = new String[] { ns };
                    ctx.getXmlWriter().pushExtensionNamespaces(extns);
                    ctx.startTagNamespaces(featIdx, feature.getElementName(), new int[] { featIdx }, new String[] { "" }).closeStartEmpty();
                    ctx.getXmlWriter().popExtensionNamespaces();
                }
            } catch (IOException ex) {
                throw new JiBXException("Error writing feature to output stream", ex);
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
    private void marshallStartTLSFeature(MarshallingContext ctx, StreamFeatures packet) throws JiBXException {
        if (!packet.isTLSSupported())
            return;
        String[] extns = new String[] { NS_STREAM_TLS };
        int tlsIdx = ctx.getNamespaces().length;
        ctx.getXmlWriter().pushExtensionNamespaces(extns);
        ctx.startTagNamespaces(tlsIdx, STARTTLS_ELEMENT_NAME, new int[] { tlsIdx }, new String[] { "" });
        // if tls is not required, close tag
        if (!packet.isTLSRequired()) {
            ctx.closeStartEmpty();
        } else {
            ctx.closeStartContent();
            // write out the <required/> element
            ctx.startTagAttributes(tlsIdx, "required").closeStartEmpty();
            // close tag
            ctx.endTag(tlsIdx, STARTTLS_ELEMENT_NAME);
        }
        ctx.getXmlWriter().popExtensionNamespaces();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IUnmarshaller#unmarshal(java.lang.Object,
     *      org.jibx.runtime.IUnmarshallingContext)
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException {
        // make sure we're at the right start tag
        UnmarshallingContext ctx = (UnmarshallingContext) ictx;
        if (!ctx.isAt(uri, name))
            ctx.throwStartTagNameError(uri, name);
        StreamFeatures packet = (StreamFeatures) obj;
        if (packet == null)
            packet = new StreamFeatures();
        // parse past the features element
        ctx.parsePastStartTag(uri, name);
        while (true) {
            ctx.toTag();
            if (ctx.isEnd() && name.equals(ctx.getName())) {
                break;
            } else if (ctx.isAt(NS_STREAM_TLS, STARTTLS_ELEMENT_NAME)) {
                unmarshallStartTLSFeature(ctx, packet);
            } else if (ctx.isAt(NS_STREAM_SASL, SASL_ELEMENT_NAME)) {
                ctx.parsePastStartTag(NS_STREAM_SASL, SASL_ELEMENT_NAME);
                ArrayList list = new ArrayList(5);
                while (ctx.isAt(NS_STREAM_SASL, MECHANISM_ELEMENT_NAME))
                    list.add(ctx.parseElementText(NS_STREAM_SASL, MECHANISM_ELEMENT_NAME));
                packet.addFeature(NS_STREAM_SASL, SASL_ELEMENT_NAME, list);
                ctx.parsePastEndTag(NS_STREAM_SASL, SASL_ELEMENT_NAME);
            } else {
                // if no unmarshaller found, then do simple feature add
                Class cls = FeridianConfiguration.getConfig().getUnmarshallerForFeature(ctx.getNamespace());
                if (cls != null) {
                    Object value = JiBXUtil.unmarshallObject(ctx, cls);
                    packet.addFeature(ctx.getNamespace(), ctx.getName(), value);
                } else {
                    packet.addFeature(ctx.getNamespace(), ctx.getName(), null);
                    ctx.parsePastElement(ctx.getNamespace(), ctx.getName());
                }
            }
        }
        return packet;
    }

    /**
     * unmarshalls the starttls feature.
     * 
     * @param ctx the unmarshalling context
     * @param packet the stream features packet
     * @throws JiBXException
     */
    private void unmarshallStartTLSFeature(UnmarshallingContext ctx, StreamFeatures packet) throws JiBXException {
        if (!ctx.isAt(NS_STREAM_TLS, STARTTLS_ELEMENT_NAME))
            ctx.throwStartTagNameError(NS_STREAM_TLS, STARTTLS_ELEMENT_NAME);
        ctx.parsePastStartTag(NS_STREAM_TLS, STARTTLS_ELEMENT_NAME);
        packet.addFeature(NS_STREAM_TLS, STARTTLS_ELEMENT_NAME, null);
        // find optional required element text
        int eventType = ctx.toTag();
        if (eventType == UnmarshallingContext.START_TAG && REQUIRED_ELEMENT_NAME.equals(ctx.getName())) {
            packet.setTLSRequired(true);
            ctx.parsePastEndTag(NS_STREAM_TLS, REQUIRED_ELEMENT_NAME);
        }
        ctx.parsePastEndTag(NS_STREAM_TLS, STARTTLS_ELEMENT_NAME);
    }

}
