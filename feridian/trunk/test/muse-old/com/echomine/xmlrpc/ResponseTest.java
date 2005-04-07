package com.echomine.xmlrpc;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ResponseTest extends TestCase {
    SerializerFactory factory = new SerializerFactory();

    public ResponseTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testResponseWithSerializers() {
        Response response = new Response(factory);
        HashMap map = new HashMap();
        response.setErrorResponse(4, "Too many parameters");
        assertTrue(response.isFault());
        map.put("faultCode", new Integer(4));
        map.put("faultString", "Too many parameters.");
        map.put("double", new Double(384839488888888D));
        map.put("boolean", new Boolean(true));
        Date date = new Date();
        map.put("date", date);
        map.put("base64", new String("testing 1 2 3").getBytes());
        ArrayList list = new ArrayList();
        list.add(new Integer(4));
        list.add("array string");
        map.put("array", list);
        response.setResponse(map);
        try {
            assertFalse(response.isFault());
            map = (HashMap)response.getResponse();
            assertEquals(4, ((Integer) map.get("faultCode")).intValue());
            assertEquals("Too many parameters.", map.get("faultString"));
            assertTrue(((Boolean) map.get("boolean")).booleanValue());
            assertEquals(384839488888888D, ((Double) map.get("double")).doubleValue(), 0);
            assertEquals(date.toString(), map.get("date").toString());
            list = (ArrayList)map.get("array");
            assertEquals(4, ((Integer) list.get(0)).intValue());
            assertEquals("array string", list.get(1));
            assertEquals("testing 1 2 3", new String((byte[]) map.get("base64")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
