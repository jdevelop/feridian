package com.echomine.net;

import alt.java.net.Socket;
import alt.java.net.SocketImpl;
import com.echomine.util.IOUtil;
import junit.framework.TestCase;

import java.io.IOException;

/**
 * this tests all the possible socket acceptor codes
 */
public class SocketAcceptorTest extends TestCase {
    ConnectionModel model;
    SocketAcceptor acceptor;
    SocketHandler handler;

    public SocketAcceptorTest(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        model = new ConnectionModel("127.0.0.1", 7000);
        acceptor = new SocketAcceptor(model);
        handler = new SocketHandler() {
            public void handle(Socket socket) throws IOException {
                //now simply write out the string
                socket.getOutputStream().write("Success".getBytes());
            }

            public void shutdown() {
            }

            public void start() {
            }
        };
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     */
    protected void tearDown() throws Exception {
        //shutdown the socket
        acceptor.close();
        //sleep for a short period so that there is time for the thread and socket
        //to close down
        Thread.sleep(100);
    }

    /**
     * Tests the asynchronous accept to see if everything is working correctly
     */
    public void testSocketAcceptorNormalAsync() throws Exception {
        acceptor.aaccept(handler);
        //now let's simulate a connect
        Socket socket = new SocketImpl(new java.net.Socket("127.0.0.1", 7000));
        byte[] data = new byte[10];
        int bytesRead = socket.getInputStream().read(data);
        if (bytesRead == -1)
            fail("Unable to read any bytes");
        String result = new String(data, 0, bytesRead);
        assertEquals("Success", result);
        IOUtil.closeSocket(socket);
    }

    /**
     * The test will check for a normal response from the acceptor
     */
    public void testSocketAcceptorNormalSync() throws Exception {
        //must run in separate thread as we're testing the synchronous method
        Thread thread = new Thread() {
            public void run() {
                acceptor.accept(handler);
            }
        };
        thread.start();
        //now let's simulate a connect
        Socket socket = new SocketImpl(new java.net.Socket("127.0.0.1", 7000));
        byte[] data = new byte[10];
        int bytesRead = socket.getInputStream().read(data);
        if (bytesRead == -1)
            fail("Unable to read any bytes");
        String result = new String(data, 0, bytesRead);
        assertEquals("Success", result);
        IOUtil.closeSocket(socket);
    }

    /**
     * Tests to see whether the connection events are being properly propogated
     */
    public void testAcceptorConnectionListener() throws Exception {
        AcceptorConnectionListener l = new AcceptorConnectionListener();
        acceptor.aaccept(handler);
        acceptor.addConnectionListener(l);
        Socket socket = new SocketImpl(new java.net.Socket("127.0.0.1", 7000));
        //no need to read data as we're only checking connection event
        Thread.sleep(500);
        IOUtil.closeSocket(socket);
        assertTrue(l.starting == true);
        assertTrue(l.established == true);
        assertTrue(l.closed == true);
    }

    /**
     * Tests the vetoing capability
     */
    public void testAcceptorVetoedConnectionListener() throws Exception {
        VetoedConnectionListener l = new VetoedConnectionListener();
        acceptor.aaccept(handler);
        acceptor.addConnectionListener(l);
        Socket socket = new SocketImpl(new java.net.Socket("127.0.0.1", 7000));
        //no need to read data as we're only checking connection event
        Thread.sleep(500);
        IOUtil.closeSocket(socket);
        assertTrue(l.starting == true);
        assertTrue(l.established == false);
        assertTrue(l.closed == true);
    }

    class AcceptorConnectionListener implements ConnectionListener {
        boolean starting = false,
        established = false,
        closed = false;

        public void connectionStarting(ConnectionEvent e) throws ConnectionVetoException {
            starting = true;
        }

        public void connectionEstablished(ConnectionEvent e) {
            established = true;
        }

        public void connectionClosed(ConnectionEvent e) {
            closed = true;
        }
    }

    class VetoedConnectionListener extends AcceptorConnectionListener {
        public void connectionStarting(ConnectionEvent e) throws ConnectionVetoException {
            super.connectionStarting(e);
            throw new ConnectionVetoException("Vetoed");
        }
    }
}
