package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.*;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.Iterator;

/**
 * A type of IQ message which contains extended information about a user.
 * <p/>
 * These information include the user's full name, home and work address,
 * email address, and a lot of other more or less useful data.
 * <p/>
 * This class can be used both for requesting and for retrieving
 * or sending a vCard message.
 * <p/>
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0054.html">JEP-0054 Version 1.1 (vcard-temp)</a></b></p>
 *
 * @author Matthias A. Benkard (kirby-2@users.sourceforge.net)
 */
public class JabberVCardMessage extends JabberIQMessage {
    private String nickname;  // Nickname
    private String family;    // Family name
    private String given;     // First name
    private String middle;    // Middle name
    private String fn;        // Full name
    private String email;     // E-Mail address
    private String bday;      // Birthday
    private String url;       // Homepage URL
    private String orgname;   // Organization name
    private String orgunit;   // Organization unit
    private String title;     // Title
    private String role;      // Role
    private String desc;      // Description
    private JID jid;       // Jabber ID
    private String age;       // Age
    private String gender;    // Gender
    private String photo;     // Photo (can be URL or Base64)
    private String photoType; // the MIME type of the photo for Base64
    private String foreground; // Nick foreground color
    private String background; // Nick foreground color

    private LocalStruct home; // Address, etc.
    private LocalStruct work; // ditto

    /**
     * Contains data about a location.
     * This includes the address as well as phone and fax numbers, if available.
     */
    public class LocalStruct {
        // Phone and stuff.
        public String phone; // phone number
        public String msg; // what is this?
        public String fax; // fax number
        // Address.
        public String street; // street
        public String extadd; // second address line
        public String locality; // town
        public String region; // province/state
        public String pcode; // postal code
        public String country; // country
    }

    /**
     * Normally used for creating a new outgoing message.
     */
    public JabberVCardMessage(String type) {
        super(type);
        initToNull();
    }

    /**
     * Creates a JabberVCardMessage of type "set".
     */
    public JabberVCardMessage() {
        super(TYPE_SET);
        initToNull();
    }

    /**
     * Gets the user's full name.
     *
     * @return The full name of the user, or an empty string if not set.
     */
    public String getFullName() {
        return fn;
    }

    /**
     * Sets the full name.
     *
     * @param pFullName The full name of the user. Set this to an empty string or <code>null</code>
     *                  to have the field be skipped when creating the vCard.
     */
    public void setFullName(String pFullName) {
        fn = pFullName;
    }

    /**
     * Gets the user's email address.
     *
     * @return The email address of the user, or an empty string if not set.
     */
    public String getMail() {
        return email;
    }

    /**
     * Gets the user's photo. Can be either URL or photo in Base64
     *
     * @return The photo of the user, or an empty string if not set.
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Sets the user's ephoto.
     *
     * @param pPhoto The photo of the user. Set this to an empty string or <code>null</code>
     *               to have the field be skipped when creating the vCard.
     */
    public void setPhoto(String pPhoto) {
        photo = pPhoto;
    }

    /**
     * Get the MIME type of the photo if the photo data is Base64. If the photo value is an URL,
     * returns null.
     *
     * @return the Photo type value, or null if photo value is an URL
     */
    public String getPhotoType() {
        return photoType;
    }

    /**
     * Set MIME type of a photo, if it's base64-encoded. Set to null if the value of
     * <code>photo</code> is a URL.
     *
     * @param photoType The new Photo type value.
     */
    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    /**
     * Gets the user's foreground.
     *
     * @return The foreground of the user, or an empty string if not set.
     */
    public String getForeground() {
        return foreground;
    }

    /**
     * Sets the user's foreground.
     *
     * @param pForeground The foreground of the user. Set this to an empty string or <code>null</code>
     *                    to have the field be skipped when creating the vCard.
     */
    public void setForeground(String pForeground) {
        foreground = pForeground;
    }

    /**
     * Gets the user's background.
     *
     * @return The background of the user, or an empty string if not set.
     */
    public String getBackground() {
        return background;
    }

    /**
     * Sets the user's background.
     *
     * @param pBackground The background of the user. Set this to an empty string or <code>null</code>
     *                    to have the field be skipped when creating the vCard.
     */
    public void setBackground(String pBackground) {
        background = pBackground;
    }

