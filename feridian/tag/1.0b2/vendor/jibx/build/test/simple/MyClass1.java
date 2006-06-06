
package simple;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;

class MyClass1 {
	private int a;
	private String b;
    private Dimension dimen;
    private Rectangle rect;
	private ArrayList ints;
    
    // force generation of default constructor
    public MyClass1(int a) {
        this.a = a;
    }

	public int getA() { return a; }
	public void setA(int a) { this.a = a; }

	public String getB() { return b; }
	public void setB(String b) { this.b = b; }
}
