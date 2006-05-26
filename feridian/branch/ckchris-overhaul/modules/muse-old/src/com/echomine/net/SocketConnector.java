package com.echomine.net;

import alt.java.net.SocketImpl;
import com.echomine.util.IOUtil;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * <p>Makes a connection to a remote client using TCP protocol.  The connector is synchronous and asynchronous; Asynchronous
 * connections have methods that begin with an "a" (ie. aconnect).  This class is simply a helper class so you don't have to
 * deal with the connection details.  Most of the details should still be implemented by the writer in the
 * SocketHandler instances.</p> <p>There are four ways to call connect.  Aside from the synchronous and asynchronous, the
 * choice is yours to use an internal socket handler or pass in your own external socket handler.  The connector is basically
 * amphibious, acting both as an instance-based single-threaded object (ie. one connector per connection) and a
 * non-instance-based multi-threaded object (ie. one connector for multiple handlers/connections).  The recommended usage is:
 * if you require listening to connection events for each connection made, then instantiate a connector for each handler (a
 * 1-to-1 connector-handler pairing); if you don't care about connection events (or if your handler fires its own events that
 * you need), then you can instantiate one connector for multiple handlers (ie. a 1-to-many connector-handler pairing).</p>
 * <p>This class also fully support SSL connections.  Certain properties can be set to change the location on where to look
 * for the keystore, passphrase, etc.</p>
 * <p>The SSL-related key property names are: com.echomine.net.keyStorePath, com.echomine.netkeyStorePassphrase,
 * com.echomine.net.trustManager.  By default, the keystore is ~/.keystore, the passphrase is empty, and trust manager
 * uses com.echomine.util.SimpleTrustManager.</p>
 */
public class SocketConnector extends TimeableConnection {

    /**
     * property that sets the name of the key store file
     */
    private static final String KEY_KEYSTORE = "com.echomine.net.keyStorePath";
    /**
     * default value of the key store file - ~/.keystore
     */
    private static final String VALUE_KEYSTORE = System.getProperty("user.home") + System.getProperty("file.separator") + ".keystore";
    /**
     * property that sets the key store password
     */
    private static final String KEY_PASSPHRASE = "com.echomine.net.keyStorePassphrase";
    /**
     * default value of the key store password - ""
     */
    private static final String VALUE_PASSPHRASE = "";
    /**
     * property that sets the class name of the trust manager
     */
    private static final String KEY_TRUSTMANAGER = "com.echomine.net.trustManager";
    /**
     * default value of the trust manager implementation class
     */
    private static final String VALUE_TRUSTMANAGER = "com.echomine.util.SimpleTrustManager";

    private SocketHandler socketHandler;

