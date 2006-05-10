
package simple;

import java.util.ArrayList;

import org.jibx.runtime.IUnmarshallingContext;

public class MyClass5 {
	private ArrayList childs1;
    private ArrayList childs2;
    private ArrayList childs3;
	
	private void unmarshalDone() {
		for (int i = 0; i < childs1.size(); i++) {
			((MyClass5a)childs1.get(i)).verify();
		}
        if (childs2 != null) {
            for (int i = 0; i < childs2.size(); i++) {
                ((MyClass5a)childs2.get(i)).verify();
            }
        }
        if (childs3 != null) {
            for (int i = 0; i < childs3.size(); i++) {
                ((MyClass5a)childs3.get(i)).verify();
            }
        }
	}
	
	private static MyClass5a bFactory() {
		MyClass5b inst = new MyClass5b();
		inst.factory = true;
		return inst;
	}
	
	private static MyClass5a cFactory(Object obj) {
		if (!(obj instanceof ArrayList)) {
			throw new IllegalStateException("factory called with wrong object");
		}
		MyClass5c inst = new MyClass5c();
		inst.factory = true;
		return inst;
	}
	
	private static MyClass5a dFactory(IUnmarshallingContext ctx) {
		if (!(ctx.getStackObject(1) instanceof MyClass5)) {
			throw new IllegalStateException("wrong object in stack");
		}
		MyClass5d inst = new MyClass5d();
		inst.factory = true;
		return inst;
	}
}