    /**
     * Sets the user's email address.
     *
     * @param pMail The email address of the user. Set this to an empty string or <code>null</code>
     *              to have the field be skipped when creating the vCard.
     */
    public void setMail(String pMail) {
        email = pMail;
    }

    /**
     * Gets the user's birthday.
     *
     * @return The birthday of the user, or an empty string if not set.
     */
    public String getBirthday() {
        return bday;
    }

    /**
     * Sets the user's birthday.
     *
     * @param pBirthday The birthday of the user. Set this to an empty string or <code>null</code>
     *                  to have the field be skipped when creating the vCard.
     */
    public void setBirthday(String pBirthday) {
        bday = pBirthday;
    }

    /**
     * Gets the user's gender.
     *
     * @return The gender of the user, or an empty string if not set.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the user's gender.
     *
     * @param pGender The gender of the user. Set this to an empty string or <code>null</code>
     *                to have the field be skipped when creating the vCard.
     */
    public void setGender(String pGender) {
        gender = pGender;
    }

    /**
     * Gets the user's age.
     *
     * @return The age of the user, or an empty string if not set.
     */
    public String getAge() {
        return age;
    }

    /**
     * Sets the user's age.
     *
     * @param pAge The age of the user. Set this to an empty string or <code>null</code>
     *             to have the field be skipped when creating the vCard.
     */
    public void setAge(String pAge) {
        age = pAge;
    }

    /**
     * Gets the user's homepage URL.
     *
     * @return The URL of the user's homepage, or an empty string if not set.
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the user's homepage URL.
     *
     * @param pURL The URL of the user's homepage. Set this to an empty string or <code>null</code>
     *             to have the field be skipped when creating the vCard.
     */
    public void setURL(String pURL) {
        url = pURL;
    }

    /**
     * Gets the organization name.
     *
     * @return The name of the user's organization, or an empty string if not set.
     */
    public String getOrgName() {
        return orgname;
    }

    /**
     * Sets the organization name.
     *
     * @param pOrgName The name of the user's organization. Set this to an empty string or <code>null</code>
     *                 to have the field be skipped when creating the vCard.
     */
    public void setOrgName(String pOrgName) {
        orgname = pOrgName;
    }

    /**
     * Gets the organization unit.
     *
     * @return The user's unit in the organization (?), or an empty string if not set.
     */
    public String getOrgUnit() {
        return orgunit;
    }

    /**
     * Sets the organization unit.
     *
     * @param pOrgUnit The user's unit in the organization (?). Set this to an empty string or
     *                 <code>null</code> to have the field be skipped when creating the vCard.
     */
    public void setOrgUnit(String pOrgUnit) {
        orgunit = pOrgUnit;
    }

    /**
     * Gets the user's title.
     *
     * @return The title of the user, or an empty string if not set.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the user's title.
     *
     * @param pTitle The title of the user. Set this to an empty string or <code>null</code>
     *               to have the field be skipped when creating the vCard.
     */
    public void setTitle(String pTitle) {
        title = pTitle;
    }

    /**
     * Gets the user's role.
     *
     * @return The role of the user, or an empty string if not set.
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     *
     * @param pRole The role of the user. Set this to an empty string or <code>null</code>
     *              to have the field be skipped when creating the vCard.
     */
    public void setRole(String pRole) {
        role = pRole;
    }

    /**
     * Gets the user's advanced description.
     *
     * @return The description of the user, or an empty string if not set.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the user's advanced description.
     *
     * @param pDesc The description of the user. Set this to an empty string or <code>null</code>
     *              to have the field be skipped when creating the vCard.
     */
    public void setDesc(String pDesc) {
        desc = pDesc;
    }

    /**
     * Gets the user's Jabber ID.
     *
     * @return The Jabber ID of the user, or null if not set.
     */
    public JID getJID() {
        return jid;
    }

    /**
     * Sets the user's Jabber ID.
     *
     * @param pJID The Jabber ID of the user. Set this to <code>null</code>
     *             to have the field be skipped when creating the vCard.
     */
    public void setJID(JID pJID) {
        jid = pJID;
    }

