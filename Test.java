public class Test {
	public void foo() {
		System.out.println("in Test");
	}

	public static void main(String[] args) {
		System.out.println("@main");
		new Test().foo();
		new Test2().foo();
	}
}

class Test2 {
	public void foo() {
		System.out.println("in Test2");
	}
}

class Add {
	private Stack stack;
	public Add(Stack s) {
		this.stack = s;
	}
	public void exec() {
		stack.push(stack.pop().add(stack.pop());
		// nenbnenbörseörse
	}
}

/* Interface fuer Methoden
 * -> Add
 * -> Sub
 * -> ...
 *
 * Typen: Int und Ausdruecke
 * zusaetzlicher Typ fuer Boolean?
 */
