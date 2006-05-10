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
import java.util.HashMap;

/**
 * Definition context information. This is used to track definitions of items
 * that can be referenced by other items. The contexts are nested, so that names
 * not found in a context may be defined by a containing context. The access
 * methods take this into account, automatically delegating to the containing
 * context (if defined) when a lookup fails.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
public class DefinitionContext
{
    /** Link to containing definition context. */
    private final DefinitionContext m_outerContext;

    /** Namespace used by default at this level for attributes. */
    private NamespaceElement m_attributeDefault;

    /** Namespace used by default at this level for elements. */
    private NamespaceElement m_elementDefault;

    /** Namespaces defined at level (lazy create). */
    private ArrayList m_namespaces;

    /** Mapping from prefix to namespace definition (lazy create). */
    private HashMap m_prefixMap;

    /** Mapping from URI to namespace definition (lazy create). */
    private HashMap m_uriMap;
    
    /** Class hierarchy context for format definitions (lazy create). */
    private ClassHierarchyContext m_formatContext;
    
    /** Class hierarchy context for template definitions (lazy create). */
    private ClassHierarchyContext m_templateContext;
    
    /** Named binding components (lazy create). */
    private HashMap m_namedStructureMap;
    
    /**
     * Constructor.
     * 
     * @param outer containing definition context (<code>null</code> if
     * at root of tree)
     */
    protected DefinitionContext(DefinitionContext outer) {
        m_outerContext = outer;
    }
    
    /**
     * Get containing context.
     * 
     * @return containing context information (<code>null</code> if at root of
     * tree)
     */
    public DefinitionContext getContaining() {
        return m_outerContext;
    }

    /**
     * Get containing format context.
     * 
     * @return innermost containing context for format definitions
     * (<code>null</code> none defined)
     */
    private ClassHierarchyContext getContainingFormatContext() {
        if (m_outerContext == null) {
            return null;
        } else {
            return m_outerContext.getFormatContext();
        }
    }
    
    /**
     * Get current format context.
     * 
     * @return innermost context for format definitions (<code>null</code> none
     * defined)
     */
    private ClassHierarchyContext getFormatContext() {
        if (m_formatContext == null) {
            return getContainingFormatContext();
        } else {
            return m_formatContext;
        }
    }

    /**
     * Get containing template context.
     * 
     * @return innermost containing context for template definitions
     * (<code>null</code> none defined)
     */
    private ClassHierarchyContext getContainingTemplateContext() {
        if (m_outerContext == null) {
            return null;
        } else {
            return m_outerContext.getTemplateContext();
        }
    }
    
    /**
     * Get current template context.
     * 
     * @return innermost context for template definitions (<code>null</code> none
     * defined)
     */
    private ClassHierarchyContext getTemplateContext() {
        if (m_templateContext == null) {
            return getContainingTemplateContext();
        } else {
            return m_templateContext;
        }
    }

    /**
     * Add namespace to set defined at this level.
     *
     * @param def namespace definition element to be added
     * @return problem information, or <code>null</code> if no problem
     */
    public ValidationProblem addNamespace(NamespaceElement def) {
        
        // initialize structures if first namespace definition
        if (m_namespaces == null) {
            m_namespaces = new ArrayList();
            m_prefixMap = new HashMap();
            m_uriMap = new HashMap();
        }

        // check for conflict as default for attributes
        if (def.isAttributeDefault()) {
            if (m_attributeDefault == null) {
                m_attributeDefault = def;
            } else {
                return new ValidationProblem
                    ("Conflicting attribute namespaces", def);
            }
        }

        // check for conflict as default for elements
        if (def.isElementDefault()) {
            if (m_elementDefault == null) {
                m_elementDefault = def;
            } else {
                return new ValidationProblem
                    ("Conflicting element namespaces", def);
            }
        }

        // check for conflict on prefix
        String prefix = def.getPrefix();
        if (m_prefixMap.get(prefix) != null) {
            return new ValidationProblem("Namespace prefix conflict", def);
        }
        
        // check for duplicate definition of same URI
        String uri = def.getUri();
        Object prior = m_uriMap.get(uri);
        if (prior != null && ((NamespaceElement)prior).getPrefix() != null) {
            // TODO: is this needed? multiple prefixes should be allowed
            return null;
        }

        // add only if successful in all tests
        m_namespaces.add(def);
        m_prefixMap.put(prefix, def);
        m_uriMap.put(uri, def);
        return null;
    }

    /**
     * Get namespace definition for element name.
     * TODO: handle multiple prefixes for namespace, proper screening
     *
     * @param name attribute group defining name
     * @return namespace definition, or <code>null</code> if none that matches
     */
    public NamespaceElement getElementNamespace(NameAttributes name) {
        String uri = name.getUri();
        String prefix = name.getPrefix();
        NamespaceElement ns = null;
        if (uri != null) {
            if (m_uriMap != null) {
                ns = (NamespaceElement)m_uriMap.get(uri);
                if (ns != null && prefix != null) {
                    if (!prefix.equals(ns.getPrefix())) {
                        ns = null;
                    }
                }
            }
        } else if (prefix != null) {
            if (m_prefixMap != null) {
                ns = (NamespaceElement)m_prefixMap.get(prefix);
            }
        } else {
            ns = m_elementDefault;
        }
        if (ns == null && m_outerContext != null) {
            ns = m_outerContext.getElementNamespace(name);
        }
        return ns;
    }

    /**
     * Get namespace definition for attribute name.
     * TODO: handle multiple prefixes for namespace, proper screening
     *
     * @param name attribute group defining name
     * @return namespace definition, or <code>null</code> if none that matches
     */
    public NamespaceElement getAttributeNamespace(NameAttributes name) {
        String uri = name.getUri();
        String prefix = name.getPrefix();
        NamespaceElement ns = null;
        if (uri != null) {
            if (m_uriMap != null) {
                ns = (NamespaceElement)m_uriMap.get(uri);
                if (ns != null && prefix != null) {
                    if (!prefix.equals(ns.getPrefix())) {
                        ns = null;
                    }
                }
            }
        } else if (prefix != null) {
            if (m_prefixMap != null) {
                ns = (NamespaceElement)m_prefixMap.get(prefix);
            }
        } else {
            ns = m_attributeDefault;
        }
        if (ns == null && m_outerContext != null) {
            ns = m_outerContext.getAttributeNamespace(name);
        }
        return ns;
    }
    
    /**
     * Add format to set defined at this level.
     *
     * @param def format definition element to be added
     * @param vctx validation context in use
     */
    public void addFormat(FormatElement def, ValidationContext vctx) {
        if (m_formatContext == null) {
            m_formatContext =
                new ClassHierarchyContext(getContainingFormatContext());
        }
        if (def.isDefaultFormat()) {
            IClass clas = def.getType();
            m_formatContext.addTypedComponent(clas, def, vctx);
        }
        if (def.getLabel() != null) {
            m_formatContext.addNamedComponent(def.getLabel(), def, vctx);
        }
    }

    /**
     * Get specific format definition for type. Finds with an exact match
     * on the class name, checking the containing definitions if a format
     * is not found at this level.
     *
     * @param type fully qualified class name to be converted
     * @return conversion definition for class, or <code>null</code> if not
     * found
     */
    public FormatElement getSpecificFormat(String type) {
        ClassHierarchyContext ctx = getFormatContext();
        if (ctx == null) {
            return null;
        } else {
            return (FormatElement)ctx.getSpecificComponent(type);
        }
    }
    
    /**
     * Get named format definition. Finds the format with the supplied
     * name, checking the containing definitions if the format is not found
     * at this level.
     *
     * @param name conversion name to be found
     * @return conversion definition with specified name, or <code>null</code>
     * if no conversion with that name
     */
    public FormatElement getNamedFormat(String name) {
        ClassHierarchyContext ctx = getFormatContext();
        if (ctx == null) {
            return null;
        } else {
            return (FormatElement)ctx.getNamedComponent(name);
        }
    }

    /**
     * Get best format definition for class. Finds the format based on the
     * inheritance hierarchy for the supplied class. If a specific format for
     * the actual class is not found (either in this or a containing level) this
     * returns the most specific superclass format.
     *
     * @param clas information for target conversion class
     * @return conversion definition for class, or <code>null</code> if no
     * compatible conversion defined
     */
    public FormatElement getBestFormat(IClass clas) {
        ClassHierarchyContext ctx = getFormatContext();
        if (ctx == null) {
            return null;
        } else {
            return (FormatElement)ctx.getMostSpecificComponent(clas);
        }
    }

    /**
     * Add template or mapping to set defined at this level.
     *
     * @param def template definition element to be added
     * @param vctx validation context in use
     */
    public void addTemplate(TemplateElementBase def, ValidationContext vctx) {
        if (m_templateContext == null) {
            m_templateContext =
                new ClassHierarchyContext(getContainingTemplateContext());
        }
        if (def.isDefaultTemplate()) {
            IClass clas = def.getHandledClass();
            m_templateContext.addTypedComponent(clas, def, vctx);
        }
        if (def instanceof TemplateElement) {
            TemplateElement tdef = (TemplateElement)def;
            if (tdef.getLabel() != null) {
                m_templateContext.addNamedComponent(tdef.getLabel(), def, vctx);
            }
        } else {
            // TODO: Remove for 2.0
            MappingElement mdef = (MappingElement)def;
            if (mdef.getTypeName() != null) {
                m_templateContext.addNamedComponent(mdef.getTypeName(),
                    def, vctx);
            }
        }
    }

    /**
     * Get specific template definition for type. Finds with an exact match
     * on the class name, checking the containing definitions if a template
     * is not found at this level.
     *
     * @param type fully qualified class name to be converted
     * @return template definition for type, or <code>null</code> if not
     * found
     */
    public TemplateElementBase getSpecificTemplate(String type) {
        ClassHierarchyContext ctx = getTemplateContext();
        if (ctx == null) {
            return null;
        } else {
            return (TemplateElementBase)ctx.getSpecificComponent(type);
        }
    }
    
    /**
     * Get named template definition. Finds the template with the supplied
     * name, checking the containing definitions if the template is not found
     * at this level.
     * TODO: Make this specific to TemplateElement in 2.0
     *
     * @param name conversion name to be found
     * @return template definition for class, or <code>null</code> if no
     * template with that name
     */
    public TemplateElementBase getNamedTemplate(String name) {
        ClassHierarchyContext ctx = getTemplateContext();
        if (ctx == null) {
            return null;
        } else {
            return (TemplateElementBase)ctx.getNamedComponent(name);
        }
    }

    /**
     * Checks if a class is compatible with one or more templates. This checks
     * based on the inheritance hierarchy for the supplied class, looks for the
     * class or interface itself as well as any subclasses or implementations.
     *
     * @param clas information for target class
     * @return <code>true</code> if compatible type, <code>false</code> if not
     */
    public boolean isCompatibleTemplateType(IClass clas) {
        ClassHierarchyContext chctx = getTemplateContext();
        if (chctx == null) {
            return false;
        } else {
            return chctx.isCompatibleType(clas);
        }
    }
    
    /**
     * Add named structure to set defined in this context. For named structures
     * only the definition context associated with the binding element should be
     * used. This is a kludge, but will go away in 2.0.
     *
     * @param def structure definition
     * @return problem information, or <code>null</code> if no problem
     */
    public ValidationProblem addNamedStructure(ContainerElementBase def) {

        // create structure if not already done
        if (m_namedStructureMap == null) {
            m_namedStructureMap = new HashMap();
        }

        // check for conflict on label before adding to definitions
        String label = def.getLabel();
        if (m_namedStructureMap.get(label) == null) {
            m_namedStructureMap.put(label, def);
            return null;
        } else {
            return new ValidationProblem("Duplicate label \"" + label + '"',
                def);
        }
    }

    /**
     * Get labeled structure definition within this context. For named
     * structures only the definition context associated with the binding
     * element should be used. This is a kludge, but will go away in 2.0.
     * 
     * @param label structure definition label
     * @return structure definition with specified label, or <code>null</code>
     * if not defined
     */
    public ContainerElementBase getNamedStructure(String label) {
        if (m_namedStructureMap == null) {
            return null;
        } else {
            return (ContainerElementBase)m_namedStructureMap.get(label);
        }
    }
}