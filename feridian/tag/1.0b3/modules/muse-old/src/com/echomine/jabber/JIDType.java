package com.echomine.jabber;

import com.echomine.common.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>a jid-type is similar to a mime-type.  It contains a category and then a subtype
 * in the form of "category/subtype" (ie. text/html).  This is the way that jabber
 * uses to identify types when browsing.  You need to work with this class to retrieve
 * information returned from a browse result.</p>
 * <p>A list of JID Types are available online at <pre>http://docs.jabber.org/draft-proto/html/browsing.html</pre></p>
 * <p>This object is not reusable once instantiated.  It is basically immutable.</p>
 */
public class JIDType {
    private static Pattern cattypePat = Pattern.compile("(\\S+)/(\\S+)");
    private static Log log = LogFactory.getLog(JIDType.class);
    private String category;
    private String subtype;
    private JID jid;
    private String name;
    private ArrayList children;
    private ArrayList nsList;

    /**
     * the constructor takes a category/subtype pair string and will parse it
     * into its distinctive parts.  The types are listed in JIDTypeCode.
     *
     * @throws ParseException if the parsing of the category/subtype errored
     */
    public JIDType(String type) throws ParseException {
        //type regex is "category/subtype"
        Matcher matcher = cattypePat.matcher(type);
        if (matcher.matches()) {
            category = matcher.group(1);
            subtype = matcher.group(2);
        } else {
            throw new ParseException("Unable to parse the category/subtype combination: " + type);
        }
    }

    public JIDType(String category, String subtype) {
        this.category = category;
        this.subtype = subtype;
    }

    /**
     * retrieves the entire JID type in the form of "category/subtype"
     */
    public String getJIDType() {
        StringBuffer buf = new StringBuffer();
        buf.append(category).append("/");
        if (subtype != null)
            buf.append(subtype);
        return buf.toString();
    }

    /**
     * @return the primary category of the type
     */
    public String getCategory() {
        return category;
    }

    /**
     * sets the category for the type.  The type is in the format of category/subtype.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the subtype for the type, null if there is none
     */
    public String getSubtype() {
        return subtype;
    }

    /**
     * sets the subtype for the type.
     */
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    /**
     * @return the JID of the type, null if empty
     */
    public JID getJID() {
        return jid;
    }

    /**
     * sets the JID of the type.  This will override any types that were previously set.
     * Normally the JID should be exactly the same as the To/From of the IQ.  You should
     * only change this to something different from the To Field if you know what you're doing.
     */
    public void setJID(JID jid) {
        this.jid = jid;
    }

    /**
     * @return the common user-recognizable name for the type if there is one, null otherwise
     */
    public String getName() {
        return name;
    }

    /**
     * sets the common name for the JID type for easier recognition.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * adds a child to the type.  This will be under the type
     */
    public void addChild(JIDType child) {
        if (children == null)
            children = new ArrayList();
        children.add(child);
    }

    /**
     * adds a namespace that the JID type recognizes on how to process.  For
     * user replies, this is normally used to advertise what features the client
     * support (ie. xhtml, pgp/encryption, oob file transfers, etc).  The transports
     * use this to advertise what type of services it supports (ie. search, register, gateway, etc).
     */
    public void addNS(Namespace ns) {
        if (nsList == null)
            nsList = new ArrayList();
        nsList.add(ns);
    }

    /**
     * retrieves the children that are contained inside this JID.
     * If there are no children, then the returned value will be null.
     */
    public List getChildren() {
        return children;
    }

    /**
     * retrieves the namespaces that the type supports.  If there are no namespaces, then the returned value will be null.
     */
    public List getNSList() {
        return nsList;
    }

    /**
     * parses the incoming message for the data.  The element passed in
     * should be the beginning of the JID Type element (ie. the <service>, <user>, etc elements).
     * It will recursively parse any internal children that are also of JID Type.
     */
    public void parse(Element browse) throws ParseException {
        //clear the children and ns list
        if (children == null)
            children = new ArrayList();
        else
            children.clear();
        if (nsList == null)
            children = new ArrayList();
        else
            nsList.clear();
        name = null;
        jid = null;
        //now parse the rest of the info
        setName(browse.getAttributeValue("name"));
        setJID(new JID(browse.getAttributeValue("jid")));
        List bchildren = browse.getChildren();
        int size = bchildren.size();
        Element child;
        JIDType subChild;
        for (int i = 0; i < size; i++) {
            child = (Element) bchildren.get(i);
            if (child.getName().equals("ns")) {
                addNS(Namespace.getNamespace(child.getText()));
            } else {
                try {
                    //all the others elements are simply more JID Types
                    //let's just do recursive parse.
                    subChild = new JIDType(child.getName(), child.getAttributeValue("type"));
                    subChild.parse(child);
                    //add the child into the children list
                    addChild(subChild);
                } catch (ParseException ex) {
                    if (log.isWarnEnabled()) log.warn("The current browse JIDType cannot be parsed and is skipped.  Service name = " + name + ", jid = " + jid);
                }
            }
        }
    }

    /**
     * Creates a DOM structure that represents all the data contained within this type
     */
    public Element getDOM() throws ParseException {
        if (category == null)
            throw new ParseException("JIDType category must be set");
        if (jid == null)
            throw new ParseException("JIDType JID must be set");
        Element browse = new Element(category, JabberCode.XMLNS_IQ_BROWSE);
        if (jid != null)
            browse.setAttribute("jid", jid.toString());
        if (subtype != null)
            browse.setAttribute("type", subtype);
        if (name != null)
            browse.setAttribute("name", name);
        //add in any <ns> tags if there are any
        if (nsList != null) {
            int size = nsList.size();
            Namespace ns;
            Element elem;
            for (int i = 0; i < size; i++) {
                ns = (Namespace) nsList.get(i);
                elem = new Element("ns", JabberCode.XMLNS_IQ_BROWSE);
                elem.setText(ns.getURI());
                browse.addContent(elem);
            }
        }
        //encode any children if there are any
        if (children != null) {
            int size = children.size();
            JIDType jidtype;
            for (int i = 0; i < size; i++) {
                jidtype = (JIDType) children.get(i);
                browse.addContent(jidtype.getDOM());
            }
        }
        return browse;
    }

    public String toString() {
        XMLOutputter os = new XMLOutputter();
        String temp = "";
        try {
            temp = os.outputString(getDOM());
        } catch (ParseException ex) {
        }
        return temp;
    }
}
