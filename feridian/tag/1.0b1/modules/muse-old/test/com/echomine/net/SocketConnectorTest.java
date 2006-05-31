package com.echomine.net;

import alt.java.net.Socket;
import alt.java.net.SocketImpl;
import com.echomine.util.IOUtil;
import junit.framework.TestCase;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * this tests all the possible socket acceptor codes
 */
public class SocketConnectorTest extends TestCase {
    ConnectionModel model;
    SocketConnector connector;
    SocketHandler handler;
    MyAcceptorThread thr = new MyAcceptorThread();

    public SocketConnectorTest(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        model = new ConnectionModel("127.0.0.1", 7000);
        handler = new SocketHandler() {
            public void handle(Socket socket) throws IOException {
                //now simply write out the string
                socket.getOutputStream().write("Success".getBytes());
                socket.getOutputStream().flush();
            }

            public void shutdown() {
            }

            public void start() {
            }
        };
        connector = new SocketConnector(handler);
        thr.open();
    }

    protected void tearDown() throws Exception {
        thr.close();
        //sleep for a short period so that there is time for the thread and socket
        //to close down
        Thread.sleep(200);
    }

    /**
     * Tests the asynchronous accept to see if everything is working correctly
     */
    public void testSocketConnectorNormalAsync() throws Exception {
        thr.start();
        connector.aconnect(model);
        Thread.sleep(500);
        assertEquals("Success", thr.str);
    }

    /**
     * The test will check for a normal response from the acceptor
     */
    public void testSocketConnectorNormalSync() throws Exception {
        thr.start();
        Thread.sleep(500);
        connector.connect(model);
        while (!thr.isFinished) Thread.yield();
        assertEquals("Success", thr.str);
    }

    /**
     * Tests to see whether the connection events are being properly propogated
     */
    public void testConnectorConnectionListener() throws Exception {
        ConnectorConnectionListener l = new ConnectorConnectionListener();
        thr.start();
        Thread.sleep(500);
        connector.addConnectionListener(l);
        connector.connect(model);
        assertTrue(l.starting == true);
        assertTrue(l.established == true);
        assertTrue(l.closed == true);
    }

    /**
     * Tests the vetoing capability
     */
    public void testConnectorVetoedConnectionListener() throws Exception {
        VetoedConnectionListener l = new VetoedConnectionListener();
        thr.start();
        Thread.sleep(500);
        connector.addConnectionListener(l);
        connector.connect(model);
        assertTrue(l.starting == true);
        assertTrue(l.established == false);
        assertTrue(l.closed == true);
    }

    class ConnectorConnectionListener implements ConnectionListener {
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

    class VetoedConnectionListener extends ConnectorConnectionListener {
        public void connectionStarting(ConnectionEvent e) throws ConnectionVetoException {
            super.connectionStarting(e);
            throw new ConnectionVetoException("Vetoed");
        }
    }

    class MyAcceptorThread extends Thread {
        String str = "";
        ServerSocket ssocket;
        boolean isFinished;

        public MyAcceptorThread() {
        }

        public void open() throws IOException {
            ssocket = new ServerSocket(7000);
        }

        public void run() {
            Socket socket = null;
            isFinished = false;
            try {
                socket = new SocketImpl(ssocket.accept());
                byte[] data = new byte[10];
                int bread = socket.getInputStream().read(data);
                str = new String(data, 0, bread);
            } catch (Exception ex) {
            } finally {
                IOUtil.closeSocket(socket);
                isFinished = true;
            }
        }

        public void close() {
            IOUtil.closeSocket(ssocket);
        }
    }
}
