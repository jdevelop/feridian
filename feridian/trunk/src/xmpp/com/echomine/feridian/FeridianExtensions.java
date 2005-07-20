package com.echomine.feridian;

import java.util.Collections;
import java.util.List;

/**
 * Represents one feridian-extensions.xml config file.
 */
public class FeridianExtensions {
    private List streamList;
    private List packetList;

    /**
     * retrieves non-modifiable list of packet extensions
     * 
     * @return Returns the packet extensions.
     */
    public List getPacketExtensions() {
        return Collections.unmodifiableList(packetList);
    }

    /**
     * Retrieves non-modifiable list of streams
     * 
     * @return list of streams registered in the config
     */
    public List getStreamList() {
        return Collections.unmodifiableList(streamList);
    }
}
