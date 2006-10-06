package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import java.util.HashMap;

/**
 * This is the object that will construct a response object.. It can used to
 * parse an incoming response request or construct an response. The response is
 * namespace aware, but by default, no namespace is associated with it unless you specifically set the namespace.
 */
public class Response {
    private String faultString;
    private int faultCode;
    private boolean isFault;
    private Object respData;
    private Namespace ns;
    private SerializerFactory factory;

    /**
     * an empty constructor that is useful when you want to set the data with object information
     */
    public Response(SerializerFactory factory) {
        if (factory == null) throw new IllegalArgumentException("Factory cannot be null");
        this.factory = factory;
    }

    /**
     * constructs a response with the proper return data
     */
    public Response(Object response, SerializerFactory factory) {
        if (factory == null) throw new IllegalArgumentException("Factory cannot be null");
        this.factory = factory;
        setResponse(response);
    }

    /**
     * constructs an error response with the provided fault code and error string
     */
    public Response(int faultCode, String faultString, SerializerFactory factory) {
        if (factory == null) throw new IllegalArgumentException("Factory cannot be null");
        this.factory = factory;
        setErrorResponse(faultCode, faultString);
    }

    /**
     * constructs a response from a dom element. This is normally used to parse an incoming message
     */
    public Response(Element elem, SerializerFactory factory) {
        if (factory == null) throw new IllegalArgumentException("Factory cannot be null");
        this.factory = factory;
        parse(elem);
    }

    /**
     * parses a response from the element. This will reset all the internal data before parsing
     */
    public void parse(Element elem) {
        faultString = null;
        faultCode = 0;
        isFault = false;
        respData = null;
        ns = elem.getNamespace();
        //check if element is a fault or regular
        Element firstElem = elem.getChild("fault", ns);
        if (firstElem != null) {
            isFault = true;
            //let's parse it as fault
            Element fault = firstElem.getChild("value", ns).getChild("struct", ns);
            respData = factory.deserialize(fault);
            faultCode = ((Integer) ((HashMap) respData).get("faultCode")).intValue();
            faultString = (String) ((HashMap) respData).get("faultString");
            return;
        }
        //check if it's a normal response
        firstElem = elem.getChild("params", ns);
        if (firstElem == null) return;
        firstElem = firstElem.getChild("param", ns);
        if (firstElem == null) return;
        firstElem = firstElem.getChild("value", ns);
        if (firstElem == null) return;
        //now get the first element
        Element data = (Element) firstElem.getChildren().get(0);
        //deserialize the data
        respData = factory.deserialize(data);
    }

    public void setNamespace(Namespace ns) {
        this.ns = ns;
    }

    public Namespace getNamespace() {
        return ns;
    }

    /**
     * check if the response is a fault/error response
     */
    public boolean isFault() {
        return isFault;
    }

    public int getFaultCode() {
        return faultCode;
    }

    public String getFaultString() {
        return faultString;
    }

    /**
     * retrieves the response data if the message is not an error.  Null is returned is no response is present.
     * You should know exactly what the type of the response is being returned since you most likely submitted
     * the call request to begin with.  If the response is an error, this method will return a HashMap of the
     * fault code/string (of course, you can also just retrieve it by calling the convenience methods).
     * If this method returns a valid response, then it will contain the data you want.
     */
    public Object getResponse() {
        return respData;
    }

    /**
     * sets the response to the indicated value.  The data should be one of the "core" types for the XMLRPC.
     * In this case, they should be Double, Integer, Boolean, byte[], ArrayList, or HashMap.
     * By setting the response, the fault data will be cleared.
     */
    public void setResponse(Object respData) {
        this.respData = respData;
        isFault = false;
        faultCode = 0;
        faultString = null;
    }

    /**
     * sets the error response.  By setting the response to an error, the response data will get set to the
     * error response message.
     */
    public void setErrorResponse(int faultCode, String faultString) {
        isFault = true;
        respData = null;
        this.faultCode = faultCode;
        this.faultString = faultString;
        HashMap faultMap = new HashMap();
        faultMap.put("faultCode", new Integer(faultCode));
        faultMap.put("faultString", faultString);
        respData = faultMap;
    }

    public Element getDOM() {
        Element root = new Element("methodResponse", ns);
        Element resp;
        //create the initial structure, which is different
        //for a normal response vs. an error response
        if (isFault()) {
            resp = new Element("fault", ns);
            root.addContent(resp);
        } else {
            Element params = new Element("params", ns);
            resp = new Element("param", ns);
            params.addContent(resp);
            root.addContent(params);
        }
        //now the code that is common to both types of responses
        //add the <value> tag
        Element value = new Element("value", ns);
        resp.addContent(value);
        //add the respData
        Element data = factory.serialize(respData, ns);
        value.addContent(data);
        return root;
    }

    public String toString() {
        Element dom = getDOM();
        XMLOutputter xos = new XMLOutputter();
        return xos.outputString(dom);
    }
}
