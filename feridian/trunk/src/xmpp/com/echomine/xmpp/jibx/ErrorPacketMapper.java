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

import com.echomine.xmpp.ErrorPacket;
import com.echomine.xmpp.NSI;
import com.echomine.xmpp.XMPPConstants;

public class ErrorPacketMapper implements IUnmarshaller, IMarshaller, IAliasable, XMPPConstants {
    private static final String TEXT_ELEMENT_NAME = "text";
    private static final String ERROR_ELEMENT_NAME = "error";

    private String uri;
    private String name;

    /**
     * Default mapper
     */
    public ErrorPacketMapper() {
        uri = NS_JABBER_STREAM;
        name = ERROR_ELEMENT_NAME;
    }

    public ErrorPacketMapper(String uri, int index, String name) {
        this.uri = NS_JABBER_STREAM;
        this.name = ERROR_ELEMENT_NAME;
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
        if (!(obj instanceof ErrorPacket)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else {
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext) ictx;
            ErrorPacket packet = (ErrorPacket) obj;
            IXMLWriter writer = ctx.getXmlWriter();
            //add extension namespaces
            String[] extns;
            if (packet.getApplicationCondition() != null) {
                extns = new String[] { packet.getApplicationCondition().getNamespaceURI() };
	            writer.pushExtensionNamespaces(extns);
            }
            ctx.startTagNamespaces(IDX_JABBER_STREAM, name, new int[] { IDX_JABBER_STREAM }, new String[] { "stream" }).closeStartContent();
            //write out defined condition
            if (packet.getCondition() == null)
                throw new JiBXException("Error packet must contain a condition");
            ctx.startTagNamespaces(IDX_XMPP_STREAMS, packet.getCondition(), new int[] { IDX_XMPP_STREAMS }, new String[] { "" }).closeStartEmpty();
            //write out the error descriptive text
            if (packet.getText() != null) {
                ctx.startTagNamespaces(IDX_XMPP_STREAMS, TEXT_ELEMENT_NAME, new int[] { IDX_XMPP_STREAMS }, new String[] { "" }).closeStartContent();
                //TODO: add xml:lang attribute in future
                ctx.content(packet.getText());
                ctx.endTag(IDX_XMPP_STREAMS, TEXT_ELEMENT_NAME);
            }
            //write out application specific error
            if (packet.getApplicationCondition() != null) {
                int appNsIdx = ctx.getNamespaces().length;
                NSI nsi = packet.getApplicationCondition();
                ctx.startTagNamespaces(appNsIdx, nsi.getName(), new int[] { appNsIdx }, new String[] { "" }).closeStartEmpty();
            }
            //close error tag
            ctx.endTag(IDX_JABBER_STREAM, name);
            writer.popExtensionNamespaces();
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
        ErrorPacket packet = (ErrorPacket) obj;
        if (packet == null)
            packet = new ErrorPacket();
        //parse past the error element
        ctx.parsePastStartTag(uri, name);
        //set our error condition
        packet.setCondition(ctx.toStart());
        //find optional error text
        ctx.parsePastEndTag(ctx.getNamespace(), ctx.getName());
        int eventType = ctx.toTag();
        if (eventType == UnmarshallingContext.START_TAG && TEXT_ELEMENT_NAME.equals(ctx.getName())) {
            packet.setText(ctx.parseElementText(NS_XMPP_STREAMS, TEXT_ELEMENT_NAME));
            eventType = ctx.toTag();
        }
        if (eventType == UnmarshallingContext.START_TAG) {
            //optional application specific condition
            packet.setApplicationCondition(new NSI(ctx.getName(), ctx.getNamespace()));
            //parse to end of the error element
            ctx.parsePastEndTag(ctx.getNamespace(), ctx.getName());
        }
        ctx.toEnd();
        return packet;
    }

}
