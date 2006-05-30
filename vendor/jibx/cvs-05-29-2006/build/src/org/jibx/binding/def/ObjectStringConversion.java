/*
Copyright (c) 2003-2004, Dennis M. Sosnoski
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

import org.jibx.binding.classes.*;
import org.jibx.runtime.JiBXException;

/**
 * Object string conversion handling. Defines serialization handling for
 * converting objects to and from a <code>String</code> value. The default is
 * to just use the object <code>toString()</code> method for serialization and
 * a constructor from a <code>String</code> value for deserialization.
 * <code>java.lang.String</code> itself is a special case, with no added code
 * used by default for either serializing or deserializing.
 * <code>java.lang.Object</code> is also a special case, with no added code
 * used by default for deserializing (the <code>String</code> value is used
 * directly). Other classes must either implement <code>toString()</code> and
 * a constructor from <code>String</code>, or use custom serializers and/or
 * deserializers.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class ObjectStringConversion extends StringConversion
{
    //
    // Constants for code generation.
    
    private static final String TOSTRING_METHOD = "toString";
    private static final String TOSTRING_SIGNATURE =
        "()Ljava/lang/String;";
    private static final String FROMSTRING_SIGNATURE =
        "(Ljava/lang/String;)V";

    //
    // Actual instance data
    
    /** Flag for conversion from <code>String</code> needed (type is anything
     other than <code>String</code> or <code>Object</code>) */
    private boolean m_needDeserialize;
    
    /** Initializer used for creating instance from <code>String</code>
     (only used if no conversion needed and no deserializer supplied;
     may be <code>null</code>) */
    private ClassItem m_initFromString;
    
    /** Flag for conversion to <code>String</code> needed (type is anything
     other than <code>String</code>) */
    private boolean m_needSerialize;
    
    /** <code>toString()</code> method for converting instance to
     <code>String</code> (only used if conversion needed and no serializer
     supplied; may be <code>null</code>) */
    private ClassItem m_instToString;
    
    /**
     * Constructor. Initializes conversion handling based on the supplied
     * inherited handling.
     *
     * @param type fully qualified name of class handled by conversion
     * @param inherit conversion information inherited by this conversion
     * @throws JiBXException if error in configuration
     */

    /*package*/ ObjectStringConversion(String type,
        ObjectStringConversion inherit)
        throws JiBXException {
        super(type, inherit);
        if (type.equals(inherit.m_typeName)) {
            m_needDeserialize = inherit.m_needDeserialize;
            m_initFromString = inherit.m_initFromString;
            m_needSerialize = inherit.m_needSerialize;
            m_instToString = inherit.m_instToString;
        } else {
            initMethods();
        }
    }

    /**
     * Constructor. Initializes conversion handling based on argument values.
     * This form is only used for constructing the default set of conversions.
     * Because of this, it throws an unchecked exception on error.
     *
     * @param dflt default value object (wrapped value for primitive types,
     * otherwise <code>String</code>)
     * @param ser fully qualified name of serialization method
     * @param deser fully qualified name of deserialization method
     * @param type fully qualified name of class handled by conversion
     */

    /*package*/ ObjectStringConversion(Object dflt, String ser, String deser, 
        String type) {
        super(dflt, ser, deser, type);
        try {
            initMethods();
        } catch (JiBXException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    /**
     * Initialize methods used for conversion of types without serializer or
     * deserializer. Sets flags for types needed, with errors thrown at time
     * of attempted use rather than at definition time. That offers the
     * advantages of simpler handling (we don't need to know which directions
     * are supported in a binding) and more flexibility (can support nested
     * partial definitions cleanly).
     */

    private void initMethods() throws JiBXException {
        if (!"java.lang.String".equals(m_typeName)) {
            m_needSerialize = true;
            m_needDeserialize = !"java.lang.Object".equals(m_typeName);
            ClassFile cf = ClassCache.getClassFile(m_typeName);
            m_initFromString = cf.getInitializerMethod(FROMSTRING_SIGNATURE);
            m_instToString = cf.getMethod(TOSTRING_METHOD, TOSTRING_SIGNATURE);
        }
    }

    /**
     * Generate code to convert <code>String</code> representation. The
     * code generated by this method assumes that the <code>String</code>
     * value has already been pushed on the stack. It consumes this and
     * leaves the converted value on the stack.
     *
     * @param mb method builder
     */

    public void genFromText(ContextMethodBuilder mb) throws JiBXException {
        
        // check if a deserializer is used for this type
        if (m_deserializer != null) {
            
            // just generate call to the deserializer (adding any checked
            //  exceptions thrown by the deserializer to the list needing
            //  handling)
            mb.addMethodExceptions(m_deserializer);
            if (m_deserializer.getArgumentCount() > 1) {
                mb.loadContext();
            }
            mb.appendCall(m_deserializer);
            
        } else if (m_initFromString != null) {
            
            // first generate code to duplicate value and check for null, with
            //  duplicate replaced by explicit null if already null (confusing
            //  in the bytecode, but will be optimized out by any native code
            //  generation)
            mb.appendDUP();
            BranchWrapper ifnnull = mb.appendIFNONNULL(this);
            mb.appendPOP();
            mb.appendACONST_NULL();
            BranchWrapper toend = mb.appendUnconditionalBranch(this);
            
            // generate code to create an instance of object and pass text value
            //  to constructor
            mb.targetNext(ifnnull);
            mb.appendCreateNew(m_typeName);
            mb.appendDUP_X1();
            mb.appendSWAP();
            mb.appendCallInit(m_typeName, FROMSTRING_SIGNATURE);
            mb.targetNext(toend);
            
        } else if (m_needDeserialize) {
            throw new JiBXException("No deserializer for " + m_typeName + 
                "; define deserializer or constructor from java.lang.String");
        }
    }

    /**
     * Generate code to parse and convert optional attribute or element. The
     * code generated by this method assumes that the unmarshalling context
     * and name information for the attribute or element have already
     * been pushed on the stack. It consumes these and leaves the converted
     * value (or converted default value, if the item itself is missing) on
     * the stack.
     *
     * @param attr item is an attribute (vs element) flag
     * @param mb method builder
     * @throws JiBXException if error in configuration
     */
    
    public void genParseOptional(boolean attr, ContextMethodBuilder mb)
        throws JiBXException {
        
        // first part of generated instruction sequence is to push the default
        //  value, then call the appropriate unmarshalling context method to get
        //  the value as a String
        mb.appendLoadConstant((String)m_default);
        String name = attr ? UNMARSHAL_OPT_ATTRIBUTE : UNMARSHAL_OPT_ELEMENT;
        mb.appendCallVirtual(name, UNMARSHAL_OPT_SIGNATURE);
        
        // second part is to actually convert to an instance of the type
        genFromText(mb);
    }

    /**
     * Generate code to parse and convert required attribute or element. The
     * code generated by this method assumes that the unmarshalling context and
     * name information for the attribute or element have already been pushed
     * on the stack. It consumes these and leaves the converted value on the
     * stack.
     *
     * @param attr item is an attribute (vs element) flag
     * @param mb method builder
     * @throws JiBXException if error in configuration
     */

    public void genParseRequired(boolean attr, ContextMethodBuilder mb)
        throws JiBXException {
        
        // first part of generated instruction sequence is a call to the
        //  appropriate unmarshalling context method to get the value as a
        //  String
        String name = attr ? UNMARSHAL_REQ_ATTRIBUTE : UNMARSHAL_REQ_ELEMENT;
        mb.appendCallVirtual(name, UNMARSHAL_REQ_SIGNATURE);
        
        // second part is to actually convert to an instance of the type
        genFromText(mb);
    }
    
    /**
     * Shared code generation for converting instance of type to
     * <code>String</code>. This override of the base class method checks for
     * serialization using the <code>toString</code> method and implements that
     * case directly, while calling the base class method for normal handling.
     * The code generated by this method assumes that the reference to the
     * instance to be converted is on the stack. It consumes the reference,
     * replacing it with the corresponding <code>String</code> value.
     *
     * @param type fully qualified class name for value on stack
     * @param mb marshal method builder
     * @throws JiBXException if error in configuration
     */

    public void genToText(String type, ContextMethodBuilder mb)
        throws JiBXException {
        if (m_serializer == null && m_needSerialize) {
            
            // report error if no handling available
            if (m_instToString == null) {
                throw new JiBXException("No serializer for " + m_typeName + 
                    "; define serializer or toString() method");
            } else {
                
                // generate code to call toString() method of instance (adding
                //  any checked exceptions thrown by the method to the list
                //  needing handling)
                mb.addMethodExceptions(m_instToString);
                mb.appendCall(m_instToString);
                
            }
        } else {
            super.genToText(type, mb);
        }
    }

    /**
     * Generate code to check if an optional value is not equal to the default.
     * The code generated by this method assumes that the actual value to be
     * converted has already been pushed on the stack. It consumes this,
     * leaving the converted text reference on the stack if it's not equal to
     * the default value.
     *
     * @param type fully qualified class name for value on stack
     * @param mb method builder
     * @param extra count of extra values to be popped from stack if missing
     * @return handle for branch taken when value is equal to the default
     * (target must be set by caller)
     * @throws JiBXException if error in configuration
     */

    protected BranchWrapper genToOptionalText(String type,
        ContextMethodBuilder mb, int extra) throws JiBXException {
        
        // check if the default value is just null
        if (m_default == null) {
            
            // generate code to call the serializer and get String value on
            //  stack
            genToText(type, mb);
            return null;
            
        } else {
            
            // start with code to call the serializer and get the String value
            //  on stack
            genToText(type, mb);
        
            // add code to check if the serialized text is different from the
            //  default, by duplicating the returned reference, pushing the
            //  default, and calling the object comparison method. This is
            //  followed by a branch if the comparison says they're not equal
            //  (nonzero result, since it's a boolean value).
            mb.appendDUP();
            mb.appendLoadConstant((String)m_default);
            mb.appendCallStatic(COMPARE_OBJECTS_METHOD,
                COMPARE_OBJECTS_SIGNATURE);
            BranchWrapper iffalse = mb.appendIFEQ(this);
            
            // finish by discarding copy of object reference on stack when
            //  equal to the default
            genPopValues(extra+1, mb);
            BranchWrapper toend = mb.appendUnconditionalBranch(this);
            mb.targetNext(iffalse);
            return toend;
        
        }
    }

    /**
     * Check if the type handled by this conversion is of a primitive type.
     *
     * @return <code>false</code> to indicate object type
     */

    public boolean isPrimitive() {
        return false;
    }
    
    /**
     * Convert text representation into default value object. For object types
     * this just returns the text value.
     *
     * @param text value representation to be converted
     * @return converted default value object
     * @throws JiBXException on conversion error
     */

    protected Object convertDefault(String text) throws JiBXException {
        return text;
    }

    /**
     * Derive from existing formatting information. This allows constructing
     * a new instance from an existing format of the same or an ancestor
     * type, with the properties of the existing format copied to the new
     * instance except where overridden by the supplied values.
     *
     * @param type fully qualified name of class handled by conversion
     * @param ser fully qualified name of serialization method
     * (<code>null</code> if inherited)
     * @param dser fully qualified name of deserialization method
     * (<code>null</code> if inherited)
     * @param dflt default value text (<code>null</code> if inherited)
     * @return new instance initialized from existing one
     * @throws JiBXException if error in configuration information
     */

    public StringConversion derive(String type, String ser, String dser,
        String dflt) throws JiBXException {
        if (type == null) {
            type = m_typeName;
        }
        StringConversion inst = new ObjectStringConversion(type, this);
        if (ser != null) {
            inst.setSerializer(ser);
        }
        if (dser != null) {
            inst.setDeserializer(dser);
        }
        if (dflt != null) {
            inst.m_default = inst.convertDefault(dflt);
        }
        return inst;
    }
}