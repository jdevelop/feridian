package com.echomine.xmpp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.echomine.net.ConnectionEvent;
import com.echomine.net.ConnectionListener;
import com.echomine.net.ConnectionVetoException;
import com.echomine.xmpp.packet.XMLTextPacket;

public class SimpleXMPPClient {
    private JID jid;

    private String password;

    private String serverName;

    private int port = IXMPPConnection.DEFAULT_XMPP_PORT;

    private IXMPPConnection conn;

    private boolean shutdown;

    public SimpleXMPPClient(JID jid, String password, String server) {
        this.jid = jid;
        this.password = password;
        this.serverName = server;
    }

    protected void setUp() throws Exception {
        conn = XMPPConnectionFactory.getFactory().createXMPPConnection();
    }

    private static void printUsageAndExit() {
        System.out.println("Usage: SimpleXMPPClient <username@domain|'nologin@domain'> [<password>] [<optional xmpp server>]");
        System.out.println("Example (Connect but don't login): SimpleXMPPClient nologin@jabber.org");
        System.out.println("Example (Connect but don't login): SimpleXMPPClient nologin@gmail.com talk.google.com");
        System.out.println("Example (Login, host same as domain): SimpleXMPPClient user@jabber.org <password>");
        System.out.println("Example (Google login, host and domain differs): SimpleXMPPClient user@gmail.com <password> talk.google.com");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) printUsageAndExit();
        if (!args[0].contains("@"))
            throw new IllegalArgumentException("username portion must be in the format of username@domain");
        JID user = JID.parseJID(args[0]);
        String server = null;
        String pass = null;
        if ("nologin".equals(user.getNode()) && args.length == 2) {
            server = args[1];
        } else if (args.length >= 3) {
            server = args[2];
            pass = args[1];
        } else if (args.length == 2) {
            server = user.getHost();
            pass = args[1];
        } else {
            server = user.getHost();
        }
        SimpleXMPPClient client = new SimpleXMPPClient(user, pass, server);
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
            conn.connect(serverName, port, jid.getHost(), true);
            if (!"nologin".equals(jid.getNode()))
                conn.login(jid.getNode(), password.toCharArray(), "Home");
            String cmd;
            BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
            XMLTextPacket packet = new XMLTextPacket();
            do {
                System.out.print("Prompt? ");
                cmd = rdr.readLine();
                if ("quit".equals(cmd)) break;
                packet.setText(cmd);
                conn.sendPacket(packet, false);
            } while (!shutdown);
        } finally {
            System.out.println("Exiting...");
            conn.disconnect();
        }
    }

    class DefaultConnectionListener implements ConnectionListener {
        public void connectionStarting(ConnectionEvent event)
                throws ConnectionVetoException {
            System.out.println("Connection starting: "
                    + event.getConnectionContext());
        }

        public void connectionEstablished(ConnectionEvent event) {
            System.out.println("Connection established: "
                    + event.getConnectionContext());
        }

        public void connectionClosed(ConnectionEvent event) {
            System.out.println("Connection closed: "
                    + event.getConnectionContext());
            shutdown = true;
        }
    }
}
