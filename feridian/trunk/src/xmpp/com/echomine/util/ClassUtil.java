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
}
