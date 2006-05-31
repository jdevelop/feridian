package com.echomine.example;

import com.echomine.jabber.*;
import com.echomine.jabber.msg.*;
import com.echomine.net.ConnectionEvent;
import com.echomine.net.ConnectionListener;
import com.echomine.net.ConnectionVetoException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JabberTest {
    private String username;
    private String password;
    private String serverName;
    private int port = JabberContext.DEFAULT_PORT;
    private JabberContext context;
    private Jabber jabber;
    private boolean ssl;

    public JabberTest(String username, String password, String server, boolean ssl) {
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
        if (args.length < 3) {
            System.out.println("Usage: JabberTest <username> <password> <method to run> [<jabber server>] [<ssl{true/false}>]");
	    System.out.println("Methods available for running: ");
            Method[] methods = JabberTest.class.getMethods();
	    for (int i = 0;i < methods.length;i++) {
	        if (methods[i].getName().startsWith("test"))
		    System.out.println("\t" + methods[i].getName());
            }
            System.exit(1);
        }
        String server = "jabber.org";
        boolean ssl = false;
        if (args.length >= 4)
            server = args[3];
        if (args.length == 5)
            ssl = Boolean.valueOf(args[4]).booleanValue();
        JabberTest test = new JabberTest(args[0], args[1], server, ssl);
        test.setUp();
        //reflection and call the method
        try {
            Class jclass = test.getClass();
            Method method = jclass.getMethod(args[2], null);
            method.invoke(test, null);
        } catch (NoSuchMethodException ex) {
            System.out.println("Specified method doesn't exist");
        } catch (IllegalAccessException ex) {
            System.out.println("Illegal access to method");
        } catch (InvocationTargetException ex) {
            System.out.println("Error when invoking method");
            ex.printStackTrace();
        }
    }

    public void testJabber() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.getConnection().addMessageListener(new DefaultMessageListener());
        try {
            session.connect(serverName, port);
            Thread.sleep(60000);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    public void testJabberLogin() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.getConnection().addMessageListener(new DefaultMessageListener());
        try {
            session.connect(serverName, port);
            //send a test

            /*
            String authMethodQuery = "<iq type=\"set\" id=\"10001\"><query xmlns=\"jabber:iq:auth\"><username>" + username +
            "</username><password>" + password + "</password><resource>" + context.getResource() +
            "</resource></query></iq>";
            JabberStringMessage msg = new JabberStringMessage(authMethodQuery);
            jabber.send(msg);
            */

            session.getUserService().login();
            Thread.sleep(30000);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    public void testJabberMultiLogins() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.getConnection().addMessageListener(new DefaultMessageListener());
        try {
            session.connect(serverName, port);
            //send a test

            /*
            String authMethodQuery = "<iq type=\"set\" id=\"10001\"><query xmlns=\"jabber:iq:auth\"><username>" + username +
            "</username><password>" + password + "</password><resource>" + context.getResource() +
            "</resource></query></iq>";
            JabberStringMessage msg = new JabberStringMessage(authMethodQuery);
            jabber.send(msg);
            */

            session.getUserService().login();
            //disconnect and reconnect again
            session.disconnect();
            Thread.sleep(1000);
            session.connect(serverName, port);
            session.getUserService().login();
            Thread.sleep(1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    public void testJabberPresence() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.getConnection().addMessageListener(new DefaultMessageListener());
        session.getConnection().addMessageListener(new DefaultPresenceListener());
        try {
            session.connect(serverName, port);
            session.getUserService().login();
            session.getPresenceService().setToAvailable(null, null, false);
            Thread.sleep(99999999);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    public void testJabberRoster() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.getConnection().addMessageListener(new DefaultMessageListener());
        session.getConnection().addMessageListener(new DefaultRosterListener());
        try {
            session.connect(serverName, port);
            session.getUserService().login();
            session.getPresenceService().setToAvailable(null, null, true);
            //get roster list
            session.getRosterService().requestRosterList(true);
            //roster list received.. let's send a subscription request
            session.getRosterService().addToRoster(new JID("cktesting@jabber.org"), "cktesting", null, true);
            session.getRosterService().requestRosterList(true);
            session.getRosterService().removeFromRoster(new JID("cktesting@jabber.org"), true);
            session.getRosterService().requestRosterList(true);
            Thread.sleep(99999999);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    public void testJabberRegister() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.getConnection().addMessageListener(new DefaultMessageListener());
        session.getConnection().addMessageListener(new DefaultRosterListener());
        try {
            session.connect(serverName, port);
            HashMap fields = new HashMap();
            fields.put("username", context.getUsername());
            fields.put("password", context.getPassword());
            fields.put("email", "blah@blah.com");
            fields.put("name", "Blah Blah");
            session.getUserService().register(context.getServerNameJID(), fields);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    public void testJabberPrivateMsg() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.getConnection().addMessageListener(new DefaultMessageListener());
        try {
            session.connect(serverName, port);
            session.getUserService().login();
            session.getChatService().sendPrivateMessage(new JID("cktesting5@jabber.org"), "This is a test msg", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    public void testBrowsing() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.addMessageListener(new DefaultBrowseListener());
        try {
            session.connect(serverName, port);
            session.getUserService().login();
            //start browsing
            JabberServerService ss = session.getServerService();
            //test browsing of server
            ss.browse(context.getServerNameJID(), "service/jabber", true);
            //test browsing of user
            ss.browse(new JID("cktesting5@jabber.org"), "user/client", true);
            //test browsing of jud
            ss.browse(new JID("users.jabber.org"), "service/jud", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    public void testChatRoom() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.addMessageListener(new DefaultChatListener());
        session.getConnection().addMessageListener(new DefaultPresenceListener());
        try {
            session.connect(serverName, port);
            session.getUserService().login();
            JabberChatService cs = session.getChatService();
            cs.joinChatRoom(new JID("testing@private.jabber.org"), "TestNick", true);
            cs.sendChatMessage(new JID("testing@private.jabber.org"), "Test message to chat room", true);
            cs.setChatAvailable(new JID("testing@private.jabber.org"), PresenceCode.SHOW_DO_NOT_DISTURB, "Out To Lunch", true);
            Thread.sleep(3000);
            cs.setChatAvailable(new JID("testing@private.jabber.org"), null, null, true);
            Thread.sleep(3000);
            cs.leaveChatRoom(new JID("testing@private.jabber.org"), true);
            Thread.sleep(3000);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    public void testChangePassword() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        session.getConnection().addMessageListener(new DefaultMessageListener());
        session.getConnection().addMessageListener(new DefaultRosterListener());
        try {
            session.connect(serverName, port);
            session.getUserService().login();
            //change password
            session.getUserService().changePassword("newpass");
            System.out.println("Password changed to something new");
            //changed successfully, let's change it back
            session.getUserService().changePassword(context.getPassword());
            System.out.println("Password changed back to original");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    public void testTranslateUser() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        try {
            session.connect(serverName, port);
            session.getUserService().login();
            //translate user
            String jid = session.getUserService().translateUserToJID(new JID("msn.jabber.org"), "test@hotmail.com");
            System.out.println("MSN JID: " + jid);
            jid = session.getUserService().translateUserToJID(new JID("aim.jabber.org"), "blah");
            System.out.println("AIM JID: " + jid);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            session.disconnect();
        }
    }

    /**
     * obtains the server's time and software/version
     */
    public void testJabberServerInfo() {
        JabberSession session = jabber.createSession(context);
        session.getConnection().addConnectionListener(new DefaultConnectionListener());
        //session.getConnection().addMessageListener(new DefaultMessageListener());
        try {
            session.connect(serverName, port);
            session.getUserService().login();
            //change password
            String time = session.getServerService().getServerTime();
            String version = session.getServerService().getServerVersion();
            String localTime = session.getServerService().getServerTimeInLocal();
            long uptime = session.getServerService().getServerUptime();
            System.out.println("Server Time (Local): " + localTime);
            System.out.println("Server Time (Server): " + time);
            System.out.println("Server Uptime (secs): " + uptime);
            System.out.println("Server Version: " + version);
            //retrieve the jabber agents
            List agents = session.getServerService().getAgents();
            if (agents == null) {
                System.out.println("No Available Agents");
            } else {
                System.out.println("Available Server Agents:");
                Agent agent;
                int size = agents.size();
                for (int i = 0; i < size; i++) {
                    agent = (Agent) agents.get(i);
                    System.out.println("  " + agent.getName());
                }
            }
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


    class DefaultBrowseListener implements JabberMessageListener {
        public void messageReceived(JabberMessageEvent event) {
            if (event.getMessageType() != JabberCode.MSG_IQ_BROWSE) return;
            BrowseIQMessage msg = (BrowseIQMessage) event.getMessage();
            JIDType type = msg.getJIDType();
            System.out.println("[Browse Message " + type.getJIDType() + "] " + type);
        }
    }


    class DefaultPresenceListener implements JabberMessageListener {
        public void messageReceived(JabberMessageEvent event) {
            if (event.getMessageType() != JabberCode.MSG_PRESENCE) return;
            JabberPresenceMessage msg = (JabberPresenceMessage) event.getMessage();
            DelayXMessage delay = msg.getDelayMessage();
            String timestamp = new Date().toString();
            if (delay != null && delay.getTimeInLocal() != null)
                timestamp = delay.getTimeInLocal().toString();
            System.out.println("[Presence Listener (" + timestamp + ")] " + msg.getFrom() + " is " + msg.getType() +
                    ": " + msg.getStatus());
        }
    }


    class DefaultRosterListener implements JabberMessageListener {
        public void messageReceived(JabberMessageEvent event) {
            if (event.getMessageType() != JabberCode.MSG_IQ_ROSTER) return;
            RosterIQMessage msg = (RosterIQMessage) event.getMessage();
            //retrieve the items
            Iterator items = msg.getRosterItems().iterator();
            RosterItem item;
            while (items.hasNext()) {
                item = (RosterItem) items.next();
                System.out.println("[Roster Listener] " + item);
            }
        }
    }


    class DefaultChatListener implements JabberMessageListener {
        public void messageReceived(JabberMessageEvent event) {
            if (event.getMessageType() != JabberCode.MSG_CHAT) return;
            JabberChatMessage msg = (JabberChatMessage) event.getMessage();
            DelayXMessage delay = msg.getDelayMessage();
            String timestamp = new Date().toString();
            if (delay != null && delay.getTimeInLocal() != null)
                timestamp = delay.getTimeInLocal().toString();
            if (msg.isRosterMessage()) {
                System.out.print("[Roster Private Msg (");
            } else {
                if (msg.getType().equals(JabberChatMessage.TYPE_CHAT) || msg.getType().equals(JabberChatMessage.TYPE_NORMAL))
                    System.out.print("[Private Msg (");
                else if (msg.getType().equals(JabberChatMessage.TYPE_GROUPCHAT))
                    System.out.print("[Group Chat (");
                else
                    System.out.print("Chat Msg (");
                System.out.println(timestamp + ") " + msg.getThreadID() + "] From: " + msg.getFrom() +
                        ", Msg; " + msg.getBody());
            }
        }
    }


    class DefaultRequestListener implements JabberMessageListener {
        public void messageReceived(JabberMessageEvent event) {
            JabberMessage msg = event.getMessage();
            try {
                //first make 100% sure that the message is not an error message
                //and that the message is a get message
                //otherwise, we'll go into a loop
                if ((msg.getMessageType() == JabberCode.MSG_IQ_LAST)) {
                    LastIQMessage lmsg = (LastIQMessage) msg;
                    //reply back with an idle time of 2000 secs for testing
                    if (!lmsg.isError() && lmsg.getType().equals(JabberIQMessage.TYPE_GET))
                        event.getSession().getClientService().sendIdleTimeReply(lmsg.getFrom(), lmsg.getMessageID(), 2000);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    class DefaultStatusListener implements JabberMessageListener {
        public void messageReceived(JabberMessageEvent event) {
            JabberMessage msg = event.getMessage();
            EventXMessage emsg = (EventXMessage) msg.getXMessage(JabberCode.XMLNS_X_EVENT.getURI());
            if (emsg != null) {
                //print out the event
                System.out.println("Received Events for " + emsg.getEventMessageID() + ": ");
                if (emsg.isComposing())
                    System.out.println(" Composing ");
                if (emsg.isDelivered())
                    System.out.println(" Delivered ");
                if (emsg.isOffline())
                    System.out.println(" Offline ");
                if (emsg.isDisplayed())
                    System.out.println(" Displayed ");
            }
        }
    }
}
