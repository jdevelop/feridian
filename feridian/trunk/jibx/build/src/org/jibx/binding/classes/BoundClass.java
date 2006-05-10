/*
 * Copyright (c) 2003-2005, Dennis M. Sosnoski All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of JiBX nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.jibx.binding.classes;

import java.io.File;
import java.util.HashMap;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.Type;

import org.jibx.binding.def.BindingDefinition;
import org.jibx.runtime.JiBXException;

/**
 * Bound class handler. Each instance controls and organizes information for a
 * class included in one or more binding definitions.
 * 
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class BoundClass
{
    //
    // Constants and such related to code generation.

    /** Class used for code munging when no specific class available. */
    private static final String GENERIC_MUNGE_CLASS = BindingDefinition.GENERATE_PREFIX
        + "MungeAdapter";

    /** Prefix used for access methods. */
    private static final String ACCESS_PREFIX = BindingDefinition.GENERATE_PREFIX
        + "access_";

    /** Empty argument type array. */
    private static final Type[] EMPTY_TYPE_ARGS = {};

    //
    // Static data.

    /**
     * Map from bound class name (or bound and munged combination) to binding
     * information.
     */
    private static HashMap s_nameMap;

    /** Package of first modifiable class. */
    private static String s_modifyPackage;

    /** Root for package of first modifiable class. */
    private static File s_modifyRoot;

    /** Class used for code generation proxy with unmodifiable classes. */
    private static MungedClass s_genericMunge;

    //
    // Actual instance data.

    /** Bound class file information. */
    private final ClassFile m_boundClass;

    /** Class receiving code generated for target class. */
    private final MungedClass m_mungedClass;

    /**
     * Map from field or method to load access method (lazy create,
     * <code>null</code> if not used).
     */
    private HashMap m_loadMap;

    /**
     * Map from field or method to store access method (lazy create,
     * <code>null</code> if not used).
     */
    private HashMap m_storeMap;

    /**
     * Constructor.
     * 
     * @param bound target class file information
     * @param munge class file for class hosting generated code
     */

    private BoundClass(ClassFile bound, MungedClass munge) {
        m_boundClass = bound;
        m_mungedClass = munge;
    }

    /**
     * Get bound class file information.
     * 
     * @return class file information for bound class
     */

    public ClassFile getClassFile() {
        return m_boundClass;
    }

    /**
     * Get bound class file name.
     * 
     * @return name of bound class
     */

    public String getClassName() {
        return m_boundClass.getName();
    }

    /**
     * Get munged class file information.
     * 
     * @return class file information for class being modified
     */

    public ClassFile getMungedFile() {
        return m_mungedClass.getClassFile();
    }

    /**
     * Check if class being changed directly.
     * 
     * @return <code>true</code> if bound class is being modified,
     * <code>false</code> if using a surrogate
     */

    public boolean isDirectAccess() {
        return m_boundClass == m_mungedClass.getClassFile();
    }

    /**
     * Get load access method for member of this class. If the access method
     * does not already exist it's created by this call. If the access method
     * does exist but without access from the context class, the access
     * permission on the method is broadened (from package to protected or
     * public, or from protected to public).
     * 
     * @param item field or method to be accessed
     * @param from context class from which access is required
     * @return the item itself if it's accessible from the required context, an
     * access method that is accessible if the item is not itself
     * @throws JiBXException on configuration error
     */

    public ClassItem getLoadMethod(ClassItem item, ClassFile from)
        throws JiBXException {

        // initialize tracking information for access methods if first time
        if (m_loadMap == null) {
            m_loadMap = new HashMap();
        }

        // check if a new access method needed
        BindingMethod method = (BindingMethod)m_loadMap.get(item);
        if (method == null) {

            // set up for constructing new method
            String name = ACCESS_PREFIX + "load_" + item.getName();
            ClassFile cf = item.getClassFile();
            Type type = Type.getType(Utility.getSignature(item.getTypeName()));
            MethodBuilder mb = new ExceptionMethodBuilder(name, type,
                EMPTY_TYPE_ARGS, cf, (short)0);

            // add the actual access method code
            mb.appendLoadLocal(0);
            if (item.isMethod()) {
                mb.addMethodExceptions(item);
                mb.appendCall(item);
            } else {
                mb.appendGetField(item);
            }
            mb.appendReturn(type);

            // track unique instance of this method
            method = m_mungedClass.getUniqueMethod(mb, true);
            m_loadMap.put(item, method);
        }

        // make sure method is accessible
        method.makeAccessible(from);
        return method.getItem();
    }

    /**
     * Get store access method for member of this class. If the access method
     * does not already exist it's created by this call. If the access method
     * does exist but without access from the context class, the access
     * permission on the method is broadened (from package to protected or
     * public, or from protected to public).
     * 
     * @param item field or method to be accessed
     * @param from context class from which access is required
     * @return the item itself if it's accessible from the required context, an
     * access method that is accessible if the item is not itself
     * @throws JiBXException on configuration error
     */

    public ClassItem getStoreMethod(ClassItem item, ClassFile from)
        throws JiBXException {

        // initialize tracking information for access methods if first time
        if (m_storeMap == null) {
            m_storeMap = new HashMap();
        }

        // check if a new access method needed
        BindingMethod method = (BindingMethod)m_storeMap.get(item);
        if (method == null) {

            // set up for constructing new method
            String name = ACCESS_PREFIX + "store_" + item.getName();
            ClassFile cf = item.getClassFile();
            Type type;
            if (item.isMethod()) {
                String sig = item.getSignature();
                int start = sig.indexOf('(');
                int end = sig.indexOf(')');
                type = Type.getType(sig.substring(start + 1, end));
            } else {
                type = Type.getType(Utility.getSignature(item.getTypeName()));
            }
            MethodBuilder mb = new ExceptionMethodBuilder(name, Type.VOID,
                new Type[] { type }, cf, (short)0);

            // add the actual access method code
            mb.appendLoadLocal(0);
            mb.appendLoadLocal(1);
            if (item.isMethod()) {
                mb.addMethodExceptions(item);
                mb.appendCall(item);
            } else {
                mb.appendPutField(item);
            }
            mb.appendReturn();

            // track unique instance of this method
            method = m_mungedClass.getUniqueMethod(mb, true);
            m_storeMap.put(item, method);
        }

        // make sure method is accessible
        method.makeAccessible(from);
        return method.getItem();
    }

    /**
     * Get unique method. Just delegates to the modified class handling, with
     * unique suffix appended to method name.
     * 
     * @param builder method to be defined
     * @return defined method item
     * @throws JiBXException on configuration error
     */

    public BindingMethod getUniqueMethod(MethodBuilder builder)
        throws JiBXException {
        return m_mungedClass.getUniqueMethod(builder, true);
    }

    /**
     * Get unique method. Just delegates to the modified class handling. The
     * supplied name is used without change.
     * 
     * @param builder method to be defined
     * @return defined method item
     * @throws JiBXException on configuration error
     */

    public BindingMethod getUniqueNamed(MethodBuilder builder)
        throws JiBXException {
        return m_mungedClass.getUniqueMethod(builder, false);
    }

    /**
     * Add binding factory to class. Makes sure that there's no surrogate class
     * for code generation, then delegates to the modified class handling.
     * 
     * @param fact binding factory name
     */

    public void addFactory(String fact) {
        if (isDirectAccess()) {
            m_mungedClass.addFactory(fact);
        } else {
            throw new IllegalStateException(
                "Internal error: not directly modifiable class");
        }
    }

    /**
     * Generate factory list. Makes sure that there's no surrogate class for
     * code generation, then delegates to the modified class handling.
     * 
     * @throws JiBXException on configuration error
     */

    public void setFactoryList() throws JiBXException {
        if (isDirectAccess()) {
            m_mungedClass.setFactoryList();
        } else {
            throw new IllegalStateException(
                "Internal error: not directly modifiable class");
        }
    }

    /**
     * Create binding information for class. This creates the combination of
     * bound class and (if different) munged class and adds it to the internal
     * tables.
     * 
     * @param key text identifier for this bound class and munged class
     * combination
     * @param bound class information for bound class
     * @param munge information for surrogate class receiving generated code, or
     * <code>null</code> if no separate class
     * @return binding information for class
     */

    private static BoundClass createInstance(String key, ClassFile bound,
        MungedClass munge) {
        BoundClass inst = new BoundClass(bound, munge);
        s_nameMap.put(key, inst);
        return inst;
    }

    /**
     * Find or create binding information for class. If the combination of bound
     * class and munged class already exists it's returned directly, otherwise
     * it's created and returned.
     * 
     * @param bound class information for bound class
     * @param munge information for surrogate class receiving generated code
     * @return binding information for class
     */

    private static BoundClass findOrCreateInstance(ClassFile bound,
        MungedClass munge) {
        String key = bound.getName() + ':' + munge.getClassFile().getName();
        BoundClass inst = (BoundClass)s_nameMap.get(key);
        if (inst == null) {
            inst = createInstance(key, bound, munge);
        }
        return inst;
    }

    /**
     * Get binding information for class. This finds the class in which code
     * generation for the target class takes place. Normally this class will be
     * the target class itself, but in cases where the target class is not
     * modifiable an alternate class will be used. This can take two forms. If
     * the context class is provided and it is a subclass of the target class,
     * code for the target class is instead added to the context class. If there
     * is no context class, or if the context class is not a subclass of the
     * target class, a unique catch-all class is used.
     * 
     * @param cf bound class information
     * @param context context class for code generation, or <code>null</code>
     * if no context
     * @return binding information for class
     * @throws JiBXException on configuration error
     */

    public static BoundClass getInstance(ClassFile cf, BoundClass context)
        throws JiBXException {

        // check if new instance needed for this class
        BoundClass inst = (BoundClass)s_nameMap.get(cf.getName());
        if (inst == null) {

            // load the basic class information and check for modifiable
            if (!cf.isInterface() && cf.isModifiable()) {

                // return instance directly
                inst = createInstance(cf.getName(), cf, MungedClass
                    .getInstance(cf));

            } else {

                // see if the context class is a subclass
                if (context != null
                    && context.getClassFile().isSuperclass(cf.getName())) {

                    // find or create munge with subclass as surrogate
                    inst = findOrCreateInstance(cf, context.m_mungedClass);

                } else {

                    // use catch-all munge class as surrogate for all else
                    if (s_genericMunge == null) {
                        String mname;
                        if (s_modifyPackage == null) {
                            mname = GENERIC_MUNGE_CLASS;
                            MungedClass.checkDirectory(s_modifyRoot, "");
                        } else {
                            mname = s_modifyPackage + '.' + GENERIC_MUNGE_CLASS;
                            MungedClass.checkDirectory(s_modifyRoot,
                                s_modifyPackage);
                        }
                        ClassFile base = ClassCache
                            .getClassFile("java.lang.Object");
                        int acc = Constants.ACC_PUBLIC | Constants.ACC_ABSTRACT;
                        ClassFile gen = new ClassFile(mname, s_modifyRoot,
                            base, acc, new String[0]);
                        gen.addDefaultConstructor();
                        s_genericMunge = MungedClass.getInstance(gen);
                        MungedClass.delayedAddUnique(gen);
                    }
                    inst = findOrCreateInstance(cf, s_genericMunge);

                }
            }
        }
        return inst;
    }

    /**
     * Get binding information for class. This version takes a fully-qualified
     * class name, calling the paired method if necessary to create a new
     * instance.
     * 
     * @param name fully qualified name of bound class
     * @param context context class for code generation, or <code>null</code>
     * if no context
     * @return binding information for class
     * @throws JiBXException on configuration error
     */

    public static BoundClass getInstance(String name, BoundClass context)
        throws JiBXException {

        // check if new instance needed for this class
        BoundClass inst = (BoundClass)s_nameMap.get(name);
        if (inst == null) {
            ClassFile cf = ClassCache.getClassFile(name);
            return getInstance(cf, context);
        }
        return inst;
    }

    /**
     * Discard cached information and reset in preparation for a new binding
     * run.
     */

    public static void reset() {
        s_nameMap = new HashMap();
        s_modifyPackage = null;
        s_modifyRoot = null;
        s_genericMunge = null;
    }

    /**
     * Discard cached information and reset in preparation for a new binding
     * run.
     */

    public static void setModify(File root, String pkg) {
        s_modifyRoot = root;
        s_modifyPackage = pkg;
        if (s_modifyPackage.length() == 0) {
            s_modifyPackage = null;
        }
    }

    /**
     * Derive generated class name for bound class. This generates a JiBX class
     * name from the name of this class, using the supplied prefix and suffix
     * information. The derived class name is always in the same package as the
     * munged class for this class.
     * 
     * @param prefix generated class name prefix
     * @param suffix generated class name suffix
     * @return derived class name
     */

    public String deriveClassName(String prefix, String suffix) {
        String pack = m_mungedClass.getClassFile().getPackage();
        if (pack.length() > 0) {
            pack += '.';
        }
        String tname = m_boundClass.getName();
        int split = tname.lastIndexOf('.');
        if (split >= 0) {
            tname = tname.substring(split + 1);
        }
        return pack + prefix + tname + suffix;
    }
}