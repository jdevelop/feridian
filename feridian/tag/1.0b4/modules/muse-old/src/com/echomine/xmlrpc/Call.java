package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import java.util.ArrayList;
import java.util.List;

/**
 * The main object to construct a method call for the XML RPC.  You set the method name, set the
 * parameters, and then output it as a string within the intended protocol.. For instance, if you
 * are using HTTP, you will need to wrap the method call inside a HTTP request.
 */
public class Call {
    private String methodName;
    private Namespace ns;
    private ArrayList params = new ArrayList();
    private SerializerFactory factory;

    /**
     * constructs an empty call object that's normally used for parsing an incoming call object
     */
    public Call(SerializerFactory factory) {
        if (factory == null) throw new IllegalArgumentException("Factory cannot be null");
        this.factory = factory;
    }

    /**
     * constructs a request object for sending out
     */
    public Call(String methodName, SerializerFactory factory) {
        this(methodName, null, factory);
    }

    /**
     * constructs a request object with the proper namespace
     */
    public Call(String methodName, Namespace ns, SerializerFactory factory) {
        if (factory == null) throw new IllegalArgumentException("Factory cannot be null");
        this.methodName = methodName;
        this.ns = ns;
        this.factory = factory;
    }

    public void setNamespace(Namespace ns) {
        this.ns = ns;
    }

    public Namespace getNamespace() {
        return ns;
    }

    public void setMethodName(String name) {
        this.methodName = name;
    }

    public String getMethodName() {
        return methodName;
    }

    public void addParameter(Object param) {
        params.add(param);
    }

    public void addParameters(Object[] param) {
        for (int i = 0; i < param.length; i++) {
            params.add(param[i]);
        }
    }

    /**
     * obtains the parameter value. You will have to either know the type of the parameter beforehand or dynamically find
     * out exactly what type the parameter value is.  Technically, you should know the method signature
     * when parsing this request.  Otherwise, you will have to check if the instance is of the base XMLRPC type (ie. Double,
     * Integer, Boolean, etc).
     */
    public Object getParameter(int idx) {
        return params.get(idx);
    }

    /**
     * retrieves all the parametes
     */
    public Object[] getParameters() {
        return params.toArray();
    }

    public Element getDOM() {
        Element root = new Element("methodCall", ns);
        Element method = new Element("methodName", ns);
        method.setText(methodName);
        root.addContent(method);
        if (params.size() == 0) return root;
        Element paramsElem = new Element("params", ns);
        root.addContent(paramsElem);
        int length = params.size();
        Element param;
        Element paramValue;
        Element valueData;
        Object obj;
        for (int i = 0; i < length; i++) {
            obj = params.get(i);
            valueData = factory.serialize(obj, ns);
            //no serialized data, skip this parameter
            if (valueData == null) continue;
            param = new Element("param", ns);
            paramValue = new Element("value", ns);
            paramValue.addContent(valueData);
            param.addContent(paramValue);
            paramsElem.addContent(param);
        }
        return root;
    }

    /**
     * parses a response from the element. This will effectively clear out the current parameters stored in the object.
     */
    public void parse(Element elem) {
        params.clear();
        methodName = null;
        ns = elem.getNamespace();
        //first element should be <methodCall>
        //look for the child <methodName>
        methodName = elem.getChildText("methodName", ns);
        //parse out parameters if there are any
        Element paramsElem = elem.getChild("params", ns);
        if (paramsElem == null) return;
        List paramList = paramsElem.getChildren();
        int len = paramList.size();
        Element data, value;
        Object paramVal;
        for (int i = 0; i < len; i++) {
            data = (Element) paramList.get(i);
            //obtain the data inside the <param><value>
            value = (Element) data.getChild("value", ns).getChildren().get(0);
            //deserialize each parameter
            paramVal = factory.deserialize(value);
            params.add(paramVal);
        }
    }

    public String toString() {
        Element dom = getDOM();
        XMLOutputter xos = new XMLOutputter();
        return xos.outputString(dom);
    }
}
