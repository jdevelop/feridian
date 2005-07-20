package com.echomine.util;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * A set of utility methods for working with classes, classloading, resource
 * loading, etc.
 */
public class ClassUtil {

    /**
     * Finds and loads the class. It uses the current thread's context
     * classloader.
     * 
     * @param className
     * @return the class associated with the class name
     * @throws ClassNotFoundException if class is not found in classpath
     */
    public static Class loadClass(String className) throws ClassNotFoundException {
        if (className == null)
            return null;
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    /**
     * Retrieves the specified resource from the classpath, and create a reader
     * with the specified encoding.
     * 
     * @param res the path location of the resource
     * @param enc the encoding
     * @return the resource as a reader
     * @throws UnsupportedEncodingException if the encoding is not supported
     */
    public static Reader getResourceAsReader(String res, String enc) throws UnsupportedEncodingException {
        return new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(res), "UTF-8");
    }

    /**
     * convenience method to check, before instantiating, whether the class is a
     * subclass or implementing class of the reference class. If not an
     * exception is thrown. If no reference class is provided (ie. null), then
     * this method will act the same as the Class.newInstance() method.
     * 
     * @param clsToInstantiate the class to instantiate objects from
     * @param referenceClass checks if the specified class implements or
     *            subclasses this reference class
     * @return the instantiated object
     * @throws IllegalAccessException permission not allowed when creating
     *             instance of the class
     * @throws InstantiationException if class does not implement/subclass
     *             reference class or class cannot be instantiated
     */
    public static Object newInstance(Class clsToInstantiate, Class referenceClass) throws InstantiationException, IllegalAccessException {
        if (referenceClass != null && !referenceClass.isAssignableFrom(clsToInstantiate))
            throw new InstantiationException("The class to instantiate is not a subclass, or does not implement, the reference class");
        return clsToInstantiate.newInstance();
    }
}
