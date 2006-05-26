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

import org.jibx.binding.classes.*;
import org.jibx.runtime.JiBXException;

/**
 * Property reference with binding defined by component. This handles loading
 * and storing the property value, calling the wrapped component methods for
 * everything else.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class ComponentProperty extends PassThroughComponent
{
    /** Property definition. */
    private final PropertyDefinition m_property;

    /** Skip marshalling code tests flag. */
    private boolean m_skipMarshal;

    /**
     * Constructor.
     *
     * @param prop actual property definition
     * @param impl component that defines marshalling and unmarshalling
     * @param skip flag for marshalling code tests to be skipped
     */

    public ComponentProperty(PropertyDefinition prop, IComponent impl,
        boolean skip) {
        super(impl);
        m_property = prop;
        m_skipMarshal = skip;
    }
    
    /**
     * Set flag for skipping marshalling presence test code generation.
     * 
     * @param skip <code>true</code> if skipping, <code>false</code> if not
     */
    public void setSkipping(boolean skip) {
        m_skipMarshal = skip;
    }
    
    /**
     * Get the property information. This is a kludge used by the ElementWrapper
     * code to store a <code>null</code> value directly to the property when
     * unmarshalling a missing or xsi:nil element.
     * 
     * @return property information
     */
    public PropertyDefinition getProperty() {
        return m_property;
    }
    
    //
    // IComponent interface method definitions (overrides of defaults)

    public boolean isOptional() {
        return m_property.isOptional();
    }

    public void genAttributeUnmarshal(ContextMethodBuilder mb)
        throws JiBXException {
        
        // start by generating code to load owning object so can finish by
        //  storing to property
        if (!m_property.isImplicit() && !m_property.isThis()) {
            mb.loadObject();
        }
        BranchWrapper ifpres = null;
        BranchWrapper tosave = null;
        if (m_property.isOptional()) {
            
            // generate code to check presence for the case of an optional item,
            //  with branch if so; if not present, set a null value with branch
            //  to be targeted at property store.
            m_component.genAttrPresentTest(mb);
            ifpres = mb.appendIFNE(this);
            mb.appendACONST_NULL();
            tosave = mb.appendUnconditionalBranch(this);
        }
            
        // generate unmarshalling code for not optional, or optional and
        //  present; get existing instance or create a new one and handle
        //  attribute unmarshalling
        mb.targetNext(ifpres);
        if (m_property.isImplicit()) {
            m_component.genNewInstance(mb);
        } else if (!m_property.isThis()) {
            
            // load current value, cast, copy, and test for non-null
            mb.loadObject();
            m_property.genLoad(mb);
            mb.appendCreateCast(m_property.getGetValueType(),
                m_component.getType());
            mb.appendDUP();
            BranchWrapper haveinst = mb.appendIFNONNULL(this);
            
            // current value null, pop copy and create a new instance
            mb.appendPOP();
            m_component.genNewInstance(mb);
            mb.targetNext(haveinst);
        }
        m_component.genAttributeUnmarshal(mb);
        
        // convert the type if necessary, then store result to property
        mb.appendCreateCast(m_component.getType(),
            m_property.getSetValueType());
        mb.targetNext(tosave);
        if (!m_property.isImplicit() && !m_property.isThis()) {
            m_property.genStore(mb);
        }
    }

    public void genAttributeMarshal(ContextMethodBuilder mb)
        throws JiBXException {
        if (m_skipMarshal) {
            
            // just generate pass-through marshal code generation
            m_component.genAttributeMarshal(mb);
            
        } else {
        
            // start by generating code to load the actual object reference
            if (!m_property.isImplicit()) {
                mb.loadObject();
                m_property.genLoad(mb);
            }
            BranchWrapper ifpres = null;
            BranchWrapper toend = null;
            if (m_property.isOptional()) {
            
                // generate code to check nonnull for the case of an optional item,
                //  with branch if so; if not present, just pop the copy with branch
                //  to be targeted past end.
                mb.appendDUP();
                ifpres = mb.appendIFNONNULL(this);
                mb.appendPOP();
                toend = mb.appendUnconditionalBranch(this);
            }
        
            // generate code for actual marshalling if not optional, or optional and
            //  nonnull; then finish by setting target for optional with
            //  null value case
            mb.targetNext(ifpres);
            m_component.genAttributeMarshal(mb);
            mb.targetNext(toend);
        }
    }

    public void genContentUnmarshal(ContextMethodBuilder mb)
        throws JiBXException {
        
        // check for both attribute and content components
        if (m_component.hasAttribute()) {
            
            // start with code to load reference from attribute unmarshalling
            if (!m_property.isImplicit()) {
                mb.loadObject();
                m_property.genLoad(mb);
            } else {
                mb.appendDUP();
            }
            BranchWrapper toend = null;
            if (m_property.isOptional()) {
            
                // generate code to check value defined for the case of an
                //  optional item, with branch if so; if not present, just pop
                //  the copy with branch to be targeted past end.
                toend = mb.appendIFNULL(this);
                if (!m_property.isImplicit()) {
                    mb.loadObject();
                    m_property.genLoad(mb);
                } else {
                    mb.appendDUP();
                }
            }
            
            // follow up in case where present with unmarshalling content
            m_component.genContentUnmarshal(mb);
            mb.appendPOP();
            mb.targetNext(toend);
            
        } else {
        
            // start by generating code to load owning object so can finish by
            //  storing to property
            if (!m_property.isImplicit() && !m_property.isThis()) {
                mb.loadObject();
            }
            BranchWrapper ifpres = null;
            BranchWrapper tosave = null;
            if (m_property.isOptional()) {
            
                // generate code to check presence for the case of an optional
                //  item, with branch if so; if not present, set a null value
                //  with branch to be targeted at property store.
                m_component.genContentPresentTest(mb);
                ifpres = mb.appendIFNE(this);
                mb.appendACONST_NULL();
                tosave = mb.appendUnconditionalBranch(this);
            }
            
            // generate unmarshalling code for not optional, or optional and
            //  present; get existing instance or create a new one and handle
            //  content unmarshalling
            mb.targetNext(ifpres);
            if (m_property.isImplicit()) {
                m_component.genNewInstance(mb);
            } else if (!m_property.isThis()) {
                
                // load current value, cast, copy, and test for non-null
                mb.loadObject();
                m_property.genLoad(mb);
                mb.appendCreateCast(m_property.getGetValueType(),
                    m_component.getType());
                mb.appendDUP();
                BranchWrapper haveinst = mb.appendIFNONNULL(this);
                
                // current value null, pop copy and create a new instance
                mb.appendPOP();
                m_component.genNewInstance(mb);
                mb.targetNext(haveinst);
            }
            m_component.genContentUnmarshal(mb);
            
            // convert the type if necessary, then store result to property
            mb.appendCreateCast(m_component.getType(),
                m_property.getSetValueType());
            mb.targetNext(tosave);
            if (!m_property.isImplicit() && !m_property.isThis()) {
                m_property.genStore(mb);
            }
        }
    }

    public void genContentMarshal(ContextMethodBuilder mb)
        throws JiBXException {
        if (m_skipMarshal) {
        
            // just generate pass-through marshal code generation
            m_component.genContentMarshal(mb);
        
        } else {
            
            // start by generating code to load the actual object reference
            if (!m_property.isImplicit()) {
                mb.loadObject();
                m_property.genLoad(mb);
            }
            BranchWrapper ifpres = null;
            BranchWrapper tonext = null;
            if (m_property.isOptional()) {
                
                // generate code to check nonull for the case of an optional item,
                //  with branch if so; if not present, just pop the copy with branch
                //  to be targeted past end.
                mb.appendDUP();
                ifpres = mb.appendIFNONNULL(this);
                mb.appendPOP();
                tonext = mb.appendUnconditionalBranch(this);
            }
            
            // generate code for actual marshalling if not optional, or optional and
            //  nonnull; then finish by setting target for optional with null value
            //  case
            mb.targetNext(ifpres);
            m_component.genContentMarshal(mb);
            mb.targetNext(tonext);
            
        }
    }
    
    // DEBUG
    public void print(int depth) {
        BindingDefinition.indent(depth);
        System.out.print("component " + m_property.toString());
        if (m_skipMarshal) {
            System.out.print(" (pass-through marshal)");
        }
        System.out.println();
        m_component.print(depth+1);
    }
}