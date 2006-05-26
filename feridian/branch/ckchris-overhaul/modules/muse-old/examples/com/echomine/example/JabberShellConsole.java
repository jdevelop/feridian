package com.echomine.example;

import bsh.ConsoleInterface;
import bsh.Interpreter;
import com.echomine.jabber.*;
import com.echomine.net.ConnectionEvent;
import com.echomine.net.ConnectionListener;
import com.echomine.net.ConnectionVetoException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

/**
 * This example application utilizes the Bean Scripting Framework (BSF) along with beanshell (bsh)
 * to allow you to write java-like codes to work directly with Jabber.  The other example application,
 * JabberConsole, allows you to send raw XML data streams to the remote server.  This application
 * allow you to work with the currently provided class to send the data instead.
 * This application will export the JabberSession out to the beanshell as variable "session".
 */
public class JabberShellConsole {
    private String username;
    private String password;
    private String serverName;
    private int port = JabberContext.DEFAULT_PORT;
    private boolean ssl = false;
    private JabberContext context;
    private Jabber jabber;
    private JabberSession session;
    private Interpreter interpreter;

    public JabberShellConsole(String username, String password, String server, boolean ssl) {
        this.username = username;
        this.password = password;
        this.serverName = server;
        this.ssl = ssl;
    }

    protected void setUp() throws Exception {
        context = new JabberContext(username, password, serverName);
        context.setSSL(ssl);
        if (ssl == true) port = JabberContext.DEFAULT_SSL_PORT;
        jabber = new Jabber();
        session = jabber.createSession(context);
        interpreter = new Interpreter(new ShellConsoleInterface());
        interpreter.set("session", session);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: JabberShellConsole <username> <password> [<jabber server>] [<ssl{true/false}>]");
            System.exit(1);
        }
        String server = "jabber.org";
        boolean ssl = false;
        if (args.length >= 3)
            server = args[2];
        if (args.length == 4)
            ssl = Boolean.valueOf(args[3]).booleanValue();
        JabberShellConsole console = new JabberShellConsole(args[0], args[1], server, ssl);
        console.setUp();
        console.runConsole();
    }

    /**
     * This test method logs and and then send messages by reading the data from the
     * console keyboard.  This is for debugging message sending without having to recompile the test class
     */
    public void runConsole() {
        System.out.println("Jabber Shell Console started.  The JabberSession can accessed through the variable 'session'");
        System.out.println("When you want to quit, simply type exit() inside the shell prompt");
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.getConnection().addMessageListener(new DefaultMessageListener());
        try {
            session.connect(serverName, port);
            session.getUserService().login();
            interpreter.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    class ShellConsoleInterface implements ConsoleInterface {
        public Reader getIn() {
            return new BufferedReader(new InputStreamReader(System.in));
        }

        public PrintStream getOut() {
            return System.out;
        }

        public PrintStream getErr() {
            return System.err;
        }

        public void println(Object o) {
            System.out.println(o);
        }

        public void print(Object o) {
            System.out.print(o);
        }

        public void error(Object o) {
            System.err.println(o);
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
}
