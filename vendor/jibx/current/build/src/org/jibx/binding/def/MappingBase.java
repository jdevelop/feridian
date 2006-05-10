/*
Copyright (c) 2003-2005, Dennis M. Sosnoski
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
 * Neither the name of JiBX nor the names of its contributors may be used
   to endorse or promote products derived from this software without specific
   prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.jibx.binding.def;

import org.apache.bcel.Constants;
import org.jibx.binding.classes.BoundClass;
import org.jibx.binding.classes.ClassFile;
import org.jibx.binding.classes.ContextMethodBuilder;
import org.jibx.binding.classes.ExceptionMethodBuilder;
import org.jibx.runtime.JiBXException;

/**
 * Base class for mapping definitions. This is used for both normal and custom
 * mappings. It handles adding the appropriate marshalling and/or unmarshalling
 * interfaces and methods to the classes.
 */
 
public abstract class MappingBase extends LinkableBase implements IMapping
{
    //
    // Constants and such related to code generation.
    
    // definitions for IMarshallable interface defined on mapped classes
    protected static final String IMARSHALLABLE_INTERFACE =
        "org.jibx.runtime.IMarshallable";
    protected static final String MARSHALLABLE_METHODNAME = "marshal";
    protected static final String MARSHALLABLE_SIGNATURE =
        "(Lorg/jibx/runtime/IMarshallingContext;)V";
    protected static final String GETINDEX_METHODNAME = "JiBX_getIndex";
    protected static final String GETINDEX_SIGNATURE = "()I";
    protected static final String CHECKEXTENDS_METHODNAME = "isExtension";
    protected static final String CHECKEXTENDS_SIGNATURE = "(I)Z";
    
    // definitions for IUnmarshallable interface defined on mapped classes
    protected static final String IUNMARSHALLABLE_INTERFACE =
        "org.jibx.runtime.IUnmarshallable";
    protected static final String UNMARSHALLABLE_METHODNAME = "unmarshal";
    protected static final String UNMARSHALLABLE_SIGNATURE =
        "(Lorg/jibx/runtime/IUnmarshallingContext;)V";
    
    // interface implemented by unmarshaller class
    protected static final String UNMARSHALLER_INTERFACE =
        "org.jibx.runtime.IUnmarshaller";
    protected static final String UNMARSHALLERUNMARSHAL_METHOD =
        "org.jibx.runtime.IUnmarshaller.unmarshal";
    protected static final String UNMARSHALLERUNMARSHAL_SIGNATURE =
        "(Ljava/lang/Object;Lorg/jibx/runtime/IUnmarshallingContext;)" +
        "Ljava/lang/Object;";
    
    // interface implemented by marshaller class
    protected static final String MARSHALLER_INTERFACE =
        "org.jibx.runtime.IMarshaller";
    protected static final String ABSTRACTMARSHALLER_INTERFACE =
        "org.jibx.runtime.IAbstractMarshaller";
    protected static final String MARSHALLERMARSHAL_METHOD =
        "org.jibx.runtime.IMarshaller.marshal";
    protected static final String MARSHALLERMARSHAL_SIGNATURE =
        "(Ljava/lang/Object;Lorg/jibx/runtime/IMarshallingContext;)V";
    
    // definitions for context methods used to find marshaller and unmarshaller
    protected static final String GETMARSHALLER_METHOD =
        "org.jibx.runtime.IMarshallingContext.getMarshaller";
    protected static final String GETMARSHALLER_SIGNATURE =
        "(ILjava/lang/String;)Lorg/jibx/runtime/IMarshaller;";
    protected static final String GETUNMARSHALLER_METHOD =
        "org.jibx.runtime.IUnmarshallingContext.getUnmarshaller";
    protected static final String GETUNMARSHALLER_SIGNATURE =
        "(I)Lorg/jibx/runtime/IUnmarshaller;";
    
    //
    // Actual instance data.
    
    /** Index number for this particular binding definition. */
    private final int m_indexNumber;

    /**
     * Constructor. This version requires the component to be set later,
     * using the {@link
     * org.jibx.binding.def.PassThroughComponent#setWrappedComponent} method.
     *
     * @param contain containing binding definition structure
     * @param type bound class name
     */

