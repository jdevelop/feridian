package com.echomine.jibx;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

/**
 * The class provides some useful utility functions to work with jibx.
 */
public class JiBXUtil {
    /**
     * Unmarshalls an object by first looking up the unmarshaller from the
     * jibx binding directory.  The method requires the current parent unmarshalling
     * context.  Furthermore, it requires that the parser is currently positioned
     * at the start tag of the element that will get unmarshalled.
     * @param parentCtx the parent unmarshalling context
     * @param cls the class object to unmarshall
     * @return the unmarshalled object
     */
    public static Object unmarshallObject(UnmarshallingContext parentCtx, Class cls) throws JiBXException {
        //unmarshall error
        IBindingFactory factory = BindingDirectory.getFactory(cls);
        UnmarshallingContext fctx = (UnmarshallingContext) factory.createUnmarshallingContext();
        fctx.setFromContext(parentCtx);
        return fctx.unmarshalElement();
    }
}
