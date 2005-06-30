package com.echomine.xmpp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains the JID resource. It also knows how to parse the information or
 * output it in the JID compliant format. This is mainly used to generate or
 * retrieve parts of the JID in a easy way. you simply pass in the JID string,
 * and then retrieve whatever you like. An instantiated JID is immutable by
 * default.
 */
public class JID {
    private static Pattern jidPat = Pattern.compile("(?:(.+)\\@)?([^/]+)(?:/(.+))?");

    private String node;
    private String host;
    private String resource;

    /**
     * takes in a JID string and parses it into a JID component.
     * 
     * @throws ParseException if the jid does not conform to the format
     */
    public static JID parseJID(String jidStr) throws ParseException {
        if (jidStr == null)
            throw new ParseException("JID cannot be null");
        //URI Syntax is [node@]domain[/resource]
        Matcher matcher = jidPat.matcher(jidStr);
        if (matcher.matches()) {
            JID jid = new JID(matcher.group(1), matcher.group(2), matcher.group(3));
            return jid;
        } else {
            throw new ParseException("Unable to parse JID: " + jidStr);
        }
    }

    /**
     * a static method to turn the JID into a String for deserialization purposes
     * 
     * @param jid the jid to deserialize
     * @return the string form of the JID
     */
    public static String toString(JID jid) {
        if (jid == null) throw new IllegalArgumentException("JID cannot be null");
        return jid.toString();
    }
    
    /**
     * takes in a set of information to create the JID object that can be use to
     * convert into a JID string
     * 
     * @param node the name of the node or user, required
     * @param host the Jabber server name, required
     * @param resource the resource used, can be null to specify none
     */
    public JID(String node, String host, String resource) {
        this.node = node;
        this.host = host;
        this.resource = resource;
    }

    /**
     * @return the node name, or commonly referred to as username
     */
    public String getNode() {
        return node;
    }

    /**
     * The value may possibly be a host name in canonical form (ie.
     * www.blah.com) or in IPv4 or IPv6 form.
     * 
     * @return the domain or host part of the JID.
     */
    public String getHost() {
        return host;
    }

    /**
     * @return optional resource name attached to JID. Null if none.
     */
    public String getResource() {
        return resource;
    }

    /**
     * this is the same as getNode(). It's here for less confusion and
     * convenience.
     */
    public String getUsername() {
        return getNode();
    }

    /**
     * retrieves the node@domain part of the JID. It is basically the normal JID
     * that you would use if you do not specify a resource. This is here for
     * convenience.
     * 
     * @return the node@domain part of the JID
     */
    public String getJIDWithoutResource() {
        return node + "@" + host;
    }

    /**
     * @return the JID in the correct format
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        if (node != null)
            buf.append(node).append("@");
        buf.append(host);
        if (resource != null)
            buf.append("/").append(resource);
        return buf.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (toString().equals(obj.toString()))
            return true;
        return false;
    }

    /**
     * Uses the full JID string as the hash code
     */
    public int hashCode() {
        return toString().hashCode();
    }
}
