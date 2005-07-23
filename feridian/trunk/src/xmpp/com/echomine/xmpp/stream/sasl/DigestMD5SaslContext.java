package com.echomine.xmpp.stream.sasl;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.echomine.util.Base64;

/**
 * Stores sasl data exchanged between two entities
 */
public class DigestMD5SaslContext {
    private static Log log = LogFactory.getLog(DigestMD5SaslContext.class);
    private static Pattern namevalPat = Pattern.compile("^(.+)=\"?(\\S+?)\"?\\s*$");
    private HashMap values = new HashMap();
    
    /**
     * @return the realm
     */
    public String getRealm() {
        return (String) values.get("realm");
    }
    
    /**
     * @return the nonce
     */
    public String getNonce() {
        return (String) values.get("nonce");
    }
    
    /**
     * 
     * @return the qop authentication method
     */
    public String getQop() {
        return (String) values.get("qop");
    }
    
    /**
     * 
     * @return the character set
     */
    public String getCharset() {
        return (String) values.get("charset");
    }
    
    /**
     * 
     * @return the algorithm
     */
    public String getAlgorithm() {
        return (String) values.get("algorithm");
    }
    
    /**
     * unwraps a base64-encoded string and decodes all the values inside
     * @param base64Str the base64-encoded string
     */
    public void unwrap(String base64Str) {
        if (base64Str == null)
            throw new IllegalArgumentException("String data is null");
        String str = Base64.decodeToString(base64Str);
        if (log.isDebugEnabled())
            log.debug("Decoded Challenge String: " + str);
        //parse through all values
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        String token;
        Matcher matcher;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            matcher = namevalPat.matcher(token);
            if (matcher.matches())
                values.put(matcher.group(1), matcher.group(2));
        }
    }
}
