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

//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.jibx.binding.classes.ClassCache;
//import org.jibx.binding.classes.ClassFile;
import org.jibx.binding.util.StringArray;
//import org.jibx.extras.DocumentComparator;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.EnumSet;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.QName;
//import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * Model component for <b>binding</b> element.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
 
public class BindingElement extends NestingElementBase
{
    /** Enumeration of allowed attribute names */
    public static final StringArray s_allowedAttributes =
        new StringArray(new String[] { "add-constructors", "direction",
        "force-classes", "forwards", "name", "package", "track-source" },
        NestingElementBase.s_allowedAttributes);
    
    //
    // Value set information
    
    public static final int IN_BINDING = 0;
    public static final int OUT_BINDING = 1;
    public static final int BOTH_BINDING = 2;
    
    /*package*/ static final EnumSet s_directionEnum = new EnumSet(IN_BINDING,
        new String[] { "input", "output", "both" });
    
    //
    // Instance data
    
    /** Binding name. */
    private String m_name;
    
    /** Binding direction. */
    private String m_direction;

    /** Input binding flag. */
    private boolean m_isInput;

    /** Output binding flag. */
    private boolean m_isOutput;

    /** Support forward references to IDs flag. */
    private boolean m_isForward;

    /** Generate souce tracking interface flag. */
    private boolean m_isTrackSource;

    /** Generate souce tracking interface flag. */
    private boolean m_isForceClasses;
    
    /** Add default constructors where needed flag. */
    private boolean m_isAddConstructors;

    /** Package for generated context factory. */
    private String m_targetPackage;
    
    /** Base URL for use with relative include paths. */
    private URL m_baseUrl;
    
    /** Set of paths for includes. */
    private HashSet m_includePaths;
    
    /** List of child elements. */
    private ArrayList m_children;
    
    /** Set of class names which can be referenced by ID. */
    private HashSet m_idClassSet;
    
    /**
     * Default constructor.
     */
    public BindingElement() {
        super(BINDING_ELEMENT);
        m_includePaths = new HashSet();
        m_children = new ArrayList();
    }
    
    /**
     * Set binding name.
     * 
     * @param name binding definition name
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * Get binding name.
     * 
     * @return binding definition name
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * Set forward references to IDs be supported in XML.
     *
     * @param forward <code>true</code> if forward references supported,
     * <code>false</code> if not
     */
    public void setForward(boolean forward) {
        m_isForward = forward;
    }
    
    /**
     * Check if forward references to IDs must be supported in XML.
     *
     * @return <code>true</code> if forward references required,
     * <code>false</code> if not
     */
    public boolean isForward() {
        return m_isForward;
    }
    
    /**
     * Set source position tracking for unmarshalling.
     *
     * @param track <code>true</code> if source position tracking enabled,
     * <code>false</code> if not
     */
    public void setTrackSource(boolean track) {
        m_isTrackSource = track;
    }
    
    /**
     * Check if source position tracking enabled for unmarshalling.
     *
     * @return <code>true</code> if source position tracking enabled,
     * <code>false</code> if not
     */
    public boolean isTrackSource() {
        return m_isTrackSource;
    }
    
    /**
     * Set force marshaller/unmarshaller class creation for top-level non-base
     * abstract mappings.
     *
     * @param force <code>true</code> if class generation forced,
     * <code>false</code> if not
     */
    public void setForceClasses(boolean force) {
        m_isForceClasses = force;
    }
    
    /**
     * Check if marshaller/unmarshaller class creation for top-level non-base
     * abstract mappings is forced.
     *
     * @return <code>true</code> if class generation forced,
     * <code>false</code> if not
     */
    public boolean isForceClasses() {
        return m_isForceClasses;
    }
    
    /**
     * Check if default constructor generation is enabled.
     *
     * @return <code>true</code> if default constructor generation enabled,
     * <code>false</code> if not
     */
    public boolean isAddConstructors() {
        return m_isAddConstructors;
    }
    
    /**
     * Set package for generated context factory class.
     * 
     * @param pack generated context factory package
     */
    public void setTargetPackage(String pack) {
        m_targetPackage = pack;
    }
    
    /**
     * Get package for generated context factory class.
     * 
     * @return package for generated context factory
     */
    public String getTargetPackage() {
        return m_targetPackage;
    }
    
    /**
     * Set base URL for relative include paths.
     * 
     * @param base
     */
    public void setBaseUrl(URL base) {
        m_baseUrl = base;
    }
    
    /**
     * Get base URL for relative include paths.
     * 
     * @return
     */
    public URL getBaseUrl() {
        return m_baseUrl;
    }
    
    /**
     * Set binding component applies for marshalling XML.
     *
     * @param out <code>true</code> if binding supports output,
     * <code>false</code> if not
     */
    public void setOutBinding(boolean out) {
        m_isOutput = out;
    }
    
