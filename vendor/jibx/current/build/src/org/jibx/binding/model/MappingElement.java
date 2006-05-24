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

import java.util.ArrayList;

import org.jibx.binding.util.StringArray;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.QName;

/**
 * Model component for <b>mapping</b> element of binding definition.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
 
public class MappingElement extends TemplateElementBase
{
    /** Enumeration of allowed attribute names */
    public static final StringArray s_allowedAttributes =
        new StringArray(new String[]
            { "abstract", "class", "extends", "type-name" },
        new StringArray(NameAttributes.s_allowedAttributes,
        ContainerElementBase.s_allowedAttributes));
    // TODO: move "mapping" to TemplateElementBase in 2.0
    
	/** Abstract mapping flag. */
	private boolean m_isAbstract;
	
    /** Name attributes information for nesting. */
    private NameAttributes m_nameAttrs;
    
	/** Name of mapped class extended by this mapping. */
    private String m_extendsName;
    
    /** Type qualified name (defaults to fully-qualified class name in
     no-namespace namespace). */
    private QName m_typeQName;

    /** Mapping extended by this mapping. */
    private MappingElement m_extendsMapping;
	
	/**
	 * Default constructor.
	 */
	public MappingElement() {
        super(MAPPING_ELEMENT);
        m_nameAttrs = new NameAttributes();
        m_children = new ArrayList();
    }
    
    /**
     * Check for abstract mapping.
     * 
     * @return <code>true</code> if abstract, <code>false</code> if not
     */
    public boolean isAbstract() {
        return m_isAbstract;
    }
    
    /**
     * Set abstract mapping.
     * 
     * @param abs <code>true</code> if abstract, <code>false</code> if not
     */
    public void setAbstract(boolean abs) {
        m_isAbstract = abs;
    }
    
    /**
     * Get type name.
     * 
     * @return type name
     */
    public String getTypeName() {
        return (m_typeQName == null) ? null : m_typeQName.toString();
    }
    
    /**
     * Set type name.
     * 
     * @param name type name
     */
    public void setTypeName(String name) {
        m_typeQName = new QName(name);
    }
    
    /**
     * Get type qualified name.
     * 
     * @return type qualified name
     */
    public QName getTypeQName() {
        return m_typeQName;
    }
    
    /**
     * Set type qualified name.
     * 
     * @param qname type qualified name
     */
    public void setTypeQName(QName qname) {
        m_typeQName = qname;
    }
    
    /**
     * Set name of mapped class extended by this one.
     * 
     * @param name
     */
    public void setExtendsName(String name) {
        m_extendsName = name;
    }

    /**
     * Get name of mapped class extended by this one.
     * 
     * @return
     */
    public String getExtendsName() {
        return m_extendsName;
    }

    /**
     * Get mapping extended by this one.
     * 
     * @return mapping extended by this one
     */
    public MappingElement getExtendsMapping() {
        return m_extendsMapping;
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.TemplateElementBase#isDefaultTemplate()
     */
    public boolean isDefaultTemplate() {
        return m_typeQName == null;
    }
    
    //
    // Name attribute delegate methods

    /**
     * Get name.
     * 
     * @return name text
     */
    public String getName() {
        return m_nameAttrs.getName();
    }
    
    /**
     * Set name.
     * 
     * @param name text for name
     */
    public void setName(String name) {
        m_nameAttrs.setName(name);
    }

    /**
     * Get specified namespace URI.
     * 
     * @return namespace URI (<code>null</code> if not set)
     */
    public String getUri() {
        return m_nameAttrs.getUri();
    }

    /**
     * Set namespace URI.
     * 
     * @param uri namespace URI (<code>null</code> if not set)
     */
    public void setUri(String uri) {
        m_nameAttrs.setUri(uri);
    }

    /**
     * Get specified namespace prefix.
     * 
     * @return namespace prefix (<code>null</code> if not set)
     */
    public String getPrefix() {
        return m_nameAttrs.getPrefix();
    }

    /**
     * Set namespace prefix.
     * 
     * @param prefix namespace prefix (<code>null</code> if not set)
     */
    public void setPrefix(String prefix) {
        m_nameAttrs.setPrefix(prefix);
    }
    
    /**
     * Get effective namespace information. This call is only meaningful after
     * validation.
     * 
     * @return effective namespace information
     */
    public NamespaceElement getNamespace() {
        return m_nameAttrs.getNamespace();
    }
    
    //
    // Validation methods
    
    /**
     * JiBX access method to set mapping type name as qualified name.
     * 
     * @param text mapping name text (<code>null</code> if none)
     * @param ictx unmarshalling context
     * @throws JiBXException on deserialization error
     */
    private void setQualifiedTypeName(String text, IUnmarshallingContext ictx)
        throws JiBXException {
        m_typeQName = QName.deserialize(text, ictx);
    }
    
    /**
     * JiBX access method to get mapping type name as qualified name.
     * 
     * @param ictx marshalling context
     * @return mapping type name text (<code>null</code> if none)
     * @throws JiBXException on deserialization error
     */
    private String getQualifiedTypeName(IMarshallingContext ictx)
        throws JiBXException {
        return QName.serialize(m_typeQName, ictx);
    }
    
    /**
     * Make sure all attributes are defined.
     *
     * @param uctx unmarshalling context
     * @exception JiBXException on unmarshalling error
     */
    private void preSet(IUnmarshallingContext uctx) throws JiBXException {
        validateAttributes(uctx, s_allowedAttributes);
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.ElementBase#prevalidate(org.jibx.binding.model.ValidationContext)
     */
    public void prevalidate(ValidationContext vctx) {
        m_nameAttrs.prevalidate(vctx);
        if (m_isAbstract) {
            if (m_typeQName != null && m_nameAttrs.getName() != null) {
                vctx.addError("Type name cannot be used with an element name");
            }
            if (m_nameAttrs.getName() != null) {
                vctx.addWarning
                    ("Using a name on an abstract mapping is deprecated");
            }
        } else {
            if (m_nameAttrs.getName() == null &&
                ((vctx.isInBinding() && getUnmarshallerName() == null) ||
                (vctx.isOutBinding() && getMarshallerName() == null))) {
                vctx.addError
                    ("Non-abstract mapping must define an element name");
            }
            if (m_typeQName != null) {
                vctx.addError
                    ("Type name can only be used with an abstract mapping");
            }
        }
        super.prevalidate(vctx);
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.ElementBase#validate(org.jibx.binding.model.ValidationContext)
     */
    public void validate(ValidationContext vctx) {
        m_nameAttrs.validate(vctx);
        
        // check use of text values in children of structure with name
        SequenceVisitor visitor = new SequenceVisitor(null, vctx);
        TreeContext tctx = vctx.getChildContext();
        tctx.tourTree(this, visitor);
        
        // make sure we can construct instances of concrete mapped class
        if (!m_isAbstract) {
            verifyConstruction(vctx, getHandledClass());
        }
        
        // check for class that probably should extend another mapped class
        if (m_extendsName == null && !m_isAbstract) {
            
            // first check ancestor classes for ones with specific mappings
            IClass sclass = getHandledClass();
            DefinitionContext defc = vctx.getCurrentDefinitions();
            while ((sclass = sclass.getSuperClass()) != null) {
                TemplateElementBase stmpl =
                    defc.getSpecificTemplate(sclass.getName());
                if (stmpl != null) {
                    if (stmpl instanceof MappingElement) {
                        MappingElement smap = (MappingElement)stmpl;
                        if (smap.getExtensionTypes().size() > 0) {
                            vctx.addWarning("Class " + getClassName() +
                                " extends " + smap.getClassName() +
                                " which has a base mapping with extensions," +
                                " but this mapping does not extend it");
                        }
                    }
                    break;
                }
                
            }
        
            // next check interfaces for ones with specific mappings
            String[] interfaces = getHandledClass().getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                String iname = interfaces[i];
                TemplateElementBase itmpl = defc.getSpecificTemplate(iname);
                if (itmpl != null) {
                    if (itmpl instanceof MappingElement) {
                        MappingElement smap = (MappingElement)itmpl;
                        if (smap.getExtensionTypes().size() > 0) {
                            vctx.addWarning("Class " + getClassName() +
                                " implements " + iname +
                                " which has a base mapping with extensions," +
                                " but this mapping does not extend it");
                        }
                    }
                    break;
                }
                
            }
        }
        
        // run base class validation
        super.validate(vctx);
    }

    /**
     * Special validation method to link extension mappings to base mappings.
     * This is called as a special step following registration, so that the
     * normal validation pass can make use of the linkage information.
     * 
     * @param vctx validation context
     */
    public void validateExtension(ValidationContext vctx) {
        if (m_extendsName != null) {
            
            // find the base class mapping
            TemplateElementBase base =
                vctx.getDefinitions().getSpecificTemplate(m_extendsName);
            if (!(base instanceof MappingElement)) {
                vctx.addFatal("No mapping found for class " + m_extendsName);
            } else {
                
                // check base class using custom marshaller/unmarshaller
                MappingElement mbase = (MappingElement)base;
                if (mbase.getMarshaller() != null ||
                    mbase.getUnmarshaller() != null) {
                    vctx.addError("Cannot extend a mapping using custom " +
                        "marshaller/unmarshaller");
                }
                
                // add reference to base class
                mbase.addExtensionType(this);
            }
        }
    }
}