    /**
     * Gets the user's middle name.
     *
     * @return The middle name of the user, or an empty string if not set.
     */
    public String getMiddle() {
        return middle;
    }

    /**
     * Sets the user's middle name.
     *
     * @param pMiddleName The middle name of the user. Set this to an empty string or <code>null</code>
     *                    to have the field be skipped when creating the vCard.
     */
    public void setMiddle(String pMiddleName) {
        middle = pMiddleName;
    }

    /**
     * Gets the user's first name.
     *
     * @return The first name of the user, or an empty string if not set.
     */
    public String getGiven() {
        return given;
    }

    /**
     * Sets the user's first name.
     *
     * @param pGivenName The first name of the user. Set this to an empty string or <code>null</code>
     *                   to have the field be skipped when creating the vCard.
     */
    public void setGiven(String pGivenName) {
        given = pGivenName;
    }

    /**
     * Gets the user's family name.
     *
     * @return The family name of the user, or an empty string if not set.
     */
    public String getFamily() {
        return family;
    }

    /**
     * Sets the user's family name.
     *
     * @param pFamilyName The family name of the user. Set this to an empty string or <code>null</code>
     *                    to have the field be skipped when creating the vCard.
     */
    public void setFamily(String pFamilyName) {
        family = pFamilyName;
    }

    /**
     * Gets the user's nickname.
     *
     * @return The nickname of the user, or an empty string if not set.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the user's nickname.
     *
     * @param pNickname The nickname of the user. Set this to an empty string or <code>null</code>
     *                  to have the field be skipped when creating the vCard.
     */
    public void setNickname(String pNickname) {
        nickname = pNickname;
    }

    /**
     * Gets the home struct.
     *
     * @return A reference to the message's {@link com.echomine.jabber.msg.JabberVCardMessage.LocalStruct} that contains
     *         information about the user's home.
     */
    public LocalStruct getHome() {
        return home;
    }

    /**
     * Gets the work struct.
     *
     * @return A reference to the message's {@link com.echomine.jabber.msg.JabberVCardMessage.LocalStruct} that contains
     *         information about the user's working place.
     */
    public LocalStruct getWork() {
        return work;
    }

    /**
     * Gets the message type.
     *
     * @return This message's type.
     * @see com.echomine.jabber.JabberCode
     */
    public int getMessageType() {
        return JabberCode.MSG_IQ_VCARD;
    }

    private void initToNull() {
        nickname = "";
        family = "";
        given = "";
        middle = "";
        photo = "";
        photoType = "";
        fn = "";
        email = "";
        bday = "";
        age = "";
        gender = "";
        url = "";
        orgname = "";
        orgunit = "";
        title = "";
        role = "";
        desc = "";
        home = new LocalStruct();
        work = new LocalStruct();
        background = "";
        foreground = "";
    }

    /**
     * Parses element/incoming message into a message object.
     *
     * @param parser  the messageg parser
     * @param msgTree The element which the message object will be constructed from.
     * @return The message object created by parsing the <code>Element msgTree</code>.
     * @throws com.echomine.common.ParseException
     *          if parsing the message failed.
     */
    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        if (msgTree == null || parser == null)
            throw new ParseException("parser and msgTree cannot be null");

        super.parse(parser, msgTree);

        Namespace ns = JabberCode.XMLNS_IQ_VCARD;
        Element vCard = msgTree.getChild("vCard", ns);
        if (vCard == null)
            throw new ParseException("No \"vCard\" element");

        Element el = vCard.getChild("N", ns);
        if (el != null) {
            family = el.getChildText("FAMILY", ns);
            given = el.getChildText("GIVEN", ns);
            middle = el.getChildText("MIDDLE", ns);
        }

        nickname = vCard.getChildText("NICKNAME", ns);
        fn = vCard.getChildText("FN", ns);
        email = vCard.getChildText("EMAIL", ns);
        // email _should_ be "" or null because the actual email address should be
        // contained in a "USERID" element inside the "EMAIL" element. But who knows...
        // Maybe there are some non-standard-compliant Jabber clients out there...
        // Anyway, fetch the address from the "USERID" element if it wasn't already
        // defined directly in the "EMAIL" element...
        if ((email == null || email.equals("")) && vCard.getChild("EMAIL", ns) != null)
            email = vCard.getChild("EMAIL", ns).getChildText("USERID", ns);

