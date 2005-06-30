package com.echomine.muse;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;

import com.echomine.jibx.JiBXUtil;

/**
 * Holds all the muse configuration information. The muse-config.xml file is
 * searched inside the "/" classpath first. If none is found, it then searches
 * in the META-INF/.
 *  
 */
public class MuseConfiguration {
    private static final Log log = LogFactory.getLog(MuseConfiguration.class);
    private static final String CONFIG_FILENAME = "muse-config.xml";
    private static MuseConfiguration config;
    private HashMap nsMappings;

    public static MuseConfiguration getConfig() throws JiBXException {
        if (config == null) {
            InputStream is = MuseConfiguration.class.getResourceAsStream("/" + CONFIG_FILENAME);
            if (is == null)
                is = MuseConfiguration.class.getResourceAsStream("/META-INF/" + CONFIG_FILENAME);
            if (is != null) {
                Reader rdr = new InputStreamReader(is);
                config = getConfig(rdr);
            } else {
                if (log.isWarnEnabled())
                    log.warn("Unable to find muse-config.xml in / or /META-INF.  Using empty config");
            }
        }
        return config;
    }

    /**
     * This will retrieve the configuration based on the provided InputStream.
     * If a configuration already exists, then this will reload the
     * configuration from the provided stream.
     * 
     * @param rdr the input stream with the file
     * @return the muse configuration
     * @throws JiBXException
     */
    public static MuseConfiguration getConfig(Reader rdr) throws JiBXException {
        if (rdr == null) throw new IllegalArgumentException("Reader stream cannot be null");
        if (config == null)
	        config = (MuseConfiguration) JiBXUtil.unmarshallObject(rdr, MuseConfiguration.class);
        return config;
    }

    /**
     * Obtains the class that is associated with the specified URI. If the class
     * cannot be found or if the URI cannot be found, then null is returned. No
     * exceptions are thrown.
     * 
     * @param ns the namespace URI to look up
     * @return the class associated with the NS or null if not found
     */
    public Class getClassForURI(String ns) {
        if (ns == null)
            return null;
        String className = (String) nsMappings.get(ns);
        if (className == null)
            return null;
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    /**
     * resets all the configuration settings back to its pristine state so that
     * the configuration object can be reused.
     */
    private void reset() {
        if (nsMappings != null)
            nsMappings.clear();
    }
}
