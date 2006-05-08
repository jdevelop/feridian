package com.echomine.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class MockSSLSocket extends SSLSocket {
    String[] supportedCipherSuites;
    String[] enabledProtocols;
    boolean wantClientAuth;
    boolean clientMode;
    boolean needClientAuth;
    boolean createSession;
    InputStream is;
    OutputStream os;
    boolean handshakeSuccess;

    public MockSSLSocket() {
        super();
    }

    /**
     * Sets whether handshake is successful or not. If set to false, then the
     * startHandshake will thrown an exception.
     * 
     * @param success
     */
    public void setSuccessfulHandshake(boolean success) {
        this.handshakeSuccess = success;
    }

    /**
     * sets the input stream to use in the mock object
     * 
     * @param is the input stream
     */
    public void setInputStream(InputStream is) {
        this.is = is;
    }

    /**
     * @return the input stream
     */
    public InputStream getInputStream() {
        return is;
    }

    /**
     * Sets the output stream to use in the mock object
     * 
     * @param os the output stream
     */
    public void setOutputStream(OutputStream os) {
        this.os = os;
    }

    /**
     * @return the output stream
     */
    public OutputStream getOutputStream() {
        return os;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#getSupportedCipherSuites()
     */
    public String[] getSupportedCipherSuites() {
        return supportedCipherSuites;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#getEnabledCipherSuites()
     */
    public String[] getEnabledCipherSuites() {
        return supportedCipherSuites;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#setEnabledCipherSuites(java.lang.String[])
     */
    public void setEnabledCipherSuites(String[] ciphers) {
        this.supportedCipherSuites = ciphers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#getSupportedProtocols()
     */
    public String[] getSupportedProtocols() {
        return enabledProtocols;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#getEnabledProtocols()
     */
    public String[] getEnabledProtocols() {
        return enabledProtocols;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#setEnabledProtocols(java.lang.String[])
     */
    public void setEnabledProtocols(String[] protocols) {
        this.enabledProtocols = protocols;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#getSession()
     */
    public SSLSession getSession() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#addHandshakeCompletedListener(javax.net.ssl.HandshakeCompletedListener)
     */
    public void addHandshakeCompletedListener(HandshakeCompletedListener arg0) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#removeHandshakeCompletedListener(javax.net.ssl.HandshakeCompletedListener)
     */
    public void removeHandshakeCompletedListener(HandshakeCompletedListener arg0) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
     * Does nothing for mock handshake
     * 
     * @see javax.net.ssl.SSLSocket#startHandshake()
     */
    public void startHandshake() throws IOException {
        if (!handshakeSuccess)
            throw new IOException("Mock Handshake Exception");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#setUseClientMode(boolean)
     */
    public void setUseClientMode(boolean clientMode) {
        this.clientMode = clientMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#getUseClientMode()
     */
    public boolean getUseClientMode() {
        return clientMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#setNeedClientAuth(boolean)
     */
    public void setNeedClientAuth(boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#getNeedClientAuth()
     */
    public boolean getNeedClientAuth() {
        return needClientAuth;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#setWantClientAuth(boolean)
     */
    public void setWantClientAuth(boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#getWantClientAuth()
     */
    public boolean getWantClientAuth() {
        return wantClientAuth;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#setEnableSessionCreation(boolean)
     */
    public void setEnableSessionCreation(boolean createSession) {
        this.createSession = createSession;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.net.ssl.SSLSocket#getEnableSessionCreation()
     */
    public boolean getEnableSessionCreation() {
        return createSession;
    }

}
