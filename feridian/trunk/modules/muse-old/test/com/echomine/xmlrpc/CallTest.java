package com.echomine.xmlrpc;

import junit.framework.TestCase;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CallTest extends TestCase {
    SerializerFactory factory = new SerializerFactory();

    public CallTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Tests that calls with no parameters are parsed and encoded properly
     */
    public void testCallWithNoParameters() throws Exception {
        Call call = new Call("getHelloWorldString", factory);
        String callStr = call.toString();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(callStr));
        Call inCall = new Call(factory);
        inCall.parse(doc.getRootElement());
        assertEquals("getHelloWorldString", inCall.getMethodName());
        assertEquals(0, inCall.getParameters().length);
    }

    public void testCallWithSerializers() throws Exception {
        Call call = new Call("getHelloWorldString", factory);
        call.addParameter("Testing 1 2 3");
        ArrayList list = new ArrayList();
        list.add("index 0");
        list.add("index 1");
        list.add("index 2");
        call.addParameter(list);
        HashMap map = new HashMap();
        map.put("faultCode", new Integer(1));
        map.put("faultString", "too few < parameters.");
        call.addParameter(map);
        call.addParameter(new Boolean(true));
        call.addParameter(new Double(2342984288D));
        Date date = new Date();
        call.addParameter(date);
        call.addParameter("a base64 string".getBytes());
        String callStr = call.toString();
        //parse the call back to original
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(callStr));
        Call inCall = new Call(factory);
        inCall.parse(doc.getRootElement());
        assertEquals("getHelloWorldString", inCall.getMethodName());
        Object[] objs = inCall.getParameters();
        assertEquals("Testing 1 2 3", objs[0]);
        assertEquals("index 0", ((ArrayList) objs[1]).get(0));
        assertEquals("index 1", ((ArrayList) objs[1]).get(1));
        assertEquals("index 2", ((ArrayList) objs[1]).get(2));
        assertEquals(1, ((Integer) ((HashMap) objs[2]).get("faultCode")).intValue());
        assertEquals("too few < parameters.", ((HashMap) objs[2]).get("faultString"));
        assertTrue(((Boolean) objs[3]).booleanValue());
        assertEquals(2342984288D, ((Double) objs[4]).doubleValue(), 0);
        assertEquals(date.toString(), objs[5].toString());
        assertEquals("a base64 string", new String((byte[]) objs[6]));
    }
}
