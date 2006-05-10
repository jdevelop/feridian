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

import java.io.File;
import java.util.ArrayList;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.Type;
import org.jibx.binding.classes.BoundClass;
import org.jibx.binding.classes.BranchWrapper;
import org.jibx.binding.classes.ClassCache;
import org.jibx.binding.classes.ClassFile;
import org.jibx.binding.classes.ClassItem;
import org.jibx.binding.classes.ExceptionMethodBuilder;
import org.jibx.binding.classes.MethodBuilder;
import org.jibx.binding.classes.MungedClass;
import org.jibx.binding.util.ArrayMap;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.JiBXException;

/**
 * Binding definition. This is the root of the object graph for a binding.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class BindingDefinition extends BindingBuilder.ContainerBase
implements IContainer
{
    //
    // Miscellaneous static data.
    
    /** Current distribution file name. This is filled in by the Ant build
     process to match the current distribution. */
    public static final String CURRENT_VERSION_NAME = "@distrib@";
    
    /** Prefix used in all code generation for methods and classes. */
    public static final String GENERATE_PREFIX = "JiBX_";
    
    /** Default prefix for automatic ID generation. */
    /*package*/ static final String DEFAULT_AUTOPREFIX = "id_";
    
    /** Minimum size to use map for index from type name. */
    private static final int TYPEMAP_MINIMUM_SIZE = 5;
    
    /** Table of defined bindings. */
    private static ArrayList s_bindings;
    
    /** Classes included in any binding. */
    private static ArrayMap s_mappedClasses;
    
    //
    // Static instances of predefined conversions.
    private static StringConversion s_byteConversion =
        new PrimitiveStringConversion(Byte.TYPE, new Byte((byte)0), "B",
        "serializeByte", "parseByte", "attributeByte", "parseElementByte");
    private static StringConversion s_charConversion =
        new PrimitiveStringConversion(Character.TYPE, new Character((char)0),
        "C", "serializeChar", "parseChar", "attributeChar", "parseElementChar");
    private static StringConversion s_doubleConversion =
        new PrimitiveStringConversion(Double.TYPE, new Double(0.0d), "D",
        "serializeDouble", "parseDouble", "attributeDouble",
        "parseElementDouble");
    private static StringConversion s_floatConversion =
        new PrimitiveStringConversion(Float.TYPE, new Float(0.0f), "F",
        "serializeFloat", "parseFloat", "attributeFloat", "parseElementFloat");
    private static StringConversion s_intConversion =
        new PrimitiveStringConversion(Integer.TYPE, new Integer(0), "I",
        "serializeInt", "parseInt", "attributeInt", "parseElementInt");
    private static StringConversion s_longConversion =
        new PrimitiveStringConversion(Long.TYPE, new Long(0L), "J",
        "serializeLong", "parseLong", "attributeLong", "parseElementLong");
    private static StringConversion s_shortConversion =
        new PrimitiveStringConversion(Short.TYPE, new Short((short)0), "S",
        "serializeShort", "parseShort", "attributeShort", "parseElementShort");
    private static StringConversion s_booleanConversion =
        new PrimitiveStringConversion(Boolean.TYPE, Boolean.FALSE, "Z",
        "serializeBoolean", "parseBoolean", "attributeBoolean",
        "parseElementBoolean");
    private static StringConversion s_dateConversion =
        new ObjectStringConversion(null,
        "org.jibx.runtime.Utility.serializeDateTime", 
        "org.jibx.runtime.Utility.deserializeDateTime", "java.util.Date");
    private static StringConversion s_sqlDateConversion =
        new ObjectStringConversion(null,
        "org.jibx.runtime.Utility.serializeSqlDate", 
        "org.jibx.runtime.Utility.deserializeSqlDate", "java.sql.Date");
    private static StringConversion s_sqlTimeConversion =
        new ObjectStringConversion(null,
        "org.jibx.runtime.Utility.serializeSqlTime", 
        "org.jibx.runtime.Utility.deserializeSqlTime", "java.sql.Time");
    private static StringConversion s_timestampConversion =
        new ObjectStringConversion(null,
        "org.jibx.runtime.Utility.serializeTimestamp", 
        "org.jibx.runtime.Utility.deserializeTimestamp", "java.sql.Timestamp");
    public static StringConversion s_base64Conversion =
        new ObjectStringConversion(null,
        "org.jibx.runtime.Utility.serializeBase64", 
        "org.jibx.runtime.Utility.deserializeBase64", "byte[]");
    
    public static StringConversion s_stringConversion =
        new ObjectStringConversion(null, null, null, "java.lang.String");
    public static StringConversion s_objectConversion =
        new ObjectStringConversion(null, null, null, "java.lang.Object");
    
    //
    // Constants for code generation
    
    private static final String FACTORY_SUFFIX = "Factory";
    private static final String FACTORY_INTERFACE =
        "org.jibx.runtime.IBindingFactory";
    private static final String[] FACTORY_INTERFACES =
    {
        FACTORY_INTERFACE
    };
    private static final String FACTORY_INSTNAME = "m_inst";
    private static final int FACTORY_INSTACCESS = 
        Constants.ACC_PRIVATE | Constants.ACC_STATIC;
    private static final String MARSHALLER_ARRAYNAME = "m_marshallers";
    private static final String UNMARSHALLER_ARRAYNAME = "m_unmarshallers";
    private static final String STRING_ARRAYTYPE = "java.lang.String[]";
    private static final String CLASSES_ARRAYNAME = "m_classes";
    private static final String URIS_ARRAYNAME = "m_uris";
    private static final String PREFIXES_ARRAYNAME = "m_prefixes";
    private static final String GNAMES_ARRAYNAME = "m_globalNames";
    private static final String GURIS_ARRAYNAME = "m_globalUris";
    private static final String IDNAMES_ARRAYNAME = "m_idNames";
    private static final String TYPEMAP_NAME = "m_typeMap";
    private static final String CREATEMARSHAL_METHODNAME =
        "createMarshallingContext";
    private static final String MARSHALCONTEXT_INTERFACE =
        "org.jibx.runtime.IMarshallingContext";
    private static final String MARSHALCONTEXT_IMPLEMENTATION =
        "org.jibx.runtime.impl.MarshallingContext";
    private static final String MARSHALCONTEXTINIT_SIGNATURE =
        "([Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;" +
        "Lorg/jibx/runtime/IBindingFactory;)V";
    private static final String CREATEUNMARSHAL_METHODNAME =
        "createUnmarshallingContext";
    private static final String UNMARSHALCONTEXT_INTERFACE =
        "org.jibx.runtime.IUnmarshallingContext";
    private static final String UNMARSHALCONTEXT_IMPLEMENTATION =
        "org.jibx.runtime.impl.UnmarshallingContext";
    private static final String UNMARSHALCONTEXTINIT_SIGNATURE =
        "(I[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;" +
        "[Ljava/lang/String;Lorg/jibx/runtime/IBindingFactory;)V";
    private static final String GETINST_METHODNAME = "getInstance";
    private static final String UNSUPPORTED_EXCEPTION_CLASS =
        "java.lang.UnsupportedOperationException";
    private static final String GETVERSION_METHODNAME = "getCompilerVersion";
    private static final String GETDISTRIB_METHODNAME =
        "getCompilerDistribution";
    private static final String GETDEFINEDNSS_METHODNAME = "getNamespaces";
    private static final String GETDEFINEDPREFS_METHODNAME = "getPrefixes";
    private static final String GETCLASSES_METHODNAME = "getMappedClasses";
    private static final String GETELEMENTNSS_METHODNAME =
        "getElementNamespaces";
    private static final String GETELEMENTNAMES_METHODNAME = "getElementNames";
    private static final String GETTYPEINDEX_METHODNAME = "getTypeIndex";
    private static final String STRINGINT_MAPTYPE =
        "org.jibx.runtime.impl.StringIntHashMap";
    private static final String STRINGINTINIT_SIGNATURE = "(I)V";
    private static final String STRINGINTADD_METHOD =
        "org.jibx.runtime.impl.StringIntHashMap.add";
    private static final String STRINGINTADD_SIGNATURE =
        "(Ljava/lang/String;I)I";
    private static final String STRINGINTGET_METHOD =
        "org.jibx.runtime.impl.StringIntHashMap.get";
    private static final String STRINGINTGET_SIGNATURE =
        "(Ljava/lang/String;)I";

    //
    // Actual instance data

    /** Binding name. */
    private final String m_name;
    
    /** Index number of this binding. */
    private final int m_index;

    /** Input binding flag. */
    private final boolean m_isInput;

    /** Output binding flag. */
    private final boolean m_isOutput;

    /** Use global ID values flag. */
    private final boolean m_isIdGlobal;

    /** Support forward references to IDs flag. */
    private final boolean m_isForwards;

    /** Generate souce tracking interface flag. */
    private final boolean m_isTrackSource;

    /** Generate marshaller/unmarshaller classes for top-level non-base abstract
     mappings flag. */
    private final boolean m_isForceClasses;

    /** Package for generated context factory. */
    private String m_targetPackage;
    
    /** File root for generated context factory. */
    private File m_targetRoot;
    
    /** Classes using unique (per class) identifiers. This is <code>null</code>
     and unused when using global ID values. */
    private ArrayMap m_uniqueIds;
    
    /** Namespaces URIs included in binding. */
    private ArrayMap m_namespaceUris;
    
    /** Original prefixes for namespaces. */
    private ArrayList m_namespacePrefixes;
    
    /** Outer definition context with default definitions. */
    private DefinitionContext m_outerContext;
    
    /** Inner definition context constructed for binding. */
    private DefinitionContext m_activeContext;
    
    /** Flag for done assigning indexes to mapped classes. */
    private boolean m_isMappedDone;
    
    /** Next index number for marshaller/unmarshaller slots used in-line. */
    private int m_mumIndex;
    
    /** Classes handled by in-line marshaller/unmarshaller references. */
    private ArrayList m_extraClasses;
    
    /** Marshaller classes used in-line. */
    private ArrayList m_extraMarshallers;
    
    /** Unmarshaller classes used in-line. */
    private ArrayList m_extraUnmarshallers;

    /**
     * Constructor. Sets all defaults, including the default name provided, and
     * initializes the definition context for the outermost level of the
     * binding.
     *
     * @param name binding name
     * @param ibind input binding flag
     * @param obind output binding flag
     * @param tpack target package
     * @param glob global IDs flag
     * @param forward support forward referenced IDs flag
     * @param source add source tracking for unmarshalled objects flag
     * @param force create marshaller/unmarshaller classes for top-level
     * non-base mappings
     * @throws JiBXException if error in transformation
     */

    public BindingDefinition(String name, boolean ibind, boolean obind,
        String tpack, boolean glob, boolean forward, boolean source,
        boolean force) throws JiBXException {
        
        // handle basic initialization
        super(null);
        m_name = name;
        m_isInput = ibind;
        m_isOutput = obind;
        m_targetPackage = tpack;
        m_isIdGlobal = glob;
        m_isForwards = forward;
        m_isTrackSource = source;
        m_isForceClasses = force;
        
        // set base class defaults
        m_styleDefault = ValueChild.ELEMENT_STYLE;
        m_autoLink = BindingBuilder.LINK_FIELDS;
        m_accessLevel = BindingBuilder.ACC_PRIVATE;
        m_nameStyle = BindingBuilder.NAME_HYPHENS;
        
        // initialize the contexts
        m_outerContext = m_activeContext = new DefinitionContext(this);
        m_activeContext = new DefinitionContext(this);
        m_namespaceUris = new ArrayMap();
        m_namespaceUris.findOrAdd("");
        m_namespacePrefixes = new ArrayList();
        m_namespacePrefixes.add("");
        m_outerContext.addNamespace(NamespaceDefinition.buildNamespace
            ("http://www.w3.org/XML/1998/namespace", "xml"));
        
        // build the default converters in outer context
        m_outerContext.setDefaultConversion("byte:default", s_byteConversion);
        m_outerContext.setDefaultConversion("char:default", s_charConversion);
        StringConversion schar = s_charConversion.derive("char",
            "org.jibx.runtime.Utility.serializeCharString",
            "org.jibx.runtime.Utility.parseCharString", null);
        m_outerContext.setNamedConversion("char:string", schar);
        m_outerContext.setDefaultConversion("double:default",
            s_doubleConversion);
        m_outerContext.setDefaultConversion("float:default", s_floatConversion);
        m_outerContext.setDefaultConversion("int:default", s_intConversion);
        m_outerContext.setDefaultConversion("long:default", s_longConversion);
        m_outerContext.setDefaultConversion("short:default", s_shortConversion);
        m_outerContext.setDefaultConversion("boolean:default",
            s_booleanConversion);
        m_outerContext.setDefaultConversion("Date:default", s_dateConversion);
        m_outerContext.setDefaultConversion("SqlDate:default",
            s_sqlDateConversion);
        m_outerContext.setDefaultConversion("SqlTime:default",
            s_sqlTimeConversion);
        m_outerContext.setDefaultConversion("Timestamp:default",
            s_timestampConversion);
        m_outerContext.setDefaultConversion("byte[]:default",
            s_base64Conversion);
        m_outerContext.setDefaultConversion("String:default",
            s_stringConversion);
        m_outerContext.setDefaultConversion("Object:default",
            s_objectConversion);
        
        // add this binding to list
        m_index = s_bindings.size();
        s_bindings.add(this);
    }
    
    /**
     * Get class linked to binding element. Implementation of
     * {@link org.jibx.binding.def.IContainer} interface, just returns
     * <code>null</code> in this case.
     *
     * @return information for class linked by binding
     */

    public BoundClass getBoundClass() {
        return null;
    }

    /**
     * Get default style for value expression. Implementation of
     * {@link org.jibx.binding.def.IContainer} interface.
     *
     * @return default style type for values
     */

    public int getStyleDefault() {
        return m_styleDefault;
    }

    /**
     * Set ID property. This parent binding component interface method should
     * never be called for the binding definition, and will throw a runtime
     * exception if it is called.
     *
     * @param child child defining the ID property
     * @return <code>false</code>
     */

    public boolean setIdChild(IComponent child) {
        throw new IllegalStateException("Internal error - setIdChild for root");
    }

    /**
     * Get default package used for code generation.
     *
     * @return default code generation package
     */

    public String getDefaultPackage() {
        return m_targetPackage;
    }

    /**
     * Get root directory for default code generation package.
     *
     * @return root for default code generation
     */

    public File getDefaultRoot() {
        return m_targetRoot;
    }

    /**
     * Set location for binding factory class generation.
     *
     * @param tpack target package for generated context factory
     * @param root target root for generated context factory
     */

    public void setFactoryLocation(String tpack, File root) {
        m_targetPackage = tpack;
        m_targetRoot = root;
    }

    /**
     * Get index number of binding.
     *
     * @return index number for this binding definition
     */

    public int getIndex() {
        return m_index;
    }

    /**
     * Check if binding is defined for unmarshalling.
     *
     * @return <code>true</code> if defined, <code>false</code> if not
     */

    public boolean isInput() {
        return m_isInput;
    }

    /**
     * Check if binding is defined for marshalling.
     *
     * @return <code>true</code> if defined, <code>false</code> if not
     */

    public boolean isOutput() {
        return m_isOutput;
    }

    /**
     * Check if global ids are used by binding.
     *
     * @return <code>true</code> if defined, <code>false</code> if not
     */

    public boolean isIdGlobal() {
        return m_isIdGlobal;
    }

    /**
     * Check if forward ids are supported by unmarshalling binding.
     *
     * @return <code>true</code> if supported, <code>false</code> if not
     */

    public boolean isForwards() {
        return m_isForwards;
    }

    /**
     * Check if source tracking is supported by unmarshalling binding.
     *
     * @return <code>true</code> if defined, <code>false</code> if not
     */

    public boolean isTrackSource() {
        return m_isTrackSource;
    }

    /**
     * Get prefix for method or class generation.
     *
     * @return prefix for names created by this binding
     */

    public String getPrefix() {
        return GENERATE_PREFIX + m_name;
    }

    /**
     * Get index for mapped class from binding. If the class is not already
     * included in any binding it is first added to the list of bound classes.
     * All bindings use the same index numbers to allow easy lookup of the
     * appropriate marshaller and unmarshaller within a particular binding, but
     * this does mean that all bindings dealing with a common set of classes
     * need to be compiled together. This uses the same sequence of values as
     * the {@link #getMarshallerUnmarshallerIndex} method but differs in that
     * the values returned by this method are unique per class. This method is
     * intended for use with &lt;mapping&gt; definitions. It is an error to call
     * this method after calling the {@link #getMarshallerUnmarshallerIndex}
     * method.
     *
     * @param name fully qualified name of mapped class
     * @return index number of class
     */

    public int getMappedClassIndex(String name) {
        if (m_isMappedDone) {
            throw new IllegalStateException
                ("Internal error: Call out of sequence");
        } else {
            return s_mappedClasses.findOrAdd(name);
        }
    }

    /**
     * Get marshaller/unmarshaller slot index in binding. This uses the same
     * sequence of values as the {@link #getMappedClassIndex} method but differs
     * in that the same class may have more than one marshaller/unmarshaller
     * slot defined. It's intended for user-defined marshallers/unmarshallers
     * where use is specific to a particular context. After the slot has been
     * assigned by this method, the {@link #setMarshallerUnmarshallerClasses}
     * method must be used to set the actual class names.
     *
     * @param clas fully qualified name of class handled by
     * marshaller/unmarshaller
     * @return slot number for marshaller/unmarshaller
     */

    public int getMarshallerUnmarshallerIndex(String clas) {
        if (!m_isMappedDone) {
            m_isMappedDone = true;
            m_mumIndex = s_mappedClasses.size();
            m_extraClasses = new ArrayList();
            m_extraMarshallers = new ArrayList();
            m_extraUnmarshallers = new ArrayList();
        }
        m_extraClasses.add(clas);
        m_extraMarshallers.add(null);
        m_extraUnmarshallers.add(null);
        return m_mumIndex++;
    }

    /**
     * Set marshaller and unmarshaller class names for slot.
     *
     * @param slot assigned marshaller/unmarshaller slot number
     * @param mclas fully qualified name of marshaller class
     * @param uclas fully qualified name of unmarshaller class
     */

    public void setMarshallerUnmarshallerClasses(int slot, String mclas,
        String uclas) {
        int index = slot - s_mappedClasses.size();
        m_extraMarshallers.set(index, mclas);
        m_extraUnmarshallers.set(index, uclas);
    }

    /**
     * Get index for ID'ed class from binding. If the class is not already
     * included it is first added to the binding. If globally unique IDs are
     * used this always returns <code>0</code>.
     *
     * @param name fully qualified name of ID'ed class
     * @return index number of class
     */

    public int getIdClassIndex(String name) {
        if (m_isIdGlobal) {
            return 0;
        } else {
            if (m_uniqueIds == null) {
                m_uniqueIds = new ArrayMap();
            }
            return m_uniqueIds.findOrAdd(name);
        }
    }

    /**
     * Get index for namespace URI in binding. If the URI is not already
     * included it is first added to the binding. The empty namespace URI
     * is always given index number <code>0</code>.
     *
     * @param uri namespace URI to be included in binding
     * @param prefix prefix used with namespace
     * @return index number of namespace
     */

    public int getNamespaceUriIndex(String uri, String prefix) {
        int index = m_namespaceUris.findOrAdd(uri);
        if (index > m_namespacePrefixes.size()) {
            m_namespacePrefixes.add(prefix);
        }
        return index;
    }

    /**
     * Generate code. First sets linkages and executes code generation for
     * each top-level mapping defined in this binding, which in turn propagates
     * the code generation all the way down. Then generates the actual binding
     * factory for this binding.
     * 
     * TODO: handle unidirectional bindings properly
     *
     * @param verbose flag for verbose output
     * @throws JiBXException if error in code generation
     */

    public void generateCode(boolean verbose) throws JiBXException {
        
        // handle basic linkage and child code generation
        BoundClass.setModify(m_targetRoot, m_targetPackage);
        m_activeContext.linkMappings();
        m_activeContext.setLinkages();
        m_activeContext.generateCode(verbose, m_isForceClasses);
        if (verbose) {
            System.out.println("After linking view of binding " + m_name + ':');
            print();
        }
        
        // build the binding factory class
        String name;
        if (m_targetPackage.length() == 0) {
            name = getPrefix() + FACTORY_SUFFIX;
        } else {
            name = m_targetPackage + '.' + getPrefix() + FACTORY_SUFFIX;
        }
        ClassFile base = ClassCache.getClassFile("java.lang.Object");
        ClassFile cf = new ClassFile(name, m_targetRoot, base,
            Constants.ACC_PUBLIC, FACTORY_INTERFACES);
        
        // add static field for instance and member fields for data
        ClassItem inst = cf.addField(FACTORY_INTERFACE,
            FACTORY_INSTNAME, FACTORY_INSTACCESS);
        ClassItem marshs = cf.addPrivateField(STRING_ARRAYTYPE,
            MARSHALLER_ARRAYNAME);
        ClassItem umarshs = cf.addPrivateField(STRING_ARRAYTYPE,
            UNMARSHALLER_ARRAYNAME);
        ClassItem classes = cf.addPrivateField(STRING_ARRAYTYPE,
            CLASSES_ARRAYNAME);
        ClassItem uris = cf.addPrivateField(STRING_ARRAYTYPE, URIS_ARRAYNAME);
        ClassItem prefs = cf.addPrivateField(STRING_ARRAYTYPE,
            PREFIXES_ARRAYNAME);
        ClassItem gnames = cf.addPrivateField(STRING_ARRAYTYPE,
            GNAMES_ARRAYNAME);
        ClassItem guris = cf.addPrivateField(STRING_ARRAYTYPE,
            GURIS_ARRAYNAME);
        ClassItem idnames = cf.addPrivateField(STRING_ARRAYTYPE,
            IDNAMES_ARRAYNAME);
        
        // add the private constructor method
        MethodBuilder mb = new ExceptionMethodBuilder("<init>",
            Type.VOID, new Type[0], cf, Constants.ACC_PRIVATE);
        
        // call the superclass constructor
        mb.appendLoadLocal(0);
        mb.appendCallInit("java.lang.Object", "()V");
        
        // create and fill array of unmarshaller class names
        int count = s_mappedClasses.size();
        int mcnt = m_isMappedDone ? m_mumIndex : count;
        if (m_isInput) {
            mb.appendLoadLocal(0);
            mb.appendLoadConstant(mcnt);
            mb.appendCreateArray("java.lang.String");
            for (int i = 0; i < count; i++) {
                String cname = (String)s_mappedClasses.get(i);
                IMapping map = m_activeContext.getMappingAtLevel(cname);
                if (map != null && map.getUnmarshaller() != null) {
                    mb.appendDUP();
                    mb.appendLoadConstant(i);
                    mb.appendLoadConstant(map.getUnmarshaller().getName());
                    mb.appendAASTORE();
                }
            }
            for (int i = count; i < mcnt; i++) {
                mb.appendDUP();
                mb.appendLoadConstant(i);
                mb.appendLoadConstant
                    ((String)m_extraUnmarshallers.get(i-count));
                mb.appendAASTORE();
            }
            mb.appendPutField(umarshs);
        }
        
        // create and fill array of marshaller class names
        if (m_isOutput) {
            mb.appendLoadLocal(0);
            mb.appendLoadConstant(mcnt);
            mb.appendCreateArray("java.lang.String");
            for (int i = 0; i < count; i++) {
                String cname = (String)s_mappedClasses.get(i);
                IMapping map = m_activeContext.getMappingAtLevel(cname);
                if (map != null && map.getMarshaller() != null) {
                    mb.appendDUP();
                    mb.appendLoadConstant(i);
                    mb.appendLoadConstant(map.getMarshaller().getName());
                    mb.appendAASTORE();
                }
            }
            for (int i = count; i < mcnt; i++) {
                mb.appendDUP();
                mb.appendLoadConstant(i);
                mb.appendLoadConstant((String)m_extraMarshallers.get(i-count));
                mb.appendAASTORE();
            }
            mb.appendPutField(marshs);
        }
        
        // create and fill array of mapped class names
        mb.appendLoadLocal(0);
        mb.appendLoadConstant(mcnt);
        mb.appendCreateArray("java.lang.String");
        for (int i = 0; i < count; i++) {
            mb.appendDUP();
            mb.appendLoadConstant(i);
            mb.appendLoadConstant((String)s_mappedClasses.get(i));
            mb.appendAASTORE();
        }
        for (int i = count; i < mcnt; i++) {
            mb.appendDUP();
            mb.appendLoadConstant(i);
            mb.appendLoadConstant((String)m_extraClasses.get(i-count));
            mb.appendAASTORE();
        }
        mb.appendPutField(classes);
        
        // create and fill array of namespace URIs
        if (m_isOutput) {
            mb.appendLoadLocal(0);
            mb.appendLoadConstant(m_namespaceUris.size());
            mb.appendCreateArray("java.lang.String");
            for (int i = 0; i < m_namespaceUris.size(); i++) {
                mb.appendDUP();
                mb.appendLoadConstant(i);
                mb.appendLoadConstant((String)m_namespaceUris.get(i));
                mb.appendAASTORE();
            }
            mb.appendPutField(uris);
        }
        
        // create and fill array of namespace prefixes
        if (m_isOutput) {
            mb.appendLoadLocal(0);
            mb.appendLoadConstant(m_namespacePrefixes.size());
            mb.appendCreateArray("java.lang.String");
            for (int i = 0; i < m_namespacePrefixes.size(); i++) {
                mb.appendDUP();
                mb.appendLoadConstant(i);
                mb.appendLoadConstant((String)m_namespacePrefixes.get(i));
                mb.appendAASTORE();
            }
            mb.appendPutField(prefs);
        }
        
        // create and fill arrays of globally mapped element names and URIs
        mb.appendLoadLocal(0);
        mb.appendLoadConstant(count);
        mb.appendCreateArray("java.lang.String");
        for (int i = 0; i < count; i++) {
            String cname = (String)s_mappedClasses.get(i);
            IMapping map = m_activeContext.getMappingAtLevel(cname);
            if (map != null) {
                NameDefinition ndef = map.getName();
                if (ndef != null) {
                    mb.appendDUP();
                    mb.appendLoadConstant(i);
                    ndef.genPushName(mb);
                    mb.appendAASTORE();
                }
            }
        }
        mb.appendPutField(gnames);
        mb.appendLoadLocal(0);
        mb.appendLoadConstant(count);
        mb.appendCreateArray("java.lang.String");
        for (int i = 0; i < count; i++) {
            String cname = (String)s_mappedClasses.get(i);
            IMapping map = m_activeContext.getMappingAtLevel(cname);
            if (map != null) {
                NameDefinition ndef = map.getName();
                if (ndef != null) {
                    mb.appendDUP();
                    mb.appendLoadConstant(i);
                    ndef.genPushUri(mb);
                    mb.appendAASTORE();
                }
            }
        }
        mb.appendPutField(guris);
        
        // create and fill array of class names with unique IDs (null if none)
        mb.appendLoadLocal(0);
        if (m_uniqueIds != null && m_uniqueIds.size() > 0) {
            mb.appendLoadConstant(m_uniqueIds.size());
            mb.appendCreateArray("java.lang.String");
            for (int i = 0; i < m_uniqueIds.size(); i++) {
                mb.appendDUP();
                mb.appendLoadConstant(i);
                mb.appendLoadConstant((String)m_uniqueIds.get(i));
                mb.appendAASTORE();
            }
        } else {
            mb.appendACONST_NULL();
        }
        mb.appendPutField(idnames);
        
        // get class names for types (abstract non-base mappings)
        ArrayList tnames = new ArrayList();
        if (m_isForceClasses) {
            for (int i = 0; i < count; i++) {
                String cname = (String)s_mappedClasses.get(i);
                IMapping map = m_activeContext.getMappingAtLevel(cname);
                if (map != null && map.isAbstract() && !map.isBase()) {
                    String tname = map.getTypeName();
                    if (tname == null) {
                        tname = cname;
                    }
                    tnames.add(tname);
                }
            }
        }
        
        // check if map needed for types
        ClassItem tmap = null;
        if (tnames.size() >= TYPEMAP_MINIMUM_SIZE) {
            
            // create field for map
            tmap = cf.addPrivateField(STRINGINT_MAPTYPE, TYPEMAP_NAME);
            
            // initialize with appropriate size
            mb.appendLoadLocal(0);
            mb.appendCreateNew(STRINGINT_MAPTYPE);
            mb.appendDUP();
            mb.appendLoadConstant(tnames.size());
            mb.appendCallInit(STRINGINT_MAPTYPE, STRINGINTINIT_SIGNATURE);
            
            // add all values to map
            for (int i = 0; i < tnames.size(); i++) {
                int index = s_mappedClasses.find(tnames.get(i));
                if (index >= 0) {
                    mb.appendDUP();
                    mb.appendLoadConstant((String)tnames.get(i));
                    mb.appendLoadConstant(index);
                    mb.appendCallVirtual(STRINGINTADD_METHOD,
                        STRINGINTADD_SIGNATURE);
                    mb.appendPOP();
                }
            }
            mb.appendPutField(tmap);
        }
        
        // finish with return from constructor
        mb.appendReturn();
        mb.codeComplete(false);
        mb.addMethod();
        
        // add the public marshalling context construction method
        mb = new ExceptionMethodBuilder(CREATEMARSHAL_METHODNAME,
            ClassItem.typeFromName(MARSHALCONTEXT_INTERFACE), new Type[0], cf,
            Constants.ACC_PUBLIC);
        if (m_isOutput) {
            
            // construct and return marshaller instance
            mb.appendCreateNew(MARSHALCONTEXT_IMPLEMENTATION);
            mb.appendDUP();
            mb.appendLoadLocal(0);
            mb.appendGetField(classes);
            mb.appendLoadLocal(0);
            mb.appendGetField(marshs);
            mb.appendLoadLocal(0);
            mb.appendGetField(uris);
            mb.appendLoadLocal(0);
            mb.appendCallInit(MARSHALCONTEXT_IMPLEMENTATION,
                MARSHALCONTEXTINIT_SIGNATURE);
            mb.appendReturn(MARSHALCONTEXT_IMPLEMENTATION);
                
        } else {
            
            // throw exception for unsupported operation
            mb.appendCreateNew(UNSUPPORTED_EXCEPTION_CLASS);
            mb.appendDUP();
            mb.appendLoadConstant
                ("Binding is input only - cannot create unmarshaller");
            mb.appendCallInit(UNSUPPORTED_EXCEPTION_CLASS,
                MethodBuilder.EXCEPTION_CONSTRUCTOR_SIGNATURE1);
            mb.appendThrow();
            
        }
        mb.codeComplete(false);
        mb.addMethod();
        
        // add the public unmarshalling context construction method
        mb = new ExceptionMethodBuilder(CREATEUNMARSHAL_METHODNAME,
            ClassItem.typeFromName(UNMARSHALCONTEXT_INTERFACE), new Type[0], cf,
            Constants.ACC_PUBLIC);
        if (m_isInput) {
            
            // construct and return unmarshaller instance
            mb.appendCreateNew(UNMARSHALCONTEXT_IMPLEMENTATION);
            mb.appendDUP();
            mb.appendLoadConstant(mcnt);
            mb.appendLoadLocal(0);
            mb.appendGetField(umarshs);
            mb.appendLoadLocal(0);
            mb.appendGetField(guris);
            mb.appendLoadLocal(0);
            mb.appendGetField(gnames);
            mb.appendLoadLocal(0);
            mb.appendGetField(idnames);
            mb.appendLoadLocal(0);
            mb.appendCallInit(UNMARSHALCONTEXT_IMPLEMENTATION,
                 UNMARSHALCONTEXTINIT_SIGNATURE);
            mb.appendReturn(UNMARSHALCONTEXT_IMPLEMENTATION);
            
        } else {
            
            // throw exception for unsupported operation
            mb.appendCreateNew(UNSUPPORTED_EXCEPTION_CLASS);
            mb.appendDUP();
            mb.appendLoadConstant
                ("Binding is output only - cannot create marshaller");
            mb.appendCallInit(UNSUPPORTED_EXCEPTION_CLASS,
                MethodBuilder.EXCEPTION_CONSTRUCTOR_SIGNATURE1);
            mb.appendThrow();
            
        }
        mb.codeComplete(false);
        mb.addMethod();
        
        // add the compiler version access method
        mb = new ExceptionMethodBuilder(GETVERSION_METHODNAME,
            Type.INT, new Type[0], cf, Constants.ACC_PUBLIC);
        mb.appendLoadConstant(IBindingFactory.CURRENT_VERSION_NUMBER);
        mb.appendReturn("int");
        mb.codeComplete(false);
        mb.addMethod();
        
        // add the compiler distribution access method
        mb = new ExceptionMethodBuilder(GETDISTRIB_METHODNAME,
            Type.STRING, new Type[0], cf, Constants.ACC_PUBLIC);
        mb.appendLoadConstant(CURRENT_VERSION_NAME);
        mb.appendReturn(Type.STRING);
        mb.codeComplete(false);
        mb.addMethod();
        
        // add the defined namespace URI array access method
        Type satype = new ArrayType(Type.STRING, 1);
        mb = new ExceptionMethodBuilder(GETDEFINEDNSS_METHODNAME,
            satype, new Type[0], cf, Constants.ACC_PUBLIC);
        mb.appendLoadLocal(0);
        mb.appendGetField(uris);
        mb.appendReturn(satype);
        mb.codeComplete(false);
        mb.addMethod();
        
        // add the defined namespace prefixes array access method
        mb = new ExceptionMethodBuilder(GETDEFINEDPREFS_METHODNAME,
            satype, new Type[0], cf, Constants.ACC_PUBLIC);
        mb.appendLoadLocal(0);
        mb.appendGetField(prefs);
        mb.appendReturn(satype);
        mb.codeComplete(false);
        mb.addMethod();
        
        // add the class name array access method
        mb = new ExceptionMethodBuilder(GETCLASSES_METHODNAME,
            satype, new Type[0], cf, Constants.ACC_PUBLIC);
        mb.appendLoadLocal(0);
        mb.appendGetField(classes);
        mb.appendReturn(satype);
        mb.codeComplete(false);
        mb.addMethod();
        
        // add the element namespace URI array access method
        mb = new ExceptionMethodBuilder(GETELEMENTNSS_METHODNAME,
            satype, new Type[0], cf, Constants.ACC_PUBLIC);
        mb.appendLoadLocal(0);
        mb.appendGetField(guris);
        mb.appendReturn(satype);
        mb.codeComplete(false);
        mb.addMethod();
        
        // add the element name array access method
        mb = new ExceptionMethodBuilder(GETELEMENTNAMES_METHODNAME,
            satype, new Type[0], cf, Constants.ACC_PUBLIC);
        mb.appendLoadLocal(0);
        mb.appendGetField(gnames);
        mb.appendReturn(satype);
        mb.codeComplete(false);
        mb.addMethod();
        
        // add the type mapping index lookup method
        mb = new ExceptionMethodBuilder(GETTYPEINDEX_METHODNAME,
            Type.INT, new Type[] { Type.STRING }, cf, Constants.ACC_PUBLIC);
        if (tnames.size() > 0) {
            if (tmap == null) {
                
                // generate in-line compares for mapping
                for (int i = 0; i < tnames.size(); i++) {
                    int index = s_mappedClasses.find(tnames.get(i));
                    if (index >= 0) {
                        mb.appendLoadLocal(1);
                        mb.appendLoadConstant((String)tnames.get(i));
                        mb.appendCallVirtual("java.lang.String.equals",
                            "(Ljava/lang/Object;)Z");
                        BranchWrapper onfail = mb.appendIFEQ(this);
                        mb.appendLoadConstant(index);
                        mb.appendReturn(Type.INT);
                        mb.targetNext(onfail);
                    }
                }
                mb.appendLoadConstant(-1);
                
            } else {
                
                // use map constructed in initializer
                mb.appendLoadLocal(0);
                mb.appendGetField(tmap);
                mb.appendLoadLocal(1);
                mb.appendCallVirtual(STRINGINTGET_METHOD,
                    STRINGINTGET_SIGNATURE);
                
            }
        } else {
            
            // no types to handle, just always return failure
            mb.appendLoadConstant(-1);
            
        }
        mb.appendReturn(Type.INT);
        mb.codeComplete(false);
        mb.addMethod();
        
        // finish with instance creation method
        mb = new ExceptionMethodBuilder(GETINST_METHODNAME,
            ClassItem.typeFromName(FACTORY_INTERFACE), new Type[0], cf,
            (short)(Constants.ACC_PUBLIC | Constants.ACC_STATIC));
        mb.appendGetStatic(inst);
        BranchWrapper ifdone = mb.appendIFNONNULL(this);
        mb.appendCreateNew(cf.getName());
        mb.appendDUP();
        mb.appendCallInit(cf.getName(), "()V");
        mb.appendPutStatic(inst);
        mb.targetNext(ifdone);
        mb.appendGetStatic(inst);
        mb.appendReturn(FACTORY_INTERFACE);
        mb.codeComplete(false);
        mb.addMethod();
        
        // add factory class to generated registry
        cf = MungedClass.getUniqueSupportClass(cf);
        String link = name;
        if (!name.equals(cf.getName())) {
            link = cf.getName() + '=' + name;
        }
        
        // record the binding factory in each top-level mapped class
        ArrayList maps = m_activeContext.getMappings();
        for (int i = 0; i < maps.size(); i++) {
            IMapping map = (IMapping)maps.get(i);
            if (map instanceof MappingBase) {
                BoundClass bound = ((MappingBase)map).getBoundClass();
                if (bound.getClassFile().isModifiable()) {
                    bound.addFactory(link);
                }
            }
        }
    }

    /**
     * Get indexed binding.
     *
     * @param index number of binding to be returned
     * @return binding at the specified index
     */

    public static BindingDefinition getBinding(int index) {
        return (BindingDefinition)s_bindings.get(index);
    }

    /**
     * Discard cached information and reset in preparation for a new binding
     * run.
     */

    public static void reset() {
        s_bindings = new ArrayList();
        s_mappedClasses = new ArrayMap();
    }
    
    //
    // IContainer interface method definitions

    public boolean isContentOrdered() {
        return true;
    }

    public boolean hasNamespaces() {
        return false;
    }

    public BindingDefinition getBindingRoot() {
        return this;
    }

    public DefinitionContext getDefinitionContext() {
        return m_activeContext;
    }
    
    // DEBUG
    private static byte[] s_blanks =
        "                                                   ".getBytes();
    public static void indent(int depth) {
        if (depth < s_blanks.length) {
            System.out.write(s_blanks, 0, depth);
        } else {
            System.out.print(s_blanks);
        }
    }
    public void print() {
        System.out.println("binding " + m_name + ":");
        m_activeContext.print(1);
    }
}