    public MappingBase(IContainer contain, String type) {
        m_indexNumber = contain.getBindingRoot().getMappedClassIndex(type);
    }

    /**
     * Constructor with wrapped component supplied.
     *
     * @param contain containing binding definition structure
     * @param type bound class name
     * @param wrap wrapped binding component
     */

    public MappingBase(IContainer contain, String type, IComponent wrap) {
        this(contain, type);
        setWrappedComponent(wrap);
    }

    /**
     * Get the mapped class information. This must be implemented in each
     * subclass to return the type of the bound class.
     *
     * @return information for mapped class
     */
    
    public abstract BoundClass getBoundClass();

	/**
     * Generate marshallable interface methods for this mapping. This is not
     * applicable to abstract mappings, since they cannot be marshalled as
     * separate items.
     *
     * @throws JiBXException if error in generating code
     */
     
	protected void addIMarshallableMethod() throws JiBXException {
	    
	    // set up for constructing actual marshal method
       BoundClass clas = getBoundClass();
	    ClassFile cf = clas.getMungedFile();
	    ContextMethodBuilder mb = new ContextMethodBuilder
	        (MARSHALLABLE_METHODNAME, MARSHALLABLE_SIGNATURE, cf,
	        Constants.ACC_PUBLIC, 0, clas.getClassFile().getName(),
	        1, MARSHALLER_INTERFACE);
	    
	    // create call to marshalling context method with class index and
	    //  actual class name
	    mb.loadContext();
	    mb.appendLoadConstant(getIndex());
	    mb.appendLoadConstant(cf.getName());
	    mb.appendCallInterface(GETMARSHALLER_METHOD, GETMARSHALLER_SIGNATURE);
	    
	    // call the returned marshaller with this object and the marshalling
	    //  context as parameters
	    mb.loadObject();
	    mb.loadContext();
	    mb.appendCallInterface(MARSHALLERMARSHAL_METHOD,
	        MARSHALLERMARSHAL_SIGNATURE);
	    mb.appendReturn();
	    
	    // add method to class
	    clas.getUniqueNamed(mb);
	    
	    // set up for constructing get index method
	    ExceptionMethodBuilder xb = new ExceptionMethodBuilder
	        (GETINDEX_METHODNAME, GETINDEX_SIGNATURE, cf, Constants.ACC_PUBLIC);
	    
	    // generate code to return the constant index number
	    xb.appendLoadConstant(getIndex());
	    xb.appendReturn("int");
	    
	    // add the method and interface to class
	    clas.getUniqueNamed(xb);
	    clas.getClassFile().addInterface(IMARSHALLABLE_INTERFACE);
	}

	/**
     * Generate unmarshallable interface method for this mapping. This is not
     * applicable to abstract mappings, since they cannot be unmarshalled as
     * separate items.
     *
     * @throws JiBXException if error in generating code
     */
     
	protected void addIUnmarshallableMethod() throws JiBXException {
	    
	    // set up for constructing new method
        BoundClass clas = getBoundClass();
	    ClassFile cf = clas.getMungedFile();
	    ContextMethodBuilder mb = new ContextMethodBuilder
	        (UNMARSHALLABLE_METHODNAME, UNMARSHALLABLE_SIGNATURE, cf,
	        Constants.ACC_PUBLIC, 0, clas.getClassFile().getName(),
	        1, UNMARSHALLER_INTERFACE);
	    
	    // create call to unmarshalling context method with class index
	    mb.loadContext();
	    mb.appendLoadConstant(getIndex());
	    mb.appendCallInterface(GETUNMARSHALLER_METHOD,
	        GETUNMARSHALLER_SIGNATURE);
	    
	    // call the returned unmarshaller with this object and the unmarshalling
	    //  context as parameters
	    mb.loadObject();
	    mb.loadContext();
	    mb.appendCallInterface(UNMARSHALLERUNMARSHAL_METHOD,
	        UNMARSHALLERUNMARSHAL_SIGNATURE);
	    mb.appendReturn();
	    
	    // add the method and interface to class
	    clas.getUniqueNamed(mb);
	    clas.getClassFile().addInterface(IUNMARSHALLABLE_INTERFACE);
	}
    
    //
    // IMapping interface method definitions
    
    public int getIndex() {
        return m_indexNumber;
    }
}