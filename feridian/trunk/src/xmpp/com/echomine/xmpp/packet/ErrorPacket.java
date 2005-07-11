package com.echomine.xmpp.packet;

import com.echomine.xmpp.IPacket;
import com.echomine.xmpp.NSI;

/**
 * This packet defines the stream level error packet.
 */
public class ErrorPacket implements IPacket {
    private String condition;
    private NSI applicationCondition;
    private String text;

    /**
     * @return Returns the application-custom condition code.
     */
    public NSI getApplicationCondition() {
        return applicationCondition;
    }

    /**
     * @param applicationCondition The application-custom condition code to set.
     */
    public void setApplicationCondition(NSI applicationCondition) {
        this.applicationCondition = applicationCondition;
    }

    /**
     * @return Returns the defined error condition.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition The error condition to set.
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * @return Returns the descriptive error text.
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The descriptive error text to set.
     */
    public void setText(String text) {
        this.text = text;
    }
}
