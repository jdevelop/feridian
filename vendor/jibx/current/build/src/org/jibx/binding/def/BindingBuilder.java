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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.bcel.classfile.Utility;
import org.jibx.binding.classes.ClassCache;
import org.jibx.binding.classes.ClassFile;
import org.jibx.binding.classes.ClassItem;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

/**
 * Binding definition constants. This gives the definitions for names and
 * namespaces used by the binding definition file.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public abstract class BindingBuilder
{
    /** Element namespace used for binding definition file. */
    private static final String URI_ELEMENTS = null;

    /** Attribute namespace used for binding definition file. */
    private static final String URI_ATTRIBUTES = null;

    /* Common style attribute. */
    private static final String COMMON_STYLE = "value-style";

    /* Common linkage attributes. */
    private static final String COMMON_AUTOLINK = "auto-link";
    private static final String COMMON_ACCESSLEVEL = "access-level";
    private static final String COMMON_STRIPPREFIX = "strip-prefix";
    private static final String COMMON_STRIPSUFFIX = "strip-suffix";
    private static final String COMMON_NAMESTYLE = "name-style";

    /* Common name attributes. */
    private static final String COMMON_NAME = "name";
    private static final String COMMON_NAMESPACE = "ns";

    /* Common object attributes. */
    private static final String COMMON_FACTORY = "factory";
    private static final String COMMON_PRESET = "pre-set";
    private static final String COMMON_POSTSET = "post-set";
    private static final String COMMON_PREGET = "pre-get";
    private static final String COMMON_MARSHALLER = "marshaller";
    private static final String COMMON_UNMARSHALLER = "unmarshaller";

    /* Common property attributes. */
    private static final String COMMON_FIELD = "field";
    private static final String COMMON_TYPE = "type";
    private static final String COMMON_USAGE = "usage";
    private static final String COMMON_TESTMETHOD = "test-method";
    private static final String COMMON_GETMETHOD = "get-method";
    private static final String COMMON_SETMETHOD = "set-method";

    /* Common string attributes. */
    private static final String COMMON_DEFAULT = "default";
    private static final String COMMON_SERIALIZER = "serializer";
    private static final String COMMON_DESERIALIZER = "deserializer";
    
    /* Common label attributes. */
    private static final String COMMON_LABEL = "label";
    private static final String COMMON_USING = "using";

    /* Common ordered and choice attributes. */
    private static final String COMMON_ORDERED = "ordered";
    private static final String COMMON_CHOICE = "choice";

    /** Definitions for "binding" element use "BINDING" prefix. */
    private static final String BINDING_ELEMENT = "binding";
    private static final String BINDING_NAME = "name";
    private static final String BINDING_DIRECTION = "direction";
    private static final String BINDING_GLOBALID = "global-id";
    private static final String BINDING_FORWARDS = "forwards";
    private static final String BINDING_PACKAGE = "package";
    private static final String BINDING_TRACKING = "track-source";
    private static final String BINDING_FORCE = "force-classes";
    // also COMMON_STYLE, and linkage group

    /** Definitions for "namespace" element use "NAMESPACE" prefix. */
    private static final String NAMESPACE_ELEMENT = "namespace";
    private static final String NAMESPACE_URI = "uri";
    private static final String NAMESPACE_PREFIX = "prefix";
    private static final String NAMESPACE_DEFAULT = "default";

    /** Definitions for "format" element use "FORMAT" prefix. */
    private static final String FORMAT_ELEMENT = "format";
    private static final String FORMAT_NAME = "label";
    private static final String FORMAT_TYPE = "type";
    // also string group

    /** Definitions for "mapping" element use "MAPPING" prefix. */
    private static final String MAPPING_ELEMENT = "mapping";
    private static final String MAPPING_CLASS = "class";
    private static final String MAPPING_ABSTRACT = "abstract";
    private static final String MAPPING_EXTENDS = "extends";
    private static final String MAPPING_TYPENAME = "type-name";
    // also COMMON_STYLE, name, object, ordered, and linkage groups

    /** Definitions for "value" element use "VALUE" prefix. */
    private static final String VALUE_ELEMENT = "value";
    private static final String VALUE_STYLE = "style";
    private static final String VALUE_FORMAT = "format";
    private static final String VALUE_CONSTANT = "constant";
    private static final String VALUE_IDENT = "ident";
    // also name, property, and string groups

    /** Definitions for "structure" element use "STRUCTURE" prefix. */
    private static final String STRUCTURE_ELEMENT = "structure";
    private static final String STRUCTURE_MAPAS = "map-as";
    // also COMMON_STYLE, name, object, ordered, property, and label groups

    /** Definitions for "collection" element use "COLLECTION" prefix. */
    private static final String COLLECTION_ELEMENT = "collection";
    private static final String COLLECTION_LOADMETHOD = "load-method";
    private static final String COLLECTION_SIZEMETHOD = "size-method";
    private static final String COLLECTION_STOREMETHOD = "store-method";
    private static final String COLLECTION_ADDMETHOD = "add-method";
    private static final String COLLECTION_ITERMETHOD = "iter-method";
    private static final String COLLECTION_ITEMTYPE = "item-type";
    // also COMMON_STYLE, name, ordered, property, and label groups
    
    /** Definitions for "include" element use "INCLUDE" prefix. */
    private static final String INCLUDE_ELEMENT = "include";
    private static final String INCLUDE_PATH = "path";
    
    //
    // Value style enumeration.
    
    private static final String[] VALUE_STYLE_NAMES =
    {
        "attribute", "cdata", "element", "text"
    };
    private static final int[] VALUE_STYLE_NUMS =
    {
        ValueChild.ATTRIBUTE_STYLE,
        ValueChild.CDATA_STYLE,
        ValueChild.ELEMENT_STYLE,
        ValueChild.TEXT_STYLE
    };
    
    private static final String[] CONTAINING_STYLE_NAMES =
    {
        "attribute", "element"
    };
    private static final int[] CONTAINING_STYLE_NUMS =
    {
        ValueChild.ATTRIBUTE_STYLE,
        ValueChild.ELEMENT_STYLE
    };
    
    //
    // Enumeration for auto-link types.
    
    /*package*/ static final int LINK_NONE = 0;
    /*package*/ static final int LINK_FIELDS = 1;
    /*package*/ static final int LINK_METHODS = 2;
    
    private static final String[] AUTO_LINK_NAMES =
    {
        "fields", "none", "methods"
    };
    private static final int[] AUTO_LINK_NUMS =
    {
        LINK_FIELDS, LINK_NONE, LINK_METHODS
    };
    
    //
    // Enumeration for access level.
    
    /*package*/ static final int ACC_PRIVATE = 0;
    /*package*/ static final int ACC_PACKAGE = 1;
    /*package*/ static final int ACC_PROTECTED = 2;
    /*package*/ static final int ACC_PUBLIC = 3;
    
    private static final String[] ACCESS_LEVEL_NAMES =
    {
        "package", "private", "protected", "public"
    };
    private static final int[] ACCESS_LEVEL_NUMS =
    {
        ACC_PACKAGE, ACC_PRIVATE, ACC_PROTECTED, ACC_PUBLIC
    };
    
    //
    // Enumeration for name generation styles.
    
    /*package*/ static final int NAME_HYPHENS = 0;
    /*package*/ static final int NAME_MIXED = 1;
    
    private static final String[] NAME_GENERATE_NAMES =
    {
        "hyphens", "mixed-case"
    };
    private static final int[] NAME_GENERATE_NUMS =
    {
        NAME_HYPHENS, NAME_MIXED
    };
    
    //
    // Attributes that imply a component object
    
    private static final String[] COMPONENT_OBJECT_NAMESPACES =
    {
        URI_ATTRIBUTES,
        URI_ATTRIBUTES,
        URI_ATTRIBUTES,
        URI_ATTRIBUTES
    };
    private static final String[] COMPONENT_OBJECT_NAMES =
    {
        COMMON_FACTORY,
        COMMON_PRESET,
        COMMON_POSTSET,
        COMMON_PREGET
    };
    
    //
    // Enumeration for namespace usage.
    
    private static final String[] NAMESPACEACCESS_NAMES =
    {
        "all", "attributes", "elements", "none"
    };
    private static final int[] NAMESPACEACCESS_NUMS =
    {
        NamespaceDefinition.ALLDEFAULT_USAGE,
        NamespaceDefinition.ATTRIBUTES_USAGE,
        NamespaceDefinition.ELEMENTS_USAGE,
        NamespaceDefinition.NODEFAULT_USAGE
    };
    
    //
    // Ident type enumeration.
    
    private static final String[] IDENTTYPE_NAMES =
    {
        "auto", "def", "direct", "ref"
    };
    private static final int[] IDENTTYPE_NUMS =
    {
        ValueChild.AUTO_IDENT,
        ValueChild.DEF_IDENT,
        ValueChild.DIRECT_IDENT,
        ValueChild.REF_IDENT
    };
    
    //
    // Binding direction enumeration.

    private static final int DIRECTION_INPUT = 0;
    private static final int DIRECTION_OUTPUT = 1;
    private static final int DIRECTION_BOTH = 2;
    
    private static final String[] BINDINGDIR_NAMES =
    {
        "both", "input", "output"
    };
    private static final int[] BINDINGDIR_NUMS =
    {
        DIRECTION_BOTH,
        DIRECTION_INPUT,
        DIRECTION_OUTPUT
    };
    
    //
    // Constants for property usage values

    private static final String USAGE_OPTIONAL = "optional";
    private static final String USAGE_REQUIRED = "required";
    
    //
    // Checking and code generation constants
    
    private static final String UNMARSHALLER_INTERFACE =
        "org.jibx.runtime.IUnmarshaller";
    private static final String MARSHALLER_INTERFACE =
        "org.jibx.runtime.IMarshaller";
    private static final String UNMARSHALLER_INTERFACETYPE =
        "Lorg/jibx/runtime/IUnmarshaller;";
    private static final String MARSHALLER_INTERFACETYPE =
        "Lorg/jibx/runtime/IMarshaller;";

    /**
     * Check if attributes supply a name definition.
     *
     * @param ctx unmarshalling context information
     * @return <code>true</code> if attributes define a name,
     * <code>false</code> if not
     */

    private static boolean isNamePresent(UnmarshallingContext ctx) {
        return ctx.attributeText(URI_ATTRIBUTES, COMMON_NAME, null) != null;
    }

    /**
     * Check for property definition present. Just checks the attributes of
     * the current element.
     *
     * @param ctx unmarshalling context information
     */

    private static boolean isPropertyPresent(UnmarshallingContext ctx) {
        return ctx.attributeText(URI_ATTRIBUTES, COMMON_FIELD, null) != null ||
            ctx.attributeText(URI_ATTRIBUTES, COMMON_GETMETHOD, null) != null ||
            ctx.attributeText(URI_ATTRIBUTES, COMMON_SETMETHOD, null) != null ||
            ctx.attributeText(URI_ATTRIBUTES, COMMON_TESTMETHOD, null) != null;
    }

    /**
     * Check if attributes define a direct object reference. Just checks the
     * attributes of the current element.
     *
     * @param ctx unmarshalling context information
     */

    private static boolean isDirectObject(UnmarshallingContext ctx) {
        return ctx.attributeText(URI_ATTRIBUTES,
            COMMON_MARSHALLER, null) != null ||
            ctx.attributeText(URI_ATTRIBUTES,
            COMMON_UNMARSHALLER, null) != null;
    }

    /**
     * Check if attributes define a mapping reference.
     *
     * @param ctx unmarshalling context information
     * @return <code>true</code> if attributes define a mapping reference,
     * <code>false</code> if not
     * @throws JiBXException if error in unmarshalling
     */
    
    private static boolean isMappingRef(UnmarshallingContext ctx)
        throws JiBXException {
        return ctx.hasAttribute(URI_ATTRIBUTES, STRUCTURE_MAPAS);
    }

    /**
     * Check for component object present. Just checks the attributes of the
     * current element, so this is not definitive - there may still be child
     * binding definitions even without attributes.
     *
     * @param ctx unmarshalling context information
     * @throws JiBXException if error in unmarshalling
     */

    private static boolean isObjectBinding(UnmarshallingContext ctx)
        throws JiBXException {
        return ctx.hasAnyAttribute(COMPONENT_OBJECT_NAMESPACES,
            COMPONENT_OBJECT_NAMES);
    }

    /**
     * Unmarshal name definition. This unmarshals directly from attributes of
     * the current element.
     *
     * @param ctx unmarshalling context information
     * @param attr flag for attribute name definition
     * @throws JiBXException if error in unmarshalling
     */

    private static NameDefinition unmarshalName(UnmarshallingContext ctx,
        boolean attr) throws JiBXException {
        String name = ctx.attributeText(URI_ATTRIBUTES, COMMON_NAME);
        String ns = ctx.attributeText(URI_ATTRIBUTES, COMMON_NAMESPACE, null);
        return new NameDefinition(name, ns, attr);
    }

    /**
     * Unmarshal namespace definition.
     *
     * @param ctx unmarshalling context information
     * @throws JiBXException if error in unmarshalling
     */

    private static NamespaceDefinition unmarshalNamespace
        (UnmarshallingContext ctx) throws JiBXException {
        
        // set up the basic information
        String uri = ctx.attributeText(URI_ATTRIBUTES, NAMESPACE_URI);
        String prefix = ctx.attributeText(URI_ATTRIBUTES,
            NAMESPACE_PREFIX, null);
        if ("".equals(prefix)) {
            prefix = null;
        }
        
        // check default usage attribute
        int usage =  ctx.attributeEnumeration(URI_ATTRIBUTES, NAMESPACE_DEFAULT,
            NAMESPACEACCESS_NAMES, NAMESPACEACCESS_NUMS,
            NamespaceDefinition.NODEFAULT_USAGE);
        
        // finish parsing the element
        ctx.parsePastEndTag(URI_ELEMENTS, NAMESPACE_ELEMENT);
        return new NamespaceDefinition(uri, prefix, usage);
    }

    /**
     * Unmarshal string conversion. Unmarshals conversion information directly
     * from the attributes of the current start tag.
     *
     * @param ctx unmarshalling context information
     * @param base conversion used as base for this conversion
     * @param type fully qualified class name of type handled by conversion
     * @throws JiBXException if error in unmarshalling
     */

    private static StringConversion unmarshalStringConversion
        (UnmarshallingContext ctx, StringConversion base, String type)
        throws JiBXException {
        String dflt = ctx.attributeText(URI_ATTRIBUTES, COMMON_DEFAULT, null);
        String ser = ctx.attributeText(URI_ATTRIBUTES, COMMON_SERIALIZER, null);
        String dser = ctx.attributeText(URI_ATTRIBUTES,
            COMMON_DESERIALIZER, null);
        return base.derive(type, ser, dser, dflt);
    }

    /**
     * Check for optional property. Just checks for the attribute and makes sure
     * it has a valid value if present, returning either the default or the
     * defined value.
     *
     * @param ctx unmarshalling context information
     * @return <code>true</code> if attribute present with value "true",
     * <code>false</code> otherwise
     * @throws JiBXException if error in unmarshalling
     */

    private static boolean isOptionalProperty(UnmarshallingContext ctx)
        throws JiBXException {
        boolean opt = false;
        String value = ctx.attributeText(URI_ATTRIBUTES, COMMON_USAGE,
            USAGE_REQUIRED);
        if (USAGE_OPTIONAL.equals(value)) {
            opt = true;
        } else if (!USAGE_REQUIRED.equals(value)) {
            ctx.throwStartTagException("Illegal value for \"" + COMMON_USAGE+
                "\" attribute");
        }
        return opt;
    }

    /**
     * Unmarshal property definition. This unmarshals directly from attributes
     * of the current element.
     *
     * @param ctx unmarshalling context information
     * @param parent containing binding definition structure
     * @param cobj context object information
     * @param opt force optional value flag
     * @throws JiBXException if error in unmarshalling
     */

    private static PropertyDefinition unmarshalProperty
        (UnmarshallingContext ctx, IContainer parent, IContextObj cobj,
        boolean opt) throws JiBXException {

        // read basic attribute values for property definition
        String type = ctx.attributeText(URI_ATTRIBUTES, COMMON_TYPE, null);
        if (!(parent instanceof NestedCollection) && isOptionalProperty(ctx)) {
            opt = true;
        }

        // read and validate method and/or field attribute values
        PropertyDefinition pdef = null;
        try {
            String fname = ctx.attributeText(URI_ATTRIBUTES,
                COMMON_FIELD, null);
            String test = ctx.attributeText(URI_ATTRIBUTES,
                COMMON_TESTMETHOD, null);
            String get = ctx.attributeText(URI_ATTRIBUTES,
                COMMON_GETMETHOD, null);
            String set = ctx.attributeText(URI_ATTRIBUTES,
                COMMON_SETMETHOD, null);
            boolean isthis = fname == null && get == null && set == null;
            pdef = new PropertyDefinition
                (parent, cobj, type, isthis, opt, fname, test, get, set);
            
        } catch (JiBXException ex) {

            // rethrow error message with position information
            ctx.throwStartTagException(ex.getMessage());
        }
        return pdef;
    }

    /**
     * Unmarshal value definition. This handles the complete element supplying
     * the value binding.
     *
     * @param ctx unmarshalling context information
     * @param parent containing binding definition structure
     * @param cobj context object information
     * @param uord unordered collection member flag
     * @param impl implicit value from collection flag
     * @param itype base type for value
     * @throws JiBXException if error in unmarshalling
     */

    private static ValueChild unmarshalValue(UnmarshallingContext ctx,
        IContainer parent, IContextObj cobj, boolean uord, boolean impl,
        String itype) throws JiBXException {
        
        // find the style for this value
        int style = ctx.attributeEnumeration(URI_ATTRIBUTES, VALUE_STYLE,
            VALUE_STYLE_NAMES, VALUE_STYLE_NUMS, parent.getStyleDefault());
            
        // set up the basic structures
        boolean isatt = style == ValueChild.ATTRIBUTE_STYLE;
        NameDefinition name = null;
        if (isatt || style == ValueChild.ELEMENT_STYLE) {
            name = unmarshalName(ctx, isatt);
            name.fixNamespace(parent.getDefinitionContext());
        } else if (isNamePresent(ctx)) {
            ctx.throwStartTagException
                ("Name not allowed for text or CDATA value");
        }
        String constant = ctx.attributeText(URI_ATTRIBUTES,
            VALUE_CONSTANT, null);
        
        // handle property information (unless auto ident or implicit value)
        int ident =  ctx.attributeEnumeration(URI_ATTRIBUTES, VALUE_IDENT,
            IDENTTYPE_NAMES, IDENTTYPE_NUMS, ValueChild.DIRECT_IDENT);
        PropertyDefinition prop = null;
        if (ident == ValueChild.AUTO_IDENT) {
            ctx.throwStartTagException
                ("Automatic id generation not yet supported");
        } else if (impl) {
            String type = ctx.attributeText(URI_ATTRIBUTES, COMMON_TYPE, itype);
            prop = new PropertyDefinition(type, cobj,
                !(parent instanceof NestedCollection) && isOptionalProperty(ctx));
        } else {
            prop = unmarshalProperty(ctx, parent, cobj,
                ctx.hasAttribute(URI_ATTRIBUTES, COMMON_DEFAULT));
            if (constant == null && prop.isThis()) {
                ctx.throwStartTagException("No property for value");
            } else if (prop.isOptional()) {
                if (ident == ValueChild.DEF_IDENT) {
                    ctx.throwStartTagException("Object ID cannot be optional");
                }
            } else if (uord) {
                ctx.throwStartTagException("All items in unordered " +
                    "structure must be optional");
            }
        }
        if (ident != ValueChild.DIRECT_IDENT && uord) {
            ctx.throwStartTagException(VALUE_IDENT +
                " not allowed in unordered structure");
        }
        
        // find the basic converter to use
        StringConversion convert = null;
        String type = (prop == null || constant != null) ?
            "java.lang.String" : prop.getTypeName();
        String format = ctx.attributeText(URI_ATTRIBUTES, VALUE_FORMAT, null);
        DefinitionContext defc = parent.getDefinitionContext();
        if (format == null) {
            
            // no format name, get specific convertor for type or best match
            convert = defc.getSpecificConversion(type);
            if (convert == null) {
                
                // find best match converter for type
                ClassFile target = ClassCache.getClassFile(type);
                convert = defc.getConversion(target);
                
                // generate specific converter for object derivative
                if (convert.getTypeName().equals("java.lang.Object")) {
                    convert = new ObjectStringConversion(type,
                        (ObjectStringConversion)convert);
                }
            }
            
        } else {
            
            // format name supplied, look it up and check compatibility
            convert = defc.getNamedConversion(format);
            if (convert == null) {
                ctx.throwStartTagException
                    ("Unknown format \"" + format + "\"");
            }
            String ctype = convert.getTypeName();
            if (!ClassItem.isAssignable(type, ctype) &&
                !ClassItem.isAssignable(ctype, type)) {
                ctx.throwStartTagException
                    ("Converter type not compatible with value type");
            }
        }
        
        // check for any special conversion handling
        String dflt = ctx.attributeText(URI_ATTRIBUTES, COMMON_DEFAULT, null);
        String ser = ctx.attributeText(URI_ATTRIBUTES, COMMON_SERIALIZER, null);
        String dser = ctx.attributeText(URI_ATTRIBUTES, 
            COMMON_DESERIALIZER, null);
        if (dflt != null || ser != null || dser != null) {
            convert = convert.derive(type, ser, dser, dflt);
        }
        
        // create the instance to be returned
        ValueChild value = new ValueChild(parent, cobj, name, prop, convert,
            style, ident, constant);
        
        // handle identifier property flag
        if (ident == ValueChild.DEF_IDENT || ident == ValueChild.AUTO_IDENT) {
            if (!cobj.setIdChild(value)) {
                ctx.throwStartTagException
                    ("Duplicate ID definition for containing mapping");
            } else if (!"java.lang.String".equals(type)) {
                ctx.throwStartTagException
                    ("ID property must be a String");
            }
        }
        
        // finish with skipping past end tag
        ctx.parsePastEndTag(URI_ELEMENTS, VALUE_ELEMENT);
        return value;
    }

    /**
     * Unmarshal direct object component. Just constructs the component to
     * be returned along with the supporting objects, and verifies that no
     * disallowed properties are present.
     *
     * @param ctx unmarshalling context information
     * @param type fully qualified class name of object type handled
     * @param parent containing binding definition structure
     * @param defc definition context to be used (if separate from parent,
     * otherwise <code>null</code>)
     * @param slot marshaller/unmarshaller slot number
     * @param name element name information (<code>null</code> if no element
     * name)
     * @return constructed direct object component
     * @throws JiBXException if error in unmarshalling
     */
    
    private static DirectObject unmarshalDirectObj(UnmarshallingContext ctx,
        String type, IContainer parent, DefinitionContext defc, int slot,
        NameDefinition name) throws JiBXException {
        
        // define and validate marshaller
        ClassFile mcf = null;
        if (parent.getBindingRoot().isOutput()) {
            String clas = ctx.attributeText(URI_ATTRIBUTES, COMMON_MARSHALLER);
            mcf = ClassCache.getClassFile(clas);
            if (!mcf.isImplements(MARSHALLER_INTERFACETYPE)) {
                ctx.throwStartTagException("Marshaller class " + clas +
                    " does not implement required interface " +
                    MARSHALLER_INTERFACE);
            }
        }
        
        // define and validate unmarshaller
        ClassFile ucf = null;
        if (parent.getBindingRoot().isInput()) {
            String clas = ctx.attributeText(URI_ATTRIBUTES,
                COMMON_UNMARSHALLER);
            ucf = ClassCache.getClassFile(clas);
            if (!ucf.isImplements(UNMARSHALLER_INTERFACETYPE)) {
                ctx.throwStartTagException("Unmarshaller class " + clas +
                    " does not implement required interface " +
                    UNMARSHALLER_INTERFACE);
            }
        }
        
        // make sure none of the prohibited attributes are present
        if (isObjectBinding(ctx)) {
            ctx.throwStartTagException("Other object attributes not " +
                "allowed when using marshaller or unmarshaller");
        } else if (isMappingRef(ctx)) {
            ctx.throwStartTagException("Mapping not allowed when " +
                "using marshaller or unmarshaller");
        } else if (ctx.hasAttribute(URI_ATTRIBUTES, COMMON_USING)) {
            ctx.throwStartTagException(COMMON_USING + 
                " attribute not allowed when using marshaller or unmarshaller");
        }
        
        // return constructed instance
        return new DirectObject(parent, defc,
            ClassCache.getClassFile(type), false, mcf, ucf, slot, name);
    }

    /**
     * Unmarshal mapping reference component. Just constructs the component to
     * be returned along with the supporting objects, and verifies that no
     * disallowed properties are present.
     *
     * @param ctx unmarshalling context information
     * @param parent containing binding definition structure
     * @param objc current object context
     * @param prop property definition
     * @param name reference name definition (only allowed with abstract
     * mappings)
     * @return constructed mapping reference component
     * @throws JiBXException if error in unmarshalling
     */
    
    private static IComponent unmarshalMappingRef(UnmarshallingContext ctx,
        IContainer parent, IContextObj objc, PropertyDefinition prop,
        NameDefinition name) throws JiBXException {
        
        // make sure no forbidden attributes are present
        if (isObjectBinding(ctx)) {
            ctx.throwStartTagException("Other object attributes not " +
                "allowed when using mapping reference");
        } else if (ctx.hasAttribute(URI_ATTRIBUTES, COMMON_USING)) {
            ctx.throwStartTagException(COMMON_USING + 
                " attribute not allowed when using mapping reference");
        }
        
        // build the actual component to be returned
        String type = (prop == null) ? null : prop.getTypeName();
        type = ctx.attributeText(URI_ATTRIBUTES, STRUCTURE_MAPAS, type);
        return new MappingReference(parent, prop, type, objc, name, false);
    }

    /**
     * Unmarshal structure reference component. Just constructs the component to
     * be returned along with the supporting objects, and verifies that no
     * disallowed properties are present.
     *
     * @param ctx unmarshalling context information
     * @param contain containing binding component
     * @param name element name information (<code>null</code> if no element
     * name)
     * @param prop property definition (<code>null</code> if no separate
     * property)
     * @param cobj context object
     * @return constructed structure reference component
     * @throws JiBXException if error in unmarshalling
     */
    
    private static IComponent unmarshalStructureRef(UnmarshallingContext ctx,
        IContainer contain, NameDefinition name, PropertyDefinition prop,
        IContextObj cobj) throws JiBXException {
        
        // make sure no forbidden attributes are present
        if (isObjectBinding(ctx)) {
            ctx.throwStartTagException("Other object attributes not " +
                "allowed when using structure reference");
        }
        
        // build the actual component to be returned
        String ident = ctx.attributeText(URI_ATTRIBUTES, COMMON_USING);
        IComponent comp = new StructureReference(contain, ident, prop,
            name != null, cobj);
        if (name != null) {
            comp = new ElementWrapper(contain.getDefinitionContext(),
                name, comp);
            if (prop != null && prop.isOptional()) {
                ((ElementWrapper)comp).setOptionalNormal(true);
                ((ElementWrapper)comp).setStructureObject(true);
                comp = new OptionalStructureWrapper(comp, prop, true);
                prop.setOptional(false);
            }
        }
        return comp;
    }

    /**
     * Unmarshal child bindings for a nested structure definition.
     *
     * @param ctx unmarshalling context information
     * @param nest nested structure definition
     * @param objc context object definition
     * @param impl property value implicit flag
     * @param itype item type for child components
     * @throws JiBXException if error in unmarshalling
     */

    private static void unmarshalStructureChildren(UnmarshallingContext ctx,
        NestedBase nest, IContextObj objc, boolean impl, String itype)
        throws JiBXException {
        boolean uord = !nest.isContentOrdered();
        while (true) {
            
            // unmarshal next child binding definition
            IComponent comp;
            if (ctx.isAt(URI_ELEMENTS, VALUE_ELEMENT)) {
                ValueChild child =
                    unmarshalValue(ctx, nest, objc, uord, impl, itype);
                comp = child;
            } else if (ctx.isAt(URI_ELEMENTS, STRUCTURE_ELEMENT)) {
                comp = unmarshalStructure(ctx, nest, objc, false, uord, impl);
            } else if (ctx.isAt(URI_ELEMENTS, COLLECTION_ELEMENT)) {
                comp = unmarshalStructure(ctx, nest, objc, true, uord, impl);
            } else {
                break;
            }
            
            // add component to structure
            nest.addComponent(comp);
        }
    }

    /**
     * Unmarshal object binding component. Just constructs the component to
     * be returned along with the supporting objects. This handles both the
     * unmarshalling of attributes, and of nested binding components.
     *
     * @param ctx unmarshalling context information
     * @param parent containing binding definition structure
     * @param objc current object context
     * @param type fully qualified name of object class
     * @return constructed structure reference component
     * @throws JiBXException if error in unmarshalling
     */
    
    private static ObjectBinding unmarshalObjectBinding
        (UnmarshallingContext ctx, IContextObj objc, IContainer parent,
        String type) throws JiBXException {
        
        // set method names from attributes of start tag
        String fact = ctx.attributeText(URI_ATTRIBUTES, COMMON_FACTORY, null);
        String pres = ctx.attributeText(URI_ATTRIBUTES, COMMON_PRESET, null);
        String posts = ctx.attributeText(URI_ATTRIBUTES, COMMON_POSTSET, null);
        String preg = ctx.attributeText(URI_ATTRIBUTES, COMMON_PREGET, null);
        ObjectBinding bind = null;
        try {
            bind = new ObjectBinding(parent, objc, type, fact, pres,
                posts, preg);
        } catch (JiBXException ex) {
            ctx.throwStartTagException(ex.getMessage(), ex);
        }
        return bind;
    }

    /**
     * Unmarshal namespace definitions. Any namespace definitions present are
     * unmarshalled and added to the supplied definition context.
     *
     * @param ctx unmarshalling context information
     * @param defc definition context for defined namespaces
     * @throws JiBXException if error in unmarshalling
     */
    
    private static void unmarshalNamespaces(UnmarshallingContext ctx,
        DefinitionContext defc) throws JiBXException {
        while (ctx.isAt(URI_ELEMENTS, NAMESPACE_ELEMENT)) {
            defc.addNamespace(unmarshalNamespace(ctx));
        }
    }

    /**
     * Unmarshal format definitions. Any format definitions present are
     * unmarshalled and added to the supplied definition context.
     *
     * @param ctx unmarshalling context information
     * @param defc definition context for defined formats
     * @throws JiBXException if error in unmarshalling
     */
    
    private static void unmarshalFormats(UnmarshallingContext ctx,
        DefinitionContext defc) throws JiBXException {
        
        // process all format definitions at level
        while (ctx.isAt(URI_ELEMENTS, FORMAT_ELEMENT)) {
            
            // find the current default format information for type
            String type = ctx.attributeText(URI_ATTRIBUTES, FORMAT_TYPE);
            String sig = Utility.getSignature(type);
            StringConversion base = null;
            if (sig.length() == 1) {
                
                // must be a primitive, check type directly
                base = defc.getSpecificConversion(type);
                if (base == null) {
                    ctx.throwStartTagException("Unsupported \"" +
                        FORMAT_TYPE + "\" value");
                }
                
            } else {
                
                // must be an object type, find best match
                ClassFile cf = ClassCache.getClassFile(type);
                base = defc.getConversion(cf);
                
            }
            
            // unmarshal with defaults provided by existing format
            StringConversion format =
                unmarshalStringConversion(ctx, base, type);
            
            // handle based on presence or absence of name attribute
            String name = ctx.attributeText(URI_ATTRIBUTES, FORMAT_NAME, null);
            if (name == null) {
                defc.setConversion(format);
            } else {
                defc.setNamedConversion(name, format);
            }
            
            // scan past end of definition
            ctx.parsePastEndTag(URI_ELEMENTS, FORMAT_ELEMENT);
        }
    }

    /**
     * Unmarshal mapping definitions. Any mapping definitions present are
     * unmarshalled and added to the supplied definition context.
     *
     * @param ctx unmarshalling context information
     * @param parent containing binding definition structure
     * @param nss extra namespaces to be included in this mapping definition
     * (may be <code>null</code>)
     * @param uord container is unordered structure flag
     * @throws JiBXException if error in unmarshalling
     */
    
    private static void unmarshalMappings(UnmarshallingContext ctx,
        IContainer parent, ArrayList nss, boolean uord) throws JiBXException {
        while (ctx.isAt(URI_ELEMENTS, MAPPING_ELEMENT)) {
            unmarshalMapping(ctx, parent, nss, uord);
        }
    }

    /**
     * Unmarshal subclass instance for structure definition. This handles all
     * combinations of attributes on the start tag, generating the appropriate
     * structure of nested components and other classes to represent the binding
     * information within the current element. This must be called with the
     * parse positioned at the start tag of the element to be unmarshalled.
     * 
     * TODO: At least split this up, or organize a better way to build binding
     *
     * @param ctx unmarshalling context information
     * @param contain containing binding definition structure
     * @param cobj context object information
     * @param coll collection structure flag
     * @param uord container is unordered structure flag
     * @param implic property value implicit flag
     * @return root of component tree constructed from binding
     * @throws JiBXException if error in unmarshalling
     */
    
    public static IComponent unmarshalStructure(UnmarshallingContext ctx,
        IContainer contain, IContextObj cobj, boolean coll, boolean uord,
        boolean implic) throws JiBXException {
        
        // get name definition if supplied (check later to see if valid)
        NameDefinition name = null;
        if (isNamePresent(ctx)) {
            name = unmarshalName(ctx, false);
        }
                
        // check for optional flag on structure
        boolean opt = isOptionalProperty(ctx);
        if (uord && !opt) {
            ctx.throwStartTagException
                ("All items in unordered structure must be optional");
        }
        if (contain instanceof NestedCollection) {
            opt = false;
        }
        
        // check for property definition supplied
        IComponent comp;
        boolean hasprop = isPropertyPresent(ctx);
        boolean thisref = false;
        if (!hasprop) {
            thisref = ctx.hasAttribute(URI_ATTRIBUTES, COMMON_TYPE);
        }
        boolean mapping = isMappingRef(ctx);
        if (hasprop || coll || implic || thisref) {
            
            // set up the property definition to be used
            PropertyDefinition prop = null;
            boolean hasobj = hasprop;
            if (implic) {
                
                // make sure no override of implicit property from collection
                if (hasprop) {
                    ctx.throwStartTagException("Property definition not " +
                        "allowed for collection items");
                } else {
                    String type = ctx.attributeText(URI_ATTRIBUTES,
                        COMMON_TYPE, null);
                    if (type == null) {
                        if (!mapping) {
                            type = "java.lang.Object";
                        }
                    } else {
                        hasobj = true;
                    }
                    prop = new PropertyDefinition(type, cobj, opt);
                }
                
            } else if (hasprop || thisref) {
                prop = unmarshalProperty(ctx, contain, cobj, opt);
            } else {
                prop = new PropertyDefinition(cobj, opt);
            }
            
            // check if using direct object marshalling and unmarshalling
            if (isDirectObject(ctx)) {
                
                // validate and configure direct marshalling and unmarshalling
                comp = new DirectProperty(prop, unmarshalDirectObj(ctx,
                    prop.getTypeName(), contain, null, -1, name));
                
            } else if (mapping) {
                
                // validate and configure reference to mapping in context
                comp = unmarshalMappingRef(ctx, contain, cobj, prop, name);
                
            } else {
                
                // check for object binding needed
                IContextObj icobj = cobj;
                ObjectBinding bind = null;
                boolean typed = false;
                if (implic) {
                    typed = !prop.getTypeName().equals("java.lang.Object");
                } else {
                    typed = !prop.getTypeName().equals
                        (cobj.getBoundClass().getClassName());
                }
                if ((hasobj && !prop.isThis()) || (!hasobj && typed)) {
                    bind = unmarshalObjectBinding(ctx, cobj, contain,
                        prop.getTypeName());
                    icobj = bind;
                }
                
                // validate and configure reference to structure in context
                if (ctx.hasAttribute(URI_ATTRIBUTES, COMMON_USING)) {
                    comp = unmarshalStructureRef(ctx, contain, name,
                        prop, icobj);
                } else {
                    
                    // validate and configure actual binding definition
                    DefinitionContext defc = contain.getDefinitionContext();
                    IComponent top = bind;
                    
                    // check for optional label definition
                    String label = ctx.attributeText(URI_ATTRIBUTES,
                        COMMON_LABEL, null);
                    
                    // set load and store handlers for collection
                    NestedCollection.CollectionLoad load = null;
                    NestedCollection.CollectionStore store = null;
                    String itype = null;
                    if (coll) {
                        
                        // get any method names and type supplied by user
                        String stname = ctx.attributeText(URI_ATTRIBUTES,
                            COLLECTION_STOREMETHOD, null);
                        String aname = ctx.attributeText(URI_ATTRIBUTES,
                            COLLECTION_ADDMETHOD, null);
                        String lname = ctx.attributeText(URI_ATTRIBUTES,
                            COLLECTION_LOADMETHOD, null);
                        String szname = ctx.attributeText(URI_ATTRIBUTES,
                            COLLECTION_SIZEMETHOD, null);
                        String iname = ctx.attributeText(URI_ATTRIBUTES,
                            COLLECTION_ITERMETHOD, null);
                        itype = ctx.attributeText(URI_ATTRIBUTES,
                            COLLECTION_ITEMTYPE, "java.lang.Object");
                        
                        // verify combinations of attributes supplied
                        if ((lname == null || szname == null) &&
                            !(lname == null && szname == null)) {
                            ctx.throwStartTagException(COLLECTION_LOADMETHOD +
                                " and " + COLLECTION_SIZEMETHOD +
                                " attributes must be used together");
                        }
                        if (iname != null && lname != null) {
                            ctx.throwStartTagException(COLLECTION_ITERMETHOD +
                                " and " + COLLECTION_LOADMETHOD +
                                " attributes cannot be used together");
                        }
                        if (aname != null && stname != null) {
                            ctx.throwStartTagException(COLLECTION_ADDMETHOD +
                                " and " + COLLECTION_STOREMETHOD +
                                " attributes cannot be used together");
                        }
                        
                        // set defaults based on collection type
                        ClassFile cf = ClassCache.getClassFile
                            (prop.getTypeName());
                        if (cf.isSuperclass("java.util.Vector")||
                            cf.isSuperclass("java.util.ArrayList")) {
                            if (stname == null && aname == null) {
                                aname = "add";
                            }
                            if (iname == null && lname == null) {
                                lname = "get";
                                szname = "size";
                            }
                        } else if (cf.isImplements("Ljava/util/Collection;")) {
                            if (stname == null && aname == null) {
                                aname = "add";
                            }
                            if (iname == null && lname == null) {
                                iname = "iterator";
                            }
                        } else if (cf.isArray()) {
                            String ptype = prop.getTypeName();
                            itype = ptype.substring(0, ptype.length()-2);
                        }
                        
                        // check binding direction(s)
                        BindingDefinition bdef = contain.getBindingRoot();
                        if (bdef.isInput()) {
                            
                            // define strategy for adding items to collection
                            if (aname != null) {
                                ClassItem meth = cf.getBestMethod(aname,
                                    null, new String[] { itype });
                                if (meth == null) {
                                    ctx.throwStartTagException
                                        ("Add method " + aname +
                                        " not found in collection type " +
                                        cf.getName());
                                }
                                boolean hasval =
                                    !"void".equals(meth.getTypeName());
                                store = new NestedCollection.AddStore(meth,
                                    hasval);
                            } else if (stname != null) {
                                ClassItem meth = cf.getBestMethod(stname,
                                    null, new String[] { "int", itype });
                                if (meth == null) {
                                    ctx.throwStartTagException
                                        ("Indexed store method " + stname +
                                        " not found in collection type " +
                                        cf.getName());
                                }
                                boolean hasval =
                                    !"void".equals(meth.getTypeName());
                                store = new NestedCollection.IndexedStore(meth,
                                    hasval);
                            } else if (cf.isArray()) {
                                store = new NestedCollection.ArrayStore(itype);
                            } else {
                                ctx.throwStartTagException
                                    ("Unknown collection " +
                                    "type with no add or store method defined");
                            }
                            
                        }
                        if (bdef.isOutput()) {
                            
                            // define strategy for loading items from collection
                            if (lname != null) {
                                ClassItem smeth = cf.getMethod(szname, "()I");
                                if (smeth == null) {
                                    ctx.throwStartTagException
                                        ("Size method " + szname +
                                        " not found in collection type " +
                                        cf.getName());
                                }
                                ClassItem lmeth = cf.getBestMethod(lname,
                                    itype, new String[] { "int" });
                                if (lmeth == null) {
                                    ctx.throwStartTagException
                                        ("Load method " + lname +
                                        " not found in collection type " +
                                        cf.getName());
                                }
                                load = new NestedCollection.
                                    IndexedLoad(smeth, lmeth);
                            } else if (iname != null) {
                                String mname = "hasNext";
                                String nname = "next";
                                ClassItem meth = cf.getMethod(iname,
                                    "()Ljava/util/Iterator;");
                                if (meth == null) {
                                    mname = "hasMoreElements";
                                    nname = "nextElement";
                                    meth = cf.getMethod(iname,
                                        "()Ljava/util/Enumeration;");
                                    if (meth == null) {
                                        ctx.throwStartTagException
                                            ("Iterator method " + iname +
                                            " not found in collection type " +
                                            cf.getName());
                                    }
                                }
                                load = new NestedCollection.
                                    IteratorLoad(meth,
                                    "java.util.Iterator." + mname,
                                    "java.util.Iterator." + nname);
                            } else if (cf.isArray()) {
                                load = new NestedCollection.ArrayLoad(itype);
                            } else {
                                ctx.throwStartTagException
                                    ("Unknown collection " +
                                    "type with no load method defined");
                            }
                        }
                        
                    }
                    
                    // unmarshal basics of nested structure
                    NestedBase nest;
                    boolean ordered = ctx.attributeBoolean(URI_ATTRIBUTES,
                        COMMON_ORDERED, true);
                    if (coll) {
                        
                        // create collection definition
                        nest = new NestedCollection(contain, icobj,
                            ordered, itype, load, store);
                        nest.unmarshal(ctx);
                        ctx.parsePastStartTag(URI_ELEMENTS,
                            COLLECTION_ELEMENT);
                            
                    } else {
                        
                        // create structure definition
                        boolean choice = ctx.attributeBoolean(URI_ATTRIBUTES,
                            COMMON_CHOICE, false);
                        nest = new NestedStructure(contain, icobj,
                            ordered, choice, false, hasobj);
                        nest.unmarshal(ctx);
                        ctx.parsePastStartTag(URI_ELEMENTS,
                            STRUCTURE_ELEMENT);
                    }
            
                    // unmarshal child bindings with optional label
                    String ctype = (itype == null) ? "java.lang.Object" : itype;
                    unmarshalFormats(ctx, nest.getDefinitionContext());
                    unmarshalMappings(ctx, contain, null, uord);
                    unmarshalStructureChildren(ctx, nest, icobj,
                        coll | (implic && !hasobj), ctype);
                    if (top == null) {
                        top = nest;
                    }
                        
                    // check for children defined
                    boolean childs = nest.hasContent();
                    boolean addref = false;
                    if (!childs) {
                        if (coll) {
                            
                            // add mapping as only child
                            if (ctype.equals("java.lang.Object")) {
                                nest.addComponent
                                    (new DirectGeneric(nest, null));
                            } else {
                                nest.addComponent(new MappingReference(contain, 
                                    new PropertyDefinition(ctype, cobj, false),
                                    ctype, icobj, null, true));
                            }
                            childs = true;
                            
                        } else if (name != null) {
                            
                            // must be abstract mappping reference, create child
                            addref = true;
                            
                        }
                    }
                    
                    // handle nested children
                    comp = top;
                    if (childs || addref) {
                        
                        // define component property wrapping object binding
                        boolean optprop = hasprop && prop.isOptional();
                        if (bind != null) {
                            boolean skip = name != null && optprop;
                            comp = new ComponentProperty(prop, comp, skip);
                            bind.setWrappedComponent(nest);
                        }
                        
                        // create reference to mapping as special case
                        //  this allows structure with name but no children to
                        //  use abstract mapping
                        if (addref) {
                            PropertyDefinition thisprop =
                                new PropertyDefinition(bind, false);
                            nest.addComponent(new MappingReference
                                (nest, thisprop, comp.getType(), icobj, null,
                                false));
                        }
                        if (name != null) {
                            comp = new ElementWrapper(defc, name, comp);
                            if (bind != null && implic) {
                                if (!hasprop) {
                                    ((ElementWrapper)comp).setDirect(true);
                                }
                                prop.setOptional(false);
                            }
                            if (optprop) {
                                ((ElementWrapper)comp).setOptionalNormal(true);
                                boolean isobj = bind != null;
                                ((ElementWrapper)comp).
                                    setStructureObject(isobj);
                                comp = new OptionalStructureWrapper(comp, prop,
                                    isobj);
                                prop.setOptional(false);
                            } else if (opt && !implic) {
                                ((ElementWrapper)comp).setOptionalNormal(true);
                                comp = new OptionalStructureWrapper(comp, prop,
                                    false);
                                prop.setOptional(false);
                            }
                        }
                        
                    } else {
                            
                        // treat as mapping, with either type or generic
                        String type = prop.getTypeName();
                        if (prop.equals("java.lang.Object")) {
                            comp = new ComponentProperty(prop, new
                                DirectGeneric(contain, null), false);
                        } else {
                            comp = new MappingReference(contain, prop, type,
                                icobj, name, false);
                        }
                    }
                    
                    // set object binding as definition for label
                    if (label != null) {
                        defc.addNamedStructure(label, top);
                    }
                }
            }
            
        } else {
            
            // structure with no separate object, verify no forbidden attributes
            if (isObjectBinding(ctx)) {
                ctx.throwStartTagException("Object attributes not " +
                    "allowed without property definition");
            } else if (isDirectObject(ctx)) {
                ctx.throwStartTagException("Marshaller and unmarshaller not " +
                    "allowed without property definition");
            }
            
            // check for reference to structure defined elsewhere
            if (mapping) {
                
                // handle "this" reference as anonymous property
                PropertyDefinition prop = new PropertyDefinition(cobj, opt);
                    
                // handle reference to defined mapping
                comp = unmarshalMappingRef(ctx, contain, cobj, prop, name);
                implic = true;
                    
            } else if (ctx.hasAttribute(URI_ATTRIBUTES, COMMON_USING)) {
                
                // make sure forbidden attribute not used
                if (ctx.hasAttribute(URI_ATTRIBUTES, COMMON_ORDERED)) {
                    ctx.throwStartTagException(COMMON_ORDERED + " attribute " +
                        " not allowed with " + COMMON_USING + " attribute");
                }
                
                // validate and configure reference to structure in context
                comp = unmarshalStructureRef(ctx, contain, name, null, cobj);
                                
            } else {
                
                // unmarshal children as nested structure
                boolean ordered = ctx.attributeBoolean(URI_ATTRIBUTES,
                    COMMON_ORDERED, true);
                boolean choice = ctx.attributeBoolean(URI_ATTRIBUTES,
                    COMMON_CHOICE, false);
                NestedStructure nest = new NestedStructure(contain, cobj,
                    ordered, choice, false, hasprop);
                nest.unmarshal(ctx);
        
                // unmarshal child bindings with optional label
                String label = ctx.attributeText(URI_ATTRIBUTES,
                    COMMON_LABEL, null);
                ctx.parsePastStartTag(URI_ELEMENTS, STRUCTURE_ELEMENT);
                unmarshalFormats(ctx, nest.getDefinitionContext());
                unmarshalMappings(ctx, contain, null, uord);
                unmarshalStructureChildren(ctx, nest, cobj, false,
                    "java.lang.Object");
                    
                // check for children defined
                DefinitionContext defc = contain.getDefinitionContext();
                if (nest.hasContent()) {
                    
                    // build structure to access children or with elment wrapper
                    if (name == null) {
                        comp = nest;
                    } else {
                        comp = new ElementWrapper(defc, name, nest);
                        if (opt) {
                            ((ElementWrapper)comp).setOptionalNormal(true);
                            ((ElementWrapper)comp).setStructureObject(true);
                        }
                    }
                    if (label != null) {
                        defc.addNamedStructure(label, nest);
                    }
                    
                } else {
                    
                    // make sure there's a name defined
                    if (name == null) {
                        ctx.throwException
                            ("Property, name, or child component required");
                    }
                        
                    // treat as throwaway portion of document
                    comp = new ElementWrapper(defc, name, null);
                    if (opt) {
                        ((ElementWrapper)comp).setOptionalIgnored(true);
                    }
                    
                }
            }
        }
        
        // finish by parsing past end tag
        ctx.parsePastEndTag(URI_ELEMENTS,
            coll ? COLLECTION_ELEMENT : STRUCTURE_ELEMENT);
        return comp;
    }

    /**
     * Unmarshal mapping definition. This handles all combinations of attributes
     * on the start tag, generating the appropriate structure of nested
     * components and other classes to represent the binding information within
     * the current element. This must be called with the parse positioned at the
     * start tag of the element to be unmarshalled.
     *
     * @param ctx unmarshalling context information
     * @param parent containing binding definition structure
     * @param nss extra namespaces to be included in this mapping definition
     * (may be <code>null</code>)
     * @param uord container is unordered structure flag
     * @return mapping definition constructed from binding
     * @throws JiBXException if error in unmarshalling
     */

    public static IMapping unmarshalMapping(UnmarshallingContext ctx,
        IContainer parent, ArrayList nss, boolean uord) throws JiBXException {
        
        // first check for an abstract mapping
        boolean abs = ctx.attributeBoolean(URI_ATTRIBUTES,
            MAPPING_ABSTRACT, false);
        String type = ctx.attributeText(URI_ATTRIBUTES, MAPPING_CLASS);
        
        // get name definition if supplied
        NameDefinition name = null;
        if (isNamePresent(ctx)) {
            name = unmarshalName(ctx, false);
        }
        
        // check if using direct object marshalling and unmarshalling
        IMapping mapping;
        if (isDirectObject(ctx)) {
            
            // check for definition context needed
            DefinitionContext defc = null;
            if (nss != null && nss.size() > 0) {
            
                // add all outer namespaces to context
                defc = new DefinitionContext(parent);
                if (nss != null && nss.size() > 0) {
                    for (int j = 0; j < nss.size(); j++) {
                        defc.addNamespace((NamespaceDefinition)nss.get(j));
                    }
                }
            }
            
            // validate and configure direct marshalling and unmarshalling
            int slot = parent.getBindingRoot().getMappedClassIndex(type);
            mapping = new MappingDirect(parent, type,
                unmarshalDirectObj(ctx, type, parent, defc, slot, name));
                        
        } else {
            
            // not direct mapping, check for missing required name
            if (!abs && name == null) {
                ctx.throwStartTagException("Non-abstract mapping must define " +
                    "an element name");
            }
            
            // check for optional definitions
            String label = ctx.attributeText(URI_ATTRIBUTES,
                COMMON_LABEL, null);
            String tname = ctx.attributeText(URI_ATTRIBUTES,
                MAPPING_TYPENAME, null);
            
            // create definition context for namespaces and formats
            String base = ctx.attributeText(URI_ATTRIBUTES,
                MAPPING_EXTENDS, null);
            ObjectBinding bind = unmarshalObjectBinding(ctx, null,
                parent, type);
            boolean ordered = ctx.attributeBoolean(URI_ATTRIBUTES,
                COMMON_ORDERED, true);
            boolean choice = ctx.attributeBoolean(URI_ATTRIBUTES,
                COMMON_CHOICE, false);
            NestedStructure nest = new NestedStructure(parent, bind,
                ordered, choice, true, true);
            nest.unmarshal(ctx);
            
            // add all outer namespaces to context
            DefinitionContext defc = nest.getDefinitionContext();
            if (nss != null && nss.size() > 0) {
                for (int j = 0; j < nss.size(); j++) {
                    defc.addNamespace((NamespaceDefinition)nss.get(j));
                }
            }
            
            // unmarshal all contained binding information
            ctx.parsePastStartTag(URI_ELEMENTS, MAPPING_ELEMENT);
            unmarshalNamespaces(ctx, nest.getDefinitionContext());
            unmarshalFormats(ctx, nest.getDefinitionContext());
            unmarshalMappings(ctx, nest, null, uord);
            unmarshalStructureChildren(ctx, nest, bind, false,
                "java.lang.Object");
            
            // validate and configure actual binding definition
            bind.setWrappedComponent(nest);
            mapping = new MappingDefinition(parent, nest.getDefinitionContext(),
                type, name, tname, abs, base, bind);
        
            // set label if defined
            if (label != null) {
                defc.addNamedStructure(label, bind);
            }
        }
        
        // finish by adding mapping and parsing past end tag
        parent.getDefinitionContext().addMapping(mapping);
        ctx.parsePastEndTag(URI_ELEMENTS, MAPPING_ELEMENT);
        return mapping;
    }

    /**
     * Unmarshal included binding. This handles the actual include element along
     * with the actual included binding. The current implementation allows for
     * nested includes, but requires that all the included bindings use
     * compatible settings for the attributes of the root element, and only
     * allows mapping elements as children of the included bindings (no
     * namespace or format elements).
     *
     * @param ctx unmarshalling context information
     * @param bdef binding defintion at root of includes
     * @param root base URL for binding, or <code>null</code> if unknown
     * @param nss list of namespaces defined
     * @param paths set of binding paths processed
     * @throws JiBXException if error in unmarshalling
     */

    public static void unmarshalInclude(UnmarshallingContext ctx,
        BindingDefinition bdef, URL root, ArrayList nss, HashSet paths)
        throws JiBXException {
        
        // make sure path hasn't already been processed
        ctx.parseToStartTag(URI_ELEMENTS, INCLUDE_ELEMENT);
        String path = ctx.attributeText(URI_ATTRIBUTES, INCLUDE_PATH);
        URL url;
        try {
            if (root == null) {
                url = new URL(path);
            } else {
                url = new URL(root, path);
            }
        } catch (MalformedURLException e) {
            throw new JiBXException("Unable to handle include path " + path, e);
        }
        String fpath = url.toExternalForm();
        if (paths.add(fpath)) {
            try {
                
                // access the included binding as input stream
                UnmarshallingContext ictx = new UnmarshallingContext();
                ictx.setDocument(url.openStream(), null);
                
                // check for compatible binding direction flags
                ictx.parseToStartTag(URI_ELEMENTS, BINDING_ELEMENT);
                if (ictx.hasAttribute(URI_ATTRIBUTES, BINDING_DIRECTION)) {
                    int dir = ictx.attributeEnumeration(URI_ATTRIBUTES,
                        BINDING_DIRECTION, BINDINGDIR_NAMES, BINDINGDIR_NUMS,
                        DIRECTION_BOTH);
                    boolean compat = true;
                    switch (dir) {
                        case DIRECTION_BOTH:
                            if (!bdef.isInput() || !bdef.isOutput()) {
                                compat = false;
                            }
                            break;
                        case DIRECTION_INPUT:
                            if (!bdef.isInput() || bdef.isOutput()) {
                                compat = false;
                            }
                            break;
                        case DIRECTION_OUTPUT:
                            if (bdef.isInput() || !bdef.isOutput()) {
                                compat = false;
                            }
                            break;
                    }
                    if (!compat) {
                        throw new JiBXException("Incompatible binding direction " +
                            "option for included binding " + path);
                    }
                }
                
                // check other attribute values
                if (ictx.hasAttribute(URI_ATTRIBUTES, BINDING_PACKAGE)) {
                    throw new JiBXException(BINDING_PACKAGE +
                        " attribute not allowed on included binding " + path);
                }
                if (ictx.hasAttribute(URI_ATTRIBUTES, BINDING_FORWARDS)) {
                    throw new JiBXException(BINDING_FORWARDS +
                        " attribute not allowed on included binding " + path);
                }
                if (ictx.hasAttribute(URI_ATTRIBUTES, BINDING_TRACKING)) {
                    throw new JiBXException(BINDING_TRACKING +
                        " attribute not allowed on included binding " + path);
                }
                if (ictx.hasAttribute(URI_ATTRIBUTES, BINDING_FORCE)) {
                    throw new JiBXException(BINDING_TRACKING +
                        " attribute not allowed on included binding " + path);
                }
                
                // check for nested includes
                ictx.parsePastStartTag(URI_ELEMENTS, BINDING_ELEMENT);
                while (ictx.isAt(URI_ELEMENTS, INCLUDE_ELEMENT)) {
                    unmarshalInclude(ictx, bdef, url, nss, paths);
                }
                
                // process all mappings defined in included binding
                unmarshalMappings(ictx, bdef, nss, false);
                
            } catch (IOException e) {
                throw new JiBXException
                    ("Error accessing included binding with path " + path, e);
            }
        }
        
        // finish by skipping past end of tag in main binding
        ctx.parsePastEndTag(URI_ELEMENTS, INCLUDE_ELEMENT);
    }
    
    /**
     * Unmarshal binding definition. This handles the entire binding definition
     * document.
     *
     * @param ctx unmarshalling context information
     * @param name default name for binding
     * @param root base URL for binding, or <code>null</code> if unknown
     * @throws JiBXException if error in unmarshalling
     */

    public static BindingDefinition unmarshalBindingDefinition
        (UnmarshallingContext ctx, String name, URL root)
        throws JiBXException {
        
        // start by reading optional binding name
        ctx.parseToStartTag(URI_ELEMENTS, BINDING_ELEMENT);
        name = ctx.attributeText(URI_ATTRIBUTES, BINDING_NAME, name);
        
        // set the binding direction flags
        int dir = ctx.attributeEnumeration(URI_ATTRIBUTES, BINDING_DIRECTION,
            BINDINGDIR_NAMES, BINDINGDIR_NUMS, DIRECTION_BOTH);
        boolean ibind = dir == DIRECTION_BOTH || dir == DIRECTION_INPUT;
        boolean obind = dir == DIRECTION_BOTH || dir == DIRECTION_OUTPUT;
        
        // read other attribute values
        String tpack = ctx.attributeText(URI_ATTRIBUTES, BINDING_PACKAGE, null);
        boolean glob = ctx.attributeBoolean(URI_ATTRIBUTES,
            BINDING_GLOBALID, true);
        boolean forward = ctx.attributeBoolean(URI_ATTRIBUTES,
            BINDING_FORWARDS, true);
        boolean track = ctx.attributeBoolean(URI_ATTRIBUTES,
            BINDING_TRACKING, false);
        boolean force = ctx.attributeBoolean(URI_ATTRIBUTES,
            BINDING_FORCE, false);
        
        // create actual binding instance
        BindingDefinition bdef = new BindingDefinition(name, ibind, obind, tpack,
            glob, forward, track, force);
        bdef.unmarshal(ctx);
        
        // unmarshal namespaces defined under root
        ctx.parsePastStartTag(URI_ELEMENTS, BINDING_ELEMENT);
        ArrayList nss = new ArrayList();
        while (ctx.isAt(URI_ELEMENTS, NAMESPACE_ELEMENT)) {
            nss.add(unmarshalNamespace(ctx));
        }
        
        // process any included binding definitions
        HashSet paths = new HashSet();
        if (root != null) {
            paths.add(root.toExternalForm());
        }
        while (ctx.isAt(URI_ELEMENTS, INCLUDE_ELEMENT)) {
            unmarshalInclude(ctx, bdef, root, nss, paths);
        }
        
        // finish with common handling for elements which can be nested
        unmarshalFormats(ctx, bdef.getDefinitionContext());
        unmarshalMappings(ctx, bdef, nss, false);
        ctx.parsePastEndTag(URI_ELEMENTS, BINDING_ELEMENT);
        return bdef;
    }
    
    /**
     * Base class for containers. This just handles unmarshalling and checking
     * the values of attributes used by all containers. The container class
     * should set the appropriate default values for all these attributes in its
     * constructor, using <code>-1</code> (for <code>int</code> values) and
     * <code>null</code> (for <code>String</code> values) if the default is to
     * simply use setting inherited from a containing component. The binding
     * definition root object must always define actual values as the defaults,
     * since otherwise the code will fall off the end of the chain of ancestors.
     */
    
    /*package*/ static class ContainerBase {
        
        /** Containing binding component. */
        protected IContainer m_container;
        
        /** Default style for value expression. */
        protected int m_styleDefault;
    
        /** Auto-link style for default mappings. */
        protected int m_autoLink;
    
        /** Access level for default mappings. */
        protected int m_accessLevel;
    
        /** Prefix text to be stripped from names. */
        protected String m_stripPrefix;
    
        /** Suffix text to be stripped from names. */
        protected String m_stripSuffix;
    
        /** Style used for generating element or attribute names. */
        protected int m_nameStyle;

        /**
         * Constructor.
         *
         * @param parent containing binding definition context
         */

        public ContainerBase(IContainer parent) {
            m_container = parent;
        }

        /**
         * Unmarshal common container attributes.
         *
         * @param ctx unmarshalling context information
         * @throws JiBXException if error in unmarshalling
         */

        public void unmarshal(UnmarshallingContext ctx) throws JiBXException {
            m_styleDefault = ctx.attributeEnumeration(URI_ATTRIBUTES,
                COMMON_STYLE, CONTAINING_STYLE_NAMES, CONTAINING_STYLE_NUMS,
                m_styleDefault);
            m_autoLink = ctx.attributeEnumeration(URI_ATTRIBUTES,
                COMMON_AUTOLINK, AUTO_LINK_NAMES, AUTO_LINK_NUMS, m_autoLink);
            m_accessLevel = ctx.attributeEnumeration(URI_ATTRIBUTES,
                COMMON_ACCESSLEVEL, ACCESS_LEVEL_NAMES, ACCESS_LEVEL_NUMS,
                m_accessLevel);
            m_stripPrefix = ctx.attributeText(URI_ATTRIBUTES,
                COMMON_STRIPPREFIX, m_stripPrefix);
            m_stripSuffix = ctx.attributeText(URI_ATTRIBUTES,
                COMMON_STRIPSUFFIX, m_stripSuffix);
            m_nameStyle = ctx.attributeEnumeration(URI_ATTRIBUTES,
                COMMON_NAMESTYLE, NAME_GENERATE_NAMES, NAME_GENERATE_NUMS,
                m_nameStyle);
        }
    
        //
        // IContainer interface method definitions (partial list)

        public int getStyleDefault() {
            if (m_styleDefault >= 0) {
                return m_styleDefault;
            } else {
                return m_container.getStyleDefault();
            }
        }
    }
}