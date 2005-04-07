package com.echomine.example;

import com.echomine.jabber.*;
import com.echomine.net.ConnectionEvent;
import com.echomine.net.ConnectionListener;
import com.echomine.net.ConnectionVetoException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * The sample application is a simple command-line console client that lets you manually
 * type in the text to be sent to the remote server and outputs any replies coming from
 * the server.  It acts as a quick and simple way to test out some sort of new message
 * commands before you feel it necessary to create custom message classes.  It's also
 * a quick and easy way to see if the server and the client is behaving properly.
 */
public class JabberConsole {
    private String username;
    private String password;
    private String serverName;
    private int port = JabberContext.DEFAULT_PORT;
    private JabberContext context;
    private Jabber jabber;
    private boolean ssl = false;

    public JabberConsole(String username, String password, String server, boolean ssl) {
        this.username = username;
        this.password = password;
        this.serverName = server;
        this.ssl = ssl;
    }

    protected void setUp() {
        context = new JabberContext(username, password, serverName);
        context.setSSL(ssl);
        if (ssl) port = JabberContext.DEFAULT_SSL_PORT;
        jabber = new Jabber();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: JabberConsole <username> <password> [<jabber server>] [<ssl{true/false}>]");
            System.exit(1);
        }
        String server = "jabber.org";
        boolean ssl = false;
        if (args.length >= 3)
            server = args[2];
        if (args.length == 4)
            ssl = Boolean.valueOf(args[3]).booleanValue();
        JabberConsole console = new JabberConsole(args[0], args[1], server, ssl);
        console.setUp();
        console.runConsole();
    }

    /**
     * This test method logs and and then send messages by reading the data from the
     * console keyboard.  This is for debugging message sending without having to recompile the test class
     */
    public void runConsole() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.getConnection().addMessageListener(new DefaultMessageListener());
        try {
            session.connect(serverName, port);
            session.getUserService().login();
            String command;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            JabberStringMessage msg = null;
            do {
                if (msg != null)
                    session.sendMessage(msg);
                System.out.print("?");
                //read the line
                command = in.readLine();
                msg = new JabberStringMessage(JabberCode.MSG_UNKNOWN, command);
            } while (!"quit".equals(command));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    class DefaultConnectionListener implements ConnectionListener {
        public void connectionStarting(ConnectionEvent event) throws ConnectionVetoException {
            System.out.println("Connection starting: " + event.getConnectionModel());
        }

        public void connectionEstablished(ConnectionEvent event) {
            System.out.println("Connection established: " + event.getConnectionModel());
        }

        public void connectionClosed(ConnectionEvent event) {
            System.out.println("Connection closed: " + event.getConnectionModel());
        }
    }


    class DefaultMessageListener implements JabberMessageListener {
        public void messageReceived(JabberMessageEvent event) {
            JabberMessage msg = event.getMessage();
            try {
                AbstractJabberMessage jmsg = (AbstractJabberMessage) msg;
                if (jmsg.isError()) {
                    System.out.println("[Error ID " + msg.getMessageID() + "] " + msg);
                } else {
                    System.out.println("[Message ID " + msg.getMessageID() + "] " + msg);
                }
            } catch (ClassCastException ex) {
                System.out.println("[Message ID " + msg.getMessageID() + "] " + msg);
            }
        }
    }
}
