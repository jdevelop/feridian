/*
Copyright (c) 2004, Dennis M. Sosnoski
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

package org.jibx.binding.classes;

import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.InstructionHandle;

/**
 * Wrapper for branch handle. This preserves a snapshot of the stack state for
 * the branch instruction, matching it against the stack state for the target
 * instruction when set.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class BranchWrapper
{
    /** Track source code location for generated branches. */
    private static boolean s_trackSource;
    
    /** Continue on after code generation error flag. */
    private static boolean s_errorOverride;
    
    /** Actual wrapped instruction handle. */
    private final BranchHandle m_branchHandle;
    
    /** Stack state for branch origin. */
    private final String[] m_stackTypes;
    
    /** Object that generated branch. */
    private final Object m_sourceObject;
    
    /** Code generation backtrace for source of branch. */
    private final Throwable m_sourceTrace;

    /**
     * Constructor.
     *
     * @param hand branch handle
     * @param types array of types of values on stack
     * @param src object responsible for generating branch
     */

    /*package*/ BranchWrapper(BranchHandle hand, String[] types, Object src) {
        m_branchHandle = hand;
        m_stackTypes = types;
        m_sourceObject = src;
        if (s_trackSource) {
            m_sourceTrace = new Throwable();
        } else {
            m_sourceTrace = null;
        }
    }
    
    /**
     * Get branch origin stack state information.
     *
     * @return array of types of values on stack
     */
     
    /*package*/ String[] getStackState() {
        return m_stackTypes;
    }
    
    /**
     * Generate description of stack state.
     *
     * @param types array of types on stack
     * @return stack state description
     */
     
    private String describeStack(String[] types) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < types.length; i++) {
            buff.append("  ");
            buff.append(i);
            buff.append(": ");
            buff.append(types[i]);
            buff.append('\n');
        }
        return buff.toString();
    }
    
    /**
     * Report branch target error. Dumps the stack trace for the source of the
     * branch, if source tracking is enabled, and generates an exception that
     * includes the stack state information.
     *
     * @param text basic error message text
     * @param types stack state description
     * @param mb method builder using this code
     * @return complete error description text
     */
     
    private String buildReport(String text, String[] types, MethodBuilder mb) {
        
        // start by dumping branch generation source trace
        if (m_sourceTrace != null) {
            System.err.println("Backtrack for branch source:");
            m_sourceTrace.printStackTrace(System.err);
        }
        
        // generate error message leading text
        StringBuffer buff = new StringBuffer(text);
        buff.append("\n in method ");
        buff.append(mb.getClassFile().getName());
        buff.append('.');
        buff.append(mb.getName());
        buff.append("\n generated by ");
        buff.append(m_sourceObject.toString());
        buff.append("\n from stack:\n");
        buff.append(describeStack(m_stackTypes));
        buff.append(" to stack:\n");
        buff.append(describeStack(types));
        return buff.toString();
    }
    
    /**
     * Set target instruction for branch. Validates the branch source stack
     * state against the branch target stack state.
     *
     * @param hand target branch instruction handle
     * @param types stack state description
     * @param mb method builder using this code
     */
     
    /*package*/ void setTarget(InstructionHandle hand, String[] types,
        MethodBuilder mb) {
        
        // match stack states
        if (types.length == m_stackTypes.length) {
            for (int i = 0; i < types.length; i++) {
                String stype = m_stackTypes[i];
                if (!types[i].equals(stype)) {
                    if (!"<null>".equals(types[i]) && !"<null>".equals(stype)) {
                        if (m_sourceTrace != null) {
                            System.err.println("Backtrack for branch source:");
                            m_sourceTrace.printStackTrace(System.err);
                        }
                        String text = buildReport
                            ("Stack value type mismatch on branch", types, mb);
                        if (s_errorOverride) {
                            new Throwable(text).printStackTrace(System.err);
                        } else {
                            throw new IllegalStateException(text);
                        }
                    }
                }
            }
        } else {
            String text =
                buildReport("Stack size mismatch on branch", types, mb);
            if (s_errorOverride) {
                new Throwable(text).printStackTrace(System.err);
            } else {
                throw new IllegalStateException(text);
            }
        }
        
        // set the branch target
        m_branchHandle.setTarget(hand);
    }
    
    /**
     * Set target instruction for branch. Validates the branch source stack
     * state against the branch target stack state.
     *
     * @param target branch target wrapper
     * @param mb method builder using this code
     */
     
    public void setTarget(BranchTarget target, MethodBuilder mb) {
        setTarget(target.getInstruction(), target.getStack(), mb);
    }
    
    /**
     * Set branch code generation tracking state. When set, this saves a stack
     * trace for each generated branch instruction, allowing the source of a
     * branch to be traced when an error occurs in setting the branch target.
     *
     * @param track <code>true</code> to enable branch code generation tracking,
     * <code>false</code> to disable it
     */
     
    public static void setTracking(boolean track) {
        s_trackSource = track;
    }
    
    /**
     * Set target setting error override state. When set, this blocks throwing
     * an exception when an error occurs on setting the branch target, instead
     * just printing the information to the console.
     *
     * @param over <code>true</code> to override exception on target error,
     * <code>false</code> to allow it
     */
     
    public static void setErrorOverride(boolean over) {
        s_errorOverride = over;
    }
}