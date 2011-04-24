package taschenrechner;

import junit.framework.TestCase;

public class AdditionTest extends TestCase {
	Addition obj;
	Stack stack;

	@Override
	public void setUp() throws Exception {
		stack = Setup.genStack();
		obj = new Addition(stack, Token.S_add);
	}
	public void testExec1() throws Exception {
		stack.add(new Int(123, stack));
		stack.add(new Int(-23, stack));
		obj.exec();
		assertEquals("ToS is 100.", 100, stack.pop().getInt());
	}
	public void testExec2() throws Exception {
		stack.add(new Int(123, stack));
		try {
			obj.exec();
			fail("not enough elements on stack");
		} catch (ExecuteException e) { }
	}
	public void testExec3() throws Exception {
		try {
			obj.exec();
			fail("not enough elements on stack");
		} catch (ExecuteException e) { }
	}
	public void testExec4() throws Exception {
		stack.add(new Unit("asdf", stack));
		stack.add(new Int(123, stack));
		try {
			obj.exec();
			fail("types don't match");
		} catch (ExecuteException e) { }
	}
}

class Setup {
	public static Stack genStack() {
		return new Stack();
	}
	public static InputList genInputList() {
		return new InputList(10);
	}
}
