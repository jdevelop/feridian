package com.echomine.xmpp.packet.mapper;

import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * This is the base packet mapper that every mapper should extend from. Although
 * this is not a definite requirement, this class implements some of the methods
 * so that subclasses do not need to write redundant codes. It also provides
 * some properties for use by subclasses.
 */
public abstract class AbstractPacketMapper implements IUnmarshaller, IMarshaller, IAliasable {
    protected String uri;
    protected String name;
    protected int index;

    /**
     * @param uri the uri of the element working with
     * @param index the index for the namespace
     * @param name the element name
     */
    public AbstractPacketMapper(String uri, int index, String name) {
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

}
