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

import org.jibx.binding.classes.ClassFile;
import org.jibx.binding.classes.ClassItem;
import org.jibx.runtime.JiBXException;

/**
 * Wrapper for class information. This wraps the BCEL-based class handling
 * implementation to support the interface defined for use with the binding
 * model.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class ClassWrapper implements IClass
{
    private final ClassFile m_class;
    
    public ClassWrapper(ClassFile clas) {
        m_class = clas;
    }
    
    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getName()
     */
    public String getName() {
        return m_class.getName();
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getSignature()
     */
    public String getSignature() {
        return m_class.getSignature();
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getPackage()
     */
    public String getPackage() {
        return m_class.getPackage();
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getSuperClass()
     */
    public IClass getSuperClass() {
        ClassFile scf = m_class.getSuperFile();
        if (scf == null) {
            return null;
        } else {
            return new ClassWrapper(m_class.getSuperFile());
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getInterfaces()
     */
    public String[] getInterfaces() {
        return m_class.getInterfaces();
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getInstanceSigs()
     */
    public String[] getInstanceSigs() {
        try {
            return m_class.getInstanceSigs();
        } catch (JiBXException e) {
            // TODO need to handle this differently - perhaps get all when created
            throw new IllegalStateException("Internal error: instance " +
                "signatures not found for class " + m_class.getName());
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#isImplements(java.lang.String)
     */
    public boolean isImplements(String sig) {
        try {
            return m_class.isImplements(sig);
        } catch (JiBXException e) {
            // TODO need to handle this differently - perhaps get all when created
            throw new IllegalStateException("Internal error: instance " +
                "signatures not found for class " + m_class.getName());
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#isModifiable()
     */
    public boolean isModifiable() {
        return m_class.isModifiable();
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#isSuperclass(org.jibx.binding.model.IClass)
     */
    public boolean isSuperclass(String name) {
        ClassFile current = m_class;
        while (current != null) {
            if (current.getName().equals(name)) {
                return true;
            } else {
                current = current.getSuperFile();
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getDirectField(java.lang.String)
     */
    public IClassItem getDirectField(String name) {
        ClassItem item = m_class.getDirectField(name);
        if (item == null) {
            return null;
        } else {
            return new ClassItemWrapper(this, item);
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getField(java.lang.String)
     */
    public IClassItem getField(String name) {
        try {
            return new ClassItemWrapper(this, m_class.getField(name));
        } catch (JiBXException e) {
            // TODO need to handle this differently - perhaps get all when created
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getMethod(java.lang.String, java.lang.String)
     */
    public IClassItem getMethod(String name, String sig) {
        ClassItem item = m_class.getMethod(name, sig);
        if (item == null) {
            return null;
        } else {
            return new ClassItemWrapper(this, item);
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getMethod(java.lang.String, java.lang.String[])
     */
    public IClassItem getMethod(String name, String[] sigs) {
        ClassItem item = m_class.getMethod(name, sigs);
        if (item == null) {
            return null;
        } else {
            return new ClassItemWrapper(this, item);
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getInitializerMethod(java.lang.String)
     */
    public IClassItem getInitializerMethod(String sig) {
        ClassItem item = m_class.getInitializerMethod(sig);
        if (item == null) {
            return null;
        } else {
            return new ClassItemWrapper(this, item);
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getStaticMethod(java.lang.String, java.lang.String)
     */
    public IClassItem getStaticMethod(String name, String sig) {
        ClassItem item = m_class.getStaticMethod(name, sig);
        if (item == null) {
            return null;
        } else {
            return new ClassItemWrapper(this, item);
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#isAccessible(org.jibx.binding.model.IClassItem)
     */
    public boolean isAccessible(IClassItem item) {
        return m_class.isAccessible(((ClassItemWrapper)item).m_item);
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#isAssignable(org.jibx.binding.model.IClass)
     */
    public boolean isAssignable(IClass other) {
        String[] sigs;
        try {
            sigs = m_class.getInstanceSigs();
        } catch (JiBXException e) {
            throw new IllegalStateException
                ("Internal error: class information not available");
        }
        String match = other.getSignature();
        for (int i = 0; i < sigs.length; i++) {
            if (match.equals(sigs[i])) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getBestMethod(java.lang.String, java.lang.String, java.lang.String[])
     */
    public IClassItem getBestMethod(String name, String type, String[] args) {
        ClassItem item = m_class.getBestMethod(name, type, args);
        if (item == null) {
            return null;
        } else {
            return new ClassItemWrapper(this, item);
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#getClassFile()
     * TODO: eliminate this method
     */
    public ClassFile getClassFile() {
        return m_class;
    }

    /* (non-Javadoc)
     * @see org.jibx.binding.model.IClass#loadClass()
     */
    public Class loadClass() {
        String name = m_class.getName();
        Class clas = ClassFile.loadClass(name);
        if (clas == null) {
            // TODO: this is a kludge
            try {
                clas = ClassUtils.class.getClassLoader().loadClass(name);
            } catch (ClassNotFoundException ex) { /* deliberately empty */ }
        }
        return clas;
    }
}