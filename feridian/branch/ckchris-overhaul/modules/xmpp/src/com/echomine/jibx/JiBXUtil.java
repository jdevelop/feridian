package com.echomine.jibx;

import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.xmpp.packet.IQPacket;
import com.echomine.xmpp.packet.mapper.IQPacketMapper;

/**
 * The class provides some useful utility functions to work with jibx.
 */
public class JiBXUtil {
    private static Log log = LogFactory.getLog(JiBXUtil.class);
    private static IQPacketMapper iqPacketMapper = new IQPacketMapper();
    private static IBindingFactory iqfactory;
    
    static {
        try {
            iqfactory = BindingDirectory.getFactory(IQPacket.class);
            if (iqfactory == null && log.isWarnEnabled())
                log.warn("No IQPacket Factory found.  IQ packet marshalling is disabled.");
        } catch (JiBXException ex) {
            if (log.isWarnEnabled())
                log.warn("Unable to instantiate IQ Packet Factory from Binding Directory.  IQ packet marshalling is disabled.", ex);
        }
    }
    
    /**
     * unmarshalls a document. This is a convenience method to unmarshall a
     * document from beginning to end without utilizing a previous context.
     * 
     * @param cls the class to unmarshall
     * @param rdr the input stream containing the document to unmarshall
     * @return the object or null if it cannot unmarshall
     * @throws JiBXException
     */
    public static final Object unmarshallObject(Reader rdr, Class cls) throws JiBXException {
        if (rdr == null)
            throw new IllegalArgumentException("Reader cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(cls);
        if (factory == null)
            return null;
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
    public static final Object unmarshallObject(UnmarshallingContext parentCtx, Class cls) throws JiBXException {
        if (parentCtx == null || cls == null)
            throw new IllegalArgumentException("Context or class reference cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(cls);
        if (factory == null)
            return null;
        UnmarshallingContext fctx = (UnmarshallingContext) factory.createUnmarshallingContext();
        fctx.setFromContext(parentCtx);
        return fctx.unmarshalElement();
    }

    /**
     * Marshalls an objects by first looking up the marshaller from the jibx
     * binding directory. This method requires a current parent unmarshalling
     * context. Furthermore, it requires that the stream writer is placed at the
     * position where the marshalled xml will go. If a marshaller cannot be
     * found for the object, then nothing is done (no exception thrown either).
     * 
     * @param parentCtx the parent marshalling context
     * @param obj the object to marshall
     * @param cls the object's class type
     * @throws JiBXException
     */
    public static final void marshallObject(MarshallingContext parentCtx, Object obj) throws JiBXException {
        if (parentCtx == null || obj == null)
            throw new IllegalArgumentException("Context or object to marshall cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(obj.getClass());
        if (factory == null)
            return;
        MarshallingContext fctx = (MarshallingContext) factory.createMarshallingContext();
        fctx.setFromContext(parentCtx);
        fctx.marshalDocument(obj);
    }

    /**
     * Marshalls an objects by first looking up the marshaller from the jibx
     * binding directory. This method requires a current stream writer context.
     * Furthermore, it requires that the stream writer is placed at the position
     * where the marshalled xml will go. If a marshaller cannot be found for the
     * object, then nothing is done (no exception thrown either).
     * 
     * @param writer the parent marshalling context
     * @param obj the object to marshall
     * @param idx the index of the marshaller
     * @throws JiBXException
     */
    public static final void marshallObject(XMPPStreamWriter writer, Object obj) throws JiBXException {
        if (writer == null || obj == null)
            throw new IllegalArgumentException("Writer or object to marshall cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(obj.getClass());
        if (factory == null)
            return;
        MarshallingContext fctx = (MarshallingContext) factory.createMarshallingContext();
        fctx.setXmlWriter(writer);
        fctx.marshalDocument(obj);
    }

    /**
     * This method will marshall the object to the writer. It does not do any
     * additional processing. If a marshaller cannot be found for the object,
     * then nothing is done (no exception thrown either).
     * 
     * @param writer the extisting output writer
     * @param obj the object to marshall
     * @throws JiBXException
     */
    public static final void marshallObject(Writer writer, Object obj) throws JiBXException {
        if (writer == null || obj == null)
            throw new IllegalArgumentException("Writer or object to marshall cannot be null");
        IBindingFactory factory = BindingDirectory.getFactory(obj.getClass());
        if (factory == null)
            return;
        MarshallingContext fctx = (MarshallingContext) factory.createMarshallingContext();
        fctx.setOutput(writer);
        fctx.marshalDocument(obj);
    }

    /**
     * IQ Packets requires special marshalling. This method is specifically
     * created to marshall IQ packets properly. If a marshaller cannot be found
     * for the object, then nothing is done (no exception thrown either).
     * 
     * @param writer the existing output stream
     * @param packet the object to marshall
     * @throws JiBXException
     */
    public static final void marshallIQPacket(XMPPStreamWriter writer, IQPacket packet) throws JiBXException {
        if (writer == null || packet == null)
            throw new IllegalArgumentException("Writer or packet to marshall cannot be null");
        if (iqfactory == null)
            return;
        MarshallingContext fctx = (MarshallingContext) iqfactory.createMarshallingContext();
        fctx.setXmlWriter(writer);
        iqPacketMapper.marshal(packet, fctx);
    }
}
