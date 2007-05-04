package com.echomine.jabber.packet;

/**
 * Defines attributes for feature structure in
 * <a href="http://www.xmpp.org/extensions/xep-0030.html">JEP-0030, Service discovery</a> 
 */
public class Feature {

    private String var;

    /**
     * @return the var
     */
    public String getVar() {
        return var;
    }

    /**
     * @param var the var to set
     */
    public void setVar(String var) {
        this.var = var;
    }

}
