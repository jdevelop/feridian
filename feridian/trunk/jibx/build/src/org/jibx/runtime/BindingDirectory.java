/*
Copyright (c) 2002-2005, Sosnoski Software Solutions, Inc.
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

package org.jibx.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Abstract class with static method to find the binding factory corresponding
 * to a binding name.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public abstract class BindingDirectory
{
    /** Name of <code>String[]</code> field giving binding factory name list. */
    public static final String BINDINGLIST_NAME = "JiBX_bindingList";
    
    /** Prefix of binding factory name. */
    public static final String BINDINGFACTORY_PREFIX = "JiBX_";
    
    /** Suffix of binding factory name. */
    public static final String BINDINGFACTORY_SUFFIX = "Factory";
    
    /** Binding factory method to get instance of factory. */
    public static final String FACTORY_INSTMETHOD = "getInstance";
    
    /** Empty argument list. */
    public static final Class[] EMPTY_ARGS = new Class[0];
 
    /**
     * Get list of bindings for class. This just accesses the static variable
     * added to each class with a top-level mapping.
     *
     * @param clas class with top-level mapping in binding
     * @return list of bindings defined for that class (as a text string)
     * @throws JiBXException on error accessing binding information
     */
    private static String getBindingList(Class clas)throws JiBXException {
        try {
            Field field = clas.getDeclaredField(BINDINGLIST_NAME);
            try {
                // should be able to access field anyway, but just in case
                field.setAccessible(true);
            } catch (Exception e) { /* deliberately left empty */ }
            return (String)field.get(null);
        } catch (NoSuchFieldException e) {
            throw new JiBXException
                ("Unable to access binding information for class " +
                clas.getName() +
                "\nMake sure the binding has been compiled", e);
        } catch (IllegalAccessException e) {
            throw new JiBXException
                ("Error in added code for class " + clas.getName() +
                "Please report this to the JiBX developers", e);
        }
    }
 
    /**
     * Get instance of factory. Loads the factory class using the classloader
     * for the supplied class, then calls the get instance method of the
     * factory class.
     *
     * @param name fully qualified name of factory class
     * @param clas class with class loader to be used for loading factory
     * @return binding factory instance
     * @throws JiBXException on error loading or accessing factory
     */
    private static IBindingFactory getFactoryFromName(String name, Class clas)
        throws JiBXException {
        Throwable ex = null;
        Object result = null;
        IBindingFactory ifact = null;
        try {
            Class factory = clas.getClassLoader().loadClass(name);
            Method method = factory.getMethod(FACTORY_INSTMETHOD, EMPTY_ARGS);
            result = method.invoke(null, null);
        } catch (SecurityException e) {
            ex = e;
        } catch (ClassNotFoundException e) {
            ex = e;
        } catch (NoSuchMethodException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        } catch (InvocationTargetException e) {
            ex = e;
        } finally {
            if (ex == null) {
                if (result instanceof IBindingFactory) {
                    ifact = (IBindingFactory)result;
                    int diff = ifact.getCompilerVersion() ^
                        IBindingFactory.CURRENT_VERSION_NUMBER;
                    if ((diff & IBindingFactory.COMPATIBLE_VERSION_MASK) != 0) {
                        throw new JiBXException
                            ("Binding information for class " + clas.getName() +
                            " must be recompiled with current binding " +
                            "compiler (compiled with " +
                            ifact.getCompilerDistribution() + ", runtime is " +
                            IBindingFactory.CURRENT_VERSION_NAME + ")");
                    }
                } else {
                    throw new JiBXException
                        ("Binding information for class " + clas.getName() +
                        " must be regenerated with current binding " +
                        "compiler");
                }
            } else {
                throw new JiBXException
                    ("Unable to access binding information for class " +
                    clas.getName() + "\nMake sure classes generated by the " +
                    "binding compiler are available at runtime", ex);
            }
        }
        return ifact;
    }
 
    /**
     * Get instance of binding factory. Finds the binding factory for the
     * named binding on the target class, then loads that factory and returns
     * an instance.
     *
     * @param name binding name
     * @param clas target class for binding
     * @return binding factory instance
     * @throws JiBXException on any error in finding or accessing factory
     */
    public static IBindingFactory getFactory(String name, Class clas)
        throws JiBXException {
        String list = getBindingList(clas);
        String match = BINDINGFACTORY_PREFIX + name +
            BINDINGFACTORY_SUFFIX + '|';
        int index = list.indexOf(match);
        if (index >= 0) {
            int mark = list.lastIndexOf('|', index);
            String fname =
                list.substring(mark+1, index + match.length() - 1);
            mark = fname.indexOf('=');
            if (mark >= 0) {
                fname = fname.substring(0, mark);
            }
            return getFactoryFromName(fname, clas);
        } else {
            throw new JiBXException("Binding " + name + 
                " not found for class " + clas.getName());
        }
    }
 
    /**
     * Get instance of binding factory. Finds the binding factory for the
     * target class, then loads that factory and returns an instance. This
     * method can only be used with target classes that are mapped in only
     * one binding.
     *
     * @param clas target class for binding
     * @return binding factory instance
     * @throws JiBXException on any error in finding or accessing factory
     */
    public static IBindingFactory getFactory(Class clas) throws JiBXException {
        String list = getBindingList(clas);
        if (list != null && list.length() > 2) {
            String fact = list.substring(1, list.length()-1);
            if (fact.indexOf('|') < 0) {
                return getFactoryFromName(fact, clas);
            }
        }
        throw new JiBXException("Multiple bindings defined for class " +
            clas.getName());
    }
}