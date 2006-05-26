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

/**
 * Model component for elements that can contain detailed binding information in
 * the form of nested child components. Elements of this type include
 * <b>mapping</b>, <b>template</b>, <b>structure</b>, and <b>collection</b>
 * elements.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
public abstract class ContainerElementBase extends NestingElementBase
{
    /** Enumeration of allowed attribute names */
    public static final StringArray s_allowedAttributes =
        new StringArray(NestingElementBase.s_allowedAttributes,
        new StringArray(ObjectAttributes.s_allowedAttributes,
        StructureAttributes.s_allowedAttributes));
    
    /** Object attributes information for nesting. */
    private ObjectAttributes m_objectAttrs;
    
    /** Structure attributes information for nesting. */
    private StructureAttributes m_structureAttrs;

    /** Label for this structure definition. */
    private String m_label;

    /** Label for structure to be used as definition. */
    private String m_using;
    
    /** Child component that contributes an ID (<code>null</code> if none). */
    private IComponent m_idChild;
    
    /** Child components defining content (created during validation, contains
     subset of child components defining element or character data content). */
    private ArrayList m_contentComponents;
    
    /** Child components defining attributes (created during validation,
     contains subset of child components defining attributes). */
    private ArrayList m_attributeComponents;
	
	/**
	 * Constructor.
     * 
     * @param type element type code
	 */
    protected ContainerElementBase(int type) {
        super(type);
        m_objectAttrs = new ObjectAttributes();
        m_structureAttrs = new StructureAttributes();
	}

    /**
     * Get label for this definition.
     * 
     * @return label for this definition
     */
    public String getLabel() {
        return m_label;
    }

    /**
     * Set label for this definition.
     * 
     * @param label label for this definition
     */
    public void setLabel(String label) {
        m_label = label;
    }

    /**
     * Get label for definition to be used.
     * 
     * @return label for definition to be used
     */
    public String getUsing() {
        return m_using;
    }

    /**
     * Set label for definition to be used.
     * 
     * @param label label for definition to be used
     */
    public void setUsing(String label) {
        m_using = label;
    }

    /**
     * Get list of child components contributing content items to this
     * container element. This call is only meaningful after validation.
     * 
     * @return list of child binding components defining content items
     */
    public ArrayList getContentComponents() {
        return m_contentComponents == null ?
            EmptyList.INSTANCE : m_contentComponents;
    }

    /**
     * Get list of child components contributing attribute items to this
     * container element. This call is only meaningful after validation.
     * 
     * @return list of child binding components defining attribute items
     */
    public ArrayList getAttributeComponents() {
        return m_attributeComponents == null ?
            EmptyList.INSTANCE : m_attributeComponents;
    }
    
    /**
     * Check if this container defines a context object.
     * 
     * @return <code>true</code> if defines context object,
     * <code>false</code> if not
     */
    public abstract boolean hasObject();
    
    /**
     * Get class linked to binding element. This call is only meaningful after
     * validation.
     *
     * @return information for class linked by binding
     */
    public abstract IClass getObjectType();

    /**
     * Set ID property child. Used to set the ID property associated with a
     * particular class instance. There can only be at most one child ID
     * property for each actual object instance.
     *
     * @param child child defining the ID property
     * @return <code>true</code> if successful, <code>false</code> if ID
     * already defined
     */
    public final void setIdChild(IComponent child, ValidationContext vctx) {
        if (m_idChild == null) {
            m_idChild = child;
            vctx.getBindingRoot().addIdClass(getObjectType());
        } else {
            vctx.addError("Only one child ID property allowed for an object " +
                "- " + ValidationProblem.componentDescription(m_idChild) +
                " and " + ValidationProblem.componentDescription(child) +
                " refer to the same object");
        }
    }

    /**
     * Get ID property child. 
     */
    public IComponent getId() {
        return m_idChild;
    }
    
    //
    // Object attribute delegate methods
    
    /**
     * Get factory method name.
     * 
     * @return fully-qualified factory class and method name (or
     * <code>null</code> if none)
     */
    public String getFactoryName() {
        return m_objectAttrs.getFactoryName();
    }
    
    /**
     * Get factory method information. This call is only meaningful after
     * validation.
     * 
     * @return factory method information (or <code>null</code> if none)
     */
    public IClassItem getFactory() {
        return m_objectAttrs.getFactory();
    }
    
    /**
     * Set factory method name.
     * 
     * @param name fully qualified class and method name for object factory
     */
    public void setFactoryName(String name) {
        m_objectAttrs.setFactoryName(name);
    }
    
    /**
     * Get pre-set method name.
     * 
     * @return pre-set method name (or <code>null</code> if none)
     */
    public String getPresetName() {
        return m_objectAttrs.getPresetName();
    }
    
    /**
     * Get pre-set method information. This call is only meaningful after
     * validation.
     * 
     * @return pre-set method information (or <code>null</code> if none)
     */
    public IClassItem getPreset() {
        return m_objectAttrs.getPreset();
    }
    
    /**
     * Set pre-set method name.
     * 
     * @param name member method name to be called before unmarshalling
     */
    public void setPresetName(String name) {
        m_objectAttrs.setPresetName(name);
    }
    
    /**
     * Get post-set method name.
     * 
     * @return post-set method name (or <code>null</code> if none)
     */
    public String getPostsetName() {
        return m_objectAttrs.getPostsetName();
    }
    
    /**
     * Get post-set method information. This call is only meaningful after
     * validation.
     * 
     * @return post-set method information (or <code>null</code> if none)
     */
    public IClassItem getPostset() {
        return m_objectAttrs.getPostset();
    }
    
    /**
     * Set post-set method name.
     * 
     * @param name member method name to be called after unmarshalling
     */
    public void setPostsetName(String name) {
        m_objectAttrs.setPostsetName(name);
    }
    
    /**
     * Get pre-get method name.
     * 
     * @return pre-get method name (or <code>null</code> if none)
     */
    public String getPregetName() {
        return m_objectAttrs.getPregetName();
    }
    
    /**
     * Get pre-get method information. This call is only meaningful after
     * validation.
     * 
     * @return pre-get method information (or <code>null</code> if none)
     */
    public IClassItem getPreget() {
        return m_objectAttrs.getPreget();
    }
    
    /**
     * Set pre-get method name.
     * 
     * @param name member method name to be called before marshalling
     */
    public void setPreget(String name) {
        m_objectAttrs.setPreget(name);
    }
    
    /**
     * Get marshaller class name.
     * 
     * @return marshaller class name (or <code>null</code> if none)
     */
    public String getMarshallerName() {
        return m_objectAttrs.getMarshallerName();
    }
    
    /**
     * Get marshaller class information. This call is only meaningful after
     * validation.
     * 
     * @return class information for marshaller (or <code>null</code> if none)
     */
    public IClass getMarshaller() {
        return m_objectAttrs.getMarshaller();
    }
    
    /**
     * Set marshaller class name.
     * 
     * @param name class name to be used for marshalling
     */
    public void setMarshallerName(String name) {
        m_objectAttrs.setMarshallerName(name);
    }
    
    /**
     * Get unmarshaller class name.
     * 
     * @return unmarshaller class name (or <code>null</code> if none)
     */
    public String getUnmarshallerName() {
        return m_objectAttrs.getUnmarshallerName();
    }
    
    /**
     * Get unmarshaller class information. This call is only meaningful after
     * validation.
     * 
     * @return class information for unmarshaller (or <code>null</code> if none)
     */
    public IClass getUnmarshaller() {
        return m_objectAttrs.getUnmarshaller();
    }
    
    /**
     * Set unmarshaller class name.
     * 
     * @param name class name to be used for unmarshalling
     */
    public void setUnmarshallerName(String name) {
        m_objectAttrs.setUnmarshallerName(name);
    }
    
    /**
     * Check if nillable object.
     * 
     * @return nillable flag
     */
    public boolean isNillable() {
        return m_objectAttrs.isNillable();
    }

    /**
     * Set nillable flag.
     * 
     * @param nillable flag
     */
    public void setNillable(boolean nillable) {
        m_objectAttrs.setNillable(nillable);
    }
    
    /**
     * Get type to be used for creating new instance.
     * 
     * @return class name for type to be created (or <code>null</code> if none)
     */
    public String getCreateType() {
        return m_objectAttrs.getCreateType();
    }
    
    /**
     * Get new instance creation class information. This method is only usable
     * after a call to {@link #validate}.
     * 
     * @return class information for type to be created (or <code>null</code> if
     * none)
     */
    public IClass getCreateClass() {
        return m_objectAttrs.getCreateClass();
    }
    
    /**
     * Set new instance type class name.
     * 
     * @param name class name to be used for creating new instance
     */
    public void setCreateType(String name) {
        m_objectAttrs.setCreateType(name);
    }
    
    //
    // Structure attribute delegate methods

    /**
     * Check if child components are ordered.
     *
     * @return <code>true</code> if ordered, <code>false</code> if not
     */
    public boolean isOrdered() {
        return m_structureAttrs.isOrdered();
    }
    
    /**
     * Set child components ordered flag.
     * 
     * @param ordered <code>true</code> if ordered, <code>false</code> if not
     */
    public void setOrdered(boolean ordered) {
        m_structureAttrs.setOrdered(ordered);
    }

    /**
     * Check if child components are a choice.
     *
     * @return <code>true</code> if choice, <code>false</code> if not
     */
    public boolean isChoice() {
        return m_structureAttrs.isChoice();
    }
    
    /**
     * Set child components choice flag.
     * 
     * @param ordered <code>true</code> if choice, <code>false</code> if not
     */
    public void setChoice(boolean choice) {
        m_structureAttrs.setChoice(choice);
    }
    
    //
    // Validation methods.

    /**
     * Check that there's a way to construct an instance of an object class for
     * input bindings. This can be a factory method, an unmarshaller, a
     * no-argument constructor already defined in the class, or a modifiable
     * class with constructor generation enabled. If a create-type is specified,
     * this is used in place of the declared type. The call always succeeds if
     * the binding is output-only.
     * 
     * @param vctx validation context
     * @param type constructed object type
     */
    protected void verifyConstruction(ValidationContext vctx, IClass type) {
        if (vctx.isInBinding() && getFactory() == null &&
            getUnmarshaller() == null) {
            IClass create = getCreateClass();
            if (create != null) {
                if (create.isAssignable(type)) {
                    type = create;
                } else {
                    vctx.addError("Specified create-type '" + create.getName() +
                        "' is not compatible with type '" +
                        type.getName() + '\'');
                }
            }
            if (!type.getName().endsWith("[]") &&
                type.getInitializerMethod("()V") == null) {
                BindingElement binding = vctx.getBindingRoot();
                if (binding.isAddConstructors()) {
                    if (!type.isModifiable()) {
                        vctx.addError("Need no-argument constructor or " +
                            "factory method for unmodifiable class " +
                            type.getName());
                    }
                } else {
                    vctx.addError("Need no-argument constructor or " +
                        "factory method for class " + type.getName());
                }
            }
        }
    }

    /**
     * Check that child components are of types compatible with the container
     * object type. This method may call itself recursively to process the
     * children of child components which do not themselves set a type. It's not
     * used directly, but is here for use by subclasses.
     * 
     * @param vctx validation context
     * @param type structure object type
     * @param children list of child components to be checked
     */
    protected void checkCompatibleChildren(ValidationContext vctx, IClass type,
        ArrayList children) {
        for (int i = 0; i < children.size(); i++) {
            ElementBase child = (ElementBase)children.get(i);
            boolean expand = true;
            if (child instanceof IComponent) {
                IComponent comp = (IComponent)child;
                IClass ctype = comp.getType();
                if (comp instanceof ContainerElementBase) {
                    ContainerElementBase contain = (ContainerElementBase)comp;
                    expand = !contain.hasObject();
                }
                if (comp.isImplicit()) {
                    if (!type.isAssignable(ctype)) {
                        vctx.addFatal
                            ("References to structure object must have " +
                            "compatible types: " + type.getName() +
                            " cannot be used as " + ctype.getName(), child);
                    }
                }
            }
            if (expand && child instanceof NestingElementBase) {
                checkCompatibleChildren(vctx, type,
                    ((NestingElementBase)child).children());
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.ElementBase#prevalidate(org.jibx.binding.model.ValidationContext)
     */
    public void prevalidate(ValidationContext vctx) {
        m_objectAttrs.prevalidate(vctx);
        m_structureAttrs.prevalidate(vctx);
        if (m_using != null && children().size() > 0) {
            vctx.addFatal("Child elements not allowed with using attribute");
        }
        super.prevalidate(vctx);
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.ElementBase#validate(org.jibx.binding.model.ValidationContext)
     */
    public void validate(ValidationContext vctx) {
        super.validate(vctx);
        m_objectAttrs.validate(vctx);
        m_structureAttrs.validate(vctx);
        ArrayList childs = children();
        if (childs == null || childs.size() == 0) {
            m_attributeComponents = EmptyList.INSTANCE;
            m_contentComponents = EmptyList.INSTANCE;
        } else {
            m_attributeComponents = new ArrayList();
            m_contentComponents = new ArrayList();
            for (int i = 0; i < childs.size(); i++) {
                Object child = childs.get(i);
                if (child instanceof IComponent) {
                    IComponent comp = (IComponent)child;
                    if (comp.hasAttribute()) {
                        m_attributeComponents.add(child);
                    }
                    if (comp.hasContent()) {
                        m_contentComponents.add(child);
                    }
                }
            }
            if (isChoice() && m_attributeComponents.size() > 0) {
                vctx.addError("Attributes cannot be included in choice");
            }
        }
    }
}