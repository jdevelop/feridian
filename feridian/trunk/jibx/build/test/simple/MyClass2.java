
package simple;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

class MyClass2 {
	private int a;
	private String b;
	private double c;
	private boolean d;
	private Object e;
	private Integer f;
	private Integer g;
    private Dimension dimen;
    private Rectangle rect;
	private List ints;
    private SortedSet orderedStrings;

	public int getA() { return a; }
	public void setA(String a) { this.a = Integer.parseInt(a); }
	public void setA(int a) { this.a = a; }

	public String getB() { return b; }
	public void setB(String b) { this.b = b; }
	public void setB(int b) { this.b = Integer.toString(b); }
	
	private Object getG() {
		return g;
	}
	
	private void setG(Object value) {
		g = (Integer)value;
	}
    
    private List getList() {
        return ints;
    }
    
    private void setList(ArrayList list) {
        ints = list;
    }
    
    private static List listFactory() {
        return new ArrayList();
    }
    
    private static ArrayList arrayListFactory() {
        return new ArrayList();
    }
    
    private static SortedSet sortedSetFactory() {
        return new TreeSet();
    }
}