	/**
     * Check if this binding component applies for marshalling XML.
     *
	 * @return <code>true</code> if binding supports output, <code>false</code>
     * if not
	 */
	public boolean isOutBinding() {
		return m_isOutput;
	}
    
    /**
     * Set binding component applies for unmarshalling XML.
     *
     * @param in <code>true</code> if binding supports input,
     * <code>false</code> if not
     */
    public void setInBinding(boolean in) {
        m_isInput = in;
    }
    
	/**
     * Check if this binding component applies for unmarshalling XML.
     *
	 * @return <code>true</code> if binding supports input, <code>false</code>
     * if not
	 */
	public boolean isInBinding() {
		return m_isInput;
	}
    
    /**
     * Add include path to set processed.
     *
     * @return <code>true</code> if new path, <code>false</code> if duplicate
     */
    public boolean addIncludePath(String path) {
        return m_includePaths.add(path);
    }
    
    /**
     * Add a class defined with a ID value. This is used to track the classes
     * with ID values for validating ID references in the binding. If the
     * binding uses global IDs, the actual ID class is added to the table along
     * with all interfaces implemented by the class and all superclasses, since
     * instances of the ID class can be referenced in any of those forms. If the
     * binding does not use global IDs, only the actual ID class is added, since
     * references must be type-specific.
     * 
     * @param clas information for class with ID value
     */
    public void addIdClass(IClass clas) {
        
        // create the set if not already present
        if (m_idClassSet == null) {
            m_idClassSet = new HashSet();
        }
        
        // add the class if not already present
        if (m_idClassSet.add(clas.getName())) {
            
            // new class, add all interfaces if not previously defined
            String[] inames = clas.getInterfaces();
            for (int i = 0; i < inames.length; i++) {
                m_idClassSet.add(inames[i]);
            }
            while (clas != null && m_idClassSet.add(clas.getName())) {
                clas = clas.getSuperClass();
            }
        }
    }
    
    /**
     * Check if a class can be referenced by ID. This just checks if any classes
     * compatible with the reference type are bound with ID values.
     *
     * @param name fully qualified name of class
     * @return <code>true</code> if class is bound with an ID,
     * <code>false</code> if not
     */
    public boolean isIdClass(String name) {
        if (m_idClassSet == null) {
            return false;
        } else {
            return m_idClassSet.contains(name);
        }
    }
    
    /**
     * Add top-level child element.
     * TODO: should be ElementBase argument, but JiBX doesn't allow yet
     * 
     * @param child element to be added as child of this element
     */
    public void addTopChild(Object child) {
        m_children.add(child);
    }
    
    /**
     * Get list of top-level child elements.
     * 
     * @return list of child elements, or <code>null</code> if none
     */
    public ArrayList topChildren() {
        return m_children;
    }
    
    /**
     * Get iterator for top-level child elements.
     * 
     * @return iterator for child elements
     */
    public Iterator topChildIterator() {
        return m_children.iterator();
    }
    
    //
    // Overrides of base class methods.

