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
 * <p>Implementation class for working with the Info namespace of the JEP-0030 Service Discovery protocol.
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
 * @see ServiceItemsIQMessage
 * @since 0.8a4
 */
public class ServiceInfoIQMessage extends JabberIQMessage {
    private static Log log = LogFactory.getLog(ServiceInfoIQMessage.class);
    private String node;
    private ArrayList identities = new ArrayList();
    private ArrayList features = new ArrayList();

    /**
     * defaults to iq type get for retrieval or for parsing incoming messages
     */
    public ServiceInfoIQMessage(String type) {
        super(type);
        //add in the query element
        getDOM().addContent(new Element("query", JabberCode.XMLNS_IQ_DISCO_INFO));
    }

    public ServiceInfoIQMessage() {
        this(TYPE_GET);
    }

    /**
     * @return the message type id
     */
    public int getMessageType() {
        return JabberCode.MSG_IQ_DISCO_INFO;
    }

    /**
     * @return the optional node name of the message, or null if none exists.
     */
    public String getNode() {
        return node;
    }

    /**
     * sets the node to the specified node value, or null to set it to none
     */
    public void setNode(String node) {
        this.node = node;
    }

    /**
     * adds a new identity to the list of services that are supported
     */
    public void addIdentity(ServiceIdentity identity) {
        if (identity == null)
            throw new IllegalArgumentException("Identity to add cannot be null");
        identities.add(identity);
    }

    /**
     * Adds a new feature support.  The feature is normally a namespace or a protocol name registered with the
     * Jabber registrar.
     */
    public void addFeature(String feature) {
        if (feature == null)
            throw new IllegalArgumentException("Feature to add cannot be null");
        features.add(feature);
    }

    /**
     * @return a unmodifiable list of ServiceIdentity objects
     */
    public List getIdentities() {
        return Collections.unmodifiableList(identities);
    }

    /**
     * @return a unmodifiable list of String objects representing features
     */
    public List getFeatures() {
        return Collections.unmodifiableList(features);
    }

    /**
     * parses the additional data out of the DOM
     */
    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        super.parse(parser, msgTree);
        if (!features.isEmpty()) features.clear();
        if (!identities.isEmpty()) identities.clear();
        Namespace ns = JabberCode.XMLNS_IQ_DISCO_INFO;
        Element query = msgTree.getChild("query", ns);
        node = query.getAttributeValue("node");
        //obtain any identity elements
        List list = query.getChildren("identity", ns);
        int size = list.size();
        ServiceIdentity ident;
        Element ielem;
        for (int i = 0; i < size; i++) {
            try {
                ielem = (Element) list.get(i);
                ident = new ServiceIdentity(ielem);
                identities.add(ident);
            } catch (ParseException ex) {
                if (log.isWarnEnabled())
                    log.warn("Error Parsing Service Identity. Ignoring this entry and continuing.", ex);
            }
        }
        //parse the features
        list = query.getChildren("feature", ns);
        size = list.size();
        for (int i = 0; i < size; i++) {
            try {
                ielem = (Element) list.get(i);
                addFeature(ielem.getAttributeValue("var"));
            } catch (Throwable tr) {
                if (log.isWarnEnabled())
                    log.warn("Error retrieving the feature name.  Ignoring this entry and continuing.", tr);
            }
        }
        return this;
    }

    /**
     * encodes the additional data
     */
    public String encode() throws ParseException {
        Namespace ns = JabberCode.XMLNS_IQ_DISCO_INFO;
        Element query = getDOM().getChild("query", ns);
        if (node == null)
            query.removeAttribute("node");
        else
            query.setAttribute("node", node);
        if (!query.getChildren().isEmpty()) query.getChildren().clear();
        if (!identities.isEmpty()) {
            int size = identities.size();
            for (int i = 0; i < size; i++)
                query.addContent(((ServiceIdentity) identities.get(i)).encode());
        }
        if (!features.isEmpty()) {
            int size = features.size();
            for (int i = 0; i < size; i++)
                query.addContent(new Element("feature", ns).setAttribute("var", (String) features.get(i)));
        }
        return super.encode();
    }
}
