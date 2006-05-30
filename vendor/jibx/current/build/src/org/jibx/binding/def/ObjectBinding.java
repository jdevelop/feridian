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

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;

import org.jibx.binding.classes.*;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.Utility;

/**
 * Binding modifiers that apply to a class reference. This adds the methods used
 * for handling binding operations to the object class, then generates calls to
 * the added methods as this binding definition is used.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class ObjectBinding extends PassThroughComponent
implements IComponent, IContextObj
{
    //
    // Constants and such related to code generation.
    
    // recognized marshal hook method (pre-get) signatures.
    private static final String[] MARSHAL_HOOK_SIGNATURES =
    {
        "(Lorg/jibx/runtime/IMarshallingContext;)V",
        "(Ljava/lang/Object;)V",
        "()V"
    };
    
    // recognized factory hook method signatures.
    private static final String[] FACTORY_HOOK_SIGNATURES =
    {
        "(Lorg/jibx/runtime/IUnmarshallingContext;)",
        "(Ljava/lang/Object;)",
        "()"
    };
    
    // recognized unmarshal hook method (pre-set, post-set) signatures.
    private static final String[] UNMARSHAL_HOOK_SIGNATURES =
    {
        "(Lorg/jibx/runtime/IUnmarshallingContext;)V",
        "(Ljava/lang/Object;)V",
        "()V"
    };
    
    // definitions used in generating calls to user defined methods
    private static final String UNMARSHAL_GETSTACKTOPMETHOD =
        "org.jibx.runtime.impl.UnmarshallingContext.getStackTop";
    private static final String MARSHAL_GETSTACKTOPMETHOD =
        "org.jibx.runtime.impl.MarshallingContext.getStackTop";
    private static final String GETSTACKTOP_SIGNATURE =
        "()Ljava/lang/Object;";
    private static final String MARSHALLING_CONTEXT =
        "org.jibx.runtime.impl.MarshallingContext";
    private static final String UNMARSHALLING_CONTEXT =
        "org.jibx.runtime.impl.UnmarshallingContext";
    private static final String UNMARSHAL_PARAMETER_SIGNATURE =
        "(Lorg/jibx/runtime/impl/UnmarshallingContext;)";
    private static final String UNMARSHAL_PUSHOBJECTMETHOD =
        "org.jibx.runtime.impl.UnmarshallingContext.pushObject";
    private static final String UNMARSHAL_PUSHTRACKEDOBJECTMETHOD =
        "org.jibx.runtime.impl.UnmarshallingContext.pushTrackedObject";
    private static final String MARSHAL_PUSHOBJECTMETHOD =
        "org.jibx.runtime.impl.MarshallingContext.pushObject";
    private static final String PUSHOBJECT_SIGNATURE =
        "(Ljava/lang/Object;)V";
    private static final String UNMARSHAL_POPOBJECTMETHOD =
        "org.jibx.runtime.impl.UnmarshallingContext.popObject";
    private static final String MARSHAL_POPOBJECTMETHOD =
        "org.jibx.runtime.impl.MarshallingContext.popObject";
    private static final String POPOBJECT_SIGNATURE = "()V";
    
    // definitions for methods added to mapped class
    private static final String NEWINSTANCE_SUFFIX = "_newinstance";
    private static final String UNMARSHAL_ATTR_SUFFIX = "_unmarshalAttr";
    private static final String MARSHAL_ATTR_SUFFIX = "_marshalAttr";
    private static final String UNMARSHAL_SUFFIX = "_unmarshal";
    private static final String MARSHAL_SUFFIX = "_marshal";
    
    // definitions for source position tracking
    private static final String SOURCE_TRACKING_INTERFACE =
        "org.jibx.runtime.impl.ITrackSourceImpl";
    private static final String SETSOURCE_METHODNAME = "jibx_setSource";
    private static final Type[] SETSOURCE_ARGS =
    {
        Type.STRING, Type.INT, Type.INT
    };
    private static final String SOURCEDOCUMENT_FIELDNAME ="jibx_sourceDocument";
    private static final String SOURCELINE_FIELDNAME = "jibx_sourceLine";
    private static final String SOURCECOLUMN_FIELDNAME = "jibx_sourceColumn";
    private static final String SOURCENAME_METHODNAME = "jibx_getDocumentName";
    private static final String SOURCELINE_METHODNAME = "jibx_getLineNumber";
    private static final String SOURCECOLUMN_METHODNAME = 
        "jibx_getColumnNumber";
    private static final Type[] EMPTY_ARGS = {};

    //
    // Actual instance data.

    /** Containing binding definition structure. */
    private final IContainer m_container;
    
    /** Class linked to mapping. */
    private BoundClass m_class;

    /** Object factory method. */
    private final ClassItem m_factoryMethod;

    /** Preset method for object. */
    private final ClassItem m_preSetMethod;

    /** Postset method for object. */
    private final ClassItem m_postSetMethod;

    /** Preget method for object. */
    private final ClassItem m_preGetMethod;
    
    /** Type to be used for creating new instances. */
    private final ClassFile m_createClass;
    
    /** Generated new instance method. */
    private ClassItem m_newInstanceMethod;
    
    /** Flag for recursion while generating attribute unmarshal. */
    private boolean m_lockAttributeUnmarshal;
    
    /** Flag for recursion while generating attribute marshal. */
    private boolean m_lockAttributeMarshal;
    
    /** Flag for recursion while generating attribute unmarshal. */
    private boolean m_lockContentUnmarshal;
    
    /** Flag for recursion while generating attribute marshal. */
    private boolean m_lockContentMarshal;
    
    /** Signature used for unmarshal methods. */
    private String m_unmarshalSignature;
    
    /** Name for unmarshal attribute method (<code>null</code> unless
     generation started). */
    private String m_unmarshalAttributeName;
    
    /** Name for  unmarshal content method (<code>null</code> unless
     generation started). */
    private String m_unmarshalContentName;
    
    /** Flag for static unmarshal methods. */
    private boolean m_isStaticUnmarshal;
    
    /** Flag for static marshal methods. */
    private boolean m_isStaticMarshal;
    
    /** Signature used for marshal methods. */
    private String m_marshalSignature;
    
    /** Name for  marshal attribute method (<code>null</code> unless
     generation started). */
    private String m_marshalAttributeName;
    
    /** Name for  marshal content method (<code>null</code> unless
     generation istarted). */
    private String m_marshalContentName;
    
    /** Generated unmarshal attribute method. */
    private ClassItem m_unmarshalAttributeMethod;
    
    /** Generated unmarshal content method. */
    private ClassItem m_unmarshalContentMethod;
    
    /** Generated marshal attribute method. */
    private ClassItem m_marshalAttributeMethod;
    
    /** Generated marshal content method. */
    private ClassItem m_marshalContentMethod;
    
    /** Child supplying instance identifier value. */
    private IComponent m_idChild;
    
    /** Flag for "this" reference, meaning that there's no separate object
     * instance created. */
    private boolean m_isThisBinding;

    /**
     * Constructor. This initializes the definition context to be the same as
     * the parent's. Subclasses may change this definition context if
     * appropriate.
     *
     * @param contain containing binding definition component
     * @param objc current object context
     * @param type fully qualified class name for bound object
     * @param fact user new instance factory method
     * @param pres user preset method for unmarshalling
     * @param posts user postset method for unmarshalling
     * @param pget user preget method for marshalling
     * @param ctype type to use for creating new instance (<code>null</code> if
     * not specified)
     * @throws JiBXException if method not found
     */
    public ObjectBinding(IContainer contain, IContextObj objc, String type,
        String fact, String pres, String posts, String pget, String ctype)
        throws JiBXException {
        
        // initialize the basics
        m_container = contain;
        BoundClass ctxc = (objc == null) ? null : objc.getBoundClass();
        m_class = BoundClass.getInstance(type, ctxc);
        ClassFile cf = m_class.getClassFile();
        if (ctype == null) {
            m_createClass = cf;
        } else {
            m_createClass = ClassCache.getClassFile(ctype);
        }
        
        // check instance creation for unmarshalling
        if (fact == null) {
            m_factoryMethod = null;
        } else {
            
            // look up supplied static factory method
            int split = fact.lastIndexOf('.');
            if (split >= 0) {
                
                // verify the method is defined
                String cname = fact.substring(0, split);
                String mname = fact.substring(split+1);
                ClassFile mcf = ClassCache.getClassFile(cname);
                m_factoryMethod = mcf.getMethod(mname,
                    FACTORY_HOOK_SIGNATURES);
                if (m_factoryMethod == null) {
                    throw new JiBXException("Factory method " + fact +
                        " not found");
                } else {
                    
                    // force access if necessary
                    m_factoryMethod.makeAccessible(cf);
                    
                }
            } else {
                m_factoryMethod = null;
            }
            if (m_factoryMethod == null) {
                throw new JiBXException("Factory method " + fact +
                    " not found.");
            }
        }
        
        // look up other method names as members of class
        if (pres == null) {
            m_preSetMethod = null;
        } else {
            m_preSetMethod = cf.getMethod(pres, UNMARSHAL_HOOK_SIGNATURES);
            if (m_preSetMethod == null) {
                throw new JiBXException("User method " + pres + " not found.");
            }
        }
        if (posts == null) {
            m_postSetMethod = null;
        } else {
            m_postSetMethod = cf.getMethod(posts, UNMARSHAL_HOOK_SIGNATURES);
            if (m_postSetMethod == null) {
                throw new JiBXException("User method " + posts + " not found.");
            }
        }
        if (pget == null) {
            m_preGetMethod = null;
        } else {
            m_preGetMethod = cf.getMethod(pget, MARSHAL_HOOK_SIGNATURES);
            if (m_preGetMethod == null) {
                throw new JiBXException("User method " + pget + " not found.");
            }
        }
    }
    
    /**
     * Abstract binding copy constructor. This is used to create a variation of
     * the object binding for a mapping which will be used for "this"
     * references. The "this" reference handling differs only in the code
     * generation, where it skips adding the source tracking interfaces and
     * does not push an instance of the object on the marshalling or
     * unmarshalling stack (since the object will already be there). This method
     * is only to be used before code generation.
     * 
     * @param base original object binding
     */
    public ObjectBinding(ObjectBinding base) {
        m_container = base.m_container;
        m_class = base.m_class;
        m_factoryMethod = null;
        m_preSetMethod = base.m_preSetMethod;
        m_postSetMethod = base.m_postSetMethod;
        m_preGetMethod = base.m_preGetMethod;
        m_createClass = base.m_createClass;
        m_idChild = base.m_idChild;
        m_component = base.m_component;
        m_isThisBinding = true;
    }
    
    /**
     * Copy constructor. This is used in handling abstract mappings, where the
     * properties of the mapping definition object binding need to be copied for
     * each use of that binding.
     * 
     * @param contain binding definition component containing copy
     */
    public ObjectBinding(IContainer contain, ObjectBinding base) {
        m_container = contain;
        m_class = base.m_class;
        m_factoryMethod = base.m_factoryMethod;
        m_preSetMethod = base.m_preSetMethod;
        m_postSetMethod = base.m_postSetMethod;
        m_preGetMethod = base.m_preGetMethod;
        m_newInstanceMethod = base.m_newInstanceMethod;
        m_unmarshalSignature = base.m_unmarshalSignature;
        m_unmarshalAttributeName = base.m_unmarshalAttributeName;
        m_unmarshalContentName = base.m_unmarshalContentName;
        m_isStaticUnmarshal = base.m_isStaticUnmarshal;
        m_isStaticMarshal = base.m_isStaticMarshal;
        m_marshalSignature = base.m_marshalSignature;
        m_marshalAttributeName = base.m_marshalAttributeName;
        m_marshalContentName = base.m_marshalAttributeName;
        m_marshalContentName = base.m_marshalContentName;
        m_unmarshalAttributeMethod = base.m_unmarshalAttributeMethod;
        m_unmarshalContentMethod = base.m_unmarshalContentMethod;
        m_marshalAttributeMethod = base.m_marshalAttributeMethod;
        m_marshalContentMethod = base.m_marshalContentMethod;
        m_createClass = base.m_createClass;
        m_idChild = base.m_idChild;
        m_component = base.m_component;
    }

    /**
     * Generate code for calling a user supplied method. The object methods
     * support three signature variations, with no parameters, with the
     * marshalling or unmarshalling context, or with the owning object.
     *
     * @param in flag for unmarshalling method
     * @param method information for method being called
     * @param mb method builder for generated code
     */
    
    private void genUserMethodCall(boolean in, ClassItem method,
        ContextMethodBuilder mb) {
        
        // load object reference for virtual call
        if (!method.isStatic()) {
            mb.loadObject();
        }
        
        // check if parameter required for call
        if (method.getArgumentCount() > 0) {
            
            // generate code to load context, then get containing object if
            //  needed for call
            mb.loadContext();
            String type = method.getArgumentType(0);
            if ("java.lang.Object".equals(type)) {
                String name = in ? UNMARSHAL_GETSTACKTOPMETHOD :
                    MARSHAL_GETSTACKTOPMETHOD;
                mb.appendCallVirtual(name, GETSTACKTOP_SIGNATURE);
            }
        }
        
        // generate appropriate form of call to user method
        mb.appendCall(method);
        mb.addMethodExceptions(method);
    }

    /**
     * Generate code to create an instance of the object for this mapping. This
     * convenience method generates the actual code for creating an instance of
     * an object. The generated code leaves the created object reference on the
     * stack.
     *
     * @param mb method builder
     * @throws JiBXException if error in generating code
     */

    private void genNewInstanceCode(ContextMethodBuilder mb)
        throws JiBXException {
        
        // check for factory supplied to create instance
        if (m_factoryMethod == null) {
            if (m_createClass.isArray()) {
                
                // construct array instance directly with basic size
                mb.appendLoadConstant(Utility.MINIMUM_GROWN_ARRAY_SIZE);
                String type = m_createClass.getName();
                mb.appendCreateArray(type.substring(0, type.length()-2));
                
            } else {
                
                // make sure we have a no argument constructor
                if (m_createClass.getInitializerMethod("()V") == null) {
                    m_createClass.addDefaultConstructor();
                }
                
                // no factory, so create an instance, duplicate the
                //  reference, and then call the null constructor
                mb.appendCreateNew(m_createClass.getName());
                mb.appendDUP();
                mb.appendCallInit(m_createClass.getName(),"()V");
                
            }
            
        } else {
            
            // generate call to factory method
            genUserMethodCall(true, m_factoryMethod, mb);
            mb.appendCreateCast(m_factoryMethod.getTypeName(),
                m_class.getClassName());
            
        }
    }

    /**
     * Generate call to new instance creation method for object. This
     * convenience method just generates code to call the generated new
     * instance method added to the class definition.
     *
     * @param mb method builder
     * @throws JiBXException if error in configuration
     */

    private void genNewInstanceCall(ContextMethodBuilder mb)
        throws JiBXException {
        
        // check if new instance method needs to be added to class
        if (m_newInstanceMethod == null) {
            
            // set up for constructing new method
            String name = m_container.getBindingRoot().getPrefix() +
                NEWINSTANCE_SUFFIX;
            String sig = UNMARSHAL_PARAMETER_SIGNATURE +
                m_class.getClassFile().getSignature();
            ClassFile cf = m_class.getMungedFile();
            ContextMethodBuilder meth = new ContextMethodBuilder(name, sig,
                cf, Constants.ACC_PUBLIC|Constants.ACC_STATIC, -1,
                m_class.getClassName(), 0, UNMARSHALLING_CONTEXT);
            
            // generate the code to build a new instance
            genNewInstanceCode(meth);
            
            // finish method code with return of new instance
            meth.appendReturn(m_class.getClassName());
            m_newInstanceMethod = m_class.getUniqueMethod(meth).getItem();
        }
        
        // generate code to call created new instance method
        mb.loadContext(UNMARSHALLING_CONTEXT);
        mb.appendCall(m_newInstanceMethod);
    }

    /**
     * Generate code to handle unmarshal source location tracking. This
     * convenience method generates the member variables and method used to
     * support setting the source location, the methods used to access the
     * information, and also adds the appropriate interfaces to the class.
     *
     * @throws JiBXException if error in generating code
     */

    private void genTrackSourceCode() throws JiBXException {
        ClassFile cf = m_class.getMungedFile();
        if (!m_isThisBinding && m_class.isDirectAccess() && !cf.isAbstract() &&
            cf.addInterface(SOURCE_TRACKING_INTERFACE)) {
        
            // add position tracking fields to class
            ClassItem srcname = cf.addPrivateField("java.lang.String;",
                SOURCEDOCUMENT_FIELDNAME);
            ClassItem srcline = cf.addPrivateField("int", SOURCELINE_FIELDNAME);
            ClassItem srccol = cf.addPrivateField("int",
                SOURCECOLUMN_FIELDNAME);
        
            // add method for setting the source information
            MethodBuilder mb = new ExceptionMethodBuilder(SETSOURCE_METHODNAME,
                Type.VOID, SETSOURCE_ARGS, cf, Constants.ACC_PUBLIC);
            mb.appendLoadLocal(0);
            mb.appendLoadLocal(1);
            mb.appendPutField(srcname);
            mb.appendLoadLocal(0);
            mb.appendLoadLocal(2);
            mb.appendPutField(srcline);
            mb.appendLoadLocal(0);
            mb.appendLoadLocal(3);
            mb.appendPutField(srccol);
            mb.appendReturn();
            mb.codeComplete(false);
            mb.addMethod();
        
            // add methods for getting the source information
            mb = new ExceptionMethodBuilder(SOURCENAME_METHODNAME,
                Type.STRING, EMPTY_ARGS, cf, Constants.ACC_PUBLIC);
            mb.appendLoadLocal(0);
            mb.appendGetField(srcname);
            mb.appendReturn(Type.STRING);
            mb.codeComplete(false);
            mb.addMethod();
            mb = new ExceptionMethodBuilder(SOURCELINE_METHODNAME,
                Type.INT, EMPTY_ARGS, cf, Constants.ACC_PUBLIC);
            mb.appendLoadLocal(0);
            mb.appendGetField(srcline);
            mb.appendReturn("int");
            mb.codeComplete(false);
            mb.addMethod();
            mb = new ExceptionMethodBuilder(SOURCECOLUMN_METHODNAME,
                Type.INT, EMPTY_ARGS, cf, Constants.ACC_PUBLIC);
            mb.appendLoadLocal(0);
            mb.appendGetField(srccol);
            mb.appendReturn("int");
            mb.codeComplete(false);
            mb.addMethod();
        }
    }
    
    /**
     * Construct fullly-qualified class and method name for method under
     * construction.
     * 
     * @param mb method to be named
     * @return fully-qualified class and method name
     */
    private String fullMethodName(ContextMethodBuilder mb) {
        return mb.getClassFile().getName() + '.' +  mb.getName();
    }
    
    /**
     * Construct fully-qualified class and method name for constructed method.
     * 
     * @param item method to be named
     * @return fully-qualified class and method name
     */
    private String fullMethodName(ClassItem item) {
        return item.getClassFile().getName() + '.' +  item.getName();
    }
    
    /**
     * Generate call to a constructed unmarshal method.
     * 
     * @param mb
     */
    private void genUnmarshalCall(String name, ContextMethodBuilder mb) {
        if (m_isStaticUnmarshal) {
            mb.appendCallStatic(name, m_unmarshalSignature);
        } else {
            mb.appendCallVirtual(name, m_unmarshalSignature);
        }
    }
    
    /**
     * Generate call to a constructed marshal method.
     * 
     * @param mb
     */
    private void genMarshalCall(String name, ContextMethodBuilder mb) {
        if (m_isStaticMarshal) {
            mb.appendCallStatic(name, m_marshalSignature);
        } else {
            mb.appendCallVirtual(name, m_marshalSignature);
        }
    }

    /**
     * Generate call to attribute unmarshal method for object. This convenience
     * method just generates code to call the generated unmarshal method added
     * to the class definition. The code generated prior to this call must have
     * loaded a reference to the object to be unmarshalled on the stack, and the
     * generated code returns the (possibly different, in the case of arrays)
     * object on the stack.
     *
     * @param mb method builder
     * @throws JiBXException if error in configuration
     */

    private void genUnmarshalAttributeCall(ContextMethodBuilder mb)
        throws JiBXException {
        
        // check if unmarshal method needs to be added to class
        if (m_unmarshalAttributeMethod == null) {
            if (m_unmarshalAttributeName == null) {
                
                // set up for constructing new method
                String name = m_container.getBindingRoot().getPrefix() +
                    UNMARSHAL_ATTR_SUFFIX;
                UnmarshalBuilder meth = new UnmarshalBuilder(name, 
                    m_class.getClassFile(), m_class.getMungedFile());
                m_unmarshalAttributeName = fullMethodName(meth);
                m_unmarshalSignature = meth.getSignature();
                m_isStaticUnmarshal = meth.isStaticMethod();
                
                // if preset method supplied add code to call it
                if (m_preSetMethod != null) {
                    meth.loadObject();
                    genUserMethodCall(true, m_preSetMethod, meth);
                }
                
                // push object being unmarshalled to unmarshaller stack
                if (!m_isThisBinding) {
                    meth.loadContext();
                    meth.loadObject();
                    meth.appendCallVirtual(UNMARSHAL_PUSHTRACKEDOBJECTMETHOD,
                        PUSHOBJECT_SIGNATURE);
                }
                
                // generate the actual unmarshalling code in method
                meth.loadObject();
                m_component.genAttributeUnmarshal(meth);
                
                // pop object from unmarshal stack
                if (!m_isThisBinding) {
                    meth.loadContext();
                    meth.appendCallVirtual(UNMARSHAL_POPOBJECTMETHOD,
                        POPOBJECT_SIGNATURE);
                }
                
                // if postset method supplied and no content add code to call it
                if (m_postSetMethod != null && !hasContent()) {
                    genUserMethodCall(true, m_postSetMethod, meth);
                }
                
                // finish by returning object
                meth.loadObject();
                meth.appendReturn(m_class.getClassFile().getName());
                
                // add method to class
                if (m_lockAttributeUnmarshal) {
                    m_unmarshalAttributeMethod =
                        m_class.getUniqueNamed(meth).getItem();
                } else {
                    m_unmarshalAttributeMethod =
                        m_class.getUniqueMethod(meth).getItem();
                    m_unmarshalAttributeName =
                        fullMethodName(m_unmarshalAttributeMethod);
                }
                
            } else {
                m_lockAttributeUnmarshal = true;
            }
        }
        
        // generate code to call created unmarshal method
        mb.loadContext(UNMARSHALLING_CONTEXT);
        genUnmarshalCall(m_unmarshalAttributeName, mb);
    }

    /**
     * Generate call to attribute marshal method for object. This convenience
     * method just generates code to call the generated marshal method added to
     * the class definition. The code generated prior to this call must have
     * loaded a reference to the object to be marshalled on the stack.
     *
     * @param mb method builder
     * @throws JiBXException if error in configuration
     */

    private void genMarshalAttributeCall(ContextMethodBuilder mb)
        throws JiBXException {
        
        // check if marshal method needs to be added to class
        if (m_marshalAttributeMethod == null) {
            if (m_marshalAttributeName == null) {
                
                // set up for constructing new method
                String name = m_container.getBindingRoot().getPrefix() +
                    MARSHAL_ATTR_SUFFIX;
                MarshalBuilder meth = new MarshalBuilder(name, 
                    m_class.getClassFile(), m_class.getMungedFile());
                m_marshalAttributeName = fullMethodName(meth);
                m_marshalSignature = meth.getSignature();
                m_isStaticMarshal = meth.isStaticMethod();
                
                // if preget method supplied add code to call it
                if (m_preGetMethod != null) {
                    genUserMethodCall(false, m_preGetMethod, meth);
                }
                
                // push object being marshalled to marshaller stack
                if (!m_isThisBinding) {
                    meth.loadContext();
                    meth.loadObject();
                    meth.appendCallVirtual(MARSHAL_PUSHOBJECTMETHOD,
                        PUSHOBJECT_SIGNATURE);
                }
                
                // generate actual marshalling code
                meth.loadContext();
                m_component.genAttributeMarshal(meth);
                
                // pop object from stack
                if (!m_isThisBinding) {
                    meth.loadContext();
                    meth.appendCallVirtual(MARSHAL_POPOBJECTMETHOD,
                        POPOBJECT_SIGNATURE);
                }
                
                // finish and add constructed method to class
                meth.appendReturn();
                if (m_lockAttributeMarshal) {
                    m_marshalAttributeMethod =
                        m_class.getUniqueNamed(meth).getItem();
                } else {
                    m_marshalAttributeMethod =
                        m_class.getUniqueMethod(meth).getItem();
                    m_marshalAttributeName =
                        fullMethodName(m_marshalAttributeMethod);
                }
                
            } else {
                m_lockAttributeMarshal = true;
            }
        }
        
        // generate code to call created marshal method
//        if (!m_directAccess) {
            mb.loadContext(MARSHALLING_CONTEXT);
//        }
        genMarshalCall(m_marshalAttributeName, mb);
    }

    /**
     * Generate call to content unmarshal method for object. This convenience
     * method just generates code to call the generated unmarshal method added
     * to the class definition. The code generated prior to this call must have
     * loaded a reference to the object to be unmarshalled on the stack, and the
     * generated code returns the (possibly different, in the case of arrays)
     * object on the stack.
     *
     * @param mb method builder
     * @throws JiBXException if error in configuration
     */

    private void genUnmarshalContentCall(ContextMethodBuilder mb)
        throws JiBXException {
        
        // check if unmarshal method needs to be added to class
        if (m_unmarshalContentMethod == null) {
            if (m_unmarshalContentName == null) {
                
                // set up for constructing new method
                String name = m_container.getBindingRoot().getPrefix() +
                    UNMARSHAL_SUFFIX;
                UnmarshalBuilder meth = new UnmarshalBuilder(name, 
                    m_class.getClassFile(), m_class.getMungedFile());
                m_unmarshalContentName = fullMethodName(meth);
                m_unmarshalSignature = meth.getSignature();
                m_isStaticUnmarshal = meth.isStaticMethod();
                
                // if preset method supplied add code to call it
                if (!hasAttribute() && m_preSetMethod != null) {
                    meth.loadObject();
                    genUserMethodCall(true, m_preSetMethod, meth);
                }
                
                // push object being unmarshalled to unmarshaller stack
                if (!m_isThisBinding) {
                    meth.loadContext();
                    meth.loadObject();
                    String mname = hasAttribute() ? UNMARSHAL_PUSHOBJECTMETHOD :
                        UNMARSHAL_PUSHTRACKEDOBJECTMETHOD;
                    meth.appendCallVirtual(mname, PUSHOBJECT_SIGNATURE);
                }
                
                // generate the actual unmarshalling code in method
                meth.loadObject();
                m_component.genContentUnmarshal(meth);
                
                // pop object from unmarshal stack
                if (!m_isThisBinding) {
                    meth.loadContext();
                    meth.appendCallVirtual(UNMARSHAL_POPOBJECTMETHOD,
                        POPOBJECT_SIGNATURE);
                }
                
                // if postset method supplied and no attributes add code to call
                if (m_postSetMethod != null) {
                    genUserMethodCall(true, m_postSetMethod, meth);
                }
                
                // finish by returning object
                meth.loadObject();
                meth.appendReturn(m_class.getClassFile().getName());
                
                // add method to class
                if (m_lockContentUnmarshal) {
                    m_unmarshalContentMethod =
                        m_class.getUniqueNamed(meth).getItem();
                } else {
                    m_unmarshalContentMethod =
                        m_class.getUniqueMethod(meth).getItem();
                    m_unmarshalContentName =
                        fullMethodName(m_unmarshalContentMethod);
                }
                
            } else {
                m_lockContentUnmarshal = true;
            }
        }
        
        // generate code to call created unmarshal method
        mb.loadContext(UNMARSHALLING_CONTEXT);
        genUnmarshalCall(m_unmarshalContentName, mb);
    }

    /**
     * Generate call to content marshal method for object. This convenience
     * method just generates code to call the generated marshal method added to
     * the class definition. The code generated prior to this call must have
     * loaded a reference to the object to be marshalled on the stack.
     *
     * @param mb method builder
     * @throws JiBXException if error in configuration
     */

    private void genMarshalContentCall(ContextMethodBuilder mb)
        throws JiBXException {
        
        // check if marshal method needs to be added to class
        if (m_marshalContentMethod == null) {
            if (m_marshalContentName == null) {
                
                // set up for constructing new method
                String name =
                    m_container.getBindingRoot().getPrefix() + MARSHAL_SUFFIX;
                MarshalBuilder meth = new MarshalBuilder(name, 
                    m_class.getClassFile(), m_class.getMungedFile());
                m_marshalContentName = fullMethodName(meth);
                m_marshalSignature = meth.getSignature();
                m_isStaticMarshal = meth.isStaticMethod();
                
                // if preget method supplied and no attributes add code to call it
                if (m_preGetMethod != null && !hasAttribute()) {
                    genUserMethodCall(false, m_preGetMethod, meth);
                }
                
                // push object being marshalled to marshaller stack
                if (!m_isThisBinding) {
                    meth.loadContext();
                    meth.loadObject();
                    meth.appendCallVirtual(MARSHAL_PUSHOBJECTMETHOD,
                        PUSHOBJECT_SIGNATURE);
                }
                
                // generate actual marshalling code
                meth.loadContext();
                m_component.genContentMarshal(meth);
                
                // pop object from stack
                if (!m_isThisBinding) {
                    meth.loadContext();
                    meth.appendCallVirtual(MARSHAL_POPOBJECTMETHOD,
                        POPOBJECT_SIGNATURE);
                }
                
                // finish and add constructed method to class
                meth.appendReturn();
                if (m_lockContentMarshal) {
                    m_marshalContentMethod =
                        m_class.getUniqueNamed(meth).getItem();
                } else {
                    m_marshalContentMethod =
                        m_class.getUniqueMethod(meth).getItem();
                    m_marshalContentName = fullMethodName(m_marshalContentMethod);
                }
                
            } else {
                m_lockContentMarshal = true;
            }
        }
        
        // generate code to call created marshal method
        mb.loadContext(MARSHALLING_CONTEXT);
        genMarshalCall(m_marshalContentName, mb);
    }
    
    //
    // IContextObj interface method definitions
    
    public BoundClass getBoundClass() {
        return m_class;
    }

    public boolean setIdChild(IComponent child) {
        if (m_idChild == null) {
            m_idChild = child;
            return true;
        } else {
            return false;
        }
    }
    
    //
    // IComponent interface method definitions
    
    public boolean isOptional() {
        return false;
    }

    public void genAttributeUnmarshal(ContextMethodBuilder mb)
        throws JiBXException {
        genUnmarshalAttributeCall(mb);
    }

    public void genAttributeMarshal(ContextMethodBuilder mb)
        throws JiBXException {
        genMarshalAttributeCall(mb);
    }

    public void genContentUnmarshal(ContextMethodBuilder mb)
        throws JiBXException {
        genUnmarshalContentCall(mb);
    }

    public void genContentMarshal(ContextMethodBuilder mb)
        throws JiBXException {
        genMarshalContentCall(mb);
    }
    
    public void genNewInstance(ContextMethodBuilder mb) throws JiBXException {
        genNewInstanceCall(mb);
    }

    public String getType() {
        return m_class.getClassName();
    }

    public boolean hasId() {
        return m_idChild != null;
    }

    public void genLoadId(ContextMethodBuilder mb) throws JiBXException {
        if (m_idChild == null) {
            throw new IllegalStateException("Internal error: no id defined");
        } else {
            m_idChild.genLoadId(mb);
        }
    }
    
    public void setLinkages() throws JiBXException {
        super.setLinkages();
        if (m_container.getBindingRoot().isTrackSource()) {
            genTrackSourceCode();
        }
    }
    
    // DEBUG
    public void print(int depth) {
        BindingDefinition.indent(depth);
        System.out.print("object binding for " +
            m_class.getClassFile().getName());
        if (m_isThisBinding) {
            System.out.print(" (\"this\" reference)");
        }
        if (m_createClass != null) {
            System.out.print(" create class " + m_createClass.getName());
        }
        System.out.println();
        m_component.print(depth+1);
    }
}