        bday = vCard.getChildText("BDAY", ns);  // format: YYYY-MM-DD
        gender = vCard.getChildText("GENDER", ns);
        age = vCard.getChildText("AGE", ns);
        url = vCard.getChildText("URL", ns);
        title = vCard.getChildText("TITLE", ns);
        role = vCard.getChildText("ROLE", ns);
        desc = vCard.getChildText("DESC", ns);
        el = vCard.getChild("JABBERID", ns);
        if (el != null)
            jid = new JID(el.getText());
        el = vCard.getChild("PHOTO", ns);
        //obtain the photo type for the photo
        if (el != null) {
            photoType = el.getChildText("TYPE", ns);

            if (photoType == null || "".equals(photoType)) {
                photo = el.getChildText("EXTVAL", ns);
            } else {
                photo = el.getChildText("BINVAL", ns);
            }
        }
        foreground = vCard.getChildText("FOREGROUND", ns);
        background = vCard.getChildText("BACKGROUND", ns);

        Iterator els = vCard.getChildren("ADR", ns).iterator();
        while (els.hasNext()) {
            el = (Element) els.next();

            if (el.getChild("HOME", ns) != null) {
                home.street = el.getChildText("STREET", ns);
                home.extadd = el.getChildText("EXTADD", ns);
                home.locality = el.getChildText("LOCALITY", ns);
                home.region = el.getChildText("REGION", ns);
                home.pcode = el.getChildText("PCODE", ns);
                home.country = el.getChildText("CTRY", ns);
            } else if (el.getChild("WORK", ns) != null) {
                work.street = el.getChildText("STREET", ns);
                work.extadd = el.getChildText("EXTADD", ns);
                work.locality = el.getChildText("LOCALITY", ns);
                work.region = el.getChildText("REGION", ns);
                work.pcode = el.getChildText("PCODE", ns);
                work.country = el.getChildText("CTRY", ns);
            }
        }

        el = vCard.getChild("ORG", ns);
        if (el != null) {
            orgname = el.getChildText("ORGNAME", ns);
            orgunit = el.getChildText("ORGUNIT", ns);
        }

        els = vCard.getChildren("TEL", ns).iterator();
        while (els.hasNext()) {
            el = (Element) els.next();

            if (el.getChild("HOME", ns) != null) {
                // Home phone, fax, voice.
                if (el.getChild("VOICE", ns) != null)
                    home.phone = el.getChildText("NUMBER", ns);
                else if (el.getChild("FAX", ns) != null)
                    home.fax = el.getChildText("NUMBER", ns);
                else if (el.getChild("MSG", ns) != null)
                    home.msg = el.getChildText("NUMBER", ns);
            } else if (el.getChild("WORK", ns) != null) {
                if (el.getChild("VOICE", ns) != null)
                    work.phone = el.getChildText("NUMBER", ns);
                else if (el.getChild("FAX", ns) != null)
                    work.fax = el.getChildText("NUMBER", ns);
                else if (el.getChild("MSG", ns) != null)
                    work.msg = el.getChildText("NUMBER", ns);
            }
        }

