package com.echomine.feridian;

import java.util.Collections;
import java.util.List;

/**
 * Represents one feridian-extensions.xml config file.
 */
public class FeridianExtensions {
    private List<FeridianStreamExtension> streamList;
    private List<FeridianPacketExtension> packetList;
    private List<FeridianAuthenticator> authenticators;

    /**
     * retrieves non-modifiable list of packet extensions
     * 
     * @return Returns the packet extensions, never null.
     */
    public List getPacketExtensions() {
        if (packetList == null)
            return Collections.EMPTY_LIST;
        return Collections.unmodifiableList(packetList);
    }

    /**
     * Retrieves non-modifiable list of streams
     * 
     * @return list of streams registered in the config, never null
     */
    public List getStreamList() {
        if (streamList == null)
            return Collections.EMPTY_LIST;
        return Collections.unmodifiableList(streamList);
    }

    /**
     * Retrieves non-modifiable list of authenticators
     * 
     * @return list of authenticators registered in the config, never null
     */
    public List getAuthenticators() {
        if (authenticators == null)
            return Collections.EMPTY_LIST;
        return Collections.unmodifiableList(authenticators);
    }
}
