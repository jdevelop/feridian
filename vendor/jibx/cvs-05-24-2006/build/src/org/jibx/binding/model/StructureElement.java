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
 * Model component for <b>structure</b> element of binding definition.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
 
public class StructureElement extends StructureElementBase
{
    /** Enumeration of allowed attribute names */
    public static final StringArray s_allowedAttributes =
        new StringArray(new String[] { "map-as" },
        StructureElementBase.s_allowedAttributes);
    
    /** Mapping type name to use for this object. */
    private String m_mapAsName;
    
    /** Mapping qualified type name to use for this object. */
    private QName m_mapAsQName;
    
    /** Flag for structure has a concrete mapping, possibly indeterminant. */
    private boolean m_hasMappingName;
    
    /** Binding to use for this object. */
    private TemplateElementBase m_mapAsMapping;
    
    /**
	 * Default constructor.
	 */
	public StructureElement() {
        super(STRUCTURE_ELEMENT);
    }
    
    /**
     * Get name of mapping type.
     * 
     * @return mapping type name (or <code>null</code> if none)
     */
    public String getMapAsName() {
        return m_mapAsName;
    }
    
    /**
     * Set name of mapping type. This method changes the qualified name to
     * match the mapping type.
     * 
     * @param name mapping type name (or <code>null</code> if none)
     */
    public void setMapAsName(String name) {
        m_mapAsName = name;
        m_mapAsQName = (name == null) ? null : new QName(name);
    }
    
    /**
     * Get qualified name of mapping type.
     * 
     * @return mapping qualified type name (or <code>null</code> if none)
     */
    public QName getMapAsQName() {
        return m_mapAsQName;
    }
    
    /**
     * Set qualified name of mapping type. This method changes the mapping name
     * to match the qualified name.
     * 
     * @param name mapping qualified type name (or <code>null</code> if none)
     */
    public void setMapAsQName(QName name) {
        m_mapAsQName = name;
        m_mapAsName = (name == null) ? null : name.toString();
    }
    
    /**
     * Get specified type mapping. This call is only meaningful after
     * validation.
     * 
     * @return specified type mapping (or <code>null</code> if none)
     */
    public TemplateElementBase getMapAsMapping() {
        return m_mapAsMapping;
    }
    
