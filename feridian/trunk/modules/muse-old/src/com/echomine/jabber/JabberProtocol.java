package com.echomine.jabber;

import alt.java.net.Socket;
import com.echomine.common.ParseException;
import com.echomine.net.SocketHandler;
import com.echomine.util.IOUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import java.io.*;

/**
 * The main Jabber protocol handler.  It actually delegates all the incoming data processing to
 * the xml SAX handlers.  This handler actually works more as a state manager.  The way it works
 * is that when incoming xml message comes in, it will select the proper message processor to parse the rest of the data.
 */
public class JabberProtocol implements SocketHandler {
    public final static String DEFAULT_PARSER = "com.echomine.jabber.parser.JabberJAXPParser";
    protected final static int SOCKETBUF = 8192;
    private static Log outlogger = LogFactory.getLog("jabber/msg/outgoing");
    private boolean shutdown;
    protected MessageRequestQueue queue;
    protected JabberErrorHandler errorHandler;
    protected JabberContentHandler contentHandler;
    protected SAXReaderThread reader;
    protected Socket socket;

    public JabberProtocol(JabberContentHandler contentHandler, MessageRequestQueue queue) {
        this.shutdown = false;
        this.queue = queue;
        this.contentHandler = contentHandler;
        errorHandler = new JabberErrorHandler() {
            public void error(SAXParseException ex) {
                super.error(ex);
                //shutdown
                shutdown();
            }

            public void fatalError(SAXParseException ex) {
                super.fatalError(ex);
                shutdown();
            }
        };
    }

    public void handle(Socket socket) throws IOException {
        this.socket = socket;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            //set socket keepalive
            socket.setKeepAlive(true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"), SOCKETBUF);
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"), SOCKETBUF);
            reader = new SAXReaderThread(br);
            //start the thread
            reader.start();
            JabberMessage msg;
            while (!shutdown) {
                msg = queue.waitForMessage();
                //write out the message
                if (msg != null) {
                    try {
                        //output the message for debugging purposes
                        if (outlogger.isDebugEnabled())
                            outlogger.debug(msg);
                        bw.write(msg.encode());
                        bw.flush();
                    } catch (ParseException ex) {
                        //a parse exception when encoding
                        //simply print out the message
                        System.out.println(ex.getMessage());
                    }
                }
                Thread.yield();
            }
            if (shutdown) {
                //send the last tag
                bw.write("</stream:stream>");
                bw.flush();
            }
        } finally {
            shutdown();
            //disconnected from server, close streams but not the socket
            IOUtil.closeStream(bw);
            IOUtil.closeStream(br);
        }
    }

    /**
     * Shuts down the protocol
     */
    public void shutdown() {
        //socket will be closed automatically once shutdown flag is set
        shutdown = true;
        //clear all the message and then interrupt the queue
        queue.shutdown();
        IOUtil.closeSocket(socket);
    }

    /**
     * Does all the resetting before a connection begins
     */
    public void start() {
        shutdown = false;
        queue.clear();
        queue.start();
    }

    /**
     * queues up the data and wait for thread to send out the data
     */
    public void send(JabberMessage msg) {
        queue.addMessage(msg);
    }

    /**
     * Used by the low level protocol handler to read in the data. Once this is initiated, it runs in its own thread
     * and starts reading data.
     */
    public class SAXReaderThread extends Thread {
        private Reader reader;

        public SAXReaderThread(Reader reader) {
            super("Jabber SAX Reader");
            this.reader = reader;
        }

        public void run() {
            //check for system property com.echomine.jabber.SAXParser
            String parserName = System.getProperty("com.echomine.jabber.SAXParser", DEFAULT_PARSER);
            try {
                //load the parser class
                Class cls = Class.forName(parserName);
                //do an explicit instantiation and cast to make sure the class is a parser
                JabberSAXParser parser = (JabberSAXParser) cls.newInstance();
                //now start the parsing
                parser.parse(false, true, contentHandler, errorHandler, new InputSource(reader));
            } catch (ClassNotFoundException ex) {
                System.out.println("Parser class " + parserName + " not found");
            } catch (ClassCastException ex) {
                System.out.println("Parser is not of type JabberSAXParser");
            } catch (InstantiationException ex) {
                System.out.println("Parser cannot be initialized to check for validity: " + ex.getMessage());
            } catch (IllegalAccessException ex) {
                System.out.println("Illegal access while checking for validity: " + ex.getMessage());
            } finally {
                shutdown();
            }
        }
    }
}
