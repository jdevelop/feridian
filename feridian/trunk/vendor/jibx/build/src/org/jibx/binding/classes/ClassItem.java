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

package org.jibx.binding.classes;

import java.util.HashMap;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.jibx.runtime.JiBXException;

/**
 * Wrapper for field or method information. Provides the information needed
 * for access to either existing or added methods in existing classes.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class ClassItem
{
    /** Empty array of strings. */
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    /** Map for primitive type signature variants. */
    private static HashMap s_primitiveMap = new HashMap();
    
    /** Map from type name to BCEL type. */
    private static HashMap s_typeMap = new HashMap();
    
    /** Map from method signature to array of argument types. */
    private static HashMap s_signatureParamsMap = new HashMap();
    
    /** Map from method signature to return type. */
    private static HashMap s_signatureTypeMap = new HashMap();
    
    static {
        s_primitiveMap.put("boolean", new String[] { "Z", "I" });
        s_primitiveMap.put("byte", new String[] { "B", "S", "I" });
        s_primitiveMap.put("char", new String[] { "C", "I" });
        s_primitiveMap.put("double", new String[] { "D" });
        s_primitiveMap.put("float", new String[] { "F" });
        s_primitiveMap.put("int", new String[] { "I" });
        s_primitiveMap.put("long", new String[] { "J" });
        s_primitiveMap.put("short", new String[] { "S", "I" });
        s_primitiveMap.put("void", new String[] { "V" });
        s_typeMap.put("boolean", Type.BOOLEAN);
        s_typeMap.put("byte", Type.BYTE);
        s_typeMap.put("char", Type.CHAR);
        s_typeMap.put("double", Type.DOUBLE);
        s_typeMap.put("float", Type.FLOAT);
        s_typeMap.put("int", Type.INT);
        s_typeMap.put("long", Type.LONG);
        s_typeMap.put("short", Type.SHORT);
        s_typeMap.put("void", Type.VOID);
    }
    
    /** Owning class information. */
    private ClassFile m_classFile;
    
    /** Item name. */
    private String m_name;
    
    /** Encoded signature. */
    private String m_signature;
    
    /** Fully qualified class name of item type. */
    private String m_typeName;
    
    /** Argument types for method. */
    private String[] m_argTypes;
    
    /** Wrapped existing item. */
    private FieldOrMethod m_item;
    
    /**
     * Constructor. Builds a wrapper for an item based on an existing field or
     * method.
     *
     * @param name field or method name
     * @param cf owning class information
     * @param item field or method information
     */
     
    public ClassItem(String name, ClassFile cf, FieldOrMethod item) {
        m_classFile = cf;
        m_name = name;
        m_item = item;
        m_signature = item.getSignature();
        if (item instanceof Method) {
            m_typeName = getTypeFromSignature(m_signature);
            m_argTypes = getParametersFromSignature(m_signature);
        } else {
            m_typeName = Utility.signatureToString(m_signature, false);
        }
    }
    
    /**
     * Get owning class information.
     *
     * @return owning class information
     */
     
    public ClassFile getClassFile() {
        return m_classFile;
    }
    
    /**
     * Get item name.
     *
     * @return item name
     */
     
    public String getName() {
        return m_name;
    }
    
    /**
     * Get item type as fully qualified class name.
     *
     * @return item type name
     */
     
    public String getTypeName() {
        return m_typeName;
    }
    
    /**
     * Get number of arguments for method.
     *
     * @return argument count for method, or zero if not a method
     */
     
    public int getArgumentCount() {
        if (m_item instanceof Method) {
            return m_argTypes.length;
        } else {
            return 0;
        }
    }
    
    /**
     * Get argument type as fully qualified class name.
     *
     * @param index argument number
     * @return argument type name
     */
     
    public String getArgumentType(int index) {
        if (m_item instanceof Method) {
            return m_argTypes[index];
        } else {
            return null;
        }
    }
    
    /**
     * Get argument types as array of fully qualified class names.
     *
     * @return array of argument types
     */
     
    public String[] getArgumentTypes() {
        if (m_item instanceof Method) {
            return m_argTypes;
        } else {
            return null;
        }
    }
    
    /**
     * Get access flags.
     *
     * @return flags for access type of field or method
     */
     
    public int getAccessFlags() {
        return m_item.getAccessFlags();
    }
    
    /**
     * Set access flags.
     *
     * @param flags access flags for field or method
     */
     
    public void setAccessFlags(int flags) {
        m_item.setAccessFlags(flags);
        m_classFile.setModified();
    }

    /**
     * Make accessible item. Check if this field or method is accessible from
     * another class, and if not decreases the access restrictions to make it
     * accessible.
     *
     * @param src class file for required access
     * @throws JiBXException if cannot be accessed
     */

    public void makeAccessible(ClassFile src) throws JiBXException {
        
        // no need to change if already public access
        int access = getAccessFlags();
        if ((access & Constants.ACC_PUBLIC) == 0) {
            
            // check for same package as most restrictive case
            ClassFile dest = getClassFile();
            if (dest.getPackage().equals(src.getPackage())) {
                if ((access & Constants.ACC_PRIVATE) != 0) {
                    access = access - Constants.ACC_PRIVATE;
                }
            } else  {
                
                // check if access is from a subclass of this method class
                ClassFile ancestor = src;
                while ((ancestor = ancestor.getSuperFile()) != null) {
                    if (ancestor == dest) {
                        break;
                    }
                }
                
                // handle access adjustments based on subclass status
                if (ancestor == null) {
                    int clear = Constants.ACC_PRIVATE |
                        Constants.ACC_PROTECTED;
                    access = (access & ~clear) | Constants.ACC_PUBLIC;
                } else if ((access & Constants.ACC_PROTECTED) == 0) {
                    access = (access & ~Constants.ACC_PRIVATE) |
                        Constants.ACC_PROTECTED;
                }
            }
            
            // set new access flags
            if (access != getAccessFlags()) {
                if (dest.isModifiable()) {
                    setAccessFlags(access);
                } else {
                    throw new JiBXException
                        ("Unable to change access permissions for " +
                        getName() + " in class " + src.getName());
                }
            }
        }
    }
    
    /**
     * Check if item is a static.
     *
     * @return <code>true</code> if a static, <code>false</code> if member
     */
     
    public boolean isStatic() {
        return (getAccessFlags() & Constants.ACC_STATIC) != 0;
    }
    
    /**
     * Get method signature.
     *
     * @return encoded method signature
     */
     
    public String getSignature() {
        return m_signature;
    }
    
    /**
     * Check if item is a method.
     *
     * @return <code>true</code> if a method, <code>false</code> if a field
     */
     
    public boolean isMethod() {
        return m_item == null || m_item instanceof Method;
    }
    
    /**
     * Check if item is an initializer.
     *
     * @return <code>true</code> if an initializer, <code>false</code> if a
     * field or normal method
     */
     
    public boolean isInitializer() {
        return m_item != null && m_item.getName().equals("<init>");
    }
    
    /**
     * Get names of exceptions thrown by method.
     *
     * @return array of exceptions thrown by method, or <code>null</code> if
     * a field
     */
     
    public String[] getExceptions() {
        if (m_item instanceof Method) {
            ExceptionTable etab = ((Method)m_item).getExceptionTable();
            if (etab != null) {
                return etab.getExceptionNames();
            } else {
                return EMPTY_STRING_ARRAY;
            }
        }
        return null;
    }
    
    /**
     * Check if type name is a primitive.
     *
     * @return <code>true</code> if a primitive, <code>false</code> if not
     */
     
    public static boolean isPrimitive(String type) {
        return s_primitiveMap.get(type) != null;
    }
    
    /**
     * Get the signature for a primitive.
     *
     * @return signature for a primitive type
     */
     
    public static String getPrimitiveSignature(String type) {
        return ((String[])s_primitiveMap.get(type))[0];
    }
    
    /**
     * Get parameter type names from method signature.
     *
     * @param sig method signature to be decoded
     * @return array of argument type names
     */
     
    public static String[] getParametersFromSignature(String sig) {
        String[] types = (String[])s_signatureParamsMap.get(sig);
        if (types == null) {
            types = Utility.methodSignatureArgumentTypes(sig, false);
            s_signatureParamsMap.put(sig, types);
        }
        return types;
    }
    
    /**
     * Get return type names from method signature.
     *
     * @param sig method signature to be decoded
     * @return return type name
     */
     
    public static String getTypeFromSignature(String sig) {
        String type = (String)s_signatureTypeMap.get(sig);
        if (type == null) {
            type = Utility.methodSignatureReturnType(sig, false);
            s_signatureTypeMap.put(sig, type);
        }
        return type;
    }

    /**
     * Create type from name.
     *
     * @param name fully qualified type name
     * @return corresponding type
     */

    public static Type typeFromName(String name) {
        
        // first check for type already created
        Type type = (Type)s_typeMap.get(name);
        if (type == null) {
            
            // new type, strip off array dimensions
            int dimen = 0;
            String base = name;
            while (base.endsWith("[]")) {
                dimen++;
                base = base.substring(0, base.length()-2);
            }
            
            // check for base type defined if array
            if (dimen > 0) {
                type = (Type)s_typeMap.get(base);
            }
            
            // create and record base type if new
            if (type == null) {
                type = new ObjectType(base);
                s_typeMap.put(base, type);
            }
            
            // create and record array type
            if (dimen > 0) {
                type = new ArrayType(type, dimen);
                s_typeMap.put(name, type);
            }
        }
        return type;
    }
    
    /**
     * Get virtual method by fully qualified name. This splits the class
     * name from the method name, finds the class, and then tries to find a
     * matching method name in that class or a superclass.
     *
     * @param name fully qualified class and method name
     * @param sigs possible method signatures
     * @return information for the method, or <code>null</code> if not found
     * @throws JiBXException if configuration error
     */
    
    public static ClassItem findVirtualMethod(String name, String[] sigs)
        throws JiBXException {
        
        // get the class containing the method
        int split = name.lastIndexOf('.');
        String cname = name.substring(0, split);
        String mname = name.substring(split+1);
        ClassFile cf = ClassCache.getClassFile(cname);
        
        // find the method in class or superclass
        for (int i = 0; i < sigs.length; i++) {
            ClassItem method = cf.getMethod(mname, sigs[i]);
            if (method != null) {
                return method;
            }
        }
        return null;
    }
    
    /**
     * Get static method by fully qualified name. This splits the class
     * name from the method name, finds the class, and then tries to find a
     * matching method name in that class.
     *
     * @param name fully qualified class and method name
     * @param sigs possible method signatures
     * @return information for the method, or <code>null</code> if not found
     * @throws JiBXException if configuration error
     */
    
    public static ClassItem findStaticMethod(String name, String[] sigs)
        throws JiBXException {
        
        // get the class containing the method
        int split = name.lastIndexOf('.');
        String cname = name.substring(0, split);
        String mname = name.substring(split+1);
        ClassFile cf = ClassCache.getClassFile(cname);
        
        // find the method in class or superclass
        for (int i = 0; i < sigs.length; i++) {
            ClassItem method = cf.getStaticMethod(mname, sigs[i]);
            if (method != null) {
                return method;
            }
        }
        return null;
    }
    
    /**
     * Get all variant signatures for a fully qualified class name. The
     * returned array gives all signatures (for interfaces or classes) which
     * instances of the class can match.
     *
     * @param name fully qualified class name
     * @return possible signature variations for instances of the class
     * @throws JiBXException if configuration error
     */
    
    public static String[] getSignatureVariants(String name)
        throws JiBXException {
        Object obj = s_primitiveMap.get(name);
        if (obj == null) {
            ClassFile cf = ClassCache.getClassFile(name);
            return cf.getInstanceSigs();
        } else {
            return (String[])obj;
        }
    }
    
    /**
     * Check if a value of one type can be directly assigned to another type.
     * This is basically the equivalent of the instanceof operator, but with
     * application to primitive types as well as object types.
     *
     * @param from fully qualified class name of initial type
     * @param to fully qualified class name of assignment type
     * @return <code>true</code> if assignable, <code>false</code> if not
     * @throws JiBXException if configuration error
     */
    
    public static boolean isAssignable(String from, String to)
        throws JiBXException {
        
        // always assignable if the two are the same
        if (from.equals(to)) {
            return true;
        } else {
            
            // try direct lookup for primitive types
            Object fobj = s_primitiveMap.get(from);
            Object tobj = s_primitiveMap.get(to);
            if (fobj == null && tobj == null) {
                
                // assignable if from type has to as a possible signature
                ClassFile cf = ClassCache.getClassFile(from);
                String[] sigs = cf.getInstanceSigs();
                String match = Utility.getSignature(to);
                for (int i = 0; i < sigs.length; i++) {
                    if (match.equals(sigs[i])) {
                        return true;
                    }
                }
                return false;
                
            } else if (fobj != null && tobj != null) {
                
                // assignable if from type has to as a possible signature
                String[] fsigs = (String[])fobj;
                String[] tsigs = (String[])tobj;
                if (tsigs.length == 1) {
                    for (int i = 0; i < fsigs.length; i++) {
                        if (fsigs[i] == tsigs[0]) {
                            return true;
                        }
                    }
                }
                return false;
                
            } else {
                
                // primitive and object types never assignable
                return false;
                
            }
        }
    }
}