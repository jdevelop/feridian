package com.echomine.jabber.packet;

/**
 * Defines attributes for identity structure in 
 * <a href="http://www.xmpp.org/extensions/xep-0030.html">JEP-0030, Service discovery</a>
 */
public class Identity {

    private String category;

    private String type;

    private String name;

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
