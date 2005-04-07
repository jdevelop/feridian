package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberIQMessage;
import com.echomine.jabber.JabberMessage;
import com.echomine.jabber.JabberMessageParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Implementation class for working with the Items namespace of the JEP-0030 Service Discovery protocol.
 * The service discovery protocol is a new protocol that will eventually replace the older JEP-0094 Agent
 * Information and JEP-0011 Browsing specs.</p>
 * <p>Note that this JEP is still considered Experimental.  Thus, the protocol may well not have been implemented
 * on the server side.  Even though the older Agents namespace is deprecated, it does not mean that you should stop
 * using it.  It also doesn't mean that you should use the new disco protocol judiciously without checking to make
 * sure it is supported by the remote server.  How would you know?  For now, you will just have to try both and
 * see what happens.</p>
 * <p>Read up on the list of categories, namespaces, and types at the Jabber Registry for an up-to-date list that
 * you may use.  The registrar is located at <a href="http://www.jabber.org/registrar/">http://www.jabber.org/registrar/</a></p>
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0030.html">JEP-0030 Version 2.0</a></b></p>
 *
 * @see ServiceInfoIQMessage
 * @since 0.8a4
 */
public class ServiceItemsIQMessage extends JabberIQMessage {
    private static Log log = LogFactory.getLog(ServiceItemsIQMessage.class);
    private ArrayList items = new ArrayList();
    private String node;

    /**
     * defaults to iq type get for retrieval or for parsing incoming messages
     */
    public ServiceItemsIQMessage(String type) {
        super(type);
        //add in the query element
        getDOM().addContent(new Element("query", JabberCode.XMLNS_IQ_DISCO_ITEMS));
    }

    public ServiceItemsIQMessage() {
        this(TYPE_GET);
    }

    /**
     * @return the message type id
     */
    public int getMessageType() {
        return JabberCode.MSG_IQ_DISCO_ITEMS;
    }

    /**
     * Adds a new service item to the message
     *
     * @param serviceItem a service item
     */
    public void addItem(ServiceItem serviceItem) {
        if (serviceItem == null)
            throw new IllegalArgumentException("service item cannot be null");
        items.add(serviceItem);
    }

    /**
     * @return a non-modifiable list of ServiceItem objects
     */
    public List getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * sets the node name so that further subnode items can be retrieved.
     *
     * @param node the node name to query further
     */
    public void setNode(String node) {
        this.node = node;
    }

    /**
     * @return the node name of the message if available, null otherwise
     */
    public String getNode() {
        return node;
    }

    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        super.parse(parser, msgTree);
        if (!items.isEmpty()) items.clear();
        Namespace ns = JabberCode.XMLNS_IQ_DISCO_ITEMS;
        Element query = msgTree.getChild("query", ns);
        node = query.getAttributeValue("node");
        List list = query.getChildren("item", ns);
        int size = list.size();
        ServiceItem item;
        Element ielem;
        for (int i = 0; i < size; i++) {
            try {
                ielem = (Element) list.get(i);
                item = new ServiceItem(ielem);
                items.add(item);
            } catch (ParseException ex) {
                if (log.isWarnEnabled())
                    log.warn("Error Parsing Service Item. Ignoring this entry and continuing.", ex);
            }
        }
        return this;
    }

    public String encode() throws ParseException {
        Namespace ns = JabberCode.XMLNS_IQ_DISCO_ITEMS;
        Element query = getDOM().getChild("query", ns);
        if (node == null)
            query.removeAttribute("node");
        else
            query.setAttribute("node", node);
        if (!query.getChildren().isEmpty()) query.getChildren().clear();
        if (!items.isEmpty()) {
            int size = items.size();
            for (int i = 0; i < size; i++)
                query.addContent(((ServiceItem) items.get(i)).encode());
        }
        return super.encode();
    }
}
