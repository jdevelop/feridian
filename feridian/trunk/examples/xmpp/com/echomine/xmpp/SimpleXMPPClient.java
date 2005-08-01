package com.echomine.xmpp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.echomine.net.ConnectionEvent;
import com.echomine.net.ConnectionListener;
import com.echomine.net.ConnectionVetoException;
import com.echomine.xmpp.packet.XMLTextPacket;

public class SimpleXMPPClient {
    private String username;
    private String password;
    private String serverName;
    private int port = IXMPPConnection.DEFAULT_XMPP_PORT;
    private IXMPPConnection conn;
    private boolean shutdown;

    public SimpleXMPPClient(String username, String password, String server) {
        this.username = username;
        this.password = password;
        this.serverName = server;
    }

    protected void setUp() throws Exception {
        conn = XMPPConnectionFactory.getFactory().createXMPPConnection();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: SimpleXMPPClient <username|'nologin'> <password> [<xmpp server>]");
            System.exit(1);
        }
        String server = "jabber.org";
        if (args.length >= 3)
            server = args[2];
        SimpleXMPPClient client = new SimpleXMPPClient(args[0], args[1], server);
        client.setUp();
        client.runConsole();
    }

    /**
     * This test method logs and and then send messages by reading the data from
     * the console keyboard. This is for debugging message sending without
     * having to recompile the test class
     */
    public void runConsole() throws Exception {
        try {
            conn.addConnectionListener(new DefaultConnectionListener());
            conn.connect(serverName, port, true);
            if (!"nologin".equals(username))
                conn.login(username, password.toCharArray(), "Home");
            String cmd;
            BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
            XMLTextPacket packet = new XMLTextPacket();
            do {
                System.out.print("Prompt? ");
                cmd = rdr.readLine();
                if ("quit".equals(cmd))
                    break;
                packet.setText(cmd);
                conn.sendPacket(packet, false);
            } while (!shutdown);
        } finally {
            System.out.println("Exiting...");
            conn.disconnect();
        }
    }

    class DefaultConnectionListener implements ConnectionListener {
        public void connectionStarting(ConnectionEvent event) throws ConnectionVetoException {
            System.out.println("Connection starting: " + event.getConnectionContext());
        }

        public void connectionEstablished(ConnectionEvent event) {
            System.out.println("Connection established: " + event.getConnectionContext());
        }

        public void connectionClosed(ConnectionEvent event) {
            System.out.println("Connection closed: " + event.getConnectionContext());
            shutdown = true;
        }
    }
}
