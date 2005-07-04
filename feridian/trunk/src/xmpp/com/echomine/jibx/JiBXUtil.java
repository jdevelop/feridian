package com.echomine.jibx;

import java.io.Reader;
import java.io.Writer;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.xmpp.IQPacket;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.jibx.IQPacketMapper;

/**
 * The class provides some useful utility functions to work with jibx.
 */
public class JiBXUtil {
    private static IQPacketMapper iqPacketMapper = new IQPacketMapper(XMPPConstants.NS_XMPP_CLIENT, XMPPConstants.IDX_XMPP_CLIENT, "iq");

    /**
     * unmarshalls a document. This is a convenience method to unmarshall a
     * document from beginning to end without utilizing a previous context.
     * 
     * @param cls the class to unmarshall
     * @param rdr the input stream containing the document to unmarshall
     * @return the object or null if it cannot unmarshall
     * @throws JiBXException
     */
    public static Object unmarshallObject(Reader rdr, Class cls) throws JiBXException {
        if (rdr == null)
            throw new IllegalArgumentException("Reader cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(cls);
        UnmarshallingContext fctx = (UnmarshallingContext) factory.createUnmarshallingContext();
        return fctx.unmarshalDocument(rdr);
    }

    /**
     * Unmarshalls an object by first looking up the unmarshaller from the jibx
     * binding directory. The method requires a current parent unmarshalling
     * context. Furthermore, it requires that the parser is currently positioned
     * at the start tag of the element that will be unmarshalled.
     * 
     * @param parentCtx the parent unmarshalling context
     * @param cls the class object to unmarshall
     * @return the unmarshalled object
     */
    public static Object unmarshallObject(UnmarshallingContext parentCtx, Class cls) throws JiBXException {
        if (parentCtx == null || cls == null)
            throw new IllegalArgumentException("Context or class reference cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(cls);
        UnmarshallingContext fctx = (UnmarshallingContext) factory.createUnmarshallingContext();
        fctx.setFromContext(parentCtx);
        return fctx.unmarshalElement();
    }

    /**
     * Marshalls an objects by first looking up the marshaller from the jibx
     * binding directory. This method requires a current parent unmarshalling
     * context. Furthermore, it requires that the stream writer is placed at the
     * position where the marshalled xml will go.
     * 
     * @param parentCtx the parent marshalling context
     * @param obj the object to marshall
     * @param cls the object's class type
     * @param idx the index of the marshaller
     * @throws JiBXException
     */
    public static void marshallObject(MarshallingContext parentCtx, Object obj, int idx) throws JiBXException {
        if (parentCtx == null || obj == null)
            throw new IllegalArgumentException("Context or object to marshall cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(obj.getClass());
        MarshallingContext fctx = (MarshallingContext) factory.createMarshallingContext();
        fctx.setFromContext(parentCtx);
        IMarshaller marshaller = fctx.getMarshaller(idx, obj.getClass().getName());
        marshaller.marshal(obj, fctx);
    }

    /**
     * Marshalls an objects by first looking up the marshaller from the jibx
     * binding directory. This method requires a current stream writer context.
     * Furthermore, it requires that the stream writer is placed at the position
     * where the marshalled xml will go.
     * 
     * @param writer the parent marshalling context
     * @param obj the object to marshall
     * @param idx the index of the marshaller
     * @throws JiBXException
     */
    public static void marshallObject(XMPPStreamWriter writer, Object obj) throws JiBXException {
        if (writer == null || obj == null)
            throw new IllegalArgumentException("Writer or object to marshall cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(obj.getClass());
        MarshallingContext fctx = (MarshallingContext) factory.createMarshallingContext();
        fctx.setXmlWriter(writer);
        fctx.marshalDocument(obj);
    }

    /**
     * This method will marshall the object to the writer. It does not do any
     * additional processing.
     * 
     * @param writer the extisting output writer
     * @param obj the object to marshall
     * @throws JiBXException
     */
    public static void marshallObject(Writer writer, Object obj) throws JiBXException {
        if (writer == null || obj == null)
            throw new IllegalArgumentException("Writer or object to marshall cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(obj.getClass());
        MarshallingContext fctx = (MarshallingContext) factory.createMarshallingContext();
        fctx.setOutput(writer);
        fctx.marshalDocument(obj);
    }

    /**
     * IQ Packets requires special marshalling. This method is specifically
     * created to marshall IQ packets properly.
     * 
     * @param writer the existing output stream
     * @param packet the object to marshall
     * @throws JiBXException
     */
    public static void marshallIQPacket(XMPPStreamWriter writer, IQPacket packet) throws JiBXException {
        if (writer == null || packet == null)
            throw new IllegalArgumentException("Writer or packet to marshall cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(IQPacket.class);
        MarshallingContext fctx = (MarshallingContext) factory.createMarshallingContext();
        fctx.setXmlWriter(writer);
        iqPacketMapper.marshal(packet, fctx);
    }
}