    /* (non-Javadoc)
     * @see org.jibx.binding.model.ElementBase#hasAttribute()
     */
    public boolean hasAttribute() {
        throw new IllegalStateException
            ("Internal error: method should never be called");
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.ElementBase#hasContent()
     */
    public boolean hasContent() {
        throw new IllegalStateException
            ("Internal error: method should never be called");
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.ElementBase#isOptional()
     */
    public boolean isOptional() {
        throw new IllegalStateException
            ("Internal error: method should never be called");
    }
    
    /**
     * Get default style value for child components. This call is only
     * meaningful after validation.
     * 
     * @return default style value for child components
     */
    public int getDefaultStyle() {
        int style = super.getDefaultStyle();
        if (style < 0) {
            style = NestingAttributes.s_styleEnum.getValue("element");
        }
        return style;
    }
    
    //
    // Validation methods
    
    /**
     * Make sure all attributes are defined.
     *
     * @param uctx unmarshalling context
     * @exception JiBXException on unmarshalling error
     */
    private void preSet(IUnmarshallingContext uctx) throws JiBXException {
        validateAttributes(uctx, s_allowedAttributes);
    }
    
    /**
     * Prevalidate all attributes of element in isolation.
     *
     * @param vctx validation context
     */
    public void prevalidate(ValidationContext vctx) {
        
        // set the direction flags
        int index = -1;
        if (m_direction != null) {
            index = s_directionEnum.getValue(m_direction);
            if (index < 0) {
                vctx.addError("Value \"" + m_direction +
                    "\" is not a valid choice for direction");
            }
        } else {
            index = BOTH_BINDING;
        }
        m_isInput = index == IN_BINDING || index == BOTH_BINDING;
        m_isOutput = index == OUT_BINDING || index == BOTH_BINDING;
        super.prevalidate(vctx);
    }
    
    private static FormatElement buildFormat(String name, String type,
        boolean use, String sname, String dname, String dflt) {
        FormatElement format = new FormatElement();
        format.setLabel(name);
        format.setTypeName(type);
        format.setDefaultFormat(use);
        format.setSerializerName(sname);
        format.setDeserializerName(dname);
        format.setDefaultText(dflt);
        return format;
    }
    
    private void defineBaseFormat(FormatElement format,
        DefinitionContext dctx, ValidationContext vctx) {
        format.prevalidate(vctx);
        format.validate(vctx);
        dctx.addFormat(format, vctx);
    }
    

    /**
     * Run the actual validation of a binding model.
     * 
     * @param vctx context for controlling validation
     */
    public void runValidation(ValidationContext vctx) {
        
        // initially enable both directions for format setup
        m_isInput = true;
        m_isOutput = true;
        
        // create outer definition context
        DefinitionContext dctx = new DefinitionContext(null);
        vctx.setGlobalDefinitions(dctx);
        defineBaseFormat(buildFormat("byte.default", "byte", true,
            "org.jibx.runtime.Utility.serializeByte",
            "org.jibx.runtime.Utility.parseByte", "0"), dctx, vctx);
        defineBaseFormat(buildFormat("char.default", "char", true,
            "org.jibx.runtime.Utility.serializeChar",
            "org.jibx.runtime.Utility.parseChar", "0"), dctx, vctx);
        defineBaseFormat(buildFormat("double.default", "double", true,
            "org.jibx.runtime.Utility.serializeDouble",
            "org.jibx.runtime.Utility.parseDouble", "0.0"), dctx, vctx);
        defineBaseFormat(buildFormat("float.default", "float", true,
            "org.jibx.runtime.Utility.serializeFloat",
            "org.jibx.runtime.Utility.parseFloat", "0.0"), dctx, vctx);
        defineBaseFormat(buildFormat("int.default", "int", true,
            "org.jibx.runtime.Utility.serializeInt",
            "org.jibx.runtime.Utility.parseInt", "0"), dctx, vctx);
        defineBaseFormat(buildFormat("long.default", "long", true,
            "org.jibx.runtime.Utility.serializeLong",
            "org.jibx.runtime.Utility.parseLong", "0"), dctx, vctx);
        defineBaseFormat(buildFormat("short.default", "short", true,
            "org.jibx.runtime.Utility.serializeShort",
            "org.jibx.runtime.Utility.parseShort", "0"), dctx, vctx);
        defineBaseFormat(buildFormat("boolean.default", "boolean", true,
            "org.jibx.runtime.Utility.serializeBoolean",
            "org.jibx.runtime.Utility.parseBoolean", "false"), dctx, vctx);
        defineBaseFormat(buildFormat("Date.default", "java.util.Date", true,
            "org.jibx.runtime.Utility.serializeDateTime",
            "org.jibx.runtime.Utility.deserializeDateTime", null), dctx, vctx);
//#!j2me{
        defineBaseFormat(buildFormat("SqlDate.default", "java.sql.Date",
            true, "org.jibx.runtime.Utility.serializeSqlDate",
            "org.jibx.runtime.Utility.deserializeSqlDate", null), dctx, vctx);
        defineBaseFormat(buildFormat("SqlTime.default", "java.sql.Time",
            true, "org.jibx.runtime.Utility.serializeSqlTime",
            "org.jibx.runtime.Utility.deserializeSqlTime", null), dctx, vctx);
        defineBaseFormat(buildFormat("SqlTimestamp.default",
            "java.sql.Timestamp", true,
            "org.jibx.runtime.Utility.serializeTimestamp",
            "org.jibx.runtime.Utility.deserializeTimestamp", null),
            dctx, vctx);
//#j2me}
        defineBaseFormat(buildFormat("byte-array.default", "byte[]", true,
            "org.jibx.runtime.Utility.serializeBase64",
            "org.jibx.runtime.Utility.deserializeBase64", null), dctx, vctx);
        defineBaseFormat(buildFormat("String.default", "java.lang.String",
            true, null, null, null), dctx, vctx);
        defineBaseFormat(buildFormat("Object.default", "java.lang.Object",
            true, null, null, null), dctx, vctx);
        FormatElement format = buildFormat("char.string", "char", false,
            "org.jibx.runtime.Utility.serializeCharString",
            "org.jibx.runtime.Utility.deserializeCharString", "0");
        format.setDefaultFormat(false);
        format.prevalidate(vctx);
        format.validate(vctx);
        dctx.addFormat(format, vctx);
        NamespaceElement ns = new NamespaceElement();
        ns.setDefaultName("all");
        ns.prevalidate(vctx);
        dctx.addNamespace(ns);
        // TODO: check for errors in basic configuration
        
        // create a definition context for the binding
        setDefinitions(new DefinitionContext(dctx));
        
        // run the actual validation
        vctx.prevalidate(this);
        RegistrationVisitor rvisitor = new RegistrationVisitor(vctx);
        rvisitor.visitTree(this);
        vctx.validate(this);
    }

    /**
     * Read a binding definition to construct binding model.
     * 
     * @param is input stream for reading binding
     * @param fname name of input file (<code>null</code> if unknown)
     * @param vctx validation context used during unmarshalling
     * @return root of binding definition model
     * @throws JiBXException on error in reading binding
     */
    public static BindingElement readBinding(InputStream is, String fname,
        ValidationContext vctx) throws JiBXException {
        
        // look up the binding factory
        IBindingFactory bfact =
            BindingDirectory.getFactory(BindingElement.class);
        
        // unmarshal document to construct objects
        IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
        uctx.setDocument(is, fname, null);
        uctx.pushObject(new UnmarshalWrapper(vctx));
        BindingElement binding = (BindingElement)uctx.unmarshalElement();
        uctx.popObject();
        return binding;
    }
    
    /**
     * Validate a binding definition.
     * 
     * @param name binding definition name
     * @param is input stream for reading binding
     * @param vctx validation context to record problems
     * @return root of binding definition model, or <code>null</code> if error
     * in unmarshalling
     * @throws JiBXException on error in binding XML structure
     */
    public static BindingElement validateBinding(String name, URL path,
        InputStream is, ValidationContext vctx) throws JiBXException {
        
        // construct object model for binding
        BindingElement binding = readBinding(is, name, vctx);
        binding.setBaseUrl(path);
        vctx.setBindingRoot(binding);
        
        // validate the binding definition
        binding.runValidation(vctx);
        
        // list validation errors
        ArrayList probs = vctx.getProblems();
        if (probs.size() > 0) {
            for (int i = 0; i < probs.size(); i++) {
                ValidationProblem prob = (ValidationProblem)probs.get(i);
                System.out.print(prob.getSeverity() >=
                    ValidationProblem.ERROR_LEVEL ? "Error: " : "Warning: ");
                System.out.println(prob.getDescription());
            }
        }
        return binding;
    }

    /**
     * Create a default validation context.
     * 
     * @return new validation context
     */
    public static ValidationContext newValidationContext() {
        IClassLocator locate = new IClassLocator() {
            public IClass getClassInfo(String name) {
                try {
                    return new ClassWrapper(ClassCache.getClassFile(name));
                } catch (JiBXException e) {
                    return null;
                }
            }
        };
        return new ValidationContext(locate);
    }
    
/*    // test runner
    // This code only used in testing, to roundtrip binding definitions
    public static void test(String ipath, String opath, ValidationContext vctx)
        throws Exception {
        
        // validate the binding definition
        FileInputStream is = new FileInputStream(ipath);
        URL url = new URL("file://" + ipath);
        BindingElement binding = validateBinding(ipath, url, is, vctx);
        
        // marshal back out for comparison purposes
        IBindingFactory bfact =
            BindingDirectory.getFactory(BindingElement.class);
        IMarshallingContext mctx = bfact.createMarshallingContext();
        mctx.setIndent(2);
        mctx.marshalDocument(binding, null, null, new FileOutputStream(opath));
        System.out.println("Wrote output binding " + opath);
        
        // compare input document with output document
        DocumentComparator comp = new DocumentComparator(System.err);
        boolean match = comp.compare(new FileReader(ipath),
            new FileReader(opath));
        if (!match) {
            System.err.println("Mismatch from input " + ipath +
                " to output " + opath);
        }
    }
    
    // test runner
    public static void main(String[] args) throws Exception {
        
        // configure class loading
        String[] paths = new String[] { "." };
        ClassCache.setPaths(paths);
        ClassFile.setPaths(paths);
        ValidationContext vctx = newValidationContext();
        
        // process all bindings listed on command line
        for (int i = 0; i < args.length; i++) {
            try {
                String ipath = args[i];
                int split = ipath.lastIndexOf(File.separatorChar);
                String opath = "x" + ipath.substring(split+1);
                test(ipath, opath, vctx);
            } catch (Exception e) {
                System.err.println("Error handling binding " + args[i]);
                e.printStackTrace();
            }
        }
    }   */
    
    /**
     * Inner class as wrapper for binding element on unmarshalling. This
     * provides a handle for passing the validation context, allowing elements
     * to check for problems during unmarshalling.
     */
    public static class UnmarshalWrapper
    {
        private final ValidationContext m_validationContext;
        
        private UnmarshalWrapper(ValidationContext vctx) {
            m_validationContext = vctx;
        }
        
        public ValidationContext getValidation() {
            return m_validationContext;
        }
    }
}
