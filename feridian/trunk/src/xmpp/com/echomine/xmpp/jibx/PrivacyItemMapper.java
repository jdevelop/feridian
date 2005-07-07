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

import com.echomine.xmpp.PrivacyItem;
import com.echomine.xmpp.XMPPConstants;

/**
 * Mapper for the privacy item class. Since the class requires special
 * marshalling and unmarshalling, the a custom mapper is required.
 */
public abstract class PrivacyItemMapper implements IUnmarshaller, IMarshaller, IAliasable, XMPPConstants {
    protected static final String BLOCK_IQ = "iq";
    protected static final String BLOCK_MESSAGE = "message";
    protected static final String BLOCK_PRESENCE_IN = "presence-in";
    protected static final String BLOCK_PRESENCE_OUT = "presence-out";
    protected static final String ACTION_NAME = "action";
    protected static final String ORDER_NAME = "order";
    protected static final String VALUE_NAME = "value";
    protected static final String TYPE_NAME = "type";
    protected static final String ACTION_ALLOW = "allow";
    protected static final String ACTION_DENY = "deny";

    protected String uri;
    protected String name;
    protected int index;

    /**
     * @param uri the uri of the element working with
     * @param index the index for the namespace
     * @param name the element name
     */
    public PrivacyItemMapper(String uri, int index, String name) {
        this.uri = uri;
        this.name = name;
        this.index = index;
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
    public boolean isPresent(IUnmarshallingContext ictx) throws JiBXException {
        return ictx.isAt(uri, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IMarshaller#marshal(java.lang.Object,
     *      org.jibx.runtime.IMarshallingContext)
     */
    public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {
        // make sure the parameters are as expected
        if (!(obj instanceof PrivacyItem)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshalling context");
        } else {
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext) ictx;
            PrivacyItem item = (PrivacyItem) obj;
            IXMLWriter writer = ctx.getXmlWriter();
            ctx.startTagAttributes(index, name);
            // marshall attributes
            if (item.isAllow())
                ctx.attribute(index, ACTION_NAME, ACTION_ALLOW);
            else
                ctx.attribute(index, ACTION_NAME, ACTION_DENY);
            ctx.attribute(index, ORDER_NAME, item.getOrder());
            if (item.getType() != null)
                ctx.attribute(index, TYPE_NAME, item.getType());
            if (item.getValue() != null)
                ctx.attribute(index, VALUE_NAME, item.getValue());
            ctx.closeStartContent();
            // marshall detailed deny settings if necessary
            if (!item.isAllow()) {
                if (item.isDenyIQ())
                    ctx.element(index, BLOCK_IQ, "");
                if (item.isDenyMessage())
                    ctx.element(index, BLOCK_MESSAGE, "");
                if (item.isDenyIncomingPresence())
                    ctx.element(index, BLOCK_PRESENCE_IN, "");
                if (item.isDenyOutgoingPresence())
                    ctx.element(index, BLOCK_PRESENCE_OUT, "");
            }
            ctx.endTag(index, name);
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
     * @see org.jibx.runtime.IUnmarshaller#unmarshal(java.lang.Object,
     *      org.jibx.runtime.IUnmarshallingContext)
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException {
        // make sure we're at the right start tag
        UnmarshallingContext ctx = (UnmarshallingContext) ictx;
        if (!ctx.isAt(uri, name)) {
            ctx.throwStartTagNameError(uri, name);
        }
        PrivacyItem item = (PrivacyItem) obj;
        if (item == null)
            item = new PrivacyItem();
        // unmarshall attributes
        if (ctx.hasAttribute(null, ACTION_NAME))
            if (ACTION_ALLOW.equals(ctx.attributeText(null, ACTION_NAME)))
                item.setAllow(true);
        if (ctx.hasAttribute(null, ORDER_NAME))
            item.setOrder(ctx.attributeInt(null, ORDER_NAME));
        if (ctx.hasAttribute(null, TYPE_NAME))
            item.setType(ctx.attributeText(null, TYPE_NAME));
        if (ctx.hasAttribute(null, VALUE_NAME))
            item.setValue(ctx.attributeText(null, VALUE_NAME));
        int eventType = ctx.next();
        if (eventType != UnmarshallingContext.END_TAG) {
            do {
                if (ctx.isAt(uri, BLOCK_IQ)) {
                    item.setDenyIQ(true);
                    ctx.parsePastElement(uri, BLOCK_IQ);
                } else if (ctx.isAt(uri, BLOCK_MESSAGE)) {
                    item.setDenyMessage(true);
                    ctx.parsePastElement(uri, BLOCK_MESSAGE);
                } else if (ctx.isAt(uri, BLOCK_PRESENCE_IN)) {
                    item.setDenyIncomingPresence(true);
                    ctx.parsePastElement(uri, BLOCK_PRESENCE_IN);
                } else if (ctx.isAt(uri, BLOCK_PRESENCE_OUT)) {
                    item.setDenyOutgoingPresence(true);
                    ctx.parsePastElement(uri, BLOCK_PRESENCE_OUT);
                } else {
                    break;
                }
            } while (true);
        }
        // safe to parse past end tag without hanging since it is
        // assumed that the item element is enclosed within the
        // iq tag
        ctx.parsePastEndTag(uri, name);
        return item;
    }
}