        return this;
    }

    /**
     * Encodes the message into XML.
     *
     * @return The XML string.
     * @throws com.echomine.common.ParseException
     *          if something wicked happened while encoding the message.
     */
    public String encode() throws ParseException {
        // Convert all the stuff into a DOM tree...
        getDOM().getChildren().clear();
        Namespace ns = JabberCode.XMLNS_IQ_VCARD;
        Element e = new Element("vCard", ns);

        add(e, "FN", fn);
        add(e, "NICKNAME", nickname);
        add(e, "URL", url);
        add(e, "BDAY", bday);
        add(e, "AGE", age);
        add(e, "GENDER", gender);
        add(e, "TITLE", title);
        add(e, "ROLE", role);
        if (jid != null)
            add(e, "JABBERID", jid.toString());
        add(e, "DESC", desc);
        if (photo != null && !"".equals(photo)) {
            Element u = new Element("PHOTO", ns);
            if (photoType == null || "".equals(photoType)) {
                add(u, "EXTVAL", photo);
            } else {
                add(u, "TYPE", photoType);
                add(u, "BINVAL", photo);
            }
            e.addContent(u);
        }
        add(e, "FOREGROUND", foreground);
        add(e, "BACKGROUND", background);

        Element u = new Element("EMAIL", ns);
        add(u, "USERID", email);
        if (u.getChildren().size() > 0) e.addContent(u);

        u = new Element("N", ns);
        add(u, "FAMILY", family);
        add(u, "GIVEN", given);
        add(u, "MIDDLE", middle);
        if (u.getChildren().size() > 0) e.addContent(u);

        u = new Element("ORG", ns);
        add(u, "ORGNAME", orgname);
        add(u, "ORGUNIT", orgunit);
        if (u.getChildren().size() > 0) e.addContent(u);

        if (work != null) {
            u = new Element("ADR", ns);
            u.addContent(new Element("WORK", ns));
            add(u, "EXTADD", work.extadd);
            add(u, "STREET", work.street);
            add(u, "LOCALITY", work.locality);
            add(u, "REGION", work.region);
            add(u, "PCODE", work.pcode);
            add(u, "CTRY", work.country);
            if (u.getChildren().size() > 1) e.addContent(u);

            u = new Element("TEL", ns);
            u.addContent(new Element("VOICE", ns));
            u.addContent(new Element("WORK", ns));
            u.addContent(new Element("NUMBER", ns).setText(work.phone));
            if (!u.getChildText("NUMBER", ns).equals("")) e.addContent(u);

            u = new Element("TEL", ns);
            u.addContent(new Element("FAX", ns));
            u.addContent(new Element("WORK", ns));
            u.addContent(new Element("NUMBER", ns).setText(work.fax));
            if (!u.getChildText("NUMBER", ns).equals("")) e.addContent(u);

            u = new Element("TEL", e.getNamespace());
            u.addContent(new Element("MSG", ns));
            u.addContent(new Element("WORK", ns));
            u.addContent(new Element("NUMBER", ns).setText(work.msg));
            if (!u.getChildText("NUMBER", ns).equals("")) e.addContent(u);
        }

        if (home != null) {
            u = new Element("ADR", ns);
            u.addContent(new Element("HOME", ns));
            add(u, "EXTADD", home.extadd);
            add(u, "STREET", home.street);
            add(u, "LOCALITY", home.locality);
            add(u, "REGION", home.region);
            add(u, "PCODE", home.pcode);
            add(u, "CTRY", home.country);
            if (u.getChildren().size() > 1) e.addContent(u);

            u = new Element("TEL", ns);
            u.addContent(new Element("VOICE", ns));
            u.addContent(new Element("HOME", ns));
            u.addContent(new Element("NUMBER", ns).setText(home.phone));
            if (!u.getChildText("NUMBER", ns).equals("")) e.addContent(u);

            u = new Element("TEL", ns);
            u.addContent(new Element("FAX", ns));
            u.addContent(new Element("HOME", ns));
            u.addContent(new Element("NUMBER", ns).setText(home.fax));
            if (!u.getChildText("NUMBER", ns).equals("")) e.addContent(u);

            u = new Element("TEL", ns);
            u.addContent(new Element("MSG", ns));
            u.addContent(new Element("HOME", ns));
            u.addContent(new Element("NUMBER", ns).setText(home.msg));
            if (!u.getChildText("NUMBER", ns).equals("")) e.addContent(u);
        }

        getDOM().addContent(e);

        return super.encode();
    }

    /**
     * A convenience method to add a pair of strings to a DOM element.
     * <p/>
     * This method adds to the <code>Element e</code> a new element with the name given
     * by the <code>elname</code> parameter and sets the new element's content to
     * the <code>eltext</code> string. If <code>eltext</code> is empty, nothing happens
     * (no Element will be added to <code>e</code> in this case).
     *
     * @param e      The {@link org.jdom.Element} to add the new element to.
     * @param elname The name of the new element.
     * @param eltext The content of the new element.
     */
    protected void add(Element e, String elname, String eltext) {
        if (e != null && !"".equals(eltext))
            e.addContent(new Element(elname, JabberCode.XMLNS_IQ_VCARD).setText(eltext));
    }
}

