package com.echomine.jabber.packet;

import com.echomine.xmpp.packet.IQPacket;

/**
 * Deals with messages for registering, password changes, user profile changes, etc with either the jabber server or
 * add-on service, such as a gateway.  This message represents the jabber:iq:register namespace.  Current implementation
 * does not support x:data and x:oob elements.  These are optional as defined in the JEP. For now, it will work
 * compatibly with older jabber servers and will ignore x:data and x:oob elements if found.
 * <p/>
 * <b>TODO: add support for x:data and x:oob</b>
 * <p/>
 * <b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0077.html">JEP-0077 Version 2.1</a></b>
 */
public class RegisterIQPacket extends IQPacket {
    String registered;
    String instructions;
    String username;
    String nick;
    String password;
    String name;
    String first;
    String last;
    String email;
    String address;
    String city;
    String state;
    String zip;
    String phone;
    String url;
    String date;
    String misc;
    String text;
    String key;
    String remove;

    /**
     * Indicates whether the specified account is registered.
     */
    public boolean isRegistered() {
        if (registered == null) return false;
        return true;
    }

    /**
     * Obtains the instructions attached to the registration process.
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * @return the username set for this registration
     */
    public String getUsername() {
        return username;
    }

    /**
     * sets the username to send to the remote host
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return nickname for the account, or null if none is found
     */
    public String getNick() {
        return nick;
    }

    /**
     * sets the nick for the account.
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * @return the password, or null if none is found
     */
    public String getPassword() {
        return password;
    }

    /**
     * sets the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the name, or null if none exists
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name for the account
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the first name, or null
     */
    public String getFirst() {
        return first;
    }

    /**
     * sets the first name
     */
    public void setFirst(String first) {
        this.first = first;
    }

    /**
     * @return the last name, or null
     */
    public String getLast() {
        return last;
    }

    /**
     * sets the last name
     */
    public void setLast(String last) {
        this.last = last;
    }

    /**
     * @return the email, or null
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the address or null
     */
    public String getAddress() {
        return address;
    }

    /**
     * sets the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the city or null
     */
    public String getCity() {
        return city;
    }

    /**
     * sets the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the state or null
     */
    public String getState() {
        return state;
    }

    /**
     * sets the state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the zipcode, or null
     */
    public String getZip() {
        return zip;
    }

    /**
     * sets the zip
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * @return the phone number, or null
     */
    public String getPhone() {
        return phone;
    }

    /**
     * sets the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the url associated with the account, or null
     */
    public String getUrl() {
        return url;
    }

    /**
     * sets the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the date, or null
     */
    public String getDate() {
        return date;
    }

    /**
     * sets the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return miscellaneous data or null
     */
    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isRemove() {
        if (remove == null) return false;
        return true;
    }

    public void setRemove(boolean isRemove) {
        if (isRemove)
            remove = "";
        else
            remove = null;
    }
}
