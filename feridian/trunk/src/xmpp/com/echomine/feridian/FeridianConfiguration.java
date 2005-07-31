package com.echomine.feridian;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;

import com.echomine.jibx.JiBXUtil;

/**
 * Holds all the configuration information. The feridian-config.xml file is
 * searched inside the "/" classpath first. If none is found, it then searches
 * in the META-INF/.
 * 
 * This configuration will load extensions off extension config files. It will
 * search through all /META-INF/feridian-extensions.xml files and parse them.
 * The order is not guaranteed. If any extensions conflict (ie. different
 * classes declared for the same namespace), then the last parsed extension will
 * be the one used.
 */
public class FeridianConfiguration {
    private static final Log log = LogFactory.getLog(FeridianConfiguration.class);
    private static final String CONFIG_FILENAME = "feridian-config.xml";
    private static final String DEFAULT_CONFIG_FILENAME = "feridian-config-default.xml";
    private static final String EXTENSIONS_FILENAME = "feridian-extensions.xml";
    private static FeridianConfiguration config;

    private HashMap extMappings = new HashMap();
    private HashMap streamMappings = new HashMap();
    private Class connectionFactoryClass;
    private Class streamFactoryClass;
    private Class idGeneratorClass;

    /**
     * Obtains the config file.
     * 
     * @return the config instance
     * @throws JiBXException when error occurs during loading of configuration
     */
    public static FeridianConfiguration getConfig() throws JiBXException {
        if (config == null) {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILENAME);
            if (is == null)
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/" + CONFIG_FILENAME);
            if (is == null)
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/" + DEFAULT_CONFIG_FILENAME);
            if (is != null) {
                Reader rdr = new InputStreamReader(is);
                config = getConfig(rdr);
            } else {
                if (log.isWarnEnabled())
                    log.warn("Unable to find feridian-config.xml in / or META-INF/.  Using empty config");
                config = new FeridianConfiguration();
            }
        }
        config.loadExtensions();
        return config;
    }

    /**
     * This will retrieve the configuration based on the provided InputStream.
     * If a configuration already exists, then this replace the current config.
     * 
     * @param rdr the input stream with the file
     * @return the configuration
     * @throws JiBXException if error occurs when loading config
     */
    public static FeridianConfiguration getConfig(Reader rdr) throws JiBXException {
        if (rdr == null)
            throw new IllegalArgumentException("Reader stream cannot be null");
        config = (FeridianConfiguration) JiBXUtil.unmarshallObject(rdr, FeridianConfiguration.class);
        config.loadExtensions();
        return config;
    }

    /**
     * Loads the extensions if there are any. It looks for all resources located
     * in META-INF/feridian-extensions.xml. All extensions must create the exact
     * file and place it in the META-INF/ directory inside the jar.
     * 
     * @throws IOException
     * 
     */
    private void loadExtensions() {
        try {
            Enumeration resources = Thread.currentThread().getContextClassLoader().getResources("META-INF/" + EXTENSIONS_FILENAME);
            URL url;
            FeridianExtensions extensions;
            while (resources.hasMoreElements()) {
                url = (URL) resources.nextElement();
                try {
                    extensions = (FeridianExtensions) JiBXUtil.unmarshallObject(new InputStreamReader(url.openStream(), "UTF-8"), FeridianExtensions.class);
                    Iterator iter = extensions.getPacketExtensions().iterator();
                    FeridianPacketExtension ext;
                    while (iter.hasNext()) {
                        ext = (FeridianPacketExtension) iter.next();
                        if (log.isInfoEnabled())
                            log.info("Packet Extension Found: URI=" + ext.getNamespace() + ", class=" + ext.getPacketClass().getName());
                        extMappings.put(ext.getNamespace(), ext.getPacketClass());
                    }
                    iter = extensions.getStreamList().iterator();
                    FeridianStreamExtension stream;
                    while (iter.hasNext()) {
                        stream = (FeridianStreamExtension) iter.next();
                        if (log.isInfoEnabled())
                            log.info("Stream Extension Found: URI=" + stream.getNamespace() + ", class=" + stream.getStreamClass().getName());
                        streamMappings.put(stream.getNamespace(), stream);
                    }
                } catch (Throwable thr) {
                    if (log.isWarnEnabled())
                        log.warn("Skipping... Unable to properly load feridian extension file. Check configuration file.", thr);
                }
            }
        } catch (IOException ex) {
            if (log.isWarnEnabled())
                log.warn("Exception occurred while trying to obtain a list of extensions resources", ex);
        }
    }

    /**
     * Obtains the class that is associated with the specified extension URI. If the
     * class cannot be found or if the URI cannot be found, then null is
     * returned. No exceptions are thrown. This is used only to obtain the Class
     * associated with iq or extension packets URIs
     * 
     * @param ns the namespace URI to look up
     * @return the class associated with the NS or null if not found
     */
    public Class getClassForUri(String ns) {
        if (ns == null)
            return null;
        return (Class) extMappings.get(ns);
    }

    /**
     * Retrieves the stream class for the specified feature namespace.
     * 
     * @param ns the namespace uri to look up
     * @return the class associated with the namespace or null if not found
     */
    public Class getStreamForFeature(String ns) {
        if (ns == null)
            return null;
        FeridianStreamExtension ext = (FeridianStreamExtension) streamMappings.get(ns);
        if (ext != null)
            return ext.getStreamClass();
        return null;
    }

    /**
     * This is used to retrieve the class associated with a specific stream
     * feature URI.
     * 
     * @param ns the namespace uri to look up
     * @return the class associated with the namespace or null if not found
     */
    public Class getUnmarshallerForFeature(String ns) {
        if (ns == null)
            return null;
        FeridianStreamExtension ext = (FeridianStreamExtension) streamMappings.get(ns);
        if (ext != null)
            return ext.getUnmarshallerClass();
        return null;
    }

    /**
     * Retrieves the factory for creating xmpp connections.
     * 
     * @return the connection factory
     */
    public Class getXMPPConnectionFactory() {
        return connectionFactoryClass;
    }

    /**
     * Retrieves the factory for creating xmpp stream objects.
     * 
     * @return the stream factory
     */
    public Class getXMPPStreamFactory() {
        return streamFactoryClass;
    }

    /**
     * the id generator class for creating unique IDs
     */
    public Class getIdGeneratorClass() {
        return idGeneratorClass;
    }
}
