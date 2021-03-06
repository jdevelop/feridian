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

import java.util.ArrayList;

import org.jibx.binding.classes.*;
import org.jibx.runtime.JiBXException;

/**
 * Structure binding definition. This handles one or more child components,
 * which may be ordered or unordered.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
public class NestedStructure extends NestedBase
{
    /** Child supplying ID for bound class. */
    private IComponent m_idChild;

    /** Flag for choice of child content (used by subclasses). */
    protected final boolean m_isChoice;
    
    /** Flag for structure has associated object. */
    private final boolean m_hasObject;
    
    /** Flag for already linked (to avoid multiple passes). */
    private boolean m_isLinked;

    /**
     * Constructor.
     *
     * @param parent containing binding definition context
     * @param objc current object context
     * @param ord ordered content flag
     * @param choice choice content flag
     * @param ctx define context for structure flag
     */
    public NestedStructure(IContainer parent, IContextObj objc,
        boolean ord, boolean choice, boolean ctx, boolean hasobj) {
        super(parent, objc, ord, ctx);
        m_isChoice = choice;
        m_hasObject = hasobj;
    }
    
    //
    // IComponent interface method definitions

    public boolean hasAttribute() {
        return m_attributes != null && m_attributes.size() > 0;
    }

    public void genAttrPresentTest(ContextMethodBuilder mb)
        throws JiBXException {
        if (m_attributes != null && m_attributes.size() > 0) {
            
            // if single possiblity just test it directly
            int count = m_attributes.size();
            if (count == 1) {
                ((IComponent)m_attributes.get(0)).genAttrPresentTest(mb);
            } else {
                
                // generate code for chained test with branches to found exit
                BranchWrapper[] tofound = new BranchWrapper[count];
                for (int i = 0; i < count; i++) {
                    IComponent comp = (IComponent)m_attributes.get(i);
                    comp.genAttrPresentTest(mb);
                    tofound[i] = mb.appendIFNE(this);
                }
                
                // fall off end of loop to push "false" on stack and jump to end
                mb.appendICONST_0();
                BranchWrapper toend = mb.appendUnconditionalBranch(this);
                
                // generate found target to push "true" on stack and continue
                for (int i = 0; i < count; i++) {
                    mb.targetNext(tofound[i]);
                }
                mb.appendICONST_1();
                mb.targetNext(toend);
                
            }
        } else {
            throw new IllegalStateException
                ("Internal error - no attributes present");
        }
    }

    public void genAttributeUnmarshal(ContextMethodBuilder mb)
        throws JiBXException {
        if (m_attributes != null && m_attributes.size() > 0) {
            for (int i = 0; i < m_attributes.size(); i++) {
                IComponent attr = (IComponent)m_attributes.get(i);
                attr.genAttributeUnmarshal(mb);
            }
        } else {
            throw new IllegalStateException
                ("Internal error - no attributes present");
        }
    }

    public void genAttributeMarshal(ContextMethodBuilder mb)
        throws JiBXException {
        if (m_attributes != null && m_attributes.size() > 0) {
            for (int i = 0; i < m_attributes.size(); i++) {
                IComponent attr = (IComponent)m_attributes.get(i);
                attr.genAttributeMarshal(mb);
            }
        } else {
            throw new IllegalStateException
                ("Internal error - no attributes present");
        }
    }

    public boolean hasContent() {
        return m_contents.size() > 0;
    }

    public void genContentUnmarshal(ContextMethodBuilder mb)
        throws JiBXException {
        if (m_contents.size() > 0) {
        
            // check for ordered or unordered content
            if (m_isOrdered) {
            
                // just generate unmarshal code for each component in order
                for (int i = 0; i < m_contents.size(); i++) {
                    IComponent child = (IComponent)m_contents.get(i);
                    child.genContentUnmarshal(mb);
                }
                
            } else {
            
                // generate unmarshal loop code that checks for each component,
                //  branching to the next component until one is found and
                //  exiting the loop only when no component is matched
                BranchWrapper link = null;
                // TODO: initialize default values
                BranchTarget first = mb.appendTargetNOP();
                BranchWrapper[] toends;
                int count = m_contents.size();
                if (m_isChoice) {
                    toends = new BranchWrapper[count+1];
                } else {
                    toends = new BranchWrapper[1];
                }
                for (int i = 0; i < count; i++) {
                    if (link != null) {
                        mb.targetNext(link);
                    }
                    IComponent child = (IComponent)m_contents.get(i);
                    child.genContentPresentTest(mb);
                    link = mb.appendIFEQ(this);
                    child.genContentUnmarshal(mb);
                    BranchWrapper next = mb.appendUnconditionalBranch(this);
                    if (m_isChoice) {
                        toends[i+1] = next;
                    } else {
                        next.setTarget(first, mb);
                    }
                }
            
                // patch final test failure branch to fall through loop
                toends[0] = link;
                mb.targetNext(toends);
            
            }
        } else {
            throw new IllegalStateException
                ("Internal error - no content present");
        }
    }

    public void genContentMarshal(ContextMethodBuilder mb)
        throws JiBXException {
        if (m_contents.size() > 0) {
            for (int i = 0; i < m_contents.size(); i++) {
                IComponent content = (IComponent)m_contents.get(i);
                content.genContentMarshal(mb);
            }
        } else {
            throw new IllegalStateException
                ("Internal error - no content present");
        }
    }

    public String getType() {
        if (m_hasObject) {
            return super.getType();
        } else if (m_attributes != null && m_attributes.size() > 0) {
            return ((IComponent)m_attributes.get(0)).getType();
        } else if (m_contents.size() > 0) {
            return ((IComponent)m_contents.get(0)).getType();
        } else {
            throw new IllegalStateException("Internal error - " +
                "no type defined for structure");
        }
    }
    
    public boolean hasId() {
        return m_idChild != null;
    }
    
    public void genLoadId(ContextMethodBuilder mb) throws JiBXException {
        if (m_idChild == null) {
            throw new IllegalStateException("No ID child defined");
        } else {
            m_idChild.genLoadId(mb);
        }
    }

    public boolean checkContentSequence(boolean text) throws JiBXException {
        for (int i = 0; i < m_contents.size(); i++) {
            IComponent content = (IComponent)m_contents.get(i);
            text = content.checkContentSequence(text);
        }
        return text;
    }

    public void setLinkages() throws JiBXException {
        if (!m_isLinked) {
            
            // set flag first in case of recursive reference
            m_isLinked = true;
        
            // process all child components to link and sort by type
            int i = 0;
            while (i < m_contents.size()) {
                IComponent comp = (IComponent)m_contents.get(i);
                comp.setLinkages();
                if (comp.hasAttribute()) {
                    if (m_attributes == null) {
                        m_attributes = new ArrayList();
                    }
                    m_attributes.add(comp);
                }
                if (!comp.hasContent()) {
                    m_contents.remove(i);
                } else {
                    i++;
                }
            }
        }
    }
    
    // DEBUG
    public void print(int depth) {
        BindingDefinition.indent(depth);
        System.out.print("structure " +
            (m_isChoice ? "choice" : (m_isOrdered ? "ordered" : "unordered")));
        if (m_idChild != null) {
            System.out.print(" (ID)");
        }
        System.out.println();
        for (int i = 0; i < m_contents.size(); i++) {
            IComponent comp = (IComponent)m_contents.get(i);
            comp.print(depth+1);
        }
        if (m_attributes != null) {
            for (int i = 0; i < m_attributes.size(); i++) {
                IComponent comp = (IComponent)m_attributes.get(i);
                comp.print(depth+1);
            }
        }
    }
}