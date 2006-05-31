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

/**
 * Model visitor for handling item registration. This works with the {@link
 * org.jibx.binding.model.ValidationContext} class to handle registration of
 * items which can be referenced by name or by function (such as ID values
 * within an object structure). The only items of this type which are not
 * handled by this visitor are <b>format</b> definitions. The formats need to be
 * accessed during prevalidation, so they're registered during that pass.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
public class RegistrationVisitor extends ModelVisitor
{
    /** Validation context running this visitor. */
    private final ValidationContext m_context;
    
    /**
     * Constructor.
     * 
     * @param vctx validation context that will run this visitor
     */
    public RegistrationVisitor(ValidationContext vctx) {
        m_context = vctx;
    }
    
    /**
     * Visit binding model tree to handle registration.
     * 
     * @param root node of tree to be visited
     */
    public void visitTree(ElementBase root) {
        
        // first handle adding references to table
        m_context.tourTree(root, this);
        
        // then handle mapping extension linkages with separate pass
        m_context.tourTree(root, new ModelVisitor() {
            
            // expand mapping elements in case child mappings are present
            public boolean visit(MappingElement node) {
                node.validateExtension(m_context);
                return true;
            }
            
            // don't bother expanding structure elements
            public boolean visit(StructureElementBase node) {
                return false;
            }
            
        });
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.ModelVisitor#visit(org.jibx.binding.model.ContainerElementBase)
     */
    public boolean visit(ContainerElementBase node) {
        if (node.getLabel() != null) {
            ValidationProblem problem = m_context.getBindingRoot().
                getDefinitions().addNamedStructure(node);
            if (problem != null) {
                m_context.addProblem(problem);
            }
        }
        return super.visit(node);
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.ModelVisitor#visit(org.jibx.binding.model.MappingElement)
     */
    public boolean visit(MappingElement node) {
        m_context.getCurrentDefinitions().addTemplate(node, m_context);
        return super.visit(node);
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.ModelVisitor#visit(org.jibx.binding.model.NamespaceElement)
     */
    public boolean visit(NamespaceElement node) {
        ValidationProblem problem =
            m_context.getCurrentDefinitions().addNamespace(node);
        if (problem != null) {
            m_context.addProblem(problem);
        }
        return super.visit(node);
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.ModelVisitor#visit(org.jibx.binding.model.TemplateElement)
     */
    public boolean visit(TemplateElement node) {
        m_context.getCurrentDefinitions().addTemplate(node, m_context);
        return super.visit(node);
    }
}