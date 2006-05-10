/*
Copyright (c) 2004-2005, Dennis M. Sosnoski
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

package org.jibx.binding.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jibx.binding.util.StringArray;

/**
 * Model component for <i>string</i> attribute group in binding definition.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
 
public class StringAttributes extends AttributeBase
{
    /** Enumeration of allowed attribute names */
    public static final StringArray s_allowedAttributes =
        new StringArray(new String[] { "default", "deserializer",
        "serializer" });
    // TODO: add "format" for 2.0
    
    //
    // Constants and such related to code generation.
    
    // signature variants allowed for serializer
    private static final String[] SERIALIZER_SIGNATURE_VARIANTS =
    {
        "Lorg/jibx/runtime/IMarshallingContext;",
        "Ljava/lang/Object;",
        ""
    };
    
    // signatures allowed for deserializer
    private static final String[] DESERIALIZER_SIGNATURES =
    {
        "(Ljava/lang/String;Lorg/jibx/runtime/IUnmarshallingContext;)",
        "(Ljava/lang/String;Ljava/lang/Object;)",
        "(Ljava/lang/String;)"
    };
    
    // signature required for constructor from string
    private static final String STRING_CONSTRUCTOR_SIGNATURE =
        "(Ljava/lang/String;)";
    
    // classes of arguments to constructor or deserializer
    private static final Class[] STRING_CONSTRUCTOR_ARGUMENT_CLASSES =
    {
        java.lang.String.class
    };
    
    //
    // Instance data.
    
    /** Referenced format name. */
    private String m_formatName;
    
    /** Default value text. */
    private String m_defaultText;
    
    /** Serializer fully qualified class and method name. */
    private String m_serializerName;
    
    /** Deserializer fully qualified class and method name. */
    private String m_deserializerName;
    
    /** Base format for conversions. */
    private FormatElement m_baseFormat;
    
    /** Value type class. */
    private IClass m_typeClass;
    
    /** Default value object. */
    private Object m_default;
	
	/** Serializer method (or toString equivalent) information. */
	private IClassItem m_serializerItem;
    
    /** Deserializer method (or constructor from string) information. */
    private IClassItem m_deserializerItem;
	
	/**
	 * Default constructor.
	 */
	public StringAttributes() {}
    
    /**
     * Set value type. This needs to be set by the owning element prior to
     * validation. Even though the type is an important part of the string
     * information, it's treated as a separate item of information because it
     * needs to be used as part of the property attributes.
     * 
     * @param type value type
     */
    public void setType(IClass type) {
        m_typeClass = type;
    }
    
    /**
     * Get value type.
     * 
     * @return value type
     */
    public IClass getType() {
        return m_typeClass;
    }
    
    /**
     * Get base format name.
     * 
     * @return referenced base format
     */
    public String getFormatName() {
        return m_formatName;
    }
    
    /**
     * Set base format name.
     * 
     * @param name referenced base format
     */
    public void setFormatName(String name) {
        m_formatName = name;
    }
    
    /**
     * Get default value text.
     * 
     * @return default value text
     */
    public String getDefaultText() {
        return m_defaultText;
    }
    
    /**
     * Get default value. This method is only usable after a
     * call to {@link #validate}.
     * 
     * @return default value object
     */
    public Object getDefault() {
        return m_default;
    }
    
    /**
     * Set default value text.
     * 
     * @param value default value text
     */
    public void setDefaultText(String value) {
        m_defaultText = value;
    }
    
    /**
     * Get serializer name.
     * 
     * @return fully qualified class and method name for serializer (or
     * <code>null</code> if none)
     */
    public String getSerializerName() {
        return m_serializerName;
    }
    
    /**
     * Get serializer method information. This method is only usable after a
     * call to {@link #validate}.
     * 
     * @return serializer information (or <code>null</code> if none)
     */
    public IClassItem getSerializer() {
        return m_serializerItem;
    }
    
    /**
     * Set serializer method name.
     * 
     * @param fully qualified class and method name for serializer
     */
    public void setSerializerName(String name) {
        m_serializerName = name;
    }
    
    /**
     * Get deserializer name.
     * 
     * @return fully qualified class and method name for deserializer (or
     * <code>null</code> if none)
     */
    public String getDeserializerName() {
        return m_serializerName;
    }
    
    /**
     * Get deserializer method information. This method is only usable after a
     * call to {@link #validate}.
     * 
     * @return deserializer information (or <code>null</code> if none)
     */
    public IClassItem getDeserializer() {
        return m_deserializerItem;
    }
    
    /**
     * Set deserializer method name.
     * 
     * @param fully qualified class and method name for deserializer
     */
    public void setDeserializerName(String name) {
        m_deserializerName = name;
    }
    
    /**
     * Get base format information. This method is only usable after a
     * call to {@link #validate}.
     * 
     * @return base format element (or <code>null</code> if none)
     */
    public FormatElement getBaseFormat() {
        return m_baseFormat;
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.AttributeBase#prevalidate(org.jibx.binding.model.ValidationContext)
     */
    public void prevalidate(ValidationContext vctx) {
        
        // make sure the type has been configured
        if (m_typeClass == null) {
            vctx.addFatal("Missing type information for conversion to string");
        } else {
            
            // get the base format (if any)
            DefinitionContext dctx = vctx.getDefinitions();
            if (m_formatName == null) {
                m_baseFormat = dctx.getBestFormat(m_typeClass);
            } else {
                m_baseFormat = dctx.getNamedFormat(m_formatName);
            }
            
            // check specified serializer and deserializer
            String tname = m_typeClass.getName();
            if (vctx.isOutBinding()) {
                if (m_serializerName != null) {
                
                    // build all possible signature variations
                    String[] tsigs = ClassUtils.
                        getSignatureVariants(tname, vctx);
                    int vcnt = SERIALIZER_SIGNATURE_VARIANTS.length;
                    String[] msigs = new String[tsigs.length * vcnt];
                    for (int i = 0; i < tsigs.length; i++) {
                        for (int j = 0; j < vcnt; j++) {
                            msigs[i*vcnt + j] = "(" + tsigs[i] +
                                SERIALIZER_SIGNATURE_VARIANTS[j] +
                                ")Ljava/lang/String;";
                        }
                    }
                
                    // find a matching static method
                    m_serializerItem = ClassUtils.
                        findStaticMethod(m_serializerName, msigs, vctx);
                    if (m_serializerItem == null) {
                        vctx.addError("Static serializer method " +
                            m_serializerName + " not found");
                    }
                    
                } else {
                    
                    // try to find an inherited serializer
                    FormatElement ances = m_baseFormat;
                    while (ances != null) {
                        m_serializerItem = ances.getSerializer();
                        if (m_serializerItem == null) {
                            ances = ances.getBaseFormat();
                        } else {
                            break;
                        }
                    }
                    if (m_serializerItem == null) {
                        m_serializerItem = m_typeClass.getMethod("toString",
                            "()Ljava/lang/String;");
                        if (m_serializerItem == null) {
                            vctx.addError("toString method not found");
                        }
                    }
                }
            }
            if (vctx.isInBinding() || m_defaultText != null) {
                if (m_deserializerName != null) {
                
                    // find a matching static method
                    m_deserializerItem = ClassUtils.
                        findStaticMethod(m_deserializerName,
                            DESERIALIZER_SIGNATURES, vctx);
                    if (m_deserializerItem == null) {
                        vctx.addError("Static deserializer method " +
                            m_deserializerName + " not found");
                    } else {
                        String result = m_deserializerItem.getTypeName();
                        if (!ClassUtils.isAssignable(result, tname, vctx)) {
                            vctx.addError("Static deserializer method " +
                                m_deserializerName +
                                " has incompatible result type");
                        }
                    }
                    
                } else {
                    
                    // try to find an inherited deserializer
                    FormatElement ances = m_baseFormat;
                    while (ances != null) {
                        m_deserializerItem = ances.getDeserializer();
                        if (m_deserializerItem == null) {
                            ances = ances.getBaseFormat();
                        } else {
                            break;
                        }
                    }
                    if (m_deserializerItem == null) {
                        
                        // try to find a constructor from string as last resort
                        m_deserializerItem = m_typeClass.
                            getInitializerMethod(STRING_CONSTRUCTOR_SIGNATURE);
                        if (m_deserializerItem == null) {
                            
                            // error unless predefined formats
                            if (vctx.getNestingDepth() > 0) {
                                StringBuffer buff = new StringBuffer();
                                buff.append
                                    ("Need deserializer or constructor ");
                                buff.append("from string");
                                if (!vctx.isInBinding()) {
                                    buff.append(" for default value of type ");
                                    buff.append(tname);
                                } else {
                                    buff.append(" for type ");
                                    buff.append(tname);
                                }
                                vctx.addError(buff.toString());
                            }
                            
                        }
                    }
                }
            }
            
            // check for default value to be converted
            if (m_defaultText != null && m_deserializerItem != null) {
                
                // first load the class to handle conversion
                IClass iclas = m_deserializerItem.getOwningClass();
                Class clas = iclas.loadClass();
                Exception ex = null;
                boolean construct = false;
                try {
                    if (clas == null) {
                        vctx.addError("Unable to load class " +
                            iclas.getName() +
                            " for converting default value of type " + tname);
                    } else if (m_deserializerItem.isInitializer()) {
                        
                        // invoke constructor to process default value
                        construct = true;
                        Constructor cons = clas.getConstructor
                            (STRING_CONSTRUCTOR_ARGUMENT_CLASSES);
                        try {
                            cons.setAccessible(true);
                        } catch (Exception e) { /* deliberately left empty */ }
                        Object[] args = new Object[1];
                        args[0] = m_defaultText;
                        m_default = cons.newInstance(args);
                        
                    } else {
                        
                        // invoke deserializer to convert default value
                        String mname = m_deserializerItem.getName();
                        Method deser = clas.getDeclaredMethod(mname,
                            STRING_CONSTRUCTOR_ARGUMENT_CLASSES);
                        try {
                            deser.setAccessible(true);
                        } catch (Exception e) { /* deliberately left empty */ }
                        Object[] args = new Object[1];
                        args[0] = m_defaultText;
                        m_default = deser.invoke(null, args);
                        
                    }
                } catch (SecurityException e) {
                    StringBuffer buff = new StringBuffer("Unable to access ");
                    if (construct) {
                        buff.append("constructor from string");
                    } else {
                        buff.append("deserializer ");
                        buff.append(m_deserializerName);
                    }
                    buff.append(" for converting default value of type ");
                    buff.append(tname);
                    vctx.addError(buff.toString());
                } catch (NoSuchMethodException e) {
                    StringBuffer buff = new StringBuffer("Unable to find ");
                    if (construct) {
                        buff.append("constructor from string");
                    } else {
                        buff.append("deserializer ");
                        buff.append(m_deserializerName);
                    }
                    buff.append(" for converting default value of type ");
                    buff.append(tname);
                    vctx.addError(buff.toString());
                } catch (IllegalArgumentException e) {
                    ex = e;
                } catch (InstantiationException e) {
                    ex = e;
                } catch (IllegalAccessException e) {
                    ex = e;
                } catch (InvocationTargetException e) {
                    ex = e;
                } finally {
                    if (ex != null) {
                        StringBuffer buff = new StringBuffer("Error calling ");
                        if (construct) {
                            buff.append("constructor from string");
                        } else {
                            buff.append("deserializer ");
                            buff.append(m_deserializerName);
                        }
                        buff.append(" for converting default value of type ");
                        buff.append(tname);
                        vctx.addError(buff.toString());
                    }
                }
            }
        }
        super.prevalidate(vctx);
    }
}