    //
    // Overrides of base class methods
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.IComponent#hasName()
     */
    public boolean hasName() {
        if (m_mapAsMapping instanceof MappingElement) {
            if (((MappingElement)m_mapAsMapping).getName() != null) {
                return true;
            }
        } else if (m_hasMappingName) {
            return true;
        }
        return super.hasName();
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.IComponent#getName()
     */
    public String getName() {
        if (m_mapAsMapping instanceof MappingElement) {
            String name = ((MappingElement)m_mapAsMapping).getName();
            if (name != null) {
                return name;
            }
        } else if (m_hasMappingName) {
            return "#" + getType().getName();
        }
        return super.getName();
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.IComponent#getUri()
     */
    public String getUri() {
        if (m_mapAsMapping instanceof MappingElement) {
            String uri = ((MappingElement)m_mapAsMapping).getUri();
            if (uri != null) {
                return uri;
            }
        }
        return super.getUri();
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.IComponent#hasAttribute()
     */
    public boolean hasAttribute() {
        if (hasName()) {
            return false;
        } else {
            return super.hasAttribute();
        }
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.IComponent#hasContent()
     */
    public boolean hasContent() {
        if (hasName()) {
            return true;
        } else {
            return super.hasContent();
        }
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.IComponent#getType()
     */
    public IClass getType() {
        if (m_mapAsMapping == null) {
            return super.getType();
        } else {
            return m_mapAsMapping.getHandledClass();
        }
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
    private void setQualifiedMapAs(String text, IUnmarshallingContext ictx)
        throws JiBXException {
        m_mapAsName = text;
        m_mapAsQName = QName.deserialize(text, ictx);
    }
    
    /**
     * JiBX access method to get mapping type name as qualified name.
     * 
     * @param ictx marshalling context
     * @return mapping type name text (<code>null</code> if none)
     * @throws JiBXException on deserialization error
     */
    private String getQualifiedMapAs(IMarshallingContext ictx)
        throws JiBXException {
        return QName.serialize(m_mapAsQName, ictx);
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
     * @see org.jibx.binding.model.ElementBase#validate(org.jibx.binding.model.ValidationContext)
     */
    public void validate(ValidationContext vctx) {
        
        // check if there's a mapping if used without children
        DefinitionContext dctx = vctx.getDefinitions();
        if (children().size() == 0) {
            if (m_mapAsQName == null) {
                
                // make sure not just a name, allowed for skipped element
                if (hasProperty() || getDeclaredType() != null) {
                    
                    // see if this is using implicit marshaller/unmarshaller
                    if ((vctx.isInBinding() && getUnmarshallerName() == null) ||
                        (vctx.isOutBinding() && getMarshallerName() == null)) {
                        if (getUsing() == null) {
                            IClass type = getType();
                            String tname = type.getName();
                            if (!"java.lang.Object".equals(tname)) {
                                m_mapAsMapping = dctx.getNamedTemplate(tname);
                                m_hasMappingName = true;
                                if (m_mapAsMapping == null) {
                                    checkMappingReference(vctx, dctx, type);
                                }
                            }
                        }
                    }
                }
                
            } else {
                
                // find mapping by type name or class name
                TemplateElementBase base =
                    dctx.getNamedTemplate(m_mapAsQName.toString());
                if (base == null) {
                    base = dctx.getSpecificTemplate(m_mapAsName);
                    if (base == null) {
                        vctx.addFatal("No mapping with type name " +
                            m_mapAsQName.toString());
                    }
                }
                if (base != null) {
                    
                    // make sure type is compatible
                    IClass type = getType();
                    if (!type.isAssignable(base.getHandledClass()) &&
                        !base.getHandledClass().isAssignable(type)) {
                        vctx.addError("Object type " + type.getName() +
                            " is incompatible with binding for class " +
                            base.getClassName());
                    }
                    m_mapAsMapping = base;
                    
                    // set flag for mapping with name
                    m_hasMappingName = base instanceof MappingElement &&
                        !((MappingElement)base).isAbstract();
                }
                
            }
        } else if (m_mapAsName != null) {
            vctx.addError("map-as attribute cannot be used with children");
        }
        IClass type = getType();
        if (type != null) {
            
            // check each child component for compatible type
            ArrayList children = children();
            if (hasProperty() || getDeclaredType() != null) {
                checkCompatibleChildren(vctx, type, children);
            }
            
            // check for only set-method supplied
            if (!vctx.isOutBinding() && getField() == null &&
                getGet() == null && getSet() != null) {
                
                // no way to handle both elements and attributes
                if (hasAttribute() && hasContent()) {
                    vctx.addError("Need way to load existing object instance " +
                        "to support combined attribute and element values");
                } else {
                    vctx.addWarning("No way to load prior value - " +
                        "new instance will be created on each unmarshalling");
                }
            }
        }
        super.validate(vctx);
    }

    /**
     * Validate mapping reference.
     * 
     * @param vctx validation context
     * @param dctx definition context
     * @param type referenced type
     */
    private void checkMappingReference(ValidationContext vctx,
        DefinitionContext dctx, IClass type) {
        
        // see if there's a mapping specific to the reference type
        String tname = type.getName();
        TemplateElementBase match = dctx.getSpecificTemplate(tname);
        if (match != null) {
            
            // mapping match, check name usage on non-abstract or base mapping
            if (match instanceof MappingElement && super.hasName()) {
                MappingElement base = (MappingElement)match;
                if (!base.isAbstract()) {
                    vctx.addError("name attribute not allowed on concrete " +
                        "mapping reference");
                } else if (base.getExtensionTypes().size() > 0) {
                    vctx.addError("name attribute not allowed on reference " +
                        "to mapping with extensions");
                }
            }
            
        } else if (!dctx.isCompatibleTemplateType(type)) {
            vctx.addFatal("No compatible mapping defined for type " + tname);
        }
    }
}