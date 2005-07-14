package com.echomine.feridian;

import java.util.Collections;
import java.util.List;

/**
 * Represents one feridian-extensions.xml config file.
 */
public class FeridianExtensions {
    private List packetList;

    /**
     * retrieves non-modifiable list of packet extensions
     * 
     * @return Returns the packet extensions.
     */
    public List getPacketExtensions() {
        return Collections.unmodifiableList(packetList);
    }
}
