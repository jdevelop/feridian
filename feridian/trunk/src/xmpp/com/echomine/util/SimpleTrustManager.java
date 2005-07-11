package com.echomine.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.X509TrustManager;

/**
 * This sub_class implements the X509TrustManager interface. MyTrustManager
 * trusts known certificate chains, and queries the user to approve unknown
 * chains. It will add trusted chains to the keystore. This class is provided as
 * an example implementation. Depending upon the calling application, it is
 * likely that this would be reimplemented to graphically display and prompt for
 * data. Set the property com.echomine.util.SimpleTrustManager.prompt to "true"
 * (default is "false") if you want the class to prompt for acceptance.
 */
public class SimpleTrustManager implements X509TrustManager {
    /**
     * property for setting promting true/on/false/off
     */
    private static final String KEY_PROMPT = "com.echomine.util.SimpleTrustManager.prompt";
    /**
     * default value for prompts
     */
    private static final String VALUE_PROMPT = "false";

    private KeyStore keyStore;
    private String keyStorePath;
    private char[] keyStorePassword;

    /**
     * SimpleTrustManager constructor. Save the keyStore object along with the
     * path to the keystore (keyStorePath) and its password (keyStorePassword).
     * If you reimplement this class be warned that SocketConnector assumes
     * there is only one constructor that can be passed the same three arguments
     * as below.
     */
    public SimpleTrustManager(KeyStore keyStore, String keyStorePath, char[] keyStorePassword) {
        this.keyStore = keyStore;
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * isClientTrusted checks to see if the chain is in the keyStore object.
     * This is done with a call to checkChainTrusted.
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        checkChainTrusted(chain);
    }

    /**
     * checks to see if the chain is in the keyStore object. This is done with a
     * call to checkChainTrusted. If not it queries the user to see if the chain
     * should be trusted and stored into the keyStore object. The keyStore is
     * then saved in the file whose path is keyStorePath. Examines the system
     * property com.echomine.util.SimpleTrustManager.prompt to determine whether
     * user should be prompted.
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        String promptVal = System.getProperty(KEY_PROMPT, VALUE_PROMPT);
        boolean prompt = true;
        if (promptVal.compareToIgnoreCase("false") == 0 || promptVal.compareToIgnoreCase("no") == 0)
            prompt = false;
        try {
            checkChainTrusted(chain); // Is the chain is in the keyStore
                                        // object?
        } catch (CertificateException ex) {
            if (prompt)
                System.out.println("Untrusted Certificate chain:");
            for (int i = 0; i < chain.length; i++) {
                // display certificate chain information
                if (prompt)
                    System.out.println("Certificate chain[" + i + "]:");
                if (prompt)
                    System.out.println("Subject: " + chain[i].getSubjectDN().toString());
                if (prompt)
                    System.out.println("Issuer: " + chain[i].getIssuerDN().toString());
            }
            // Ask the user if the certificate should be trusted.
            if (prompt)
                System.out.println("Trust this certificate chain and" + " add it to the keystore? (y or n) ");
            String s = new String("y");
            if (prompt) {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    s = in.readLine();
                } catch (IOException ioe) {
                    System.err.println("Unable to read input - assuming affirmative response");
                }
            }
            if (s.compareToIgnoreCase("y") == 0) {
                // Trust the chain.
                try {
                    for (int i = 0; i < chain.length; i++) { // Add Chain to
                                                                // the keyStore.
                        keyStore.setCertificateEntry(chain[i].getIssuerDN().toString(), chain[i]); // throws
                                                                                                    // KeyStoreException
                    }
                } catch (KeyStoreException kse) {
                    System.err.println("Unable to add a certificate to the keystore");
                }
                if (prompt)
                    System.out.println("Saving the certificate chain to the keystore.");
                FileOutputStream keyStoreOStream = null;
                try {
                    keyStoreOStream = new FileOutputStream(keyStorePath);
                    try {
                        keyStore.store(keyStoreOStream, keyStorePassword);
                    } catch (CertificateException ce) {
                        System.err.println("Certificate exception when trying to store the key in the keystore");
                    } catch (KeyStoreException kse) {
                        System.err.println("Key store exception when trying to store the key in the keystore");
                    } catch (IOException ioe) {
                        System.err.println("Io exception when trying to store the key in the keystore");
                    } catch (NoSuchAlgorithmException nsae) {
                        System.err.println("No such algorithm exception when trying to store the key in the keystore");
                    }
                } catch (FileNotFoundException fnfe) {
                    System.err.println("Keystore file: " + keyStorePath + " not found - key not saved");
                } finally {
                    if (keyStoreOStream != null) {
                        try {
                            keyStoreOStream.close();
                            System.out.println("Keystore saved in " + keyStorePath);
                        } catch (IOException ioe) {
                            System.err.println("Unable to close the key store file");
                        }
                        keyStoreOStream = null;
                    }
                }
            } else {
                throw new CertificateException("Server Chain is not trusted");
            }
        }
    }

    // getAcceptedIssuers retrieves all of the certificates in the keyStore
    // and returns them in an X509Certificate array.
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] X509Certs = null;
        try {
            // See how many certificates are in the keystore.
            int numberOfEntry = keyStore.size();
            // If there are any certificates in the keystore.
            if (numberOfEntry > 0) {
                // Create an array of X509Certificates
                X509Certs = new X509Certificate[numberOfEntry];

                // Get all of the certificate alias out of the keystore.
                Enumeration aliases = keyStore.aliases();

                // Retrieve all of the certificates out of the keystore
                // via the alias name.
                int i = 0;
                while (aliases.hasMoreElements()) {
                    X509Certs[i] = (X509Certificate) keyStore.getCertificate((String) aliases.nextElement());
                    i++;
                }

            }
        } catch (Exception e) {
            System.out.println("getAcceptedIssuers Exception: " + e.toString());
            X509Certs = null;
        }
        return X509Certs;
    }

    /**
     * checkChainTrusted searches the keyStore for any certificate in the
     * certificate chain.
     * 
     * @throws CertificateException if the chain is not trusted
     */
    private void checkChainTrusted(X509Certificate[] chain) throws CertificateException {
        try {
            // Start with the root and see if it is in the Keystore.
            // The root is at the end of the chain.
            for (int i = chain.length - 1; i >= 0; i--)
                if (keyStore.getCertificateAlias(chain[i]) != null)
                    break;
        } catch (Exception ex) {
            System.out.println("checkChainTrusted Exception: " + ex.toString());
            throw new CertificateException("Chain is not trusted: " + ex.getMessage());
        }
    }
}