    public SocketConnector(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    /**
     * Do-nothing constructor.  Usually used for multi-threading reuse of this instance since the Handler can be passed in as
     * a parameter for connect().
     */
    public SocketConnector() {
    }

    /**
     * Synchronous connect method using internal socket handler.  The method will return when
     * handling of the connection is finished.
     */
    public void connect(ConnectionModel connectionModel) throws ConnectionFailedException {
        connect(socketHandler, connectionModel);
    }

    /**
     * helper to return the String array of all ciphers we could use
     */
    private String[] getCiphers(SSLSocket s) {
        int num = 0;
        String[] supported = s.getSupportedCipherSuites(); // cipher suites supported by the implementation
        int[] cipherIndexes = new int[supported.length];     // indexes to supported ciphers
        for (int i = 0; i < supported.length; i++) {
            if (supported[i].toLowerCase().indexOf("null") < 0) cipherIndexes[num++] = i;
        }
        String[] wesupport = new String[num];
        for (int i = 0; i < num; i++) {
            wesupport[i] = supported[cipherIndexes[i]];
        }
        return wesupport;
    }

    /**
     * helper method to negotiate the SSL connection before passing the connection handling to the
     * SocketHandler. Throws an IOException if any error occurs in the negotiation.
     */
    private Socket negotiateSSLConnection(ConnectionModel connectionModel) throws IOException {
        InetAddress host = connectionModel.getHost();
        int port = connectionModel.getPort();
        String keyStorePath = System.getProperty(KEY_KEYSTORE, VALUE_KEYSTORE);
        String keyStorePassphrase = System.getProperty(KEY_PASSPHRASE, VALUE_PASSPHRASE);
        char[] keyStorePassword = keyStorePassphrase.toCharArray();
        // Open the keystore file.
        FileInputStream keyStoreIStream = null;
        try {
            // Open the stream to read in the keystore.
            keyStoreIStream = new FileInputStream(keyStorePath);
        } catch (FileNotFoundException e) {
            // If the path does not exist then a null stream means
            // the keystore is initialized empty. If an untrusted
            // certificate chain is trusted by the user, then it will be
            // saved in the file pointed to by keyStorePath.
            keyStoreIStream = null;
        }
        // Create a KeyStore Object - in memory collection of keys and certs
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); // ususally returns 'jks'}
        } catch (KeyStoreException e) {
            e.printStackTrace();             // Tell someone
            throw new IOException("Unable to get a keystore of type " + KeyStore.getDefaultType());
        }
        // Init the Keystore with the contents of the keystore file.
        // If the input stream is null the keystore is initialized empty.
        try {
            keyStore.load(keyStoreIStream, keyStorePassword);  // throws IOException
        } catch (NoSuchAlgorithmException nsa) {
            nsa.printStackTrace();             // Tell someone
            throw new IOException("No such algorithm - keystore load");
        } catch (CertificateException ce) {
            ce.printStackTrace();             // Tell someone
            throw new IOException("Certificate exception - keystore load");
        }
        // Close keystore input stream
        if (keyStoreIStream != null) {
            keyStoreIStream.close();
            keyStoreIStream = null;
        }
        // Create an SSL context
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();             // Tell someone
            throw new IOException("No such algorithm - getting ssl context");
        }
        // Create a KMF (source of authentication keys) & initialize it with Sun default manager
        KeyManagerFactory keyManagerFactory = null;
        try {
            keyManagerFactory = KeyManagerFactory.getInstance("SunX509"); // pass algorithm name
        } catch (NoSuchAlgorithmException nsa) {
            nsa.printStackTrace();             // Tell someone
            throw new IOException("No such algorithm - getting keymgr. factory");
        }
        try {
            keyManagerFactory.init(keyStore, keyStorePassword);
        } catch (KeyStoreException kse) {
            kse.printStackTrace();             // Tell someone
            throw new IOException("Key store exception - keystore init");
        } catch (NoSuchAlgorithmException nsa) {
            nsa.printStackTrace();             // Tell someone
            throw new IOException("No such algorithm - keystore init");
        } catch (UnrecoverableKeyException uke) {
            uke.printStackTrace();             // Tell someone
            throw new IOException("Unrecoverable key exception - keystore init");
        }
        // Instantiate a TrustManager that will handle server certificate
        // chains. The source of peer authentication trust decisions.
        String trustManager = System.getProperty(KEY_TRUSTMANAGER, VALUE_TRUSTMANAGER);
        Class tmClass = null;
        try {
            tmClass = Class.forName(trustManager, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException chfe) { // Give up
            chfe.printStackTrace();
            System.exit(3);
        }
        // We assume that there is only 1 constuctor with three arguments
        // FIXME: Check for more than 1 constr - throw?
        Constructor[] constrs = tmClass.getConstructors(); // throws SecurityException
        Object[] args = {keyStore, keyStorePath, keyStorePassword};
        TrustManager tm = null;
        try {
            tm = (TrustManager) constrs[0].newInstance(args);  // make a new TrustManager
        } catch (InstantiationException ie) {
            ie.printStackTrace();                        // Give up
            System.exit(4);
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();                       // Give up
            System.exit(5);
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();                       // Give up
            System.exit(6);
        }
        TrustManager[] trustManagers = {tm};
        // Initialize the context with the key managers
        try {
            context.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
        } catch (KeyManagementException e) {
            throw new IOException("Unable to initiaize SSL context");
        }

        // Get a sockey factory
        SSLSocketFactory sslSocketfactory = context.getSocketFactory();

        // Make a socket from the factory
        SSLSocket socket = null;
        try {
            socket = (SSLSocket) sslSocketfactory.createSocket(host, port); // throws IOException
        } catch (UnknownHostException uhe) {
            throw new IOException("Unknown host exception");
        }
        socket.setUseClientMode(true);
        socket.setEnabledCipherSuites(getCiphers(socket));
        socket.startHandshake();                             // synchronous for the first time, throws IOException
        return socket;
    }

    /**
     * Synchronous connect method.  The method will return when handling of the connection is finished.
     */
    public void connect(SocketHandler socketHandler, ConnectionModel connectionModel) throws ConnectionFailedException {
        alt.java.net.Socket socket = null;
        try {
            ConnectionEvent event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_STARTING);
            ConnectionEvent vetoEvent = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_VETOED);
            fireConnectionStarting(event, vetoEvent);
            socketHandler.start();
            if (connectionModel.isSSL()) {
                socket = new SocketImpl(negotiateSSLConnection(connectionModel));  // throws IOException
            } else {
                socket = new SocketImpl(new Socket(connectionModel.getHost(), connectionModel.getPort()));
            }
            try {
                event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_OPENED);
                fireConnectionEstablished(event);
                socketHandler.handle(socket);
                event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_CLOSED);
                fireConnectionClosed(event);
            } catch (IOException ex) {
                event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error while handling connection: " + ex.getMessage());
                fireConnectionClosed(event);
            } finally {
                IOUtil.closeSocket(socket);
            }
        } catch (IOException ex) {
            ConnectionEvent event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error connecting to host: " + ex.getMessage());
            fireConnectionClosed(event);
            throw new ConnectionFailedException("Cannot Connect to remote host");
        } catch (ConnectionVetoException ex) {
            //do nothing, connection closed event already fired
        }
    }

    /**
     * makes a connection asynchronously using internal socket handler.  This means that the method will be run in a separate
     * thread and return control to the caller of the method immediately.
     */
    public void aconnect(ConnectionModel connectionModel) {
        aconnect(socketHandler, connectionModel);
    }

    /**
     * makes a connection asynchronously.  This means that the method will be run in a separate thread and return control to
     * the caller of the method immediately.
     */
    public void aconnect(final SocketHandler socketHandler, final ConnectionModel connectionModel) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                alt.java.net.Socket socket = null;
                try {
                    ConnectionEvent event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_STARTING);
                    ConnectionEvent vetoEvent = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_VETOED);
                    fireConnectionStarting(event, vetoEvent);
                    socketHandler.start();
                    if (connectionModel.isSSL()) {
                        socket = new SocketImpl(negotiateSSLConnection(connectionModel));
                    } else {
                        socket = new SocketImpl(new Socket(connectionModel.getHost(), connectionModel.getPort()));
                    }
                    try {
                        event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_OPENED);
                        fireConnectionEstablished(event);
                        socketHandler.handle(socket);
                        event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_CLOSED);
                        fireConnectionClosed(event);
                    } catch (IOException ex) {
                        event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error while handling connection: " + ex.getMessage());
                        fireConnectionClosed(event);
                    } finally {
                        IOUtil.closeSocket(socket);
                    }
                } catch (IOException ex) {
                    ConnectionEvent event = new ConnectionEvent(connectionModel, ConnectionEvent.CONNECTION_ERRORED, "Error..." + ex.getMessage());
                    fireConnectionClosed(event);
                } catch (ConnectionVetoException ex) {
                    //do nothing, connection closed event already fired
                }
            }
        });
        thread.start();
    }

    public SocketHandler getSocketHandler() {
        return socketHandler;
    }

    public void setSocketHandler(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